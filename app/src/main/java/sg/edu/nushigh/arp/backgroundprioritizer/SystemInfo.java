package sg.edu.nushigh.arp.backgroundprioritizer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.lang.reflect.Field;

public class SystemInfo {
    private final Context c;

    private final String uptime;
    private final WifiInfo wifi;

    public SystemInfo(Context c){
        this.c = c;
        uptime = Utilities.executeCommand("uptime").get(0);
        if(wifiConnected())
            wifi = ((WifiManager)c.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        else
            wifi = null;
    }

    // Android version
    public String androidVersionName(){
        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int fieldValue = -1;

            try {
                fieldValue = field.getInt(new Object());
            } catch (IllegalArgumentException|IllegalAccessException|NullPointerException e) {}

            if (fieldValue == Build.VERSION.SDK_INT) {
                return fieldName;
            }
        }
        return "Error retrieving version";
    }
    
    public String androidVersionCode(){
        return Build.VERSION.RELEASE;
    }

    // System uptime
    public String uptime(){
        return uptime.substring(9, 17);
    }
    public String idletime(){
        return uptime.substring(30, 38);
    }
    public String sleeptime(){
        return uptime.substring(52, 60);
    }

    // IMEI/ESN, or whatever unique identifier for the device
    public String imei(){
        return ((TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    // Brand and model
    public String brand(){
        return Build.BRAND;
    }
    public String model(){
        return Build.MODEL;
    }

    // Wireless and networks
    public boolean wifiConnected(){
        ConnectivityManager connManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mWifi.isConnected();
    }
    public String wifiSSID(){
        return "";
    }




}
