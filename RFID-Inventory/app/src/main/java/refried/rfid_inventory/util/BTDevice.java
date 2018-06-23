package refried.rfid_inventory.util;

/**
 * An item representing one Bluetooth Device
 */
public class BTDevice {
    public final String name;
    public final String address;
    public final String details;

    public BTDevice(String name, String address, String details) {
        this.name = name;
        this.address = address;
        this.details = details;
    }

    @Override
    public String toString() {
        return address;
    }
}
