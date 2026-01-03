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
