package refried.rfid_inventory.settings;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import refried.rfid_inventory.R;
import refried.rfid_inventory.bluetooth.BTContract;
import refried.rfid_inventory.util.BTDevice;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link BTDevice} and makes a call to the
 * specified {@link OnDeviceSelectedListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class BTDeviceRecyclerViewAdapter extends RecyclerView.Adapter<BTDeviceRecyclerViewAdapter.ViewHolder> {

    private final List<BTDevice> mValues;
    private final OnDeviceSelectedListener mListener;
    private BTContract.Presenter mPresenter;

    public BTDeviceRecyclerViewAdapter(List<BTDevice> items, BTContract.Presenter presenter) {
        mValues = items;
        mPresenter = presenter;
        mListener = new OnDeviceSelectedListener() {
            @Override
            public void onRVItemInteraction(int pos) {
                mPresenter.pairToDevice(mValues.get(pos));
            }
        };
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_btdevice_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bind(mValues.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public BTDevice mItem;
        public OnDeviceSelectedListener mListener;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.id);
            mContentView = view.findViewById(R.id.content);
        }

        public void bind(BTDevice item, OnDeviceSelectedListener listener) {
            mListener = listener;

            mIdView.setText(item.name);
            mContentView.setText(item.address);
            mItem = item;
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position >= 0) {
                        mListener.onRVItemInteraction(position);
                    }
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    /**
     * This interface must be implemented by this Recycler View Adapter so that
     * { @Link BTDeviceRecyclerViewAdapter.ViewHolder }
     * can talk to it.
     */
    public interface OnDeviceSelectedListener {
        void onRVItemInteraction(int pos);
    }

}
