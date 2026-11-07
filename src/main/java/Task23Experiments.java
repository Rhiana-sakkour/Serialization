import java.util.Arrays;

/**
 * Task 2.3 - the three exploration experiments of the Java Serialization lab.
 *   1) transient / static fields after deserialization
 *   2) a non-Serializable field type (Address)
 *   3) the payload printed as text (+ hex) to show how much structure leaks
 */
public class Task23Experiments {

    private static CustomerProfile newProfile() {
        return new CustomerProfile(
                "Ahmad Saleh",
                Arrays.asList("Laptop", "Mouse"),
                new int[]{1200, 25},
                true,
                "SECRET-TOKEN-999");
    }

    public static void main(String[] args) throws Exception {
        ConsoleUtil.initUtf8();

        // ---------------------------------------------------------------- 1
        System.out.println("==== Experiment 1: transient and static fields after deserialization ====");
        CustomerProfile original = newProfile();
        System.out.println("Before serialization : tempDiscountToken = " + original.getTempDiscountToken()
                + " , systemVersion = " + CustomerProfile.systemVersion);

        byte[] payload = JavaSerializationLab.serialize(original);
        CustomerProfile.systemVersion = "v3.0-UPDATED";           // change the static value before deserializing
        CustomerProfile restored = (CustomerProfile) JavaSerializationLab.deserialize(payload);

        System.out.println("After deserialization: tempDiscountToken = " + restored.getTempDiscountToken()
                + " , systemVersion = " + CustomerProfile.systemVersion);
        System.out.println("(Deserialization ran inside the same JVM; run StaticFieldCrossJvmDemo to verify across JVMs)");

        // ---------------------------------------------------------------- 2
        System.out.println("\n==== Experiment 2: a field whose class does not implement Serializable ====");
        CustomerProfile withAddress = newProfile();
        withAddress.setAddress(new Address("Mazzeh St.", "Damascus"));
        try {
            byte[] bad = JavaSerializationLab.serialize(withAddress);
            System.out.println("No exception was thrown! size = " + bad.length);
        } catch (java.io.NotSerializableException e) {
            System.out.println("Exception class   : " + e.getClass().getName());
            System.out.println("Exception message : " + e.getMessage());
            System.out.println("Full              : " + e);
        }

        // ---------------------------------------------------------------- 3
        System.out.println("\n==== Experiment 3: printing the payload as text ====");
        CustomerProfile.systemVersion = "v2.4";
        byte[] clean = JavaSerializationLab.serialize(newProfile());
        System.out.println("Payload size: " + clean.length + " bytes\n");

        System.out.println("--- new String(payload) (as written in the lab) ---");
        System.out.println(new String(clean));

        System.out.println("\n--- Same content, non-printable bytes replaced by '.' ---");
        System.out.println(ConsoleUtil.printable(clean));

        System.out.println("\n--- Hex dump ---");
        System.out.println(ConsoleUtil.hex(clean));

        System.out.println("\nDoes 'SECRET-TOKEN-999' appear in the payload? "
                + new String(clean).contains("SECRET-TOKEN-999"));
        System.out.println("Does the class name 'CustomerProfile' appear? "
                + new String(clean).contains("CustomerProfile"));
        System.out.println("Do the field names (customerName / itemPrices ...) appear? "
                + (new String(clean).contains("customerName") && new String(clean).contains("itemPrices")));
    }
}
