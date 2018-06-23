package refried.rfid_inventory.database;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SortedSetMultimap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements the audit use case.
 * <br>
 * Defines an audit of items currently Inventoried
 */

public class InventoryAuditInteractor
        implements InventoryAuditContract, DBQuery.QueryObserver<InventoryAudit> {
    /* Our implementation will use a single node in Firebase fora all audits
     * Each subnode is one inventory item UID.
     * Beyond that, each subnode contains several data points. Those data points are made of:
     * - Last Seen Date
     * - User who saw that piece of data
     * - The location that the data was seen in
     * A partially-complete audit will show... what?
     */

    private String mCurrentUser;

    private FirebaseDBContract mDatabase;

    // Multimap of Inventory Items and their audit dates, in a NavigableSet
    private SortedSetMultimap<String, InventoryAudit> mAuditMap;

    public InventoryAuditInteractor(FirebaseDBContract db) {
        mDatabase = db;
        mDatabase.sendQuery(new FirebaseAuditQuery(this));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            mCurrentUser = user.getUid();
        } else {
            mCurrentUser = "Anonymous"; // TODO: Handle failures more safely
        }
        mAuditMap = MultimapBuilder.hashKeys().treeSetValues().build();
    }

    @Override
    public void detachDBListener() {
        mDatabase.removeQuery();
    }

    @Override
    public ImmutableSortedSet getAuditHistory(String uid) {
        return ImmutableSortedSet.copyOf(mAuditMap.get(uid));
    }

    @Override
    public ImmutableMap getLatestAudit() {
        // TODO: Should return a Map view
        Map<String, InventoryAudit> ret = new HashMap<>();
        for(String key : mAuditMap.keySet()) {
            ret.put(key, mAuditMap.get(key).last());
        }
        return ImmutableMap.copyOf(ret);
    }

    @Override
    public InventoryAudit getLatestAudit(String uid) {
        return mAuditMap.get(uid).last();
    }

    @Override
    public void addAudit(InventoryItem itemToAudit) {
        InventoryAudit newAudit = new InventoryAudit.Builder()
                .by(mCurrentUser)
                .withItemUID(itemToAudit.getUnique_id())
                .locatedAt(itemToAudit.getLast_location())
                .build();
        uploadAudit(newAudit);
    }

    private void uploadAudit(InventoryAudit auditToAdd) {
        mDatabase.addInventoryAudit(auditToAdd);
    }

    @Override
    public void onDataFound(InventoryAudit newAudit) {
        mAuditMap.put(newAudit.getUid(), newAudit);
    }
}
