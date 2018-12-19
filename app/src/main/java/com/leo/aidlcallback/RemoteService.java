package com.leo.aidlcallback;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class RemoteService extends Service {
    private ArrayList<IRemoteCallback> mCallbacks = new ArrayList<>();

    public RemoteService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    IRemoteService.Stub stub = new IRemoteService.Stub() {
        @Override
        public void register(IRemoteCallback callback) throws RemoteException {
            if (null == callback) {
                return;
            }
            Log.i("LEO", "注册回调");
            mCallbacks.add(callback);
        }

        @Override
        public void unRegister(IRemoteCallback callback) throws RemoteException {
            if (null == callback) {
                return;
            }
            Log.i("LEO", "反注册回调");
            mCallbacks.remove(callback);
        }

        @Override
        public void send(String packageName, String func, String params) throws RemoteException {
            Log.i("LEO", "接收到请求0");
            if (TextUtils.isEmpty(func)) {
                return;
            }
            switch (func) {
                case "test":
                    Log.i("LEO", "接收到请求1");
                    for (IRemoteCallback iRemoteCallback:mCallbacks){
                        iRemoteCallback.onSuccess(func, "接口回调回来的数据" + System.currentTimeMillis());
                    }
                    break;
            }
        }
    };
}
