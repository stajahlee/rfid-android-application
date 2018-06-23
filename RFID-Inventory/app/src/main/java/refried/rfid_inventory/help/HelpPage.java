package refried.rfid_inventory.help;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import refried.rfid_inventory.R;

public class HelpPage extends Fragment {

    View myview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Help");

        myview = inflater.inflate(R.layout.help_layout,container,false);
        return myview;

    }

}
