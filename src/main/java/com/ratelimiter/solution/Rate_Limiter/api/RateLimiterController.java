package com.ratelimiter.solution.Rate_Limiter.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ratelimiter.solution.Rate_Limiter.RateLimiterConstants;
import com.ratelimiter.solution.Rate_Limiter.request.Resource;
import com.ratelimiter.solution.Rate_Limiter.request.User;
import com.ratelimiter.solution.Rate_Limiter.response.Response;

/**
 * @author gjangid
 *
 */
@RestController
public class RateLimiterController {

	private static final Logger logger = Logger.getLogger(RateLimiterController.class.getName());

	private static Map<RateLimitParam, RateLimiter> limiters = new ConcurrentHashMap<>();

	private List<RateLimitParam> listOfRequests = new ArrayList<>();

	// Loading Data from databaseFile in the static block.
	static {
		final String databaseFile = RateLimiterConstants.DB_FILE;

		File dbfile = new File(databaseFile);
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(dbfile));
			String currentLine;
			while ((currentLine = bufferedReader.readLine()) != null) {
				String[] inputs = currentLine.split(" ");
				RateLimitParam key = new RateLimitParam(new User(Integer.parseInt(inputs[0])), new Resource(inputs[1]));

				RateLimiter rateLimiter = RateLimiter.createRateLimiter(key.getUser().getUserId(),
						key.getResource().getUrlString(), Integer.parseInt(inputs[2]));
				limiters.put(key, rateLimiter);
			}
			bufferedReader.close();
		} catch (IOException ex) {
			logger.logp(Level.SEVERE, RateLimiterController.class.getName(), "",
					"Error populating data from database file." + databaseFile);
		}
	}

	// Loading of input data from inputDataFile and creating a list of request out
	// of inputs.
	@RequestMapping(value = "/")
	public void loadInput() {
		logger.log(Level.INFO, "RateLimiterController::loadInput()");
		final String inputFilePath = RateLimiterConstants.INPUT_FILE;

		File file = new File(inputFilePath);
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			String currentLine;
			while ((currentLine = bufferedReader.readLine()) != null) {
				String[] inputs = currentLine.split(" ");
				RateLimitParam request = new RateLimitParam(new User(Integer.parseInt(inputs[0])),
						new Resource(inputs[1]));
				getRateLimiter(request);
				// Creating a list of requests(RateLimitParam) containing User and Resource
				// details using InputDataFile.
				listOfRequests.add(request);

			}
			bufferedReader.close();
		} catch (IOException ex) {
			logger.logp(Level.SEVERE, RateLimiterController.class.getName(), "RateLimiterController::loadInput()",
					"Error reading data from input file" + inputFilePath);
		}
		logger.log(Level.INFO, "RateLimiterController::loadInput(): Loading of Input is done.");
	}

	// Handling requests for each of the input request which is executed
	// periodically at the interval of 1 second.
	@RequestMapping(value = "/handleRateLimit")
	public void handleRateLimit() throws Exception {
		logger.log(Level.INFO, "RateLimiterController::handleRateLimit()");
		ExecutorService executor = Executors.newFixedThreadPool(listOfRequests.size());

		while (true) {
			for (int i = 0; i < listOfRequests.size(); i++) {
				System.out.println("Time: " + LocalTime.now() + " Request:: Handling API request: "
						+ listOfRequests.get(i).getResource().getUrlString() + " from the "
						+ listOfRequests.get(i).getUser());
				// Executing the input requests and consuming the permits available to
				// [user+api] pair.
				Future<Response> future = executor.submit(new WorkerThread(limiters.get(listOfRequests.get(i))));
				Response response = future.get();
				System.out.println(response.getMessage());
			}
			// Executing each input requests at every API_REQUEST_FREQUENCY
			// time period(kept 1 second by default).
			Thread.sleep(RateLimiterConstants.API_REQUEST_FREQUENCY);
		}

	}

	/**
	 * @param key
	 *            key to retrieve the Rate Limiter associated with it
	 * @return RateLimiter
	 */
	private RateLimiter getRateLimiter(RateLimitParam key) {
		if (limiters.containsKey(key)) {
			return limiters.get(key);
		} else {
			synchronized (key) {
				// Double Check to avoid reinitialization.
				if (limiters.containsKey(key)) {
					return limiters.get(key);
				}

				RateLimiter rateLimiter = RateLimiter.createDefaultRateLimiter(key.getUser().getUserId(),
						key.getResource().getUrlString());
				limiters.put(key, rateLimiter);
				return rateLimiter;
			}
		}
	}

}

class WorkerThread implements Callable<Response> {

	private RateLimiter rateLimiter;

	/**
	 * @param rateLimiter
	 */
	public WorkerThread(RateLimiter rateLimiter) {
		this.rateLimiter = rateLimiter;
	}

	@Override
	public Response call() throws Exception {

		Long firstRequestTime = rateLimiter.getFirstRequestTime();

		if (firstRequestTime == null) {
			firstRequestTime = System.currentTimeMillis();
			rateLimiter.setFirstRequestTime(firstRequestTime);
			rateLimiter.resettingPermits();
		}
		boolean allowRequest = rateLimiter.tryAcquire();
		Response response = new Response();
		if (!allowRequest) {
			response.setMessage(
					"Time: " + LocalTime.now() + " Response:: Rejecting Request for the user: " + rateLimiter.getUser()
							+ " as there are too many API Request: " + rateLimiter.getResource().getUrlString());
		} else {
			response.setMessage("Time: " + LocalTime.now() + " Response:: Available permits for the user: "
					+ rateLimiter.getUser() + " & API Request : " + rateLimiter.getResource().getUrlString() + " are : "
					+ rateLimiter.getSemaphore().availablePermits());
		}

		response.setTimelyLimit(rateLimiter.getSemaphore().availablePermits());
		return response;
	}

}
