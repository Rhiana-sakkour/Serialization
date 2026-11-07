import com.google.protobuf.InvalidProtocolBufferException;

import java.util.Arrays;

/**
 * Report question 2 (part 2): what happens when the receiver regenerates its stub from a .proto in which
 *   (a) only the field NAME changed  : customer_name = 1  ->  client_full_name = 1   (works)
 *   (b) the TAG NUMBER changed       : client_full_name = 7                          (fails, negative control)
 * The sender is NOT touched: it keeps using com.distributed.lab.CustomerProfileProto.
 */
public class ProtobufRenameExperiment {

    public static void main(String[] args) throws Exception {
        ConsoleUtil.initUtf8();

        // ----- sender: unchanged schema (customer_name = 1) -----
        byte[] wire = com.distributed.lab.CustomerProfileProto.CustomerProfile.newBuilder()
                .setCustomerName("Ahmad Saleh")
                .addAllPurchasedItems(Arrays.asList("Laptop", "Mouse"))
                .addAllItemPrices(Arrays.asList(1200, 25))
                .setIsHolidayDealEligible(true)
                .build()
                .toByteArray();

        System.out.println("Bytes on the wire (" + wire.length + "):");
        System.out.println(ConsoleUtil.hex(wire));
        System.out.println("First byte 0x0A = (field number 1 << 3) | wire type 2 (length-delimited): the NAME is not on the wire.\n");

        // ----- (a) receiver regenerated with client_full_name = 1 -----
        System.out.println("=== (a) receiver: client_full_name = 1  (name changed, tag unchanged) ===");
        com.distributed.lab.receiver.CustomerProfileReceiverProto.CustomerProfile receiver =
                com.distributed.lab.receiver.CustomerProfileReceiverProto.CustomerProfile.parseFrom(wire);
        System.out.println("Deserialization OK");
        System.out.println("getClientFullName()  = " + receiver.getClientFullName());
        System.out.println("getPurchasedItemsList() = " + receiver.getPurchasedItemsList());
        System.out.println("getItemPricesList()     = " + receiver.getItemPricesList());

        // ----- (b) negative control: tag 1 -> 7 -----
        System.out.println("\n=== (b) negative control: client_full_name = 7  (tag changed) ===");
        try {
            com.distributed.lab.wrongtag.CustomerProfileWrongTagProto.CustomerProfile bad =
                    com.distributed.lab.wrongtag.CustomerProfileWrongTagProto.CustomerProfile.parseFrom(wire);
            System.out.println("Unexpected success: " + bad.getClientFullName());
        } catch (InvalidProtocolBufferException e) {
            System.out.println("Deserialization FAILED: " + e.getClass().getName());
            System.out.println("Message: " + e.getMessage());
        }
    }
}
