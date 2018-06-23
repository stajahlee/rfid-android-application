package refried.rfid_inventory.database;

import android.support.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

// TODO: Make abstract class with {@link FirebaseItemQuery}
public class FirebaseAuditQuery implements DBQuery {
    private static final String node = DataType.AUDIT.getDatabaseNode();

    private final InventoryAudit.Field property;
    private final FilteringMethod filteringMethod;
    private final String searchString;
    private final QueryObserver<InventoryAudit> mObserver;
    private final ChildEventListener mListener = new ChildEventListener() {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            InventoryAudit item = dataSnapshot.getValue(InventoryAudit.class);
            mObserver.onDataFound(item);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            // TODO: Really, children should not change.
            InventoryAudit item = dataSnapshot.getValue(InventoryAudit.class);
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
    public FirebaseAuditQuery(QueryObserver<InventoryAudit> observer) {
        this.mObserver = observer;
        this.property = InventoryAudit.Field.DEFAULT;
        this.filteringMethod = FilteringMethod.DEFAULT;
        this.searchString = "";
    }

    public FirebaseAuditQuery(QueryObserver<InventoryAudit> observer,
                              InventoryAudit.Field property,
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
