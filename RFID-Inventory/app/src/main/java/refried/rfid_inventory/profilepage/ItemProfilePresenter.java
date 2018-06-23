package refried.rfid_inventory.profilepage;

import refried.rfid_inventory.database.FirebaseDBContract;
import refried.rfid_inventory.database.InventoryItem;
import refried.rfid_inventory.database.DBQuery;
import refried.rfid_inventory.database.FirebaseItemQuery;

/**
 * Presenter (simple) for displaying an InventoryItem
 */

public class ItemProfilePresenter implements ItemProfileContract.Presenter, DBQuery.QueryObserver<InventoryItem> {
    private ItemProfileContract.View mView;
    private FirebaseDBContract mModel;

    public ItemProfilePresenter(ItemProfileContract.View view, FirebaseDBContract model) {
        mView = view;
        mModel = model;
    }

    @Override
    public void stop() {
        mModel.removeQuery();
    }

    @Override
    public void getItem(InventoryItem item) {
        mModel.sendQuery(new FirebaseItemQuery(this, InventoryItem.Field.UNIQUE_ID, DBQuery.FilteringMethod.EQUALS, item.getUnique_id()));
    }

    @Override
    public void onDataFound(InventoryItem item) {
        mView.bind(item);
    }
}
