import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Minimal HTTP server (JDK built-in com.sun.net.httpserver, like the previous Lab-2 project)
 * so that the curl command of task 1.1 has something to talk to.
 *
 *   POST /api/analyze        -> analyses a customer purchase profile (JSON)
 *   POST /api/prices/product -> task 1.2 receiver: strict {"prices":[numbers]} -> product of the prices
 *
 * Usage: AnalyzeServer [port]      (default 8080, listens on 127.0.0.1 only)
 */
public class AnalyzeServer {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        ConsoleUtil.initUtf8();
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;

        HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0);
        server.createContext("/api/analyze", AnalyzeServer::handleAnalyze);
        server.createContext("/api/prices/product", AnalyzeServer::handleProduct);
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();

        System.out.println("AnalyzeServer listening on http://localhost:" + port);
        System.out.println("  POST /api/analyze");
        System.out.println("  POST /api/prices/product");
        System.out.println("Press Ctrl+C to stop.");
    }

    // ------------------------------------------------------------------ /api/analyze
    private static void handleAnalyze(HttpExchange ex) throws IOException {
        try {
            if (!"POST".equals(ex.getRequestMethod())) {
                send(ex, 405, error("Only POST is allowed"));
                return;
            }
            String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            JsonNode root;
            try {
                root = MAPPER.readTree(body);
            } catch (JsonProcessingException e) {
                send(ex, 400, error("Malformed JSON: " + e.getOriginalMessage()));
                return;
            }
            if (root == null || !root.isObject()) {
                send(ex, 400, error("Request body must be a JSON object"));
                return;
            }

            // ---- validate types (this is the "schema" the server silently assumes) ----
            List<String> problems = new ArrayList<>();
            if (!root.path("customerName").isTextual()) problems.add("customerName must be a string");
            if (!root.path("city").isTextual()) problems.add("city must be a string");
            if (!root.path("isHolidayDealEligible").isBoolean()) problems.add("isHolidayDealEligible must be a boolean");

            JsonNode items = root.path("purchasedItems");
            if (!items.isArray()) {
                problems.add("purchasedItems must be an array of strings");
            } else {
                for (int i = 0; i < items.size(); i++) {
                    if (!items.get(i).isTextual()) problems.add("purchasedItems[" + i + "] must be a string");
                }
            }

            JsonNode prices = root.path("itemPrices");
            double total = 0;
            if (!prices.isArray()) {
                problems.add("itemPrices must be an array of numbers");
            } else {
                for (int i = 0; i < prices.size(); i++) {
                    if (!prices.get(i).isNumber()) {
                        problems.add("itemPrices[" + i + "] must be a number");
                    } else {
                        total += prices.get(i).asDouble();
                    }
                }
            }
            if (items.isArray() && prices.isArray() && items.size() != prices.size()) {
                problems.add("purchasedItems and itemPrices must have the same length");
            }
            JsonNode orders = root.path("pastOrders");
            if (!orders.isMissingNode() && !orders.isArray()) problems.add("pastOrders must be an array of objects");

            if (!problems.isEmpty()) {
                ObjectNode err = error("Validation failed");
                ArrayNode list = err.putArray("problems");
                problems.forEach(list::add);
                send(ex, 400, err);
                return;
            }

            // ---- analysis (illustrative business rule, not part of the lab text) ----
            boolean eligible = root.get("isHolidayDealEligible").asBoolean();
            String promotion = !eligible ? "NONE" : (total >= 1000 ? "HOLIDAY_15_PERCENT" : "HOLIDAY_10_PERCENT");

            ObjectNode out = MAPPER.createObjectNode();
            out.put("customerName", root.get("customerName").asText());
            out.put("city", root.get("city").asText());
            out.put("pastOrdersCount", orders.isArray() ? orders.size() : 0);
            out.put("itemsCount", items.size());
            out.put("totalPrice", total);
            out.put("isHolidayDealEligible", eligible);
            out.put("suggestedPromotion", promotion);
            send(ex, 200, out);

        } catch (RuntimeException e) {
            send(ex, 500, error("Internal error: " + e));
        }
    }

    // ------------------------------------------------------------------ /api/prices/product
    private static void handleProduct(HttpExchange ex) throws IOException {
        try {
            if (!"POST".equals(ex.getRequestMethod())) {
                send(ex, 405, error("Only POST is allowed"));
                return;
            }
            String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            try {
                double[] prices = StrictPriceReceiver.parsePrices(body);
                ObjectNode out = MAPPER.createObjectNode();
                out.put("count", prices.length);
                out.put("product", StrictPriceReceiver.product(prices));
                send(ex, 200, out);
            } catch (DeserializationException e) {
                ObjectNode err = error("DeserializationException: " + e.getMessage());
                send(ex, 400, err);
            }
        } catch (RuntimeException e) {
            send(ex, 500, error("Internal error: " + e));
        }
    }

    // ------------------------------------------------------------------ helpers
    private static ObjectNode error(String message) {
        ObjectNode n = MAPPER.createObjectNode();
        n.put("error", message);
        return n;
    }

    private static void send(HttpExchange ex, int status, JsonNode node) throws IOException {
        byte[] out = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsBytes(node);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(status, out.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(out);
        }
    }
}
