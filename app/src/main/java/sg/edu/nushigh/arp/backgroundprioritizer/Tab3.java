package sg.edu.nushigh.arp.backgroundprioritizer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Tab3 extends Fragment {

    View v;
    TextView android;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        v = inflater.inflate(R.layout.tab_3, container, false);

        SystemInfo si = new SystemInfo(getContext());

        android = (TextView) v.findViewById(R.id.value_android);
        android.setText(si.androidVersionCode() + " (" + si.androidVersionName() + ")");

        Runnable update = new Runnable(){
            @Override
            public void run(){
                // update stuff here
                // remember that UI things have to be declared final before accessing here (for some reason)
            }
        };

        ScheduledExecutorService s = new ScheduledThreadPoolExecutor(10);
        s.scheduleAtFixedRate(update, 0, 1, TimeUnit.SECONDS);


        return v;
    }
}
