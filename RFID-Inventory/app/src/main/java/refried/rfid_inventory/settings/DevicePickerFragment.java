package refried.rfid_inventory.settings;

import android.app.DialogFragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import refried.rfid_inventory.R;
import refried.rfid_inventory.bluetooth.BTContract;
import refried.rfid_inventory.bluetooth.BluetoothSettingsPresenter;
import refried.rfid_inventory.bluetooth.TSLModel;
import refried.rfid_inventory.util.BTDevice;

import static android.Manifest.permission.*;

/**
 * A dialog fragment for choosing and configuring Bluetooth scanning devices.
 */
public class DevicePickerFragment extends DialogFragment implements BTContract.View {
    private static final int BLUETOOTH_PERMISSION_REQUEST = 1;

    private BTContract.Presenter mPresenter;

    RecyclerView mPairedRV;
    RecyclerView mUnpairedRV;
    TextView mPairedDeviceHeader;
    TextView mUnpairedDeviceHeader;
    Button mScanButton;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DevicePickerFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DevicePickerFragment newInstance() {
        return new DevicePickerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_btdevice_list, container, false);
        getDialog().setCanceledOnTouchOutside(true);

        mPresenter = new BluetoothSettingsPresenter(this, TSLModel.getInstance());

        // Check permissions for Bluetooth
        if(!hasPermissions(getActivity(), BLUETOOTH, BLUETOOTH_ADMIN, ACCESS_COARSE_LOCATION)) {
            mPresenter.permissionsGranted(false);
            ActivityCompat.requestPermissions(
                    getActivity(), new String[] {BLUETOOTH, BLUETOOTH_ADMIN, ACCESS_COARSE_LOCATION},
                    BLUETOOTH_PERMISSION_REQUEST);
        } else {
            mPresenter.permissionsGranted(true);
        }

        // Configure list of paired items
        mPairedRV = rootView.findViewById(R.id.paired_list);
        mPairedRV.setAdapter(new BTDeviceRecyclerViewAdapter(new ArrayList<BTDevice>(), mPresenter));

        // Configure list of unpaired items
        mUnpairedRV = rootView.findViewById(R.id.unpaired_list);
        mUnpairedRV.setAdapter(new BTDeviceRecyclerViewAdapter(new ArrayList<BTDevice>(), mPresenter));

        mPairedDeviceHeader = rootView.findViewById(R.id.title_paired_devices);
        mUnpairedDeviceHeader = rootView.findViewById(R.id.title_new_devices);

        mScanButton = rootView.findViewById(R.id.button_scan);
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.doDiscovery();
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPairedRV.setAdapter(null);
        mUnpairedRV.setAdapter(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case BLUETOOTH_PERMISSION_REQUEST: {
                // TODO: iterate through all permissions
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPresenter.permissionsGranted(true);
                } else {
                    mPresenter.permissionsGranted(false);
                }
            }
        }
    }

    // Class-specific functions
    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    // Overridden functions
    @Override
    public void onStart() {
        mPresenter.start(getActivity());
        super.onStart();
    }

    @Override
    public void onStop() {
        mPresenter.stop();
        super.onStop();
    }

    // Overridden functions from BTContract.View
    @Override
    public void requestEnableBluetooth() {

    }

    @Override
    public void showPairedDevices(ArrayList<BTDevice> devices) {
        mPairedRV.swapAdapter(new BTDeviceRecyclerViewAdapter(devices, mPresenter), false);
    }

    @Override
    public void showPairedDeviceCount(int count) {
        if(count == 0) {
            mPairedDeviceHeader.setText("Paired Devices (None)");
        } else {
            String deviceCountFormat = "Paired Devices (%d)";
            String someDevices = String.format(deviceCountFormat, count);
            mPairedDeviceHeader.setText(someDevices);
        }
    }

    @Override
    public void showUnpairedDevices(ArrayList<BTDevice> devices) {
        mUnpairedRV.swapAdapter(new BTDeviceRecyclerViewAdapter(devices, mPresenter), false);
    }

    @Override
    public void updatePairedDevices() {
        mPairedRV.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void updateUnpairedDevices() {
        mUnpairedRV.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void showUnpairedDeviceCount(int count) {
        if(count == 0) {
            mUnpairedDeviceHeader.setText("Unpaired Devices (None)");
        } else {
            String deviceCountFormat = "Unpaired Devices (%d)";
            String someDevices = String.format(deviceCountFormat, count);
            mUnpairedDeviceHeader.setText(someDevices);
        }
    }

    @Override
    public void showScanning() {
        mScanButton.setEnabled(false);

    }

    @Override
    public void showNotScanning() {
        mScanButton.setEnabled(true);
    }
}
