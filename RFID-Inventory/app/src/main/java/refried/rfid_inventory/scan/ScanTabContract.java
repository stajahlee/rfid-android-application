package refried.rfid_inventory.scan;

import java.util.List;

import refried.rfid_inventory.database.InventoryItem;

/**
 * Defines the relationship between {@link ScanPage} and {@link ScanTabPresenter}
 */

public interface ScanTabContract {
    interface View{
        void showConnectionStatus();
        void showStartScanning();
        void showNoScannerError();
        void showReconnectAttempt();
        void showScannerConnected();
        void refreshData();
        void showDisconnect();
    }
    interface Presenter{

        /**
         * Configure the presenter to work at the same time as Android onStart()
         */
        void start();

        /**
         * Turn off the presenter at the same time as Android onStop();
         */
        void stop();

        /**
         *  Returns a reference to the data
         */
        List<InventoryItem> initializeData();

        /**
         * Clear the List
         */
        void clear();

        /**
         * Perform an Inventory Scan
         */
        void scan();

        /**
         * performs mCommander dissconnect
         */
        void disconnect();

        /**
         *returns the status of connection with reader
         */
        String readerStatus();
        /**
         *returns the status of connection with reader
         */
        boolean getStatus();
        /**
         *returns true/false for if scanner is connected
         */
        int getMinPower();
        /**
         *returns max power for scanner
         */
        int getMaxPower();
        /**
         *returns min power for scanner
         */
        void setPowerLevel(int level);

    }
}