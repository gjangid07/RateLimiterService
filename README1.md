1. Execution instructions:
	1. Extract RateLimiterSolution.zip file and open the RateLimiterSolution project into Eclipse IDE.
	2. Once the project is opened in the Eclipse, resolve the dependencies mentioned in the pom.xml (Right Click the project folder-> Maven-> Update Project).
		This step shall resolve all the dependicies required.
	3. Execute/Run the java file (Rate-Limiter/src/main/java/com/ratelimiter/solution/Rate_Limiter/RateLimiterApiApplication.java) as Java application.
	4. Once a tomcat server is started, Open a browser and enter the url 'http://localhost:8080/' to load the inputfile. 
	5. Once the inputfile is loaded(can check the same on eclipse console), enter the url 'http://localhost:8080/handleRateLimit'.
	    This will start the rate limit service for all the input requests

2. Input Format: Input file content should always be in the format "User_ID API". Input file(inputDataFile) is located in the the project directory.
   Avoid providing redundant entries in the input file.

3. Database: In place of real database, database file(databaseFile) is used which is located in the the project directory. 
   Database file content format is "User_ID API Rate_Limit". Configurable "Rate Limit" for a User for an API can be given through this.
   This Database file is loaded in a ConcurrentHashmap which is acting as like a DB here.

4. Output : Output can be seen in the console of eclipse showing response messages for each of the request of a user for an API.


	




