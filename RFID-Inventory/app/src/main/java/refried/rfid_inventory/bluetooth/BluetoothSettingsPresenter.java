package refried.rfid_inventory.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Intent;
import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import refried.rfid_inventory.util.BTDevice;

/**
 * Presenter for controlling Bluetooth interactions. Configures the TSLModel.
 */

public class BluetoothSettingsPresenter implements BTContract.Presenter {

    private BTContract.View mView;
    private TSLModelContract mModel;
    private Context mActivityContext;

    private static BluetoothAdapter mBluetoothAdapter;
    private final Set<BTDevice> mNewDevicesSet = new HashSet<>();
    private final ArrayList<BTDevice> mNewDevicesArrayList = new ArrayList<>();
    private final ArrayList<BTDevice> mPairedDevicesArrayList = new ArrayList<>();

    private static boolean mPermissionsGranted = false;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                if(deviceName == null) {
                    deviceName = "No Name Provided";
                }
                // TODO: DELETE THIS WHEN DONE. MAC addresses in the open are a concern.
                String deviceHardwareAddress = device.getAddress(); // MAC address
                BTDevice newDevice =
                        new BTDevice(deviceName, deviceHardwareAddress, "not-detailed");
                // TODO: Check duplicates, add to a big ArrayList, set count
                if(!mNewDevicesSet.contains(newDevice)){
                    mNewDevicesSet.add(newDevice);
                    mNewDevicesArrayList.add(newDevice);
                    mView.updateUnpairedDevices();
                    mView.showUnpairedDeviceCount(mNewDevicesArrayList.size());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // TODO: Change dialog box title, enable scan button, reset title.
                mView.showNotScanning();
            }
        }
    };

    // Class-specific functions
    public BluetoothSettingsPresenter(BTContract.View v, TSLModelContract m) {
        mView = v;
        mModel = m;

    }

    private void getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        mPairedDevicesArrayList.clear();

        mView.showPairedDeviceCount(pairedDevices.size());

        if(!pairedDevices.isEmpty()) {
            for (BluetoothDevice dev : pairedDevices) {
                mPairedDevicesArrayList.add(
                        new BTDevice(dev.getName(),
                                                  dev.getAddress(),
                                                  "not-a-detail"));
            }
        }

        mView.updatePairedDevices();
    }

    /**
     * Check whether (a) Bluetooth exists, and (b) we have permission to use it.
     * <br>
     * Send an error to the View if we cannot.
     */
    private boolean checkCapabilities() {
        boolean flag = true;
        if(!mPermissionsGranted) {
            //mView.showError(new String(""));
            flag = false;
        }
        if(BluetoothAdapter.getDefaultAdapter() == null) {
            //mView.showError(new String(""));
            flag = false;
        }
        return flag;
    }

    // Overridden functions from BTContract.Presenter
    @Override
    public void start(Context c) {
        mActivityContext = c;

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mActivityContext.registerReceiver(mReceiver, filter);

        if(!checkCapabilities()) {
            return;
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!mBluetoothAdapter.isEnabled()) {
            mView.requestEnableBluetooth();
        }

        getPairedDevices();

        mView.showPairedDevices(mPairedDevicesArrayList);
        mView.showUnpairedDevices(mNewDevicesArrayList);
    }

    @Override
    public void stop() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }

        mActivityContext.unregisterReceiver(mReceiver);
        mActivityContext = null;
    }

    @Override
    public void doDiscovery() {
        if(!checkCapabilities()) {
            return;
        }

        mNewDevicesArrayList.clear();
        mView.showScanning();
        mView.showUnpairedDeviceCount(0);

        if(mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();

    }

    @Override
    public void pairToDevice(BTDevice device) {
        // TODO: Add asynchronous capabilities for continued attempts.
        if(!checkCapabilities()) {
            return;
        }

        mModel.connectTo(device.address);

    }

    @Override
    public void permissionsGranted(boolean granted) {
        mPermissionsGranted = granted;
    }
}
