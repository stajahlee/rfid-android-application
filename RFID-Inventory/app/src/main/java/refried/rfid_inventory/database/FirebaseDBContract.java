package refried.rfid_inventory.database;

import android.net.Uri;

/**
 * Interface Specification for all communications affecting Firebase.
 * This includes
 *   Firebase Realtime Database (inventory items)
 *   Firebase Auth (user profiles, if we choose to implement them)
 *   Firebase Storage (images)
 * This risks creating a god class; we may split this file up later.
 */
public interface FirebaseDBContract {

    /**
     * Adds an item to the database
     *
     * @param item A filled-in item to add
     * @param uri URI of a photo on the local device
     */
    void addInventoryItem(InventoryItem item, Uri uri);

    /**
     * Adds an audit data point to the database, as associated to an existing item
     *
     * @param audit Pre-defiened audit
     */
    void addInventoryAudit(InventoryAudit audit);

    /**
     * Given an existing item, update it in the database
     *
     * @param it the new item to update
     * @param uri The URI (on the local filesystem) of the photograph to associate
     */
    void updateInventoryItem(InventoryItem it, Uri uri);

    /**
     * Given an existing item, update it in the database
     *
     * @param it The new item to update
     */
    void updateInventoryItem(InventoryItem it);

    /**
     * Submit a query to Firebase
     */
    void sendQuery(DBQuery query);

    /**
     * Remove the currently active query
     */
    void removeQuery();
}