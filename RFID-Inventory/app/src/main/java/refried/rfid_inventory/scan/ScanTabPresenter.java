package refried.rfid_inventory.scan;

import android.os.Message;
import android.support.annotation.NonNull;

import com.uk.tsl.rfid.ModelBase;
import com.uk.tsl.rfid.WeakHandler;
import com.uk.tsl.rfid.asciiprotocol.AsciiCommander;
import com.uk.tsl.rfid.asciiprotocol.DeviceProperties;

import java.util.ArrayList;
import java.util.List;

import refried.rfid_inventory.bluetooth.TSLModel;
import refried.rfid_inventory.bluetooth.TSLModelContract;
import refried.rfid_inventory.database.FirebaseDBContract;
import refried.rfid_inventory.database.InventoryAudit;
import refried.rfid_inventory.database.InventoryAuditContract;
import refried.rfid_inventory.database.InventoryItem;
import refried.rfid_inventory.database.DBQuery;
import refried.rfid_inventory.database.FirebaseItemQuery;
import refried.rfid_inventory.inventoryinteractor.RFIDInventoryContract;


public class ScanTabPresenter implements ScanTabContract.Presenter, DBQuery.QueryObserver<InventoryItem> {
    private ScanTabContract.View mView;
    private RFIDInventoryContract mInventoryModel;
    private InventoryAuditContract mAuditModel;
    private TSLModelContract mModel = TSLModel.getInstance();
    private AsciiCommander mCommander = mModel.getCommander();
    private FirebaseDBContract mDatabaseModel;
    private DeviceProperties deviceProperties = mCommander.getDeviceProperties();

    private final List<String> mTagNumbers= new ArrayList<>();
    private final List<InventoryItem> mItems = new ArrayList<>();

    private boolean mStatus_Bool = false;

    public ScanTabPresenter(@NonNull ScanTabContract.View view,
                            @NonNull RFIDInventoryContract inventoryModel,
                            @NonNull FirebaseDBContract dbModel,
                            @NonNull InventoryAuditContract auditModel) {
        mView = view;
        mInventoryModel = inventoryModel;
        mDatabaseModel = dbModel;
        mAuditModel = auditModel;
        mModel.reconnectDevice();
    }

    @Override
    public void start() {
        mInventoryModel.attachListener(new RFIDInventoryContract.itemFoundCallback() {
            @Override
            public void addRFIDTagID(String msg) {
                if(!mTagNumbers.contains(msg)){
                    mTagNumbers.add(msg);
                    InventoryItem uncreatedItem = new InventoryItem.Builder()
                                                        .named("Uncreated Item")
                                                        .withRFIDTag(msg)
                                                        .withDescription("")
                                                        .withSerialNum("")
                                                        .withPrice("")
                                                        .location("")
                                                        .withTagColor("")
                                                        .withPhotographURL("")
                                                        .withUniqueID("")
                                                        .withStatus("")
                                                        .withNameQueryable("uncreated item")
                                                        .build();
                    mItems.add(uncreatedItem);
                }
                mDatabaseModel.sendQuery(genQuery(msg));
                mView.refreshData();
                // Do something with the new data
            }
        });
    }



//query then when come back check that empty then do something with tag
    @Override
    public void stop() {
        mInventoryModel.detachListener();
        mDatabaseModel.removeQuery();
        mAuditModel.detachDBListener();
    }

    @Override
    public List<InventoryItem> initializeData() {
        mItems.clear();
        return mItems;
    }

    @Override
    public void scan(){
        mInventoryModel.scan();
    }

    @Override
    public void clear() {
        mItems.clear();
        mTagNumbers.clear();
        mView.refreshData();
    }

    @Override
    public void disconnect(){
        mInventoryModel.disconnect();
        mView.showDisconnect();
    }

    @Override
    public String readerStatus() {
        String message = "";
        switch(mCommander.getConnectionState()) {
            case CONNECTED:
                message += mCommander.getConnectedDeviceName();
                mStatus_Bool = true;
                break;
            case CONNECTING:
                message += "Connecting...";
                mStatus_Bool = false;
                break;
            default:
                mStatus_Bool = false;
                message += "Disconnected";
        }
        return message;
    }

    @Override
    public boolean getStatus(){
        return mStatus_Bool;
    }

    @Override
    public int getMaxPower(){
        return deviceProperties.getMaximumCarrierPower();
    }

    @Override
    public int getMinPower(){
        return deviceProperties.getMinimumCarrierPower();
    }

    @Override
    public void setPowerLevel(int level){
        mInventoryModel.getCommand().setOutputPower(level);
        if(mModel.getCommander().isConnected()) {
            mInventoryModel.updateConfig();
        }
    }

    @Override
    public void onDataFound(InventoryItem newItem) {
        for(int i = 0; i < mItems.size(); ++i){
            if(mItems.get(i).getRfid_tag_num().equals(newItem.getRfid_tag_num())){
                mItems.set(i,newItem);
                mView.refreshData();
                mAuditModel.addAudit(newItem);
            }
        }
    }

    private FirebaseItemQuery genQuery(String rfidTagNumber) {
        return new FirebaseItemQuery(this, InventoryItem.Field.RFID_TAG_NUMBER, DBQuery.FilteringMethod.EQUALS, rfidTagNumber);
    }


}