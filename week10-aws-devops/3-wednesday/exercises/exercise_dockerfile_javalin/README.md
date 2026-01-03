# Exercise: Containerizing a Java Javalin Application

## Objective

Create a multi-stage Dockerfile to containerize a Java Javalin web application using Maven, applying best practices for layer optimization, security, and minimal image size.

---

## Learning Outcomes

By completing this exercise, you will:
- Write a multi-stage Dockerfile for a Java application
- Understand Maven build integration in Docker
- Apply layer caching strategies for faster builds
- Use JRE-only runtime images for smaller footprint
- Implement security best practices (non-root user)
- Create and use `.dockerignore` files for Java projects

---

## Prerequisites

- Completed Exercise 3 (Dockerfile Python)
- Completed Exercise 4 (Multi-Stage Optimization)
- Docker running
- Basic understanding of Java and Maven (from Week 2-3)

---

## Time Estimate

45 minutes

---

## Project Structure

```
javalin-app/
├── pom.xml                # Maven project file
├── src/
│   └── main/
│       └── java/
│           └── App.java   # Javalin application
├── Dockerfile             # Multi-stage build
└── .dockerignore          # Excluded files
```

---

## Background: What is Javalin?

**Javalin** is a lightweight web framework for Java and Kotlin that's perfect for containerization because:
- **Minimal footprint**: Small JAR files, fast startup
- **Embedded server**: No external Tomcat/Jetty required
- **Simple API**: Easy to create REST endpoints
- **Production-ready**: Used in microservices architectures

---

## Tasks

### Task 1: Create the Javalin Application (10 minutes)

1. **Create Project Directory**
   ```bash
   mkdir -p javalin-app/src/main/java
   cd javalin-app
   ```

