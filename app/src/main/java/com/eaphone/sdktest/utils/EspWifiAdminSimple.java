package com.eaphone.sdktest.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import java.util.List;

/**
 * @ClassName: EspWifiAdminSimple
 * @Description: java类作用描述
 * @Author: he lin hua
 * @CreateDate: 2020/9/8 15:59
 * @Version: 2.0
 */
public class EspWifiAdminSimple {

	private final Context mContext;

	
	public EspWifiAdminSimple(Context context) {
		mContext = context;
	}



	public boolean is5GHz() {
		WifiInfo mWifiInfo = getConnectionInfo();
		Log.e("wifi", "mWifiInfo:" + mWifiInfo.toString());
		int mFrequency = 0;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			mFrequency = mWifiInfo.getFrequency();
		}
		return is5GHz(mFrequency);
	}

	public String getWifiConnectedSsid() {
		WifiInfo mWifiInfo = getConnectionInfo();
		String ssid = null;
		if (mWifiInfo != null && isWifiConnected()) {
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

	// get the wifi info which is "connected" in wifi-setting
	private WifiInfo getConnectionInfo() {
		WifiManager mWifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		return wifiInfo;
	}

	private boolean isWifiConnected() {
		NetworkInfo mWiFiNetworkInfo = getWifiNetworkInfo();
		boolean isWifiConnected = false;
		if (mWiFiNetworkInfo != null) {
			isWifiConnected = mWiFiNetworkInfo.isConnected();
		}
		return isWifiConnected;
	}

	private NetworkInfo getWifiNetworkInfo() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWiFiNetworkInfo = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWiFiNetworkInfo;
	}

	public boolean is5GHz(int freq) {
		return freq > 4900 && freq < 5900;
	}


	/**
	 * 获取wifi信号强度
	 * @return
	 */
	public int getWifiStatus(){
		int status = 0;
		WifiInfo mWifiInfo = getConnectionInfo();
		status = mWifiInfo.getRssi();
		return status;
	}

	private List<ScanResult> getWifiList() {
		WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> scanWifiList = wifiManager.getScanResults();
		return scanWifiList;
	}

	//判断当前连接的5G wifi是否是混合wifi 支持2.4G
	public boolean is5Gor4G(String ssid){
		List<ScanResult> list = getWifiList();
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
