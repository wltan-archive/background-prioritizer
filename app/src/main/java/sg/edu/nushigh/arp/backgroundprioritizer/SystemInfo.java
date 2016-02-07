package sg.edu.nushigh.arp.backgroundprioritizer;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Enumeration;

public final class SystemInfo {
    private final Context c;

    private final ConnectivityManager connManager;
    private final WifiManager wifi;
    private final WifiInfo wifiInfo;
    private final BluetoothAdapter bluetooth;

    public SystemInfo(Context c){
        this.c = c;
        connManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifi = ((WifiManager) c.getSystemService(Context.WIFI_SERVICE));
        wifiInfo = wifiConnected()?wifi.getConnectionInfo():null;
        bluetooth = BluetoothAdapter.getDefaultAdapter();
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
        return wifi.isWifiEnabled();
    }
    public void toggleWifi(){
        wifi.setWifiEnabled(!wifi.isWifiEnabled());
    }
    public boolean wifiConnected(){
        return connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
    }
    public String wifiMac(){
        boolean off = !wifiOn();
        String result;
        if(off){
            wifi.setWifiEnabled(true);
            result = wifi.getConnectionInfo().getMacAddress();
            wifi.setWifiEnabled(false);
        }else{
            result = wifiInfo.getMacAddress();
        }
        return result;
    }
    // Check that wifi is connected with the above method before proceeding to use these methods
    public String wifiSSID(){
        return wifiInfo.getSSID();
    }
    public String wifiIP(){
        int ipAddress = wifiInfo.getIpAddress();

        // Convert little-endian to big-endian if needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN))
            ipAddress = Integer.reverseBytes(ipAddress);

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        try {
            return InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("wifi info (IP)", "Unable to get host address");
            return null;
        }
    }
    public String wifiSpeed(){
        return wifiInfo.getLinkSpeed() + " " + WifiInfo.LINK_SPEED_UNITS;
    }
    public boolean bluetoothSupported(){
        return bluetooth != null;
    }
    // Check that bluetooth is supported first
    public boolean bluetoothOn(){
        return bluetooth.isEnabled();
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
