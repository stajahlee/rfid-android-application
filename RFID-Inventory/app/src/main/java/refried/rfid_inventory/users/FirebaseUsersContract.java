package refried.rfid_inventory.users;


/**
 * Interface Specification for all communications affecting
 * Firebase Users.
 */
public interface FirebaseUsersContract {

    /**
     * get the currennt user name
     *
     */
    String getCurrentUser();

    /**
     * Binds a data user to the class.
     *
     */
    void attachFirebaseAuthListener();

    /**
     * Unbinds a data user from the class
     *
     */
    void detachFirebaseAuthListener();

    /**
     * user information like name and
     * whether they accepted the terms
     * saved to the database
     */
    void saveUserInformationToDatabase();

    /**
     * when user accepts terms this
     * saves to the database
     */
    void userAcceptedTerms();
}