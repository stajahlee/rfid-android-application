package refried.rfid_inventory.util;

import android.content.Context;

/**
 * Repository object for the Android System context.
 *   This prevents the Presenter from knowing Android specific logic
 *   while allowing the TSL Bluetooth device to access system context.
 */
public class ContextRepository {
    private Context context;

    public ContextRepository(Context c) {
        context = c;
    }

    public Context getApplicationContext() {
        return context;
    }
}