2. **Create `pom.xml`**
   
   Create the Maven configuration file:
   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <project xmlns="http://maven.apache.org/POM/4.0.0"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                                http://maven.apache.org/xsd/maven-4.0.0.xsd">
       <modelVersion>4.0.0</modelVersion>
   
       <groupId>com.example</groupId>
       <artifactId>javalin-docker-demo</artifactId>
       <version>1.0.0</version>
       <packaging>jar</packaging>
   
       <properties>
           <maven.compiler.source>17</maven.compiler.source>
           <maven.compiler.target>17</maven.compiler.target>
           <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
           <javalin.version>5.6.3</javalin.version>
       </properties>
   
       <dependencies>
           <!-- Javalin Web Framework -->
           <dependency>
               <groupId>io.javalin</groupId>
               <artifactId>javalin</artifactId>
               <version>${javalin.version}</version>
           </dependency>
           
           <!-- SLF4J for logging -->
           <dependency>
               <groupId>org.slf4j</groupId>
               <artifactId>slf4j-simple</artifactId>
               <version>2.0.9</version>
           </dependency>
           
           <!-- JSON serialization -->
           <dependency>
               <groupId>com.fasterxml.jackson.core</groupId>
               <artifactId>jackson-databind</artifactId>
               <version>2.15.3</version>
           </dependency>
       </dependencies>
   
       <build>
           <plugins>
               <!-- Maven Shade Plugin for Fat JAR -->
               <plugin>
                   <groupId>org.apache.maven.plugins</groupId>
                   <artifactId>maven-shade-plugin</artifactId>
                   <version>3.5.1</version>
                   <executions>
                       <execution>
                           <phase>package</phase>
                           <goals>
                               <goal>shade</goal>
                           </goals>
                           <configuration>
                               <transformers>
                                   <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                       <mainClass>App</mainClass>
                                   </transformer>
                               </transformers>
                           </configuration>
                       </execution>
                   </executions>
               </plugin>
           </plugins>
       </build>
   </project>
   ```

3. **Create `src/main/java/App.java`**
   ```java
   import io.javalin.Javalin;
   import io.javalin.json.JavalinJackson;
   import java.time.Instant;
   import java.util.Map;
   import java.net.InetAddress;

   public class App {
       public static void main(String[] args) {
           String version = System.getenv().getOrDefault("APP_VERSION", "1.0.0");
           int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
           
           Javalin app = Javalin.create(config -> {
               config.jsonMapper(new JavalinJackson());
           }).start(port);
           
           // Home endpoint
           app.get("/", ctx -> {
               String hostname = InetAddress.getLocalHost().getHostName();
               ctx.json(Map.of(
                   "message", "Hello from Dockerized Javalin!",
                   "hostname", hostname,
                   "timestamp", Instant.now().toString(),
                   "version", version
               ));
           });
           
           // Health endpoint
           app.get("/health", ctx -> {
               ctx.json(Map.of(
                   "status", "healthy",
                   "service", "javalin-demo"
               ));
           });
           
           // Environment info endpoint
           app.get("/env", ctx -> {
               ctx.json(Map.of(
                   "javaVersion", System.getProperty("java.version"),
                   "appVersion", version,
                   "port", port
               ));
           });
           
           System.out.println("Javalin app started on port " + port);
       }
   }
   ```

**Checkpoint:** Application files created ✓

---

### Task 2: Write the Multi-Stage Dockerfile (15 minutes)

1. **Create the Multi-Stage Dockerfile**
   
   Create a file named `Dockerfile`:
   ```dockerfile
   # ============================================
   # Stage 1: Build with Maven
   # ============================================
   FROM maven:3.9-eclipse-temurin-17 AS build
   
   WORKDIR /app
   
   # Copy pom.xml first for dependency caching
   COPY pom.xml .
   
   # Download dependencies (cached if pom.xml unchanged)
   RUN mvn dependency:go-offline -B
   
   # Copy source code
   COPY src ./src
   
   # Build the application (skip tests for faster build)
   RUN mvn package -DskipTests -B
   
   # ============================================
   # Stage 2: Runtime with minimal JRE
   # ============================================
   FROM eclipse-temurin:17-jre-alpine
   
   WORKDIR /app
   
   # Create non-root user for security
   RUN addgroup -g 1001 -S javalin && \
       adduser -S javalin -u 1001 -G javalin
   
   # Copy only the JAR from build stage
   COPY --from=build /app/target/javalin-docker-demo-1.0.0.jar app.jar
   
   # Set ownership
   RUN chown javalin:javalin app.jar
   
   # Switch to non-root user
   USER javalin
   
   # Environment variables
   ENV APP_VERSION=1.0.0
   ENV PORT=8080
   
   # Document the port
   EXPOSE 8080
   
   # Run the application
   ENTRYPOINT ["java", "-jar", "app.jar"]
   ```

2. **Understand Each Stage:**

   | Stage | Base Image | Purpose | Included |
   |-------|------------|---------|----------|
   | Build | `maven:3.9-eclipse-temurin-17` (~800MB) | Compile Java code | Maven, JDK, all build tools |
   | Runtime | `eclipse-temurin:17-jre-alpine` (~150MB) | Run application | JRE only, minimal Alpine |

3. **Create `.dockerignore`**
   ```bash
   cat > .dockerignore << 'EOF'
   # Java build artifacts
   target/
   *.class
   *.jar
   *.war
   
   # IDE files
   .idea/
   *.iml
   .vscode/
   .settings/
   .project
   .classpath
   
   # Git
   .git/
   .gitignore
   
   # Docker
   Dockerfile*
   .dockerignore
   docker-compose*.yml
   
   # Documentation
   *.md
   docs/
   
   # Logs
   *.log
   logs/
   
   # OS files
   .DS_Store
   Thumbs.db
   EOF
   ```

**Checkpoint:** Multi-stage Dockerfile created ✓

---

### Task 3: Build and Compare Images (10 minutes)

1. **Build the Multi-Stage Image**
   ```bash
   docker build -t javalin-demo:1.0 .
   ```
   
   Watch the build process—notice the two stages!

2. **Check the Image Size**
   ```bash
   docker images javalin-demo
   ```
   
   Expected: **~150-200 MB** (vs ~800MB+ if single-stage)

3. **Inspect Image Layers**
   ```bash
   docker history javalin-demo:1.0
   ```

4. **Verify No Build Tools in Final Image**
   ```bash
   # Maven should NOT be present
   docker run --rm javalin-demo:1.0 which mvn || echo "No Maven (correct!)"
   
   # Only JRE, no JDK
   docker run --rm javalin-demo:1.0 java -version
   docker run --rm javalin-demo:1.0 which javac || echo "No javac (correct!)"
   ```

**Checkpoint:** Image built and verified ✓

---

### Task 4: Run and Test the Application (10 minutes)

1. **Run the Container**
   ```bash
   docker run -d \
     --name javalin-app \
     -p 8080:8080 \
     javalin-demo:1.0
   ```

2. **Test All Endpoints**
   ```bash
   # Home endpoint
   curl http://localhost:8080
   
   # Health endpoint
   curl http://localhost:8080/health
   
   # Environment endpoint  
   curl http://localhost:8080/env
   ```

3. **View Logs**
   ```bash
   docker logs javalin-app
   ```

4. **Verify Running as Non-Root**
   ```bash
   docker exec javalin-app whoami
   # Expected: javalin (not root!)
   ```

5. **Test with Custom Environment**
   ```bash
   docker rm -f javalin-app
   
   docker run -d \
     --name javalin-app \
     -p 8080:8080 \
     -e APP_VERSION=2.0.0 \
     javalin-demo:1.0
   
   # Verify new version
   curl http://localhost:8080
   ```

**Checkpoint:** Application running and tested ✓

---

### Task 5: Layer Caching Optimization (Bonus)

1. **Make a Code Change**
   
   Modify the message in `App.java`:
   ```java
   // Change "Hello from Dockerized Javalin!" to "Hello from Javalin v2!"
   ```

2. **Rebuild**
   ```bash
   docker build -t javalin-demo:1.1 .
   ```
   
   **Observe:** The `dependency:go-offline` step shows `CACHED`!
   
   Only the source code layers are rebuilt.

3. **Compare Build Times**
   - First build: ~60-90 seconds (downloading dependencies)
   - Subsequent builds: ~10-20 seconds (dependencies cached)

---

## Verification Checklist

- [ ] Created Javalin application with multiple endpoints
- [ ] Wrote multi-stage Dockerfile (build + runtime stages)
- [ ] Created `.dockerignore` file
- [ ] Built image successfully
- [ ] Final image is under 200MB
- [ ] No Maven/JDK in final image (only JRE)
- [ ] Container runs as non-root user
- [ ] All three endpoints respond correctly
- [ ] Layer caching works on rebuild

---

## Deliverables

1. Your `Dockerfile` contents
2. Screenshot of `docker images javalin-demo` showing size
3. Output of `curl http://localhost:8080`
4. Output of `docker exec javalin-app whoami` showing non-root user

