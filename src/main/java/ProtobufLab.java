import com.distributed.lab.CustomerProfileProto.CustomerProfile;
import java.util.Arrays;

/** Task 3.2 - build the message with the generated Builder, serialize, parse back. */
public class ProtobufLab {
    public static void main(String[] args) throws Exception {
        ConsoleUtil.initUtf8();

        // Build the object using the Builder pattern
        CustomerProfile profile = CustomerProfile.newBuilder()
                .setCustomerName("Ahmad Saleh")
                .addAllPurchasedItems(Arrays.asList("Laptop", "Mouse"))
                .addAllItemPrices(Arrays.asList(1200, 25))
                .setIsHolidayDealEligible(true)
                .build();

        // Serialize to a byte array
        byte[] protoPayload = profile.toByteArray();
        System.out.println("Protobuf payload size: " + protoPayload.length + " bytes");

        // Deserialize
        CustomerProfile reconstructed = CustomerProfile.parseFrom(protoPayload);
        System.out.println("Restored name: " + reconstructed.getCustomerName());
        System.out.println("Default city: " + reconstructed.getCity());

        // ---- extra: inspecting the generated API (task 3.2, step 1) ----
        System.out.println("\n--- Inspecting the generated API ---");
        System.out.println("hasCity() on the sent message : " + profile.hasCity()
                + "  (field was never set, so nothing is sent on the wire)");
        System.out.println("getCity() on the sent message : " + profile.getCity() + "  (default value declared in the .proto file)");
        System.out.println("getPurchasedItemsCount()    : " + reconstructed.getPurchasedItemsCount());
        System.out.println("getPurchasedItemsList()     : " + reconstructed.getPurchasedItemsList());
        System.out.println("getItemPricesList()         : " + reconstructed.getItemPricesList());
        System.out.println("getItemPrices(1)            : " + reconstructed.getItemPrices(1));
        System.out.println("getIsHolidayDealEligible()  : " + reconstructed.getIsHolidayDealEligible());
        System.out.println("hasIsHolidayDealEligible()  : " + reconstructed.hasIsHolidayDealEligible());

        System.out.println("\n--- Hex dump of the message (tags + values only, no field names) ---");
        System.out.println(ConsoleUtil.hex(protoPayload));
        System.out.println("\nAs printable text: " + ConsoleUtil.printable(protoPayload));
    }
}
