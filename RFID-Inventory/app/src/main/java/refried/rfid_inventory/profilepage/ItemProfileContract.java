package refried.rfid_inventory.profilepage;

import refried.rfid_inventory.database.InventoryItem;


public interface ItemProfileContract {
    interface View {
        /**
         * Provides an item to display
         * @param item Item fetched from database
         */
        void bind(InventoryItem item);
    }

    interface Presenter {
        void stop();
        void getItem(InventoryItem item);
    }
}
