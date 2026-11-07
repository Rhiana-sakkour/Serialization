import java.io.*;
import java.util.Arrays;
import java.util.List;

/** Task 2.1 - the serializable class (exactly as in the lab + the Address field of task 2.3). */
public class CustomerProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    // Class-level (static) field - NOT serialized
    public static String systemVersion = "v2.4";

    // Temporary (transient) field - NOT serialized
    private transient String tempDiscountToken;

    // Core instance fields - serialized automatically
    private String customerName;
    private List<String> purchasedItems;
    private int[] itemPrices;
    private boolean isHolidayDealEligible;

    // Task 2.3 (experiment 2): Address does NOT implement Serializable.
    // It stays null in every normal run, so tasks 2.2 / 3.x are not affected;
    // the experiment sets it to a real object to trigger the exception.
    private Address address;

    // No-arg constructor
    public CustomerProfile() {}

    public CustomerProfile(String customerName, List<String> purchasedItems,
                           int[] itemPrices, boolean isHolidayDealEligible,
                           String token) {
        this.customerName = customerName;
        this.purchasedItems = purchasedItems;
        this.itemPrices = itemPrices;
        this.isHolidayDealEligible = isHolidayDealEligible;
        this.tempDiscountToken = token;
    }

    public String getCustomerName() { return customerName; }

    public String getTempDiscountToken() { return tempDiscountToken; }

    public Address getAddress() { return address; }

    public void setAddress(Address address) { this.address = address; }

    @Override
    public String toString() {
        return "CustomerProfile{" +
                "systemVersion='" + systemVersion + '\'' +
                ", tempDiscountToken='" + tempDiscountToken + '\'' +
                ", customerName='" + customerName + '\'' +
                ", purchasedItems=" + purchasedItems +
                ", itemPrices=" + Arrays.toString(itemPrices) +
                ", isHolidayDealEligible=" + isHolidayDealEligible +
                ", address=" + address +
                '}';
    }
}
