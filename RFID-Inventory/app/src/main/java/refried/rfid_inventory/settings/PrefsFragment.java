package refried.rfid_inventory.settings;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import refried.rfid_inventory.R;

/**
 * Fragment to declare and manage preferences for the application.
 */
public class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Settings");

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        // Begin preference configuration (callbacks and listeners)
        Preference myPref = findPreference("connectKey");
        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DevicePickerFragment nextFragment = DevicePickerFragment.newInstance();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                nextFragment.show(transaction, null);
                return true;
            }
        });
    }
}
