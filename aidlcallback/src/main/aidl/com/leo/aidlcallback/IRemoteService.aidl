// IRemoteService.aidl
package com.leo.aidlcallback;
import com.leo.aidlcallback.IRemoteCallback;
// Declare any non-default types here with import statements

interface IRemoteService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     void register(in String pkgName,in IRemoteCallback callback);
     void unRegister(in String pkgName,in IRemoteCallback callback);
     void send(in String packageName,in String func,in String params);
     String fetch(in String packageName,in String func);
}
