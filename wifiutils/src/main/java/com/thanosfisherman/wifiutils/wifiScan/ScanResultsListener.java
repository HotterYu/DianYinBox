package com.thanosfisherman.wifiutils.wifiScan;


import android.net.wifi.ScanResult;

import java.util.List;

public interface ScanResultsListener
{
    void onScanResults(List<ScanResult> scanResults);
}
