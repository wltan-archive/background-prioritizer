package sg.edu.nushigh.arp.backgroundprioritizer;

import android.app.Activity;
import android.os.Bundle;
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
    TextView android, uptime, imei, model, rooted, wifi, ipAddress, ipAddressDesc, macAddress, networkSsid, networkSsidDesc, bluetooth;
    ImageView image_wifi, image_bluetooth;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        v = inflater.inflate(R.layout.tab_3, container, false);

        android       = (TextView) v.findViewById(R.id.value_android);
        uptime        = (TextView) v.findViewById(R.id.value_uptime);
        imei          = (TextView) v.findViewById(R.id.value_imei);
        model         = (TextView) v.findViewById(R.id.value_model);
        rooted        = (TextView) v.findViewById(R.id.value_rooted);
        wifi          = (TextView) v.findViewById(R.id.value_wifi);
        image_wifi    = (ImageView) v.findViewById(R.id.image_wifi);
        ipAddress     = (TextView) v.findViewById(R.id.value_ip_address);
        ipAddressDesc = (TextView) v.findViewById(R.id.description_ip_address);
        macAddress    = (TextView) v.findViewById(R.id.value_mac_address);
        networkSsid   = (TextView) v.findViewById(R.id.value_network_ssid);
        networkSsidDesc=(TextView) v.findViewById(R.id.description_network_ssid);
        bluetooth     = (TextView) v.findViewById(R.id.value_bluetooth);
        image_bluetooth=(ImageView) v.findViewById(R.id.image_bluetooth);

        final SystemInfo si = new SystemInfo(getContext());

        android.setText(si.androidVersionCode() + " (" + si.androidVersionName() + ")");
        model.setText(si.brand() + " " + si.model());
        imei.setText(si.imei());
        rooted.setText(""); //TODO
        macAddress.setText(si.wifiMac());

        final Activity a = this.getActivity();

        Runnable update = new Runnable(){
            @Override
            public void run(){
                a.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uptime.setText(si.uptime());
                        wifi.setText(si.wifiOn() ? "On" : "Off");
                        image_wifi.setImageResource(si.wifiOn() ? R.drawable.ic_info_wifi : R.drawable.ic_info_wifi_off);
                        if(si.wifiConnected()){
                            ipAddress.setText(si.wifiIP()); //TODO
                            ipAddress.setVisibility(View.VISIBLE);
                            ipAddressDesc.setVisibility(View.VISIBLE);
                            networkSsid.setText(si.wifiSSID());
                            networkSsid.setVisibility(View.VISIBLE);
                            networkSsidDesc.setVisibility(View.VISIBLE);
                        }else{
                            ipAddress.setVisibility(View.GONE);
                            ipAddressDesc.setVisibility(View.GONE);
                            networkSsid.setVisibility(View.GONE);
                            networkSsidDesc.setVisibility(View.GONE);
                        }
                        bluetooth.setText(si.bluetoothOn() ? "On" : "Off");
                        image_bluetooth.setImageResource(si.bluetoothOn() ? R.drawable.ic_info_bluetooth : R.drawable.ic_info_bluetooth_off);
                    }
                });
            }
        };

        ScheduledExecutorService s = new ScheduledThreadPoolExecutor(10);
        s.scheduleAtFixedRate(update, 0, 1, TimeUnit.SECONDS);

        return v;
    }
}
