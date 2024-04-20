package com.ratelimiter.solution.Rate_Limiter.api;

import java.time.LocalTime;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.ratelimiter.solution.Rate_Limiter.RateLimiterConstants;
import com.ratelimiter.solution.Rate_Limiter.request.Resource;
import com.ratelimiter.solution.Rate_Limiter.request.User;

/**
 * @author gjangid
 *
 */
public class RateLimiter {
	private Semaphore semaphore;
	private int maxPermits;
	private TimeUnit timeUnit;
	private ScheduledExecutorService scheduler;
	private Long firstRequestTime;
	private User user;
	private Resource resource;

	// Creating a Rate Limiter object for each User+API pair.
	public static RateLimiter create(int permits, TimeUnit timeUnit, int userId, String apiUrl) {
		RateLimiter limiter = new RateLimiter(permits, timeUnit, userId, apiUrl);
		return limiter;
	}

	// Creating a default Rate Limiter object for User+API pair which is not
	// available in DB.
	public static RateLimiter createDefaultRateLimiter(int userId, String apiUrl) {
		return create(RateLimiterConstants.DEFAULT_PERMITS_NUMBER, TimeUnit.MINUTES, userId, apiUrl);
	}

	// Creating a Rate Limiter object for each User+API pair.
	public static RateLimiter createRateLimiter(int userId, String apiUrl, int permits) {
		return create(permits, TimeUnit.MINUTES, userId, apiUrl);
	}

	private RateLimiter(int permits, TimeUnit timeUnit, int userId, String apiUrl) {
		this.semaphore = new Semaphore(permits);
		this.maxPermits = permits;
		this.timeUnit = timeUnit;
		this.user = new User(userId);
		this.resource = new Resource(apiUrl);
	}

	// Acquiring permits for each of the input request.
	public boolean tryAcquire() {
		return semaphore.tryAcquire();
	}

	// To Stop the scheduler associated with a Rate Limiter.
	public void stop() {
		scheduler.shutdownNow();
	}

	// Resetting Permits of the Rate Limiter at the interval of 1 minute(default)
	// after the initial delay = firstRequestTime
	public void resettingPermits() {
		scheduler = Executors.newScheduledThreadPool(1);

		scheduler.scheduleAtFixedRate(() -> {
			System.out.println("Time: " + LocalTime.now() + " Resetting permits for the user: " + user.toString()
					+ " and API Request: " + resource.getUrlString());
			semaphore.release(maxPermits - semaphore.availablePermits());
		}, RateLimiterConstants.TIME_INTERVAL, RateLimiterConstants.INITIAL_TIME_DELAY, timeUnit);

	}

	public User getUser() {
		return user;
	}

	public Resource getResource() {
		return resource;
	}

	public Semaphore getSemaphore() {
		return semaphore;
	}

	public Long getFirstRequestTime() {
		return firstRequestTime;
	}

	public void setFirstRequestTime(Long firstRequestTime) {
		this.firstRequestTime = firstRequestTime;
	}

}
