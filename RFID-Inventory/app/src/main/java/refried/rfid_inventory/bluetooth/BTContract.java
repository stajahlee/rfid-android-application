package refried.rfid_inventory.bluetooth;

import android.content.Context;

import java.util.ArrayList;

import refried.rfid_inventory.util.BTDevice;

/**
 * Contract defining what functionality the RFID scanner should provide.
 */
public interface BTContract {
    interface View {

        /**
         * Request to prompt the user to enable Bluetooth if disabled
         */
        void requestEnableBluetooth();

        /**
         * Provides a list of currently paired devices for the View to display.
         * @param devices Devices to be displayed
         *                (reference; will be updated later by
         *                {@link #updatePairedDevices() updatePairedDevices})
         */
        void showPairedDevices(ArrayList<BTDevice> devices);

        /**
         * Tells the View to update the visible list of paired devices.
         */
        void updatePairedDevices();

        /**
         * Forwards the number of paired devices to the View to show the user.
         * @param count Non-negative count of devices.
         */
        void showPairedDeviceCount(int count);

        /**
         * Provides a list of currently unpaired devices for the View to display.
         * @param devices Devices to be displayed
         *                (reference; will be updated later by
         *                {@link #updateUnpairedDevices() updatedUnpairedDevices})
         */
        void showUnpairedDevices(ArrayList<BTDevice> devices);

        /**
         * Tells the View to update the visible list of unpaired devices
         */
        void updateUnpairedDevices();

        /**
         * Forwards the number of paired devices to the View to show the user.
         * @param count Non-negative count of devices.
         */
        void showUnpairedDeviceCount(int count);

        /**
         * Informs the View that Bluetooth is scanning for nearby devices.
         */
        void showScanning();

        /**
         * Informs the View that Bluetooth is no longer scanning for nearby devices
         */
        void showNotScanning();
    }

    interface Presenter {

        /**
         * Tells the Presenter to begin operating upon Activity startup.
         * <br>
         * Call in Activity onStart()
         * @param c Activity Context for registering listeners and receivers
         */
        void start(Context c);

        /**
         * Tells the Presenter to unregister all listeners and receivers.
         * <br>
         * Call in Activity onStop()
         */
        void stop();

        /**
         * Tells the Presenter to begin scanning for nearby Bluetooth devices
         */
        void doDiscovery();

        /**
         * When the user has chosen a particular device, hand it to the Presenter for connecting.
         * @param device The user's chosen device.
         */
        void pairToDevice(BTDevice device);

        /**
         * The presenter defaults to an inert state.
         *  The View must tell the Presenter when it has permission to run.
         * @param granted TRUE if permission granted, FALSE otherwise.
         */
        void permissionsGranted(boolean granted);
    }

}
