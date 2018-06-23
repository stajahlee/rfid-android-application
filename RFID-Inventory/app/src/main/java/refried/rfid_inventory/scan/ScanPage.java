package refried.rfid_inventory.scan;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.uk.tsl.rfid.asciiprotocol.parameters.AntennaParameters;
import refried.rfid_inventory.R;
import refried.rfid_inventory.database.FirebaseDBContract;
import refried.rfid_inventory.database.FirebaseDBInteractor;
import refried.rfid_inventory.database.InventoryAuditInteractor;
import refried.rfid_inventory.database.InventoryItem;
import refried.rfid_inventory.inventoryinteractor.RFIDInventoryInteractor;
import refried.rfid_inventory.profilepage.ItemProfileFragment;
import refried.rfid_inventory.settings.DevicePickerFragment;
import refried.rfid_inventory.util.ItemsAdapter;

/**
 * The Tab Fragment for users to scan items by RFID
 */
public class ScanPage extends Fragment implements ScanTabContract.View,
        ItemsAdapter.ItemsAdapterCallbackListener {

    TextView mScannerStatus;
    TextView mRvTitle;
    TextView mPowerTextView;
    SeekBar mPowerSeekBar;
    Button mButtonConnect;
    Button mButtonScan;
    Button mButtonClear;
    RecyclerView mTagContainer;
    ItemsAdapter mTagContainerAdapter;
    ScanTabContract.Presenter mPresenter;
    private int mPowerLevel = AntennaParameters.MaximumCarrierPower;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mPresenter = new ScanTabPresenter(this, new RFIDInventoryInteractor(), new FirebaseDBInteractor(), new InventoryAuditInteractor(new FirebaseDBInteractor()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.scan_tab, container, false);

        mScannerStatus = rootView.findViewById(R.id.Scanner_Status);
        mRvTitle = rootView.findViewById(R.id.rvTitle);
        mPowerTextView = rootView.findViewById(R.id.powerTextView);
        mPowerSeekBar = rootView.findViewById(R.id.powerSeekBar);
        mButtonConnect = rootView.findViewById(R.id.buttonConnect);
        mButtonScan = rootView.findViewById(R.id.buttonScan);
        mButtonClear = rootView.findViewById(R.id.buttonClear);
        mTagContainer = rootView.findViewById(R.id.recyclerView);
        mTagContainer.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTagContainerAdapter = new ItemsAdapter(this, mPresenter.initializeData());
        mTagContainer.setAdapter(mTagContainerAdapter);

        setPowerSeekBarLimits();

        mPowerSeekBar.setOnSeekBarChangeListener(mPowerSeekBarListener);
        mButtonConnect.setOnClickListener(mButtonConnectListener);
        mButtonScan.setOnClickListener(mButtonScanListener);
        mButtonClear.setOnClickListener(mButtonClearListener);

        getActivity().setTitle("RFID Scan");
        return rootView;
    }

    @Override
    public void onStart() {
        showReconnectAttempt();
        mPresenter.start();
        super.onStart();
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        showConnectionStatus();
        displayAndSetReaderStatus();
        updateUI();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.stop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void showConnectionStatus() {
        if (mPresenter.getStatus()) {
            showScannerConnected();
            showStartScanning();
        } else {
            showNoScannerError();
        }
    }

    @Override
    public void showStartScanning() {
        Toast.makeText(getActivity(), "Start Scanning Now",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void showDisconnect() {
        Toast.makeText(getActivity(), "Scanner Disconnected",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void showNoScannerError() {
        Toast.makeText(getActivity(), "Error: No Connected Scanner",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showScannerConnected() {
        Toast.makeText(getActivity(), "Scanner Connected",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void showReconnectAttempt(){
        Toast.makeText(getActivity(), "Attempting to reconnect to last used reader...",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void refreshData(){
        mTagContainerAdapter.notifyDataSetChanged();
    }

    private void setPowerSeekBarLimits() {
        int max , min;
        max = mPresenter.getMaxPower();
        min = mPresenter.getMinPower();
       mPowerSeekBar.setMax(max - min);
       mPowerLevel = max;
       mPowerSeekBar.setProgress(mPowerLevel - min);
       updatePowerSetting(mPowerLevel);
       mPowerTextView.setText(mPowerLevel + " dBm");
    }

    private void displayAndSetReaderStatus() {
        String statusMessage = "Scanner: ";
        statusMessage += mPresenter.readerStatus();
        mScannerStatus.setText(statusMessage);
    }

    Button.OnClickListener mButtonConnectListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(mPresenter.getStatus()){
                mPresenter.disconnect();
            }else{
                DevicePickerFragment nextFragment = DevicePickerFragment.newInstance();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                nextFragment.show(transaction, null);
            }
            updateUI();
        }
    };

    Button.OnClickListener mButtonScanListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            showStartScanning();
            mPresenter.scan();
            refreshData();
            updateUI();
        }
    };

    Button.OnClickListener mButtonClearListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            mPresenter.clear();
        }
    };

    private SeekBar.OnSeekBarChangeListener mPowerSeekBarListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Nothing to do here
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Update the reader's setting only after the user has finished changing the value
            updatePowerSetting(mPresenter.getMinPower() + seekBar.getProgress());
            mPresenter.setPowerLevel(mPowerLevel);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            updatePowerSetting(mPresenter.getMinPower() + progress);
        }

    };

    private void updatePowerSetting(int level)	{
        mPowerLevel = level;
        mPowerTextView.setText( mPowerLevel + " dBm");
    }

    public void updateUI(){
        displayAndSetReaderStatus();
        //mButtonScan.setEnabled(mStatus_Bool);
        if(mPresenter.getStatus()){
            mButtonConnect.setText("DISCONNECT".toString());
        }else{
            mButtonConnect.setText("CONNECT".toString());
        }
        refreshData();
    }

    @Override
    public void transitionToProfileView(InventoryItem itemToView) {
        getFragmentManager().beginTransaction()
                .addToBackStack(null)
                .hide(this)
                .add(R.id.drawer_layout, ItemProfileFragment.newInstance(itemToView))
                .commit();
    }
}