---

## Key Takeaways

| Concept | Why It Matters |
|---------|----------------|
| Multi-stage builds | 5-6x smaller images, no build tools in production |
| Dependency caching | Copy `pom.xml` first = fast rebuilds |
| JRE vs JDK | Runtime needs only JRE (~150MB vs ~400MB) |
| Alpine images | Minimal base OS, smaller attack surface |
| Non-root user | Security best practice for production |

---

## Clean-Up

```bash
# Stop and remove container
docker rm -f javalin-app

# Remove images
docker rmi javalin-demo:1.0 javalin-demo:1.1

# Remove project directory (optional)
cd ..
rm -rf javalin-app
```

---

## Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| Build fails at `mvn` | Network/Maven issue | Check internet, retry |
| JAR not found | Wrong artifact name | Verify pom.xml artifactId matches COPY |
| Port 8080 in use | Another service | Use `-p 8081:8080` |
| Out of memory | JVM defaults | Add `-e JAVA_OPTS="-Xmx256m"` |

---

## Additional Resources

- [Javalin Documentation](https://javalin.io/documentation)
- [Eclipse Temurin Docker Images](https://hub.docker.com/_/eclipse-temurin)
- [Maven Docker Plugin](https://github.com/spotify/dockerfile-maven)
- [Docker Best Practices for Java](https://www.docker.com/blog/intro-guide-to-dockerfile-best-practices/)
