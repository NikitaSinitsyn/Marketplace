**Marketplace**

Marketplace is an online platform that allows users to create listings for selling goods and services. Users can also browse listings, leave comments, and communicate with other users. The project is implemented using Java, Spring Boot, and React.js.

**Project Contributors:**

Nikita Sinitsyn (Developer)


**Project Description**

Marketplace provides a simple and convenient way to post and search for listings. It allows users to:

Create accounts with personal information such as name, email, and more.

Log in and log out using their credentials.

Create listings with a title, description, price, and image.

Browse listings posted by other users.

Leave comments on listings.

Search for listings based on various criteria.


**Technologies and Libraries Used**

The project is built using the following technologies and libraries:

**Backend:**

Java

Spring Boot

Spring Security

Hibernate

PostgreSQL

Liquibase


**Frontend:**

React.js

**Infrastructure:**

Docker


**Running the Application**

**Running the Backend**

To run the backend, follow these steps:

Clone the backend repository to your computer.

Open the project in your IDE (e.g., IntelliJ IDEA).

Ensure you have Java and PostgreSQL installed.

Create a PostgreSQL database and configure the connection parameters in the application.properties file.

Run the backend application.


**Running the Frontend**

To run the frontend using Docker, execute the following command in your command prompt (or terminal):

docker run -p 3000:3000 --rm ghcr.io/bizinmitya/front-react-avito:v1.18

After running this command, the frontend will be available on port 3000. You can open it in a web browser by navigating to http://localhost:3000.
