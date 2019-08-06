package com.badrit.MacAddress;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * The Class MacAddressPlugin.
 */
public class MacAddressPlugin extends CordovaPlugin {

    /*
     * (non-Javadoc)
     *
     * @see org.apache.cordova.api.Plugin#execute(java.lang.String,
     * org.json.JSONArray, java.lang.String)
     */
    @Override
    public boolean execute(String action, JSONArray args,
                           CallbackContext callbackContext) {

        if (action.equals("getMacAddress")) {
            JSONObject macAddress = this.getMacAddress();
            if (macAddress != null) {
                callbackContext.success(macAddress);
            }
        }
        return false;
    }


    /**
     * Gets the mac address on version >= Marshmallow.
     *
     * @return the mac address
     */
    private JSONObject getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            JSONObject jsonObject = new JSONObject();

            for (NetworkInterface nif : all) {
                for (Enumeration<InetAddress> enumIpAddr = nif.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address) {
                        if ("eth0".equals(nif.getName()) || "wlan0".equals(nif.getName())) {
                            jsonObject.put("ip", inetAddress.getHostAddress());
                            byte[] macBytes = nif.getHardwareAddress();
                            if (macBytes == null) {
                                jsonObject.put("mac", null);
                            }

                            StringBuilder res1 = new StringBuilder();
                            for (byte b : macBytes) {
                                res1.append(String.format("%02x", (b & 0xFF)) + ":");
                            }

                            if (res1.length() > 0) {
                                res1.deleteCharAt(res1.length() - 1);
                            }
                            jsonObject.put("mac", res1.toString());
                            return jsonObject;
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }
}
