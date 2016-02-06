package sg.edu.nushigh.arp.backgroundprioritizer;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

public class SystemInfo {
    private final Context c;

    private final String uptime = Utilities.executeCommand("uptime").get(0);

    public SystemInfo(Context c){
        this.c = c;
    }

    // Android version
    public String androidVersionName(){
        return Build.VERSION.CODENAME;
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
        return ((TelephonyManager)c.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    // Brand and model
    public String brand(){
        return Build.BRAND;
    }
    public String model(){
        return Build.MODEL;
    }



}
