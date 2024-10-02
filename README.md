# Camel Jobs - J2EE Web Application

## Overview
This project is a J2EE web application packaged as a WAR file. It uses **Spring 5.3.31**, **Apache Camel**, and is designed to be deployed on **JBoss EAP 7.x** and above. The application includes two Camel routes:
1. **XML-based route** - Processes files from a dynamic URI (local or SFTP).
2. **Java DSL-based route** - Triggered by a cron expression to process files.

## Project Structure
The project is structured as a single **web module** and includes the following key components:

- **Camel Routes**: One route defined in XML and another defined in Java DSL.
- **Spring Configuration**: The application uses Spring for dependency injection and managing the lifecycle of Camel routes.

### Key Technologies
- **Spring Framework 5.3.31**
- **Apache Camel 3.21+**
- **J2EE APIs**
- **JBoss EAP 7.x**
- **Java 8+**

## Installation
To set up and build this project locally:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/varkrish/j2ee-jboss-camel.git
   ```

2. **Install dependencies**:
   Ensure you have Maven and JBoss EAP 7.x installed.

3. **Build the project**:
   ```bash
   mvn clean install
   ```

4. **Deploy to JBoss EAP**:
   - Copy the generated WAR file from `target/cameljobs-web-1.0-SNAPSHOT.war` to the JBoss EAP deployment directory.
   - Start JBoss EAP and access the web application via the configured URL.

## Deployment on JBoss EAP
This application is designed for deployment on **JBoss EAP 7.x** and above.

1. Start JBoss EAP:
   ```bash
   ./standalone.sh
   ```

2. Copy the WAR to the JBoss deployment folder:
   ```bash
   cp target/cameljobs-web-1.0-SNAPSHOT.war $JBOSS_HOME/standalone/deployments/
   ```

3. Verify the deployment:
   Open the JBoss management console or check logs to confirm the successful deployment.

## Configuration
- **Spring Configuration**: The Spring context is initialized via `applicationContext.xml` located in the `WEB-INF` directory of the web module.
- **Camel Routes**: Three routes are configured:
  - Twos route defined in XML demonstrating CBR and a simple timer based route
  - One route defined in Java DSL that processes files

## License
This project is licensed under the MIT License.

