package refried.rfid_inventory.profilepage;

import android.net.Uri;

import refried.rfid_inventory.database.FirebaseDBContract;
import refried.rfid_inventory.database.InventoryItem;
import refried.rfid_inventory.database.DBQuery;
import refried.rfid_inventory.database.FirebaseItemQuery;

/**
 * Presents information to UI for creating and editing Inventory Items.
 */

public class ItemCreateAndEditPresenter
        implements ItemCreateAndEditContract.Presenter, DBQuery.QueryObserver<InventoryItem> {
    private ItemCreateAndEditContract.View mView;
    private FirebaseDBContract mModel;

    // TODO: Allow user to update visible item when database changes
    private InventoryItem mExistingItem;
    private boolean alreadyExists;
    private String potentialRFIDTag;

    public ItemCreateAndEditPresenter(ItemCreateAndEditContract.View view, FirebaseDBContract model) {
        mView = view;
        mModel = model;
        alreadyExists = false; // Default to unseen; create new item
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        mModel.removeQuery();
    }

    @Override
    public void initItem(InventoryItem item) {
        mExistingItem = item;
        if(item.getUnique_id() != null) {
            FirebaseItemQuery currentItem =
                    new FirebaseItemQuery(this, InventoryItem.Field.UNIQUE_ID,
                            DBQuery.FilteringMethod.EQUALS, item.getUnique_id());
            mModel.sendQuery(currentItem);
            alreadyExists = true;
        }
    }

    @Override
    public void setRFIDTag(String s) {
        potentialRFIDTag = s;
        FirebaseItemQuery rfidQuery =
                new FirebaseItemQuery(this, InventoryItem.Field.RFID_TAG_NUMBER,
                        DBQuery.FilteringMethod.EQUALS, s);
        mModel.sendQuery(rfidQuery);
    }

    @Override
    public void saveItem(InventoryItem item, Uri photoUri) {
        if(!isValidItem(item)) {
            return;
        }
        String s = formatPrice(item.getOriginal_price());
        item.setOriginal_price(s);
        if(alreadyExists) {
            updateOldItem(item, photoUri);
        } else {
            makeNewItem(item, photoUri);
        }
        mView.savedSuccessfully();
    }

    private boolean isValidItem(InventoryItem item) {
        boolean flag = true;
        if(item.getName().isEmpty()) {
            mView.showNameError();
            flag = false;
        }
        if(item.getDescription().isEmpty()) {
            mView.showDescError();
            flag = false;
        }
        if(item.getOriginal_price().isEmpty()) {
            mView.showPriceError();
            flag = false;
        }
        return flag;
    }

    private void makeNewItem(InventoryItem item, Uri photoUri) {
        mModel.addInventoryItem(item, photoUri);
    }

    private void updateOldItem(InventoryItem item, Uri photoUri) {
        if (photoUri == null) {
            mModel.updateInventoryItem(item);
        } else {
            mModel.updateInventoryItem(item, photoUri);
        }
    }

    private String formatPrice(String price){
        if (price.equals(".")) {
            return "0.00";
        } else {
            try {
                Double dPrice = Double.parseDouble(price);
                return String.format("%.2f", dPrice);
            } catch (NumberFormatException e) {
                mView.showPriceError();
            }
        }
        return price;
    }

    @Override
    public void onDataFound(InventoryItem item) {
        // Case 1: Initial state. We're checking for existing items.

        // Case 2: We've found an item and we're just getting it back again.
        // Do nothing

        // Case 3: We've found an item and we're getting a different item... it's an RFID query
        // Need to track whether or not the RFID tag matches, and if it does match then is it the same item...
        if(potentialRFIDTag != null && item.getRfid_tag_num().equals(potentialRFIDTag)) {
            if(mExistingItem == null || !mExistingItem.equals(item)) {
                mView.showDuplicateError();
            }
        }
    }
}
