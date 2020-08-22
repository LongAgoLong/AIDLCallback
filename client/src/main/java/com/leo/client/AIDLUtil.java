package com.leo.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import android.util.Log;

import com.leo.aidlcallback.IRemoteCallback;
import com.leo.aidlcallback.IRemoteService;

public class AIDLUtil {
    private static AIDLUtil aidlUtil;
    private IRemoteService iRemoteService;
    private IRemoteCallback iRemoteCallback;
    private final Handler sendHandler;

    private AIDLUtil() {
        HandlerThread sendThread = new HandlerThread("client-send-thread");
        sendThread.start();
        sendHandler = new Handler(sendThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (null == iRemoteService) {
                    return true;
                }
                Bundle data = message.getData();
                String func = data.getString("func", "");
                String params = data.getString("params", "");
                try {
                    iRemoteService.send(ContextHelp.getContext().getPackageName(), func, params);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
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
                // 设置死亡代理，服务意外挂掉才能回调onServiceDisconnected
                iRemoteService.asBinder().linkToDeath(deathRecipient, 0);
                iRemoteService.register(ContextHelp.getContext().getPackageName(), iRemoteCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (null != iRemoteService) {
                iRemoteService.asBinder().unlinkToDeath(deathRecipient, 0);
            }
            iRemoteService = null;
            iRemoteCallback = null;
        }
    };

    private IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {

        }
    };

    public void bindService(Context context, @NonNull IRemoteCallback iRemoteCallback) {
        this.iRemoteCallback = iRemoteCallback;
        Intent intent = new Intent("com.leo.aidlTest");
        intent.setPackage("com.leo.aidlcallback");
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void unbindService(Context context) {
        try {
            if (null != iRemoteCallback) {
                iRemoteService.unRegister(ContextHelp.getContext().getPackageName(), iRemoteCallback);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        context.unbindService(connection);
        iRemoteService = null;
        iRemoteCallback = null;
    }

    public void send() {
        Message message = sendHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("func", "test");
        bundle.putString("params", "");
        message.setData(bundle);
        sendHandler.sendMessage(message);
    }
}
