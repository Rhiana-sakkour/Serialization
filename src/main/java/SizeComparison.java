import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Arrays;

/**
 * Report question 2 (part 1): size of exactly the same data in JSON, Java Serialization and Protobuf.
 * Same data = customerName "Ahmad Saleh", items [Laptop, Mouse], prices [1200, 25], eligible = true.
 * (city is not part of it: the Java class has no city field and Protobuf does not transmit unset fields.)
 */
public class SizeComparison {

    public static void main(String[] args) throws Exception {
        ConsoleUtil.initUtf8();

        // ---- JSON (compact, UTF-8) ----
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("customerName", "Ahmad Saleh");
        json.putArray("purchasedItems").add("Laptop").add("Mouse");
        json.putArray("itemPrices").add(1200).add(25);
        json.put("isHolidayDealEligible", true);
        byte[] jsonBytes = mapper.writeValueAsBytes(json);

        // ---- Java Serialization ----
        CustomerProfile javaObj = new CustomerProfile("Ahmad Saleh", Arrays.asList("Laptop", "Mouse"),
                new int[]{1200, 25}, true, "SECRET-TOKEN-999");
        byte[] javaBytes = JavaSerializationLab.serialize(javaObj);

        // ---- Protobuf ----
        com.distributed.lab.CustomerProfileProto.CustomerProfile protoObj =
                com.distributed.lab.CustomerProfileProto.CustomerProfile.newBuilder()
                        .setCustomerName("Ahmad Saleh")
                        .addAllPurchasedItems(Arrays.asList("Laptop", "Mouse"))
                        .addAllItemPrices(Arrays.asList(1200, 25))
                        .setIsHolidayDealEligible(true)
                        .build();
        byte[] protoBytes = protoObj.toByteArray();

        System.out.println("JSON text sent       : " + new String(jsonBytes, "UTF-8"));
        System.out.println();
        System.out.printf("%-26s %8s %12s%n", "Format", "Bytes", "x Protobuf");
        System.out.printf("%-26s %8d %12.2f%n", "Protocol Buffers", protoBytes.length, 1.0);
        System.out.printf("%-26s %8d %12.2f%n", "JSON (compact)", jsonBytes.length,
                (double) jsonBytes.length / protoBytes.length);
        System.out.printf("%-26s %8d %12.2f%n", "Java Serialization", javaBytes.length,
                (double) javaBytes.length / protoBytes.length);

        System.out.println();
        System.out.println("Protobuf byte breakdown (proto2, unpacked repeated fields):");
        System.out.println("  customer_name   : 1 tag + 1 len + 11 chars       = 13");
        System.out.println("  purchased_items : (1+1+6) + (1+1+5)              = 15");
        System.out.println("  item_prices     : (1 tag + 2 varint) + (1 + 1)   =  5");
        System.out.println("  is_holiday_...  : 1 tag + 1 value                =  2");
        System.out.println("  total expected                                   = 35   (measured: " + protoBytes.length + ")");

        // ---- rough performance comparison (serialize + deserialize round trip) ----
        // Simple micro-benchmark (warm-up + System.nanoTime), NOT JMH: use it for orders of magnitude only.
        final int warmup = 20_000;
        final int runs = 100_000;
        Task jsonTask = () -> mapper.readTree(mapper.writeValueAsBytes(json));
        Task javaTask = () -> JavaSerializationLab.deserialize(JavaSerializationLab.serialize(javaObj));
        Task protoTask = () -> com.distributed.lab.CustomerProfileProto.CustomerProfile.parseFrom(protoObj.toByteArray());

        timeMs(jsonTask, warmup);
        timeMs(javaTask, warmup);
        timeMs(protoTask, warmup);

        System.out.println();
        System.out.println("Round trip (serialize + deserialize), " + runs + " iterations after warm-up:");
        System.out.printf("%-26s %12s %16s%n", "Format", "Total ms", "microsec/op");
        printTime("Protocol Buffers", timeMs(protoTask, runs), runs);
        printTime("JSON (Jackson tree)", timeMs(jsonTask, runs), runs);
        printTime("Java Serialization", timeMs(javaTask, runs), runs);
        System.out.println("(timings depend on your CPU/JVM; compare the ratios, not the absolute numbers)");
    }

    private interface Task {
        void run() throws Exception;
    }

    private static double timeMs(Task task, int iterations) throws Exception {
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            task.run();
        }
        return (System.nanoTime() - start) / 1_000_000.0;
    }

    private static void printTime(String name, double totalMs, int iterations) {
        System.out.printf("%-26s %12.1f %16.3f%n", name, totalMs, totalMs * 1000.0 / iterations);
    }
}
