package sg.edu.nushigh.arp.backgroundprioritizer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.telephony.TelephonyManager;

import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class SystemInfo {
    private final Context c;

    private final ConnectivityManager connManager;
    private final WifiInfo wifi;

    public SystemInfo(Context c){
        this.c = c;
        connManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
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
        long ms = SystemClock.uptimeMillis();
        ms /= 1000;
        long sec = ms%60;
        ms /= 60;
        long min = ms%60;
        ms /= 60;
        long hour = ms%24;
        ms /= 24;
        long days = ms;

        return days + "d " + hour + "h " + min + "m " + sec + "s";
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
    public boolean wifiOn(){
        WifiManager wifi = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }
    public void toggleWifi(){
        WifiManager wifi = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(!wifi.isWifiEnabled());
    }
    public boolean wifiConnected(){
        return connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
    }
    // You should check that wifi is connected with the above method before proceeding to use these methods
    // Results if wifi is not connected are untested
    public String wifiSSID(){
        return wifi.getSSID().substring(1, wifi.getSSID().length() - 1);
    }
    public int wifiIP(){
        return wifi.getIpAddress();
    }
    public String wifiMac(){
        return wifi.getMacAddress();
    }
    public String wifiSpeed(){
        return wifi.getLinkSpeed() + " " + WifiInfo.LINK_SPEED_UNITS;
    }
    public boolean bluetoothOn(){
        return connManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH).isConnected();
    }
    public boolean mobileOn(){
        return connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
    }
    public String mobileType(){
        return connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getSubtypeName();
    }
    public String mobileIp(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }

                }
            }
        } catch (SocketException e) {}
        return null;
    }





}
