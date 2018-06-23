package refried.rfid_inventory.database;

import android.support.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class FirebaseItemQuery implements DBQuery {
    private static final String node = DataType.ITEM.getDatabaseNode();

    private final InventoryItem.Field property;
    private final FilteringMethod filteringMethod;
    private final String searchString;
    private final QueryObserver<InventoryItem> mObserver;
    private final ChildEventListener mListener = new ChildEventListener() {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            InventoryItem item = dataSnapshot.getValue(InventoryItem.class);
            mObserver.onDataFound(item);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            InventoryItem item = dataSnapshot.getValue(InventoryItem.class);
            mObserver.onDataFound(item);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {}

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

        @Override
        public void onCancelled(DatabaseError databaseError) {}
    };

    /**
     * Default query gets everything
     */
    public FirebaseItemQuery(QueryObserver<InventoryItem> observer) {
        this.mObserver = observer;
        this.property = InventoryItem.Field.DEFAULT;
        this.filteringMethod = FilteringMethod.DEFAULT;
        this.searchString = "";
    }

    public FirebaseItemQuery(QueryObserver<InventoryItem> observer,
                             InventoryItem.Field property,
                             FilteringMethod method,
                             @NonNull String searchString) {
        this.mObserver = observer;
        this.property = property;
        this.filteringMethod = method;
        this.searchString = searchString;
    }

    @Override
    public String getNode() {
        return node;
    }

    @Override
    public String getProperty() {
        return property.getTextField();
    }

    @Override
    public FilteringMethod getFilteringMethod() {
        return filteringMethod;
    }

    @Override
    public @NonNull String getSearchString() {
        return searchString;
    }

    @Override
    public ChildEventListener getEventListener() {
        return mListener;
    }
}
