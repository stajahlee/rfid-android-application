package refried.rfid_inventory.profilepage;

import android.app.DialogFragment;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import refried.rfid_inventory.util.PictureUtils;

/**
 * A fragment to enlarge a photo as a dialog.
 */
public class PhotoEnlargeFragment extends DialogFragment {

    public static final String PHOTO_URL = "url";

    public static PhotoEnlargeFragment newInstance(String photoUrl) {
        Bundle args = new Bundle();
        args.putSerializable(PHOTO_URL, photoUrl);

        PhotoEnlargeFragment fragment = new PhotoEnlargeFragment();
        fragment.setArguments(args);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        return fragment;
    }

    private ImageView mImageView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup parent, Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mImageView = new ImageView(getActivity());
        String url = (String)getArguments().getSerializable(PHOTO_URL);
        BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(), url);

        mImageView.setImageDrawable(image);

        return mImageView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PictureUtils.cleanImageView(mImageView);
    }

}
