package refried.rfid_inventory.inventoryinteractor;

import com.uk.tsl.rfid.asciiprotocol.commands.InventoryCommand;
import com.uk.tsl.rfid.asciiprotocol.enumerations.TriState;

/**
 * Defines capabilities of an RFID Inventory class.
 *
 * This interactor allows the user to create, modify, and get results from an RFID inventory scan.
 */

public interface RFIDInventoryContract {

    /**
     * Implmements all model feedback to the presenter. Implemented by the presenter.
     */
    interface itemFoundCallback {
        /**
         * Send text message to the presenter containing RFID scan results.
         * <br>
         * TODO: Add more functions based on interactor results.
         * @param message String containing RFID scan results
         */
        void addRFIDTagID(String message);
    }

    /**
     * Start the interactor with a presenter callback
     * @param c Communicates with the presenter
     */
    void attachListener(itemFoundCallback c);

    /**
     * Stop the interactor
     */
    void detachListener();

    /**
     * Undo any configuration of the interactor done by the presenter (set to default)
     */
    void resetToDefault();

    /**
     * Scan for RFID tags
     */
    void scan();

    void disconnect();

    InventoryCommand getCommand();

    void updateConfig();
}