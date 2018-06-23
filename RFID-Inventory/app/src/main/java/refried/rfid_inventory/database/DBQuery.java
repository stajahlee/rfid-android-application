package refried.rfid_inventory.database;

import android.support.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;

public interface DBQuery {
    /**
     * Callback when data is found by the query.
     * <br>
     * Parameterized by the data type the query is supposed to return.
     */
    interface QueryObserver<T> {
        void onDataFound(T item);
    }

    /**
     * Returns which database node or table to query
     *
     * @return String representation of the table/node
     */
    String getNode();

    /**
     * Returns property (i.e. column) of the row to search/filter by
     *
     * @return String representation of the column header
     */
    String getProperty();

    /**
     * Returns the type of filtering method for the database query as defined by {@link FilteringMethod}
     *
     * @return An element of an enumerated type for the filtering type
     */
    FilteringMethod getFilteringMethod();

    /**
     * The search string of the row/column match to filter by. Must not be null (use empty string)
     *
     * @return String representation of the value to query
     */
    @NonNull
    String getSearchString();

    /**
     * Returns a Firebase EventListener create for this particular query.
     */
    ChildEventListener getEventListener();

    /**
     * Defines the types of data in the database.
     */
    enum DataType {
        AUDIT("audits"),
        ITEM("inventory-item"),
        USER("users"),
        ;

        private final String databaseNode;

        DataType(String node) {
            databaseNode = node;
        }

        public String getDatabaseNode() {
            return databaseNode;
        }
    }

    /**
     * Defines possible filtering methods for our queries
     */
    enum FilteringMethod {
        DEFAULT,
        EQUALS,
        START_AT,
        ;
    }
}
