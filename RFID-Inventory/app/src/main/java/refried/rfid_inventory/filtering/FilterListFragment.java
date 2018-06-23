package refried.rfid_inventory.filtering;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import refried.rfid_inventory.R;
import refried.rfid_inventory.database.InventoryItem;
import refried.rfid_inventory.viewinventory.ViewTabContract;

public class FilterListFragment extends Fragment {

    private static ViewTabContract.Presenter mPresenter;

    private Button alphaSortButton, priceSortButton, applyButton, clearFilters;
    private RadioButton radioButtonRedTag, radioButtonGreenTag, radioButtonNoTagColor;
    TextInputEditText minPriceEditText, maxPriceEditText;
    TextInputLayout minPriceLayout, maxPriceLayout;
    Spinner spinner;
    private String sortBy = "alpha";
    private InventoryItem.TagColors filterByTag = InventoryItem.TagColors.ANY;
    private String filterByLocation = "Any Location";
    private Integer minPrice = 0;
    private Integer maxPrice = 500000;


    public FilterListFragment() {
        // Required empty public constructor
    }

    /**
     * Create a new instance of {@link FilterListFragment}
     * using the provided parameters.
     *
     * @return A new instance of fragment FilterListFragment.
     */
    public static FilterListFragment newInstance(ViewTabContract.Presenter p) {
        FilterListFragment fragment = new FilterListFragment();
        mPresenter = p;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        String[] data = mPresenter.getCurrentFilterData();
        sortBy = (data[0] == null ? "alpha" : data[0]);

        if(data[1].equalsIgnoreCase("red")) {
            filterByTag = InventoryItem.TagColors.RED;
        } else if(data[1].equalsIgnoreCase("green")) {
            filterByTag = InventoryItem.TagColors.GREEN;
        } else if(data[1].equalsIgnoreCase("none")) {
            filterByTag = InventoryItem.TagColors.NONE;
        } else {
            filterByTag = InventoryItem.TagColors.ANY;
        }

        filterByLocation = data[2];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_filtering, container, false);

        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            getActivity().setActionBar(toolbar);
        }

        alphaSortButton = rootView.findViewById(R.id.alphaSort);
        priceSortButton = rootView.findViewById(R.id.priceSort);
        radioButtonGreenTag = rootView.findViewById(R.id.green_tag);
        radioButtonRedTag = rootView.findViewById(R.id.red_tag);
        radioButtonNoTagColor = rootView.findViewById(R.id.no_tag_color);
        minPriceEditText = rootView.findViewById(R.id.price_min_edittext);
        maxPriceEditText = rootView.findViewById(R.id.price_max_edittext);
        minPriceLayout = rootView.findViewById(R.id.price_min_layout);
        maxPriceLayout = rootView.findViewById(R.id.price_max_layout);
        spinner = rootView.findViewById(R.id.spinner);
        applyButton = rootView.findViewById(R.id.applyButton);
        clearFilters = rootView.findViewById(R.id.clearFilters);

        switch(filterByTag) {
            case RED:
                radioButtonRedTag.setChecked(true);
                break;
            case GREEN:
                radioButtonGreenTag.setChecked(true);
                break;
            case NONE:
                radioButtonNoTagColor.setChecked(true);
                break;
            case ANY:
            default:
                break;
        }

        if (sortBy.equalsIgnoreCase("alpha")) {
            alphaSortButton.setBackgroundColor(0xFF1e6da0);  //teal
            alphaSortButton.setTextColor(0xFFFFFFFF);  // white
            priceSortButton.setBackgroundColor(0xFFEDF0F2);  //gray
            priceSortButton.setTextColor(0xFF000000);  // black
        } else if (sortBy.equalsIgnoreCase("price")) {
            priceSortButton.setBackgroundColor(0xFF1e6da0);  // teal
            priceSortButton.setTextColor(0xFFFFFFFF);  // white
            alphaSortButton.setBackgroundColor(0xFFEDF0F2);  // gray
            alphaSortButton.setTextColor(0xFF000000);  // black
        }

        alphaSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alphaSortButton.setBackgroundColor(0xFF1e6da0);  //teal
                alphaSortButton.setTextColor(0xFFFFFFFF);  // white
                priceSortButton.setBackgroundColor(0xFFEDF0F2);  //gray
                priceSortButton.setTextColor(0xFF000000);  // black
                sortBy = "alpha";
            }
        });

        priceSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priceSortButton.setBackgroundColor(0xFF1e6da0);  // teal
                priceSortButton.setTextColor(0xFFFFFFFF);  // white
                alphaSortButton.setBackgroundColor(0xFFEDF0F2);  // gray
                alphaSortButton.setTextColor(0xFF000000);  // black
                sortBy = "price";
            }
        });

        radioButtonRedTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterByTag = InventoryItem.TagColors.RED;
            }
        });

        radioButtonGreenTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterByTag = InventoryItem.TagColors.GREEN;
            }
        });

        radioButtonNoTagColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterByTag = InventoryItem.TagColors.NONE;
            }
        });

        minPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if ((Integer.parseInt(s.toString())) < 0) {
                        minPriceLayout.setError("Minimum price must be at least 0.");
                    } else if ((Integer.parseInt(s.toString())) > 500000) {
                        minPriceLayout.setError("Minimum price must be $500,000 or less.");
                    } else {
                        minPriceLayout.setError(null);
                        minPrice = (Integer.parseInt(s.toString()));
                    }
                } catch (Exception e) {
                    Log.d("Minimum price error", e.toString());
                }
            }
        });

        maxPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if ((Integer.parseInt(s.toString())) > 500000) {
                        maxPriceLayout.setError("Maximum price must be $500,000 or less.");
                    } else if ((Integer.parseInt(s.toString())) < 0) {
                        maxPriceLayout.setError("Maximum price must be at least 0.");
                    } else {
                        maxPriceLayout.setError(null);
                        maxPrice = (Integer.parseInt(s.toString()));
                    }
                } catch (Exception e) {
                    Log.d("Minimum price error", e.toString());
                }


            }
        });

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        // Spinner Drop Down for Location filtering
        List<String> locations = new ArrayList<>();
        locations.add("Any Location"); // position 0
        locations.add("Stocker");
        locations.add("ARC");
        locations.add("Bentley Hall");
        locations.add("Morton");      // -> position 4

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, locations);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        if (filterByLocation != null) {
            Log.d("test", filterByLocation);
            int spinnerPosition = dataAdapter.getPosition(filterByLocation);
            spinner.setSelection(spinnerPosition);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                switch (position) {
                    case 1: filterByLocation = "Stocker";
                            break;
                    case 2: filterByLocation = "ARC";
                        break;
                    case 3: filterByLocation = "Bentley Hall";
                        break;
                    case 4: filterByLocation = "Morton";
                        break;
                    default: filterByLocation = "Any Location";
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.sortByCriteria(sortBy, filterByTag, minPrice, maxPrice, filterByLocation);
                getFragmentManager().popBackStack();
            }
        });

        clearFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.sortByCriteria("alpha", InventoryItem.TagColors.ANY, 0, 500000, "Any Location");
                getFragmentManager().popBackStack();
            }
        });

        return rootView;
    }
}
