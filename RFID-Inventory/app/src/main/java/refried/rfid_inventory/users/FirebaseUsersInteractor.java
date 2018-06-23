package refried.rfid_inventory.users;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.net.Uri;
import android.text.format.DateFormat;


/**
 * This class is the Interactor for Firebase Users in our MVP architecture.
 */
public class FirebaseUsersInteractor implements refried.rfid_inventory.users.FirebaseUsersContract {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mInventoryDatabaseReference = mFirebaseDatabase.getReference("users");

    private String mCurrentUser, uid, date_time;
    private boolean accepted_terms = true;
    private static final String ANONYMOUS = " ";

    // constructor for the Firebase Interactor class:
    public FirebaseUsersInteractor() {
        // Initialize items
        mCurrentUser = ANONYMOUS;
        attachFirebaseAuthListener();
        date_time = getDateTime();
        saveUserInformationToDatabase();
    }

    @Override
    public void attachFirebaseAuthListener() {
        // clear list so new database snapshot can populate list

        // initialize Firebase Authorization component
        mFirebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            mCurrentUser = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project.
            uid = user.getUid();
        }
    }

    @Override
    public void detachFirebaseAuthListener() {
        // to do
    }

    @Override
    public String getCurrentUser() {
        return mCurrentUser;
    }

    @Override
    public  void userAcceptedTerms() {
        accepted_terms = true;
    }

    @Override
    public void saveUserInformationToDatabase() {
        mInventoryDatabaseReference.child(uid).child("name").setValue(mCurrentUser);
        mInventoryDatabaseReference.child(uid).child("accepted_terms").setValue(Boolean.toString(accepted_terms));
        mInventoryDatabaseReference.child(uid).child("last_access_date_time").setValue(date_time);
    }

    private String getDateTime() {
        date_time = (DateFormat.format("MM-dd-yyyy hh:mm:ss", new java.util.Date()).toString());
        return date_time;
    }

}
