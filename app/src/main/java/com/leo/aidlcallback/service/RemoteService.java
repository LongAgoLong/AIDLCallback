package com.leo.aidlcallback.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;

import com.leo.aidlcallback.IRemoteCallback;
import com.leo.aidlcallback.IRemoteService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class RemoteService extends Service {
    private HashMap<String, IRemoteCallback> mHashMap;
    private Handler sendHandler;
    private Handler receiveHandler;

    public RemoteService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHashMap = new HashMap<>();
        initSendHandler();
        initReceiveHandler();

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
            if (mHashMap.containsKey(pkgName)) {
                return;
            }
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
            mHashMap.remove(pkgName);
        }

        @Override
        public void send(String packageName, final String func, String params) throws RemoteException {
            if (TextUtils.isEmpty(func)) {
                return;
            }
            Message message = receiveHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("packageName", packageName);
            bundle.putString("func", func);
            bundle.putString("params", params);
            message.setData(bundle);
            receiveHandler.sendMessage(message);
        }

        @Override
        public String fetch(String packageName, String func) throws RemoteException {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("func", func);
                jsonObject.put("result", "主动获取的结果：" + System.currentTimeMillis());
                return jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    };

    private void push() {
        PushThread pushThread = new PushThread();
        pushThread.start();
    }

    private class PushThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.CHINA);
                String format = sdf.format(new Date(System.currentTimeMillis()));

                Message message = sendHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("packageName", "");
                bundle.putString("func", "push");
                bundle.putString("params", format + ":接口主动推送的数据");
                message.setData(bundle);
                sendHandler.sendMessage(message);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initReceiveHandler() {
        HandlerThread receiveThread = new HandlerThread("receive-thread");
        receiveThread.start();
        receiveHandler = new Handler(receiveThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                Bundle data = message.getData();
                String packageName = data.getString("packageName", "");
                String func = data.getString("func", "");
                String params = data.getString("params", "");
                try {
                    IRemoteCallback iRemoteCallback = mHashMap.get(packageName);
                    if (null != iRemoteCallback) {
                        iRemoteCallback.onSuccess(func, "收到请求啦，给你返回来的数据请接收");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }

    private void initSendHandler() {
        HandlerThread sendThread = new HandlerThread("send-thread");
        sendThread.start();
        sendHandler = new Handler(sendThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                Bundle data = message.getData();
                String packageName = data.getString("packageName", "");
                String func = data.getString("func", "");
                String params = data.getString("params", "");
                if (TextUtils.isEmpty(func)) {
                    return true;
                }
                try {
                    if (!TextUtils.isEmpty(packageName)) {
                        if (mHashMap.containsKey(packageName)) {
                            IRemoteCallback iRemoteCallback = mHashMap.get(packageName);
                            if (null == iRemoteCallback) {
                                return true;
                            }
                            iRemoteCallback.onSuccess(func, params);
                        }
                    } else {
                        Set<Map.Entry<String, IRemoteCallback>> entries = mHashMap.entrySet();
                        for (Map.Entry<String, IRemoteCallback> entry : entries) {
                            entry.getValue().onSuccess(func, params);
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }
}
