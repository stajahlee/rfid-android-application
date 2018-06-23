package refried.rfid_inventory.profilepage;

import android.net.Uri;

import refried.rfid_inventory.database.InventoryItem;

/**
 * Defines the interactions to create and edit Inventory Items
 */

public interface ItemCreateAndEditContract {
    interface View {
        /**
         * Gives an item to the View for displaying
         * @param item item to display
         */
        void bindItem(final InventoryItem item);

        /**
         * Tells the GUI to show an error
         *   when creating an item without a name
         */
        void showNameError();

        /**
         * Tells the GUI to show an error
         *   when creating an item with no description
         */
        void showDescError();

        /**
         * Tells the GUI to show an error
         *   when creating an item with no price associated
         */
        void showPriceError();

        /**
         * Tells the GUI to inform the user that this item is not unique.
         */
        void showDuplicateError();

        /**
         * Inform the View that the item was saved successfully
         */
        void savedSuccessfully();

    }

    interface Presenter {
        void start();
        void stop();

        /**
         * Send item to presenter to decide if this fragment will edit or create an item.
         * @param item {@link InventoryItem} as already provided to View.
         */
        void initItem(InventoryItem item);

        /**
         * Specifies the RFID tag to query in the database
         */
        void setRFIDTag(String s);

        /**
         * Saves an item.
         * <br>
         * Depending on if the item exists or not,
         * it will either edit the existing item or create a new one.
         * @param item The new {@link InventoryItem}
         * @param uri URI of the new image, or null if there is no new image.
         */
        void saveItem(InventoryItem item, Uri uri);
    }
}
