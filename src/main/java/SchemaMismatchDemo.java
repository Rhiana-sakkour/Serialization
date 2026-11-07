/**
 * Task 1.2 - simulates the "no single source of truth" problem between two micro-services.
 *   Service 1 (sender)   sends   {"prices": ["1200", "25", "85"]}          (strings)
 *   Service 2 (receiver) expects {"prices": [1200.00, 25.00, 85.00]}       (numbers)
 */
public class SchemaMismatchDemo {

    public static void main(String[] args) {
        ConsoleUtil.initUtf8();

        String fromSender   = "{\"prices\": [\"1200\", \"25\", \"85\"]}";
        String whatReceiverExpects = "{\"prices\": [1200.00, 25.00, 85.00]}";

        System.out.println("Sender (service 1) sends        : " + fromSender);
        System.out.println("Receiver (service 2) expects    : " + whatReceiverExpects);

        System.out.println("\n--- Receiver processes the sender's message ---");
        try {
            double[] prices = StrictPriceReceiver.parsePrices(fromSender);
            System.out.printf("Product of prices = %.2f%n", StrictPriceReceiver.product(prices));
        } catch (DeserializationException e) {
            System.out.println("DeserializationException: " + e.getMessage());
            System.out.println("-> The message only describes the mismatch; it does not say which service is right.");
        }

        System.out.println("\n--- Receiver processes a message in the expected format ---");
        try {
            double[] prices = StrictPriceReceiver.parsePrices(whatReceiverExpects);
            System.out.printf("Product of prices = %.2f%n", StrictPriceReceiver.product(prices));
        } catch (DeserializationException e) {
            System.out.println("DeserializationException: " + e.getMessage());
        }

        System.out.println("\nNote: in JSON the numbers 1200 and 1200.00 are identical; the concept of 'two decimal places'"
                + " only exists if it is defined in an external schema.");
    }
}
