// IRemoteCallback.aidl
package com.leo.aidlcallback;

// Declare any non-default types here with import statements

interface IRemoteCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onReceiver(in String func,in int code,in String params);
    String fetch(in String func);
}
