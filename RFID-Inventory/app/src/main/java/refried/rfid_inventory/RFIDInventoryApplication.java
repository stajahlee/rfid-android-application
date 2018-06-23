package refried.rfid_inventory;

import android.app.Application;

import refried.rfid_inventory.bluetooth.TSLModel;
import refried.rfid_inventory.bluetooth.TSLModelContract;
import refried.rfid_inventory.util.ContextRepository;

/**
 * Extended Application object to initialize model data at app startup.
 */

public class RFIDInventoryApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TSLModelContract RFIDModel = TSLModel.getInstance();
        RFIDModel.initialize(new ContextRepository(getApplicationContext()));
    }
}
