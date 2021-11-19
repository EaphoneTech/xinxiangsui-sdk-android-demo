package com.eaphone.sdktest.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import java.util.List;


public class WifiUitils {

	public static boolean is5GHz(Context mContext) {
		WifiInfo mWifiInfo = getConnectionInfo(mContext);
		int mFrequency = 0;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			mFrequency = mWifiInfo.getFrequency();
		}
		return is5GHz(mFrequency);
	}

	public static String getWifiConnectedSsid(Context mContext) {
		WifiInfo mWifiInfo = getConnectionInfo(mContext);
		String ssid = null;
		if (mWifiInfo != null && isWifiConnected(mContext)) {
			int len = mWifiInfo.getSSID().length();
			if (mWifiInfo.getSSID().startsWith("\"")
					&& mWifiInfo.getSSID().endsWith("\"")) {
				ssid = mWifiInfo.getSSID().substring(1, len - 1);
			} else {
				ssid = mWifiInfo.getSSID();
			}
		}
		return ssid;
	}

	private static WifiInfo getConnectionInfo(Context mContext) {
		WifiManager mWifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		return wifiInfo;
	}

	private static boolean isWifiConnected(Context mContext) {
		NetworkInfo mWiFiNetworkInfo = getWifiNetworkInfo(mContext);
		boolean isWifiConnected = false;
		if (mWiFiNetworkInfo != null) {
			isWifiConnected = mWiFiNetworkInfo.isConnected();
		}
		return isWifiConnected;
	}

	private static NetworkInfo getWifiNetworkInfo(Context mContext) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWiFiNetworkInfo = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWiFiNetworkInfo;
	}

	private static boolean is5GHz(int freq) {
		return freq > 4900 && freq < 5900;
	}

	/**
	 * 获取wifi信号强度
	 * @return
	 */
	public static int getWifiStatus(Context mContext){
		int status = 0;
		WifiInfo mWifiInfo = getConnectionInfo(mContext);
		status = mWifiInfo.getRssi();
		return status;
	}

	private static List<ScanResult> getWifiList(Context mContext) {
		WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> scanWifiList = wifiManager.getScanResults();
		return scanWifiList;
	}

	//判断当前连接的5G wifi是否是混合wifi 支持2.4G
	public static boolean is5Gor4G(Context mContext, String ssid){
		List<ScanResult> list = getWifiList(mContext);
		boolean isOK = false;
		int it = 0;
		for(int i = 0; i < list.size(); i++){
			ScanResult scanResult = list.get(i);
			boolean is1 = ssid.equals(scanResult.SSID);
			boolean is2 =2400<scanResult.frequency && scanResult.frequency< 2500;
			if( is1 && is2){
				it++;
			}
		}
		if(it > 0){
			isOK = true;
		}
		return  isOK;
	}

}
