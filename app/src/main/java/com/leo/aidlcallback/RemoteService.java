package com.leo.aidlcallback;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RemoteService extends Service {
    private HashMap<String, IRemoteCallback> mHashMap;

    public RemoteService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHashMap = new HashMap<>();
        push();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    IRemoteService.Stub stub = new IRemoteService.Stub() {
        @Override
        public void register(String pkgName, IRemoteCallback callback) throws RemoteException {
            if (null == callback) {
                return;
            }
            Log.i("LEO", "注册回调");
            callback.asBinder().linkToDeath(new PkgDeathRecipient(pkgName) {
                @Override
                public void binderDied() {
                    super.binderDied();
                    // 设置死亡代理，进程意外挂掉等移除callback
                    IRemoteCallback iRemoteCallback = mHashMap.get(getPkgName());
                    if (null != iRemoteCallback) {
                        iRemoteCallback.asBinder().unlinkToDeath(this, 0);
                    }
                    mHashMap.remove(getPkgName());
                }
            }, 0);
            mHashMap.put(pkgName, callback);
        }

        @Override
        public void unRegister(String pkgName, IRemoteCallback callback) throws RemoteException {
            if (null == callback) {
                return;
            }
            Log.i("LEO", "反注册回调");
            mHashMap.remove(pkgName);
        }

        @Override
        public void send(String packageName, final String func, String params) throws RemoteException {
            if (TextUtils.isEmpty(func)) {
                return;
            }
            IRemoteCallback iRemoteCallback = mHashMap.get(packageName);
            if (null != iRemoteCallback) {
                iRemoteCallback.onSuccess(func, "收到请求啦，给你返回来的数据请接收");
            }
        }
    };

    private void push() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    Set<Map.Entry<String, IRemoteCallback>> entries = mHashMap.entrySet();
                    for (Map.Entry<String, IRemoteCallback> entry : entries) {
                        try {
                            entry.getValue().onSuccess("push",
                                    "接口主动推送的数据" + System.currentTimeMillis());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
