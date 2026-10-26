# CinePass — Movie Ticket Booking System

This is a Java Spring Boot + MySQL project intended to run in Visual Studio Code.

## Requirements
- JDK 21 (make sure `java -version` reports version 21)
- MySQL 8.x, running locally on port 3306
- Apache Maven 3.9.x (the included `mvnw.cmd` expects it at `%USERPROFILE%\\.m2\\apache-maven-3.9.6`)

## Open in VS Code
1. Extract the ZIP.
2. In VS Code, choose **File → Open Folder** and open the extracted `movie ticket booking` folder (the folder containing `pom.xml`).
3. Install the **Extension Pack for Java** and **Spring Boot Extension Pack** if prompted.
4. Edit `src/main/resources/application.properties` and set the MySQL username/password for your machine:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```
   If your local MySQL root account has no password, leave the password empty.
5. Start MySQL.
6. Open the VS Code terminal in the project folder and run:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
   Or run `run.bat` from File Explorer / terminal.
7. Open `http://localhost:8080` in your browser.

The application creates `movie_booking_db` automatically and Hibernate creates/updates its tables.

## Troubleshooting
- **`mvnw.cmd` says Maven not found:** install Apache Maven 3.9.x or update the `MAVEN_HOME` path inside `mvnw.cmd`.
- **Database access denied:** correct `spring.datasource.username` and `spring.datasource.password`.
- **Port 8080 in use:** change `server.port=8081` in `application.properties` and open `http://localhost:8081`.
- First Maven run requires internet access to download dependencies.

Repository interfaces under `src/main/java/com/movieticket/repository` were missing from the uploaded source. They have been restored, including the custom queries used by the services.
