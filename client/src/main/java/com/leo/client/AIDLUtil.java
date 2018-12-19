package com.leo.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.leo.aidlcallback.IRemoteCallback;
import com.leo.aidlcallback.IRemoteService;

public class AIDLUtil {
    private static AIDLUtil aidlUtil;
    private IRemoteService iRemoteService;
    private IRemoteCallback iRemoteCallback;

    private AIDLUtil() {
    }

    public static AIDLUtil getInstance() {
        if (null == aidlUtil) {
            synchronized (AIDLUtil.class) {
                if (null == aidlUtil) {
                    aidlUtil = new AIDLUtil();
                }
            }
        }
        return aidlUtil;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("LEO", "绑定成功");
            iRemoteService = IRemoteService.Stub.asInterface(service);
            try {
                iRemoteService.register(iRemoteCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iRemoteService = null;
            iRemoteCallback = null;
        }
    };

    public void bindService(Context context, @NonNull IRemoteCallback iRemoteCallback) {
        this.iRemoteCallback = iRemoteCallback;
        Intent intent = new Intent("com.leo.aidlTest");
        intent.setPackage("com.leo.aidlcallback");
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void unbindService(Context context) {
        if (null == iRemoteService) {
            return;
        }
        try {
            if (null!=iRemoteCallback){
                iRemoteService.unRegister(iRemoteCallback);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        context.unbindService(connection);
        iRemoteService = null;
        iRemoteCallback = null;
    }

    public void send(Context context) {
        if (null == iRemoteService) {
            return;
        }
        try {
            iRemoteService.send(context.getPackageName(), "test", "");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
