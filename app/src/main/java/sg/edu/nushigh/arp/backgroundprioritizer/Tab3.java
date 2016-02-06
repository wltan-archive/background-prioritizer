package sg.edu.nushigh.arp.backgroundprioritizer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Tab3 extends Fragment {

    View v;
    TextView android;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        v = inflater.inflate(R.layout.tab_3, container, false);

        SystemInfo si = new SystemInfo(getContext());

        android = (TextView) v.findViewById(R.id.value_android);
        android.setText(si.androidVersionCode() + " (" + si.androidVersionName() + ")");

        return v;
    }
}
