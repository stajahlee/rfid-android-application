/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uk.tsl.rfid;

import java.util.Set;

import com.uk.tsl.rfid.devicelist.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class DeviceListActivity extends Activity {
    // Debugging
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    private Button mScanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);

        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);

        // Initialize the button to perform device discovery
        mScanButton = (Button) findViewById(R.id.button_scan);
        mScanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setEnabled(false);
            }
        });

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume()
    {
    	super.onResume();
    	listPairedDevices();
    }
    
    private void listPairedDevices()
    {
        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        TextView pairedDevicesHeader = (TextView)findViewById(R.id.title_paired_devices);

        mPairedDevicesArrayAdapter.clear();

        if (pairedDevices.size() > 0) {
        	String deviceCountFormat = getResources().getText(R.string.title_paired_devices_count_format).toString();
            String someDevices = String.format(deviceCountFormat, pairedDevices.size());
            pairedDevicesHeader.setText(someDevices);
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.title_paired_devices_empty).toString();
            pairedDevicesHeader.setText(noDevices);
            // User has no devices paired so scan for devices if no devices currently listed
            if( mNewDevicesArrayAdapter.getCount() == 0 ) {
            	mScanButton.setEnabled(false);
            	doDiscovery();
            }
        }
    }
    
    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");

        // Re-create the list
    	mNewDevicesArrayAdapter.clear();

    	// Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        TextView otherDevicesHeader = (TextView)findViewById(R.id.title_new_devices);
    	String noDevices = getResources().getText(R.string.title_other_devices_empty).toString();
        otherDevicesHeader.setText(noDevices);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            if( info.length() >= 17) {
            	String address = info.substring(info.length() - 17);

            	// Check that it looks like a a bluetooth address
            	// Check for 5 colons in the right place
            	boolean hasCorrectColons = true;
            	for( int i = 0; i < 5; ++i ) {
            		if( address.charAt(2 + 3 * i) != ':'  ) {
            			hasCorrectColons = false;
            			break;
            		}
            	}

            	if( hasCorrectColons ) {
            		// Cancel discovery because it's costly and we're about to connect
            		mBtAdapter.cancelDiscovery();

            		// Create the result Intent and include the MAC address
            		Intent intent = new Intent();
            		intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            		// Set result and finish this Activity
            		setResult(Activity.RESULT_OK, intent);
            		finish();
            	}
            }
        }
    };

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            TextView otherDevicesHeader = (TextView)findViewById(R.id.title_new_devices);

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                	String listText = device.getName() + "\n" + device.getAddress();
                	boolean isNew = true;
                	for( int i = 0; i < mNewDevicesArrayAdapter.getCount(); ++i ) {
                		if( mNewDevicesArrayAdapter.getItem(i).equals(listText)) {
                			isNew = false;
                			break;
                		}
                	}
                	if( isNew ) {
                		mNewDevicesArrayAdapter.add(listText);
                	}
                	String deviceCountFormat = getResources().getText(R.string.title_other_devices_count_format).toString();
                    String someDevices = String.format(deviceCountFormat, mNewDevicesArrayAdapter.getCount());
                    otherDevicesHeader.setText(someDevices);
                }
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                Button scanButton = (Button) findViewById(R.id.button_scan);
                scanButton.setEnabled(true);
            }
        }
    };

}
