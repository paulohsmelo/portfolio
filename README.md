# Portfolio App

### Overview

This is an application built in `Java` for backend and `React` for frontend, used to show and edit portfolio details.

### Technical Details

List of tools, languages and technologies used in this application:
* Backend
  * Java (JDK 11)
  * Spring Boot
  * Spring Data Jpa
  * Maven
  * MySQL Database
  * REST APIs
  * Twitter API Client Library for Java (https://github.com/twitterdev/twitter-api-java-sdk)
  * H2 (for tests only)


* Fronted
  * React
  * Node
  * Npm
  * Yarn
  * Bootstrap
  * React Router
  * Reactstrap


#### Note

The backend was built to receive any portfolio ID, but for now the frontend is fetching a `fixed portfolio ID = 188`, which is a portfolio I created in the database.

The total time to build this application was around 18 hours.

#### Endpoints

The application provides 2 endpoints:
* `GET /portfolio/{id}` : Retrieves portfolio information and a list containing the last 5 tweets for the given ID.
  * Returns:
    * `404` If there's no portfolio for the given ID.
    * `500` If the twitter integration fails.
    * `200` In case of success.


* `PUT /portfolio/{id}` : Change portfolio information with provided request body for the given ID, if there's no portfolio for the given ID a new portfolio is created.
  * Returns:
    * `400` If any validation fails (all the information cannot be null and `twitterUserId` must be a number).
    * `200` In case of success.

### Building and Running

Both backend and frontend are in the same git repository as they are actually a single application. The `frontend-maven-plugin` is used to package all content together.

Prerequisites:
* Git, to clone the repository.
* Maven, to build the application.
* Java 11, to run the application.

Steps to clone, build and run the application:
1) Run `git@github.com:paulohsmelo/portfolio.git`
2) Run `mvn spring-boot:run`
3) Access `http://localhost:8080/`

You can also build the project using `mvn install` and run using `java -jar target/portfolio-0.0.1-SNAPSHOT.jar`

### Next Steps

To improve this application, the next versions can add or improve:
* Add a API documentation (swagger).
* Change the Integration Tests and include E2E test with frontend using [Test Containers](https://www.baeldung.com/docker-test-containers). 
* Create a frontend page to list all portfolios in the database and remove the `fixed ID 118` from the code.
* Improve the frontend adding proper css and styling.