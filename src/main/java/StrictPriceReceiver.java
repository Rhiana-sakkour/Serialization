import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The "receiver" microservice of task 1.2.
 * It expects  {"prices": [1200.00, 25.00, 85.00]}  - an array of JSON NUMBERS.
 * Strings such as "1200" are rejected (no silent coercion), which is what exposes the missing schema.
 */
public final class StrictPriceReceiver {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private StrictPriceReceiver() {}

    public static double[] parsePrices(String json) throws DeserializationException {
        JsonNode root;
        try {
            root = MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            throw new DeserializationException("Malformed JSON: " + e.getOriginalMessage(), e);
        }

        if (root == null || !root.isObject()) {
            throw new DeserializationException("Expected a JSON object at $ but found "
                    + (root == null ? "nothing" : root.getNodeType()));
        }
        JsonNode prices = root.get("prices");
        if (prices == null || !prices.isArray()) {
            throw new DeserializationException("Expected an ARRAY at $.prices but found "
                    + (prices == null ? "nothing (field missing)" : prices.getNodeType()));
        }

        double[] result = new double[prices.size()];
        for (int i = 0; i < prices.size(); i++) {
            JsonNode element = prices.get(i);
            if (!element.isNumber()) {
                throw new DeserializationException(String.format(
                        "Cannot deserialize $.prices[%d]: expected NUMBER but found %s (value=%s)",
                        i, element.getNodeType(), element));
            }
            result[i] = element.asDouble();
        }
        return result;
    }

    public static double product(double[] values) {
        double p = 1.0;
        for (double v : values) {
            p *= v;
        }
        return p;
    }
}
