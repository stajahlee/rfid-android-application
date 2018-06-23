package refried.rfid_inventory.profilepage;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.Locale;

import refried.rfid_inventory.MainContentActivity;
import refried.rfid_inventory.R;
import refried.rfid_inventory.database.FirebaseDBInteractor;
import refried.rfid_inventory.database.InventoryItem;

/**
 * A Fragment to show a profile of an InventoryItem
 * Use the {@link ItemProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemProfileFragment extends Fragment
    implements ItemProfileContract.View {

    private static final String INVENTORY_ITEM = "Inventory Item";
    private static final String DIALOG_IMAGE = "image";

    private ItemProfileContract.Presenter mPresenter;

    // TODO: Rename and change types of parameters
    private InventoryItem mInventoryItem;
    private CollapsingToolbarLayout collapsingToolbar;
    private ViewHolder mViewHolder = new ViewHolder();

    public ItemProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Create a new instance of {@link ItemProfileFragment}
     * using the provided parameters.
     *
     * @param item The InventoryItem to display
     * @return A new instance of fragment ItemProfileFragment.
     */
    public static ItemProfileFragment newInstance(InventoryItem item) {
        ItemProfileFragment fragment = new ItemProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(INVENTORY_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mInventoryItem = getArguments().getParcelable(INVENTORY_ITEM);
        }
        mPresenter = new ItemProfilePresenter(this, new FirebaseDBInteractor());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_profile_parent_page, container, false);

        collapsingToolbar = rootView.findViewById(R.id.toolbar_layout);

        mViewHolder.profilePhoto = rootView.findViewById(R.id.profile_photo);

        mViewHolder.rfidTag = rootView.findViewById(R.id.rfid_tag_num);
        mViewHolder.rfidTag.getEditText().setEnabled(false);
        mViewHolder.location = rootView.findViewById(R.id.last_location);
        mViewHolder.location.getEditText().setEnabled(false);
        mViewHolder.serialNum = rootView.findViewById(R.id.serial_num);
        mViewHolder.serialNum.getEditText().setEnabled(false);
        mViewHolder.price = rootView.findViewById(R.id.original_price);
        mViewHolder.price.getEditText().setEnabled(false);
        mViewHolder.description = rootView.findViewById(R.id.item_description);
        mViewHolder.description.getEditText().setEnabled(false);

        mViewHolder.redTagged = rootView.findViewById(R.id.red_tag);
        mViewHolder.redTagged.setEnabled(false);
        mViewHolder.greenTagged = rootView.findViewById(R.id.green_tag);
        mViewHolder.greenTagged.setEnabled(false);
        mViewHolder.notTagged = rootView.findViewById(R.id.no_tag_color);
        mViewHolder.notTagged.setEnabled(false);
        mViewHolder.otherStatusDescription = rootView.findViewById(R.id.otherStatusDescription);
        mViewHolder.otherStatusDescription.getEditText().setEnabled(false);

        mViewHolder.activeStatus = rootView.findViewById(R.id.statusActive);
        mViewHolder.activeStatus.setEnabled(false);
        mViewHolder.surplusStatus = rootView.findViewById(R.id.statusSurplus);
        mViewHolder.surplusStatus.setEnabled(false);
        mViewHolder.otherStatus = rootView.findViewById(R.id.statusOther);
        mViewHolder.otherStatus.setEnabled(false);

        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        bind(mInventoryItem);
        editItem(mInventoryItem, rootView);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.getItem(mInventoryItem);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.stop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void bind(final InventoryItem inventoryItem) {
        // item name is in the collapsing toolbar rather than a TextView
        collapsingToolbar.setTitle(inventoryItem.getName());

        // pull in photo
        Glide.with(mViewHolder.profilePhoto.getContext())
                .load(inventoryItem.getPhotograph())
                .into(mViewHolder.profilePhoto);

        mViewHolder.profilePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity().getFragmentManager();
                PhotoEnlargeFragment.newInstance(mInventoryItem.getPhotograph())
                        .show(fm, DIALOG_IMAGE);
            }
        });

        mViewHolder.rfidTag.getEditText().setText(inventoryItem.getRfid_tag_num());
        mViewHolder.location.getEditText().setText(inventoryItem.getLast_location());
        mViewHolder.serialNum.getEditText().setText(inventoryItem.getSerial_num());
        mViewHolder.price.getEditText().setText(formatCurrency(inventoryItem.getOriginal_price()));
        mViewHolder.description.getEditText().setText(inventoryItem.getDescription());

        // pull in tag color
        if ("red".equals(inventoryItem.getTag_color())) {
            mViewHolder.redTagged.setChecked(true);
        }
        else if ("green".equals(inventoryItem.getTag_color())) {
            mViewHolder.greenTagged.setChecked(true);
        }
        else {
            mViewHolder.notTagged.setChecked(true);
        }

        // pull in status
        if ("active".equals(inventoryItem.getStatus())) {
            mViewHolder.activeStatus.setChecked(true);
            mViewHolder.otherStatusDescription.setVisibility(View.GONE);
//            mViewHolder.otherStatusDescription.setVisibility(LinearLayout.GONE);
        }
        else if ("surplus".equals(inventoryItem.getStatus())) {
            mViewHolder.surplusStatus.setChecked(true);
            mViewHolder.otherStatusDescription.setVisibility(View.GONE);
//            mViewHolder.otherStatusDescription.setVisibility(LinearLayout.GONE);
        }
        else {
            mViewHolder.otherStatus.setChecked(true);
            mViewHolder.otherStatusDescription.getEditText().setText(inventoryItem.getStatus());
        }
    }

    private void editItem(final InventoryItem item, View rootView){
        FloatingActionButton myFab = rootView.findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                transitionToCreateAndEdit(item);
            }
        });
    }

    private void transitionToCreateAndEdit(InventoryItem item) {
        getFragmentManager().beginTransaction()
                .addToBackStack(null)
                .hide(this)
                .add(R.id.drawer_layout, ItemCreateAndEditFragment.newInstance(item))
                .commit();
    }

    private String formatCurrency(String price) {
        try {
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
            Double priceNumber = Double.parseDouble(price);
            return numberFormat.format(priceNumber);
        } catch (Exception e) {
            Log.d("Currency format Exc", e.toString());
        }
        return price;
    }

    private class ViewHolder {
        ImageButton profilePhoto;

        // Text Views for Inventory Item properties
        TextInputLayout rfidTag;
        TextInputLayout location;
        TextInputLayout serialNum;
        TextInputLayout price;
        TextInputLayout description;

        // Radio buttons for tag status (red/green/none)
        RadioButton redTagged;
        RadioButton greenTagged;
        RadioButton notTagged;

        // Radio buttons for activity status (active/surplus/other)
        RadioButton activeStatus;
        RadioButton surplusStatus;
        RadioButton otherStatus;
        TextInputLayout otherStatusDescription;
    }
}