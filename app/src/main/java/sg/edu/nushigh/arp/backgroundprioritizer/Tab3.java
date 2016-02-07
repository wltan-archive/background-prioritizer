package sg.edu.nushigh.arp.backgroundprioritizer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Tab3 extends Fragment {

    View v;
    TextView android, uptime, imei, model, rooted, wifi, ipAddress, ipAddressDesc, macAddress, networkSsid, networkSsidDesc,
            bluetooth, bluetoothAddress, bluetoothAddressDesc, mobiledata, mobileNetworkType, ramUsed, ramFree,
            storageInternalUsed, storageInternalFree, batteryLevel, batteryHealth, batteryState, batterySource,
            batteryTechnology, batteryVoltage;
    ImageView image_wifi, image_bluetooth, image_mobiledata;
    LinearLayout container_bluetooth;
    ProgressBar bar_ram, bar_storage_internal, bar_battery;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        v = inflater.inflate(R.layout.tab_3, container, false);

        android             = (TextView) v.findViewById(R.id.value_android);
        uptime              = (TextView) v.findViewById(R.id.value_uptime);
        imei                = (TextView) v.findViewById(R.id.value_imei);
        model               = (TextView) v.findViewById(R.id.value_model);
        rooted              = (TextView) v.findViewById(R.id.value_rooted);
        wifi                = (TextView) v.findViewById(R.id.value_wifi);
        image_wifi          = (ImageView) v.findViewById(R.id.image_wifi);
        ipAddress           = (TextView) v.findViewById(R.id.value_ip_address);
        ipAddressDesc       = (TextView) v.findViewById(R.id.description_ip_address);
        macAddress          = (TextView) v.findViewById(R.id.value_mac_address);
        networkSsid         = (TextView) v.findViewById(R.id.value_network_ssid);
        networkSsidDesc     = (TextView) v.findViewById(R.id.description_network_ssid);
        bluetooth           = (TextView) v.findViewById(R.id.value_bluetooth);
        image_bluetooth     = (ImageView) v.findViewById(R.id.image_bluetooth);
        bluetoothAddress    = (TextView) v.findViewById(R.id.value_bluetooth_address);
        bluetoothAddressDesc= (TextView) v.findViewById(R.id.description_bluetooth_address);
        container_bluetooth = (LinearLayout) v.findViewById(R.id.container_bluetooth); //to remove extra padding since none of the bluetooth subfields remain when bluetooth is off
        mobiledata          = (TextView) v.findViewById(R.id.value_mobiledata);
        image_mobiledata    = (ImageView) v.findViewById(R.id.image_mobiledata);
        mobileNetworkType   = (TextView) v.findViewById(R.id.value_network_type);
        bar_ram             = (ProgressBar) v.findViewById(R.id.bar_ram);
        ramUsed             = (TextView) v.findViewById(R.id.value_ram_used);
        ramFree             = (TextView) v.findViewById(R.id.value_ram_free);
        bar_storage_internal= (ProgressBar) v.findViewById(R.id.bar_storage_internal);
        storageInternalUsed = (TextView) v.findViewById(R.id.value_storage_internal_used);
        storageInternalFree = (TextView) v.findViewById(R.id.value_storage_internal_free);
        bar_battery         = (ProgressBar) v.findViewById(R.id.bar_battery);
        batteryLevel        = (TextView) v.findViewById(R.id.value_battery_level);
        batteryHealth       = (TextView) v.findViewById(R.id.value_battery_health);
        batteryState        = (TextView) v.findViewById(R.id.value_battery_state);
        batterySource       = (TextView) v.findViewById(R.id.value_battery_source);
        batteryTechnology   = (TextView) v.findViewById(R.id.value_battery_technology);
        batteryVoltage      = (TextView) v.findViewById(R.id.value_battery_voltage);

        SystemInfo si = new SystemInfo(getContext());

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
                        SystemInfo si = new SystemInfo(a.getApplicationContext());
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
                        if(si.bluetoothOn()){
                            bluetoothAddress.setText(si.bluetoothAddress());
                            bluetoothAddress.setVisibility(View.VISIBLE);
                            bluetoothAddressDesc.setVisibility(View.VISIBLE);
                            container_bluetooth.setVisibility(View.VISIBLE);

                        }else{
                            bluetoothAddress.setVisibility(View.GONE);
                            bluetoothAddressDesc.setVisibility(View.GONE);
                            container_bluetooth.setVisibility(View.GONE);
                        }
                        mobiledata.setText(si.mobileOn() ? "On" : "Off");
                        image_mobiledata.setImageResource(si.mobileOn() ? R.drawable.ic_info_mobiledata_enabled : R.drawable.ic_info_mobiledata_disabled);
                        mobileNetworkType.setText(si.mobileType());
                        bar_ram.setProgress(si.ramTotal() == 0 ? 500000 : (int) (si.ramUsed() / si.ramTotal() * 1000000));
                        ramUsed.setText(String.valueOf(si.ramUsed()));
                        ramFree.setText(String.valueOf(si.ramFree()));
                        bar_storage_internal.setProgress((int) ((si.intStorageTotal() - si.intStorageFree()) / si.intStorageTotal() * 1000000));
                        storageInternalUsed.setText(String.valueOf(si.intStorageTotal() - si.intStorageFree()));
                        storageInternalFree.setText(String.valueOf(si.intStorageFree()));
                        bar_battery.setMax(100);
                        bar_battery.setProgress(si.batteryLevel());
                        batteryLevel.setText(si.batteryLevel() + "%");
                        batteryHealth.setText(si.batteryHealth());
                        batteryState.setText(si.batteryChargingState());
                        batterySource.setText(si.batteryChargingSource());
                        batteryTechnology.setText(si.batteryTechnology());
                        batteryVoltage.setText(si.batteryVoltage() + "V");
                    }
                });
            }
        };

        ScheduledExecutorService s = new ScheduledThreadPoolExecutor(10);
        s.scheduleAtFixedRate(update, 0, 1, TimeUnit.SECONDS);

        return v;
    }
}
