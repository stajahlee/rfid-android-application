package refried.rfid_inventory.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;

/**
 * Class to assist in using pictures
 * using source:
 * https://github.com/tkunstek/android-big-nerd-ranch/blob/master/20_CameraImage_CriminalIntent/src/com/bignerdranch/android/criminalintent/PictureUtils.java
 * for reference and following book as well.
 */
public class PictureUtils {
    /**
     * Get a BitmapDrawable from a local file that is scaled down
     * to fit the current Window size.
     */
    @SuppressWarnings("deprecation")
    public static BitmapDrawable getScaledDrawable(Activity a, String path) {

        Bitmap bitmap = getBitmapFromURL(path);

        return new BitmapDrawable(a.getResources(), bitmap);
    }

    private static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            return BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            System.out.println(e);
            return null;
        }

    }


    public static void cleanImageView(ImageView imageView) {
        if (!(imageView.getDrawable() instanceof BitmapDrawable))
            return;

        // clean up the view's image for the sake of memory
        BitmapDrawable b = (BitmapDrawable)imageView.getDrawable();
        b.getBitmap().recycle();
        imageView.setImageDrawable(null);
    }
}
