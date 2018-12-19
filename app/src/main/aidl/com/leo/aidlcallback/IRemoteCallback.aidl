// IRemoteCallback.aidl
package com.leo.aidlcallback;

// Declare any non-default types here with import statements

interface IRemoteCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onSuccess(String func, String params);
    void onError(String func, int errorCode);
}
