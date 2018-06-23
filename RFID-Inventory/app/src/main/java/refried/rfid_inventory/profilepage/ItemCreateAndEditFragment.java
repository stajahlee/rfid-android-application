package refried.rfid_inventory.profilepage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import refried.rfid_inventory.MainContentActivity;
import refried.rfid_inventory.R;
import refried.rfid_inventory.database.FirebaseDBInteractor;
import refried.rfid_inventory.database.InventoryItem;
import refried.rfid_inventory.util.CircleTransform;

/**
 * Fragment to create or edit an InventoryItem.
 * Use the {@link ItemCreateAndEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemCreateAndEditFragment extends Fragment implements ItemCreateAndEditContract.View {
    public static final int PICK_IMAGE = 1;
    private static final String INVENTORY_ITEM = "Inventory Item";

    private ItemCreateAndEditContract.Presenter mPresenter;

    private InventoryItem mInventoryItem;

    private ViewHolder mViewHolder = new ViewHolder();

    private Uri selectedImageUri = null;
    private String uniqueId, photoURL, tagColor;
    private String status = "other";

    public ItemCreateAndEditFragment() {
        // Required empty public constructor
    }

    /**
     * Fragment Factory Method to configure a new {@link ItemCreateAndEditFragment}
     * @param item InventoryItem
     * @return A new instance of fragment ItemCreateAndEditFragment.
     */
    public static ItemCreateAndEditFragment newInstance(InventoryItem item) {
        ItemCreateAndEditFragment fragment = new ItemCreateAndEditFragment();
        Bundle args = new Bundle();
        args.putParcelable(INVENTORY_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ItemCreateAndEditPresenter(this, new FirebaseDBInteractor());
        if (getArguments() != null) {
            mInventoryItem = getArguments().getParcelable(INVENTORY_ITEM);
        }
        mPresenter.initItem(mInventoryItem);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.create_and_edit_item_page2, container, false);

        Button cancelButton = rootView.findViewById(R.id.cancel_button);
        Button saveButton = rootView.findViewById(R.id.save_button);

        mViewHolder.profilePhoto = rootView.findViewById(R.id.photo_image_button);
        mViewHolder.itemName = rootView.findViewById(R.id.item_name_edit);

        mViewHolder.rfidTag = rootView.findViewById(R.id.rfid_tag_num);
        mViewHolder.location = rootView.findViewById(R.id.last_location);
        mViewHolder.serialNum = rootView.findViewById(R.id.serial_num);
        mViewHolder.price = rootView.findViewById(R.id.original_price);
        mViewHolder.description = rootView.findViewById(R.id.item_description);

        mViewHolder.radioButtons = rootView.findViewById(R.id.roleRadioGroup);
        mViewHolder.redTagged = rootView.findViewById(R.id.red_tag);
        mViewHolder.greenTagged = rootView.findViewById(R.id.green_tag);
        mViewHolder.notTagged = rootView.findViewById(R.id.no_tag_color);

        mViewHolder.statusRadioButtons = rootView.findViewById(R.id.statusRadioGroup);
        mViewHolder.activeStatus = rootView.findViewById(R.id.statusActive);
        mViewHolder.surplusStatus = rootView.findViewById(R.id.statusSurplus);
        mViewHolder.otherStatus = rootView.findViewById(R.id.statusOther);
        mViewHolder.otherStatusDescription = rootView.findViewById(R.id.otherStatusDescription);

        mViewHolder.otherStatusDescription.setVisibility(View.GONE);

        mViewHolder.rfidTag.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPresenter.setRFIDTag(editable.toString());
            }
        });

        mViewHolder.itemName.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b && mViewHolder.itemName.isErrorEnabled()) {
                    mViewHolder.itemName.setErrorEnabled(false);
                }
            }
        });

        mViewHolder.rfidTag.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b && mViewHolder.rfidTag.isErrorEnabled()) {
                    mViewHolder.rfidTag.setErrorEnabled(false);
                }
            }
        });

        mViewHolder.description.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b && mViewHolder.description.isErrorEnabled()) {
                    mViewHolder.description.setErrorEnabled(false);
                }
            }
        });

        mViewHolder.price.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b && mViewHolder.price.isErrorEnabled()) {
                    mViewHolder.price.setErrorEnabled(false);
                }
            }
        });

        mViewHolder.profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();
            }
        });

        mViewHolder.radioButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = mViewHolder.radioButtons.getCheckedRadioButtonId();
                switch (id) {
                    case R.id.green_tag:
                        tagColor = "green";
                        break;
                    case R.id.red_tag:
                        tagColor = "red";
                        break;
                    default:
                        tagColor = "none";
                        break;
                }
            }
        });

        mViewHolder.statusRadioButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = mViewHolder.statusRadioButtons.getCheckedRadioButtonId();
                switch (id) {
                    case R.id.statusActive:
                        mViewHolder.otherStatusDescription.setVisibility(View.GONE);
                        status = "active";
                        break;
                    case R.id.statusSurplus:
                        mViewHolder.otherStatusDescription.setVisibility(View.GONE);
                        status = "surplus";
                        break;
                    case R.id.statusOther:
                        mViewHolder.otherStatusDescription.setVisibility(View.VISIBLE);
                        break;
                    default:
                        status = "other";
                        break;
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.saveItem(makeItem(), selectedImageUri);
                hideKeyboard(getActivity());
            }
        });

        bindItem(mInventoryItem);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.stop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data.getData();
            photoURL = selectedImageUri.toString();
            Log.d("URI", photoURL);
            bindImage();
        }
    }

    @Override
    public void bindItem(final InventoryItem inventoryItem) {
        photoURL = inventoryItem.getPhotograph();
        bindImage();

        mViewHolder.itemName.getEditText().setText(inventoryItem.getName());
        mViewHolder.rfidTag.getEditText().setText(inventoryItem.getRfid_tag_num());
        mViewHolder.location.getEditText().setText(inventoryItem.getLast_location());
        mViewHolder.serialNum.getEditText().setText(inventoryItem.getSerial_num());
        mViewHolder.price.getEditText().setText(inventoryItem.getOriginal_price());
        mViewHolder.description.getEditText().setText(inventoryItem.getDescription());
        uniqueId = inventoryItem.getUnique_id();

        if ("red".equals(inventoryItem.getTag_color())) {
            mViewHolder.redTagged.setChecked(true);
            tagColor = "red";
        }
        else if ("green".equals(inventoryItem.getTag_color())) {
            mViewHolder.greenTagged.setChecked(true);
            tagColor = "green";
        }
        else {
            mViewHolder.notTagged.setChecked(true);
            tagColor = "none";
        }
        mViewHolder.description.getEditText().setText(inventoryItem.getDescription());
        uniqueId = inventoryItem.getUnique_id();

        if ("active".equals(inventoryItem.getStatus())) {
            mViewHolder.activeStatus.setChecked(true);
            status = "active";

        }
        else if ("surplus".equals(inventoryItem.getStatus())) {
            mViewHolder.surplusStatus.setChecked(true);
            status = "surplus";
        }
        else {
            mViewHolder.otherStatusDescription.setVisibility(View.VISIBLE);
            mViewHolder.otherStatus.setChecked(true);
            mViewHolder.otherStatusDescription.getEditText().setText(inventoryItem.getStatus());
            status = "other";
        }
    }

    private void bindImage() {
        Glide.with(this)
                .load(photoURL)
//                .diskCacheStrategy(DiskCacheStrategy.NONE) // Do some devices need this too? See below
                .skipMemoryCache(true)
                .transform(new CircleTransform(getActivity())) // applying the image transformer
                .into(mViewHolder.profilePhoto);
    }

    private InventoryItem makeItem() {
        if (mViewHolder.otherStatus.isChecked()) {
            if (mViewHolder.otherStatusDescription.getEditText().getText().toString().trim().length() == 0) {
                status = "other";
            } else {
                status = mViewHolder.otherStatusDescription.getEditText().getText().toString();
            }
        }
        return new InventoryItem.Builder()
                .named(mViewHolder.itemName.getEditText().getText().toString())
                .withRFIDTag(mViewHolder.rfidTag.getEditText().getText().toString())
                .withDescription(mViewHolder.description.getEditText().getText().toString())
                .withSerialNum(mViewHolder.serialNum.getEditText().getText().toString())
                .withPrice(mViewHolder.price.getEditText().getText().toString())
                .location(mViewHolder.location.getEditText().getText().toString())
                .withTagColor(tagColor)
                .withPhotographURL(photoURL)
                .withUniqueID(uniqueId)
                .withStatus(status)
                .withNameQueryable(mViewHolder.itemName.getEditText().getText().toString().toLowerCase())
                .build();
    }

    @Override
    public void showNameError() {
        mViewHolder.itemName.setErrorEnabled(true);
        mViewHolder.itemName.setError("Item name cannot be empty!");
    }

    @Override
    public void showDescError() {
        mViewHolder.description.setErrorEnabled(true);
        mViewHolder.description.setError("Item description cannot be empty!");
    }

    @Override
    public void showPriceError() {
        mViewHolder.price.setErrorEnabled(true);
        mViewHolder.price.setError("Item price is not a valid format!");
    }

    @Override
    public void showDuplicateError() {
        mViewHolder.rfidTag.setErrorEnabled(true);
        mViewHolder.rfidTag.setError("This RFID Tag matches another item!");
        Log.d("ERROR", "This item is a duplicate of another item!");
    }

    @Override
    public void savedSuccessfully() {
        Toast.makeText(getActivity(), "Inventory item updated.", Toast.LENGTH_SHORT).show();
        getFragmentManager().popBackStack();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private class ViewHolder {
        ImageButton profilePhoto;
        TextInputLayout itemName;

        // Text Views for Inventory Item properties
        TextInputLayout rfidTag;
        TextInputLayout location;
        TextInputLayout serialNum;
        TextInputLayout price;
        TextInputLayout description;

        // Radio buttons for tag status (red/green/none)
        RadioGroup radioButtons;
        RadioButton redTagged;
        RadioButton greenTagged;
        RadioButton notTagged;

        // Radio buttons for activity status (active/surplus/other)
        RadioGroup statusRadioButtons;
        RadioButton activeStatus;
        RadioButton surplusStatus;
        RadioButton otherStatus;
        TextInputLayout otherStatusDescription;
    }
}