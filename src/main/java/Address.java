/**
 * Task 2.3 (experiment 2): a class that deliberately does NOT implement java.io.Serializable.
 * Using it as a non-null field of CustomerProfile makes ObjectOutputStream fail with
 * java.io.NotSerializableException: Address
 */
public class Address {
    private String street;
    private String city;

    public Address() {}

    public Address(String street, String city) {
        this.street = street;
        this.city = city;
    }

    @Override
    public String toString() {
        return "Address{street='" + street + "', city='" + city + "'}";
    }
}
