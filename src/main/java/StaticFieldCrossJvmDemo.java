import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Proves that static fields are NOT part of the serialized stream.
 *   write : JVM #1 sets systemVersion="WRITER-JVM-9.9", serializes, saves results/customer.ser
 *   read  : JVM #2 (a brand-new process) reads the file -> systemVersion is the class default "v2.4"
 */
public class StaticFieldCrossJvmDemo {
    public static void main(String[] args) throws Exception {
        ConsoleUtil.initUtf8();
        Path file = Path.of("results", "customer.ser");
        String mode = args.length > 0 ? args[0] : "";

        if ("write".equals(mode)) {
            CustomerProfile.systemVersion = "WRITER-JVM-9.9";
            CustomerProfile p = new CustomerProfile("Ahmad Saleh", Arrays.asList("Laptop", "Mouse"),
                    new int[]{1200, 25}, true, "SECRET-TOKEN-999");
            Files.createDirectories(file.getParent());
            byte[] data = JavaSerializationLab.serialize(p);
            Files.write(file, data);
            System.out.println("[JVM #1] systemVersion at serialization time = " + CustomerProfile.systemVersion);
            System.out.println("[JVM #1] wrote " + data.length + " bytes to " + file);
        } else if ("read".equals(mode)) {
            byte[] data = Files.readAllBytes(file);
            CustomerProfile p = (CustomerProfile) JavaSerializationLab.deserialize(data);
            System.out.println("[JVM #2] read " + data.length + " bytes from " + file);
            System.out.println("[JVM #2] restored object : " + p);
            System.out.println("[JVM #2] systemVersion   = " + CustomerProfile.systemVersion
                    + "   (the writer's value 'WRITER-JVM-9.9' was NOT transported)");
            System.out.println("[JVM #2] tempDiscountToken = " + p.getTempDiscountToken());
        } else {
            System.out.println("Usage: StaticFieldCrossJvmDemo write|read   (run 'write' first)");
        }
    }
}
