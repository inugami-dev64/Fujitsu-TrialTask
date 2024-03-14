# Fujitsu Java programming trial task

The objective of the task is to develop a sub-functionality for a food delivery application, which
calculates the delivery fee for food couriers based on regional base fee, vehicle type and weather conditions.

The project is written using following technologies:
* Java 17
* Springboot
* H2

## Implemented features

The list of implemented features are following:  
* REST interface, which enables querying the delivery fee based on input parameters (including historical data when specific timestamp is requested)
* Configurable scheduled task for importing weather observations from Ilmateenistus XML ticker API
* Fee calculation business logic CRUD interface

## Quick-start guide

Please keep in mind that this project has been developed and tested on OpenJDK 17 and as such it is the recommended JDK to use for building and running this application.
Other JDK implementations might also work but the compatibility is not fully guaranteed.

The quickest way to build and package the fee application is by using `Maven`. Doing so is relatively straight forward.

On Linux/MacOS
```
./mvnw package
```

On Windows
```
.\mvnw.cmd package
```

### Configuring the application

The application's configuration is based around internal `application.properties` file, which contains sane defaults for development purposes but should be overriden for any 
potential production environments. The configuration values can be supplemented when needed by using appropriate spring environment properties. For instance let's say that
the configuration values are held at `/path/to/overriden.properties`. In that case the application should be run with following commandline arguments:  
```
java -jar feeservice-1.0.0-SNAPSHOT.jar --spring.config.location=/path/to/overriden.properties
```

An example properties file can look something like this:  
```
spring.datasource.url=jdbc:h2:file:./data/db
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
scheduling.weatherimport.cron=0 15 * * * ?
spring.datasource.username=courierfee
spring.datasource.password=password123
spring.h2.console.enabled=false
```

Overriding `application.properties` allows us to define the general behaviour of the application such as the cron value for scheduled weather data imports,
DBMS, database credentials and spring datasource driver. 

### Seeding the database with initial data

Although the implementation has a CRUD interface for defining business rules, it might become tedious to define all predefined rules manually (as mentioned in the requirements document). 
In order to simplify the database initialization process, it is possible to have the database initialized with predefined data by simply making one HTTP request.
```seeding
GET /api/admin/data/init
```
This request only works when all database tables are empty, otherwise it will fail with an error message.

## REST api endpoints

In general the API endpoints can be summarized with following bullet list:  
* `GET /api/admin/data/init` represents database initialization endpoint, which only succeeds when all database tables are empty.
* `GET /api/courierfee?city=<city>&vehicle=<vehicle>[&unixTimestamp=<ts>]` represents courier fee calculation endpoint, where
  * `<city>` represents the city name where the courier is currently working. Initially the database gets seeded with `Tallinn`, `Tartu` and `Pärnu`.
  * `<vehicle>` represents the type of vehicle courier is using. Valid values are `car`, `scooter`, `bike` (case insensitive).
  * `<ts>` optionally specifies the Unix timestamp at which point in time the returned fee was valid.
* `GET /api/locations/{id}` represents single location read endpoint for business logic CRUD
  * `/{id}` specifies the id of the location to query for
* `GET /api/locations` represents READ all endpoint for business logic CRUD
* `POST /api/locations` represents CREATE new endpoint for business logic CRUD
* `PUT /api/locations/{id}` represents UPDATE endpoint for business logic CRUD
* `DELETE /api/locations/{id}` represents DELETE endpoint for business logic CRUD
