package refried.rfid_inventory.viewinventory;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import refried.rfid_inventory.R;
import refried.rfid_inventory.database.FirebaseDBInteractor;
import refried.rfid_inventory.database.InventoryItem;
import refried.rfid_inventory.filtering.FilterListFragment;
import refried.rfid_inventory.profilepage.ItemCreateAndEditFragment;
import refried.rfid_inventory.profilepage.ItemProfileFragment;
import refried.rfid_inventory.util.ItemsAdapter;

/**
 * The Tab Fragment for users to view the current contents of the database.
 *   Uses a Recycler View as the list view.
 *   Does not require any special permissions.
 */
public class ViewPage extends Fragment implements ViewTabContract.View,
        ItemsAdapter.ItemsAdapterCallbackListener {

    RecyclerView rvItems;
    ItemsAdapter rvItemsAdapter;
    SearchView mSearchView;
    ViewTabContract.Presenter mPresenter;
    FloatingActionButton mFab;
    ImageButton mfilterButton;

    public static ViewPage newInstance() {
        ViewPage fragment = new ViewPage();
        return fragment;
    }

    public ViewPage() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onAttach(Context c) {
        super.onAttach(c);
        mPresenter = new ViewTabPresenter(this, new FirebaseDBInteractor());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        rvItems = rootView.findViewById(R.id.rvItems);

        mSearchView = rootView.findViewById(R.id.viewTabSearchView);

        mSearchView.setIconifiedByDefault(false);
        // Expanded view always

        mSearchView.setSubmitButtonEnabled(true);
        // Make submit button

        mSearchView.setBackgroundColor(0x201703CA);
        // Set the background color of search bar

        mSearchView.clearFocus();

        rvItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvItemsAdapter = new ItemsAdapter(this, mPresenter.initializeData());
        rvItems.setAdapter(rvItemsAdapter);
        mFab = rootView.findViewById(R.id.myfab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionToItemCreateAndEditFragment();
            }
        });

        mfilterButton = rootView.findViewById(R.id.filter_button);
        mfilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionToFilteringView();
            }
        });
        getActivity().setTitle("View Inventory");
        return rootView;
    }

    @Override
    public void onStart() {
        mPresenter.start();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.nameQuery(null);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mPresenter.nameQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Maybe use this; maybe not.

                //reset query when the search bar is empty
                if (newText.equals("")){
                    mPresenter.nameQuery(null);
                }
                return true;
            }
        });
    }

    @Override
    public void onStop() {
        mPresenter.stop();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        rvItems = null;
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        mPresenter = null;
        super.onDetach();
    }

    @Override
    public void refreshData() {
        rvItemsAdapter.notifyDataSetChanged();
//        if (rvItemsAdapter.getItemCount() == 0) {
//            Snackbar.make(getActivity().findViewById(android.R.id.content),
//                    "No matching items found. Please adjust filters.", Snackbar.LENGTH_LONG).show();
//        }
    }

    @Override
    public void transitionToProfileView(InventoryItem itemToView) {
        getFragmentManager().beginTransaction()
                .addToBackStack(null)
                .hide(this)
                .add(R.id.drawer_layout, ItemProfileFragment.newInstance(itemToView))
                .commit();
    }

    private void transitionToItemCreateAndEditFragment() {
        getFragmentManager().beginTransaction()
                .addToBackStack(null)
                .hide(this)
                .add(R.id.drawer_layout, ItemCreateAndEditFragment.newInstance(new InventoryItem()))
                .commit();
    }


    public void transitionToFilteringView() {
        getFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.content_frame, FilterListFragment.newInstance(mPresenter))
                .commit();
    }
}
