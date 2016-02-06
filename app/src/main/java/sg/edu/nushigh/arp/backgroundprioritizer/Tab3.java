package sg.edu.nushigh.arp.backgroundprioritizer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Tab3 extends Fragment {

    View v;
    TextView android, uptime, imei, model, rooted, wifi;
    ImageView image_wifi;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        v = inflater.inflate(R.layout.tab_3, container, false);

        final SystemInfo si = new SystemInfo(getContext());

        android = (TextView) v.findViewById(R.id.value_android);
        android.setText(si.androidVersionCode() + " (" + si.androidVersionName() + ")");
        uptime = (TextView) v.findViewById(R.id.value_uptime);
        imei = (TextView) v.findViewById(R.id.value_imei);
        imei.setText(si.imei());
        model = (TextView) v.findViewById(R.id.value_model);
        model.setText(si.brand() + " " + si.model());
        rooted = (TextView) v.findViewById(R.id.value_rooted);
        rooted.setText(""); //TODO
        wifi = (TextView) v.findViewById(R.id.value_wifi);
        image_wifi = (ImageView) v.findViewById(R.id.image_wifi);

        final Activity a = this.getActivity();

        Runnable update = new Runnable(){
            @Override
            public void run(){
                a.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uptime.setText(si.uptime());
                        wifi.setText(si.wifiConnected() ? "On" : "Off");
                        image_wifi.setImageResource(si.wifiConnected() ? R.drawable.ic_info_wifi : R.drawable.ic_info_wifi_off);
                    }
                });
            }
        };

        ScheduledExecutorService s = new ScheduledThreadPoolExecutor(10);
        s.scheduleAtFixedRate(update, 0, 1, TimeUnit.SECONDS);

        return v;
    }
}
