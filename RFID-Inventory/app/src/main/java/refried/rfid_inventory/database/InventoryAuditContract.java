package refried.rfid_inventory.database;

import com.google.common.collect.ImmutableSet;

import java.util.Map;

/**
 * Contract defining behaviors for an Inventory Audit.
 * <br>
 * Audits allow the user to poll the current list of Inventoried items and confirm presence and location.
 *
 */

public interface InventoryAuditContract {
    /**
     * Detaches presenter (stop listening)
     */
    void detachDBListener();

    /**
     * Fetches prior audits from a specific item.
     * @param uid Unique Identifier of a particular Inventory Item
     */
    ImmutableSet getAuditHistory(String uid);

    /**
     * Fetches the last seen date for all Inventoried Items
     */
    Map getLatestAudit();

    /**
     * Fetches the last seen date for one specific item
     * @param uid Unique Identifier of a particular Inventory Item
     */
    InventoryAudit getLatestAudit(String uid);

    /**
     * Commits an inventory audit data point.
     * <br>
     * A Data Point consists of date/time, location, and the user who created the point.
     * The database should store the user name internally
     */
    void addAudit(InventoryItem itemToAudit);
}
