package refried.rfid_inventory.viewinventory;

import java.util.List;

import refried.rfid_inventory.database.InventoryItem;

/**
 * Interface specification for all communications between Views and Presenters.
 */

public interface ViewTabContract {
    interface View {
        /**
         * Informs the view that the backing data model has changed,
         * and it should re-calculate layouts
         */
        void refreshData();
    }
    interface Presenter {
        /**
         * Informs the presenter that it should begin gathering data
         */
        void start();

        /**
         * Informs the presenter that it should stop gathering data and updating the view
         */
        void stop();

        /**
         * Gets reference to View backing data
         * @return reference to data to display to the user
         */
        List<InventoryItem> initializeData();

        /**
         * Informs the View to filter names of items
         * @param query an exact string match from the start of the name field
         */
        void nameQuery(String query);

        /**
         *
         * @param sortBy
         * @param filterByTag
         * @param filterByPriceMin
         * @param filterByPriceMax
         * @param filterByLoc
         */
        void sortByCriteria(String sortBy, InventoryItem.TagColors filterByTag,
                            Integer filterByPriceMin, Integer filterByPriceMax,
                            String filterByLoc);

        /**
         * Obtains the current filtering data for the View to display/initialize with
         * @return String array-of-three.
         * <br>
         *     ret[0] = String defining what kind of sort to use; <br>
         *     ret[1] = String defining the Item Tag Color; <br>
         *     ret[2] = String defining the location prefix;
         */
        String[] getCurrentFilterData();
    }
}
