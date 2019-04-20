// IDownloadCallback.aidl
package com.znt.download;

// Declare any non-default types here with import statements

interface IDownloadCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void actionPerformed(int actionId, String arg1, String arg2, String arg3);
}
