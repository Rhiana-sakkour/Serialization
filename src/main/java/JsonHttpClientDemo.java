import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * Java equivalent of the curl commands (same java.net.http.HttpClient + CompletableFuture style
 * as the previous Lab-2 project). Start AnalyzeServer first.
 *
 * Usage: JsonHttpClientDemo [baseUrl]     default baseUrl = http://localhost:8080
 * Must be started from the project root (it reads the *.json files there).
 */
public class JsonHttpClientDemo {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static void main(String[] args) throws Exception {
        ConsoleUtil.initUtf8();
        String base = args.length > 0 ? args[0] : "http://localhost:8080";

        post(base + "/api/analyze", "customer_request.json");
        post(base + "/api/prices/product", "payload_prices_sender.json");    // strings  -> expected 400
        post(base + "/api/prices/product", "payload_prices_receiver.json");  // numbers  -> expected 200
    }

    private static void post(String url, String file) throws Exception {
        String body = Files.readString(Path.of(file), StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        CompletableFuture<HttpResponse<String>> future =
                CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        HttpResponse<String> response = future.join();

        System.out.println("POST " + url + "  (" + file + ")");
        System.out.println("-> HTTP " + response.statusCode());
        System.out.println(response.body());
        System.out.println();
    }
}
