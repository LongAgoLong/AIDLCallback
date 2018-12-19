// IRemoteService.aidl
package com.leo.aidlcallback;
import com.leo.aidlcallback.IRemoteCallback;
// Declare any non-default types here with import statements

interface IRemoteService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     void register(IRemoteCallback callback);
     void unRegister(IRemoteCallback callback);
     void send(String packageName,String func, String params);
}
