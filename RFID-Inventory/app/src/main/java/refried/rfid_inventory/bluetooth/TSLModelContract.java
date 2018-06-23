package refried.rfid_inventory.bluetooth;

import com.uk.tsl.rfid.asciiprotocol.AsciiCommander;

import refried.rfid_inventory.util.ContextRepository;

/**
 * Contract defining capabilities of the TSL Model
 */

public interface TSLModelContract {
    void initialize(ContextRepository c);

    /**
     * Connect to the device using a supplied MAC address.
     * @param address A string MAC address in hexadecimal using capital letters, e.g. AA:BB:CC:DD:EE:FF
     *                <br>
     *                If null is supplied, connect to the previously connected device.
     */
    void connectTo(String address);

    /**
     * Disconnect from the currently connected scanner.
     */
    void disconnectFrom();

    AsciiCommander getCommander();

    /**
     * Reconnect device if remembered
     */
    void reconnectDevice();
}
