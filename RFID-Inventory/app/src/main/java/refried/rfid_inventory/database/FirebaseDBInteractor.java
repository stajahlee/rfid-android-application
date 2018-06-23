package refried.rfid_inventory.database;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;


import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is the Interactor for Firebase in our MVP architecture.
 */
public class FirebaseDBInteractor implements FirebaseDBContract {
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();

    private final DatabaseReference mInventoryAuditReference = mFirebaseDatabase.getReference("audits");
    private final DatabaseReference mInventoryDatabaseReference = mFirebaseDatabase.getReference("inventory-item");
    private StorageReference mInventoryStorageReference = mFirebaseStorage.getReference().child("photos");

    private Map<Query,ChildEventListener> mDBListeners = new HashMap<>();

    public FirebaseDBInteractor() { }

    // Underneath here are all the functions to read/write/interact with the database
    @Override
    public void addInventoryItem(final InventoryItem item, Uri uri) {
        final DatabaseReference itemLocation = mInventoryDatabaseReference.push();
        final String key = itemLocation.getKey();
        item.setUnique_id(key);
        mInventoryDatabaseReference.child(key).setValue(item);
        uploadPhoto(itemLocation, uri);
    }

    @Override
    public void addInventoryAudit(final InventoryAudit audit) {
        final DatabaseReference itemLocation = mInventoryAuditReference.push();
        itemLocation.setValue(audit);
    }

    /**
     * Uploads a photo to Firebase Storage and creates a reference to it in Firebase Realtime
     * @param itemNode Location to create the reference
     * @param photoUri URI pointing to a local file containing a picture
     */
    private void uploadPhoto(final DatabaseReference itemNode, Uri photoUri) {
        if(photoUri == null) {
            itemNode.child("photograph").setValue(null);
        } else {
            StorageReference photoRef = mInventoryStorageReference.child(photoUri.getLastPathSegment());
            UploadTask mUploadTask = photoRef.putFile(photoUri);

            mUploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()) {
                        Uri downloadUrl = task.getResult().getDownloadUrl();
                        if (downloadUrl != null) {
                            itemNode.child("photograph").setValue(downloadUrl.toString());
                        }
                        Log.d("SUCCESS", "New Download URL: " + downloadUrl);
                    } else {
                        Log.d("FAIL", "Photo failed to upload");
                    }
                }
            });
        }
    }

    @Override
    public void updateInventoryItem(final InventoryItem item, @NonNull Uri uri) {
        final DatabaseReference itemLocation = mInventoryDatabaseReference.child(item.getUnique_id());
        itemLocation.setValue(item);
        uploadPhoto(itemLocation, uri);
    }

    @Override
    public void updateInventoryItem(final InventoryItem item) {
        mInventoryDatabaseReference.child(item.getUnique_id()).setValue(item);
    }

    @Override
    public void sendQuery(DBQuery query) {
        Query tmpQuery = mFirebaseDatabase.getReference(query.getNode()).orderByChild(query.getProperty());
        ChildEventListener nextListener = query.getEventListener();

        if(mDBListeners.containsKey(tmpQuery)) {
            tmpQuery.removeEventListener(mDBListeners.get(tmpQuery));
        }

        switch(query.getFilteringMethod()) {
            case EQUALS:
                tmpQuery = tmpQuery.equalTo(query.getSearchString());
                break;
            case START_AT:
                tmpQuery = tmpQuery.startAt(query.getSearchString()).endAt(query.getSearchString() + "\uf8ff");
                break;
            case DEFAULT: // INTENTIONAL FALL THROUGH
            default: // INTENTIONAL FALL THROUGH
                // INTENTIONAL NO-OP
                break;
        }

        tmpQuery.addChildEventListener(nextListener);
        mDBListeners.put(tmpQuery, nextListener);
    }

    @Override
    public void removeQuery() {
        for(Map.Entry<Query, ChildEventListener> e : mDBListeners.entrySet()) {
            e.getKey().removeEventListener(e.getValue());
        }
        mDBListeners.clear();
    }
}
