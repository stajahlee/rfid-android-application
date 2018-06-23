package refried.rfid_inventory.util;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import refried.rfid_inventory.R;
import refried.rfid_inventory.database.InventoryItem;

/**
 * Adapter for database items. May refactor to be less generic.
 */
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    /**
     * Manages the data that goes into each row of View screen (into card view).
     *
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;          // Item Name
        private TextView descTextView;   // Item Description (short)
        private ImageView pictureImageView;       // Picture of the Item
        private final View mView;
        private ImageView tagColorDot; // dot indicating what color tag
        private OnInventoryItemSelectedListener clickListener;

        public ViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;

            nameTextView = itemView.findViewById(R.id.item_name);
            descTextView = itemView.findViewById(R.id.short_description);
            pictureImageView = itemView.findViewById(R.id.itemPic);
            tagColorDot = itemView.findViewById(R.id.tag_color);
        }

        void bind (InventoryItem it, OnInventoryItemSelectedListener listener) {
            clickListener = listener;
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position >= 0) {
                        clickListener.onRVItemInteraction(position);
                    }
                }
            });

            nameTextView.setText(it.getName());

            String shortdesc = (it.getDescription());
            if (it.getDescription().length() > 35) {
                shortdesc = (it.getDescription().substring(0,34) + "...");
            }
            descTextView.setText(shortdesc);

            Glide.with(pictureImageView.getContext())
                    .load(it.getPhotograph())
                    .transform(new CircleTransform(getContext())) // applying the image transformer
                    .into(pictureImageView);

            if ("red".equals(it.getTag_color())) {
                tagColorDot.setColorFilter(Color.parseColor("#c90808")); // #c90808 == Red
            }
            else if ("green".equals(it.getTag_color())) {
                tagColorDot.setColorFilter(Color.parseColor("#198e1d")); // #198e1d == Green
            }
            else {
                tagColorDot.setColorFilter(Color.GRAY);
            }
        }
    }

    // Local Variables
    private ItemsAdapterCallbackListener mFragment;
    private List<InventoryItem> mItems;
    private OnInventoryItemSelectedListener mListener;

    // Constructor
    public ItemsAdapter(Fragment f, List<InventoryItem> items) {
        if(f instanceof ItemsAdapterCallbackListener) {
            mFragment = (ItemsAdapterCallbackListener) f;
        } else {
            throw new RuntimeException(f.toString()
                + " must implement ItemsAdapterCallbackListener");
        }
        mItems = items;
        mListener = new OnInventoryItemSelectedListener() {
            @Override
            public void onRVItemInteraction(int pos) {
                mFragment.transitionToProfileView(mItems.get(pos));
            }
        };
    }

    private Context getContext() {
        return ((Fragment)mFragment).getActivity();
    }

    @Override
    public ItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context c = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(c);

        View itemView = inflater.inflate(R.layout.card_item, parent, false);

        return (new ViewHolder(itemView));
    }

    @Override
    public void onBindViewHolder(ItemsAdapter.ViewHolder viewHolder, int pos) {
        InventoryItem it = mItems.get(pos);
        viewHolder.bind(it, mListener);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * This interface must be implemented by this Recycler View Adapter so that
     * {@link ItemsAdapter.ViewHolder }
     * can talk to it.
     */
    public interface OnInventoryItemSelectedListener {
        void onRVItemInteraction (int pos);
    }

    /**
     * This interface must be implemented by any any class that contains {@link ItemsAdapter}
     * so that it can interact with its parent container
     */
    public interface ItemsAdapterCallbackListener {
        void transitionToProfileView(InventoryItem itemToView);
    }
}
