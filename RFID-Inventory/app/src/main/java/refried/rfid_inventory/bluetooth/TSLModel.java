package refried.rfid_inventory.bluetooth;

import android.bluetooth.BluetoothAdapter;

import com.uk.tsl.rfid.asciiprotocol.AsciiCommander;

import refried.rfid_inventory.util.ContextRepository;

/**
 * Model class for TSL Bluetooth device
 *   Extends ModelBase from TSL templated code.
 *
 * TODO: Should be a Singleton, however passing the context is yet to be solved.
 */
public class TSLModel implements TSLModelContract {
    private static final TSLModel INSTANCE = new TSLModel();

    private AsciiCommander mCommander;

    private TSLModel() {
    }

    public static TSLModelContract getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the AsciiCommander.
     *
     * @return Returns an ASCII Commander.
     */
    @Override
    public AsciiCommander getCommander() {
        return mCommander;
    }

    @Override
    public void initialize(ContextRepository c) {
        try {
            mCommander = new AsciiCommander(c.getApplicationContext());
        } catch (Exception e) {
            // TODO: Handle exceptions
        }
    }

    @Override
    public void connectTo(String address) {
        // TODO: Continue to attempt to connect in case the reader is off.
        // Do it on a non-UI thread. Maybe do it in the presenter.
        if(address != null) {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            // TODO: Currently fails silently if there is no device on the other end.
            mCommander.connect(adapter.getRemoteDevice(address));
        } else {
            // Connect to the last-used device.
            mCommander.connect(null);
        }
    }

    @Override
    public void disconnectFrom() {
        mCommander.disconnect();
    }

    @Override
    public void reconnectDevice()
    {
        getCommander().connect(null);
    }
}
