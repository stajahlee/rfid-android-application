package refried.rfid_inventory.viewinventory;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import refried.rfid_inventory.database.FirebaseDBContract;
import refried.rfid_inventory.database.InventoryItem;
import refried.rfid_inventory.database.DBQuery;
import refried.rfid_inventory.database.FirebaseItemQuery;

/**
 * Presenter for ViewPage.java
 *   Handles logic for Viewing and forwards queries to the database.
 */
public class ViewTabPresenter implements ViewTabContract.Presenter, DBQuery.QueryObserver<InventoryItem> {

    // Variables
    private ViewTabContract.View mView;
    private FirebaseDBContract mModel;

    private FirebaseItemQuery mCurrentQuery;
    private final List<InventoryItem> mItems;

    private String mSortBy;
    private String mFilterByLoc;
    private InventoryItem.TagColors mFilterByTag = InventoryItem.TagColors.ANY;
    private Integer mFilterByPriceMin, mFilterByPriceMax;

    // Constructors
    public ViewTabPresenter( @NonNull ViewTabContract.View v, @NonNull FirebaseDBContract m) {
        mView = v;
        mModel = m;
        mItems = new ArrayList<>();
    }

    // ViewTabContract.Presenter Interface
    @Override
    public void start() {
        mItems.clear();
        if(mCurrentQuery == null) {
            mCurrentQuery = new FirebaseItemQuery(this);
        }
        mModel.sendQuery(mCurrentQuery);
    }

    @Override
    public void stop() {
        mModel.removeQuery();
    }

    @Override
    public List<InventoryItem> initializeData() {
        return mItems;
    }

    @Override
    public void nameQuery(String query) {
        mModel.removeQuery();
        mItems.clear();
        mView.refreshData();
        if(query != null) {
            mCurrentQuery = new FirebaseItemQuery(this, InventoryItem.Field.NAME_QUERYABLE, DBQuery.FilteringMethod.START_AT, query);
        } else {
            mModel.sendQuery(new FirebaseItemQuery(this));
        }
        mModel.sendQuery(mCurrentQuery);
    }

    @Override
    public void sortByCriteria(String sortBy, InventoryItem.TagColors filterByTag,
                               Integer filterByPriceMin, Integer filterByPriceMax,
                               String filterByLoc) {
         mSortBy = sortBy;


         mFilterByTag = filterByTag;

         mFilterByPriceMin = filterByPriceMin;
         mFilterByPriceMax = filterByPriceMax;
         mFilterByLoc = filterByLoc;
    }

    @Override
    public void onDataFound(InventoryItem item) {
        boolean changed = false;

        // Filter by Tag
        switch(mFilterByTag) {
            default:
                if(!item.getTag_color().equalsIgnoreCase(mFilterByTag.getTag())) {
                    return;
                }
            case ANY:
                break;
        }

        // Filter by Price
        Double originalPrice = 0.0;
        try {
            originalPrice = Double.parseDouble(item.getOriginal_price());
        } catch(Exception e) {
            Log.d("ERROR", "The Item \'" + item.getName() + "\' has an invalid price");
        }

        if (mFilterByPriceMin!=null
            && originalPrice < mFilterByPriceMin.doubleValue()) {
            return;
        }
        if (mFilterByPriceMax != null
            && originalPrice > mFilterByPriceMax.doubleValue()) {
            return;
        }

        // Filter by Location
        if (mFilterByLoc!=null && !mFilterByLoc.equalsIgnoreCase("Any Location")) {
            if(item.getLast_location() != null
                && !item.getLast_location().toLowerCase().contains(mFilterByLoc.toLowerCase())) {
                return;
            }
        }

        if(!mItems.contains(item)) {
            mItems.add(item);
            changed = true;
        }

        // Now Sort what is left
        if(mSortBy != null && mSortBy.equalsIgnoreCase("alpha")) {
            Collections.sort(mItems, new alphaComparator());
        } else if (mSortBy != null && mSortBy.equalsIgnoreCase("price")){
            Collections.sort(mItems, new priceComparator());
        } else {
            Collections.sort(mItems, new alphaComparator());
        }

        if(changed) {
            mView.refreshData();
        }
    }

    static class alphaComparator implements Comparator<InventoryItem>
    {
        @Override
        public int compare(InventoryItem lhs, InventoryItem rhs) {
            return lhs.getName_queryable().compareTo(rhs.getName_queryable());
        }
    }

    static class priceComparator implements Comparator<InventoryItem> {
        @Override
        public int compare(InventoryItem lhs, InventoryItem rhs) {
            try {
                Double lhsPrice = Double.parseDouble(lhs.getOriginal_price());
                Double rhsPrice = Double.parseDouble(rhs.getOriginal_price());
                return Double.compare(lhsPrice, rhsPrice);
            } catch (NumberFormatException e) {
                Log.d("Price Sort Error", e.toString());
            }
            return lhs.getOriginal_price().compareTo(rhs.getOriginal_price());
        }
    }

    @Override
    public String[] getCurrentFilterData() {
        String[] str = new String[3];
        str[0] = mSortBy;
        str[1] = mFilterByTag.getTag();
        str[2] = mFilterByLoc;
        return str;
    }

}
