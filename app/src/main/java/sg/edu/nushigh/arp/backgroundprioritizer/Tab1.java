package sg.edu.nushigh.arp.backgroundprioritizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

public class Tab1 extends Fragment {

    View v;
    Button toggle;
    NumberPicker priopick;
    EditText polltime;
    static boolean on;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        v = inflater.inflate(R.layout.tab_1, container, false);
        toggle = (Button) v.findViewById(R.id.onoff);
        priopick = (NumberPicker) v.findViewById(R.id.priopick);
        polltime = (EditText) v.findViewById(R.id.polltime);

        priopick.setMaxValue(15);
        priopick.setMinValue(1);
        priopick.setValue(PriorityChangeService.NICE_SHIFT);

        toggle.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                toggleService(v);
            }
        });

        polltime.setText("" + PriorityChangeService.POLL_TIME);
        on = (PriorityChangeService.instance != null);
        toggle.setText(on?"Deactivate Service":"Activate Service");
        return v;
    }

    private static final int MIN_POLLTIME = 1000;

    public void toggleService(View view){
        if(!on){
            Toast.makeText(getActivity().getApplicationContext(), "Starting service...", Toast.LENGTH_SHORT).show();

            int pollt = Integer.parseInt(polltime.getText().toString());
            if(pollt < MIN_POLLTIME){
                pollt = MIN_POLLTIME;
                Toast.makeText(getActivity().getApplicationContext(), "Poll time must be at least " + MIN_POLLTIME + "ms", Toast.LENGTH_SHORT).show();
                polltime.setText(""+pollt);
            }

            PriorityChangeService.NICE_SHIFT = priopick.getValue();
            PriorityChangeService.POLL_TIME = pollt;
            getActivity().startService(new Intent(getActivity(), PriorityChangeService.class));
            Log.i("priority change", "service started!");
            toggle.setText("Deactivate Service");
            priopick.setEnabled(false);
            polltime.setEnabled(false);
        }else{
            Toast.makeText(getActivity().getApplicationContext(), "Stopping service...", Toast.LENGTH_SHORT).show();
            getActivity().stopService(new Intent(getActivity(), PriorityChangeService.class));
            Log.i("priority change", "service stopped!");
            toggle.setText("Activate Service");
            priopick.setEnabled(true);
            polltime.setEnabled(true);
        }
        on = !on;
    }
}
