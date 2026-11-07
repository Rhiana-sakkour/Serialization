import java.io.*;
import java.util.Arrays;

/** Task 2.2 - serialize to byte[] and deserialize back. */
public class JavaSerializationLab {

    // 1. Serialization
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        return byteArrayOutputStream.toByteArray();
    }

    // 2. Deserialization
    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return objectInputStream.readObject();
    }

    public static void main(String[] args) throws Exception {
        ConsoleUtil.initUtf8();

        CustomerProfile original = new CustomerProfile(
                "Ahmad Saleh",
                Arrays.asList("Laptop", "Mouse"),
                new int[]{1200, 25},
                true,
                "SECRET-TOKEN-999"
        );

        System.out.println("Before serialization:\n" + original);

        // Convert the object into a stream of bytes
        byte[] payload = serialize(original);
        System.out.println("\nPayload size: " + payload.length + " bytes");

        // Change the static field before deserialization to observe its behaviour
        CustomerProfile.systemVersion = "v3.0-UPDATED";

        // Rebuild the object
        CustomerProfile reconstructed = (CustomerProfile) deserialize(payload);
        System.out.println("\nAfter deserialization:\n" + reconstructed);
    }
}
