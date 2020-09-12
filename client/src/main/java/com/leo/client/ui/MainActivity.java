package com.leo.client.ui;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.leo.aidlcallback.IRemoteCallback;
import com.leo.client.R;
import com.leo.client.util.BridgeManager;
import com.leo.client.util.ContextHelp;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mBindBtn;
    private Button mUnbindBtn;
    private Button mAsynRequestBtn;
    private Button mSyncRequestBtn;
    private TextView mResultTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextHelp.setContext(this);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mBindBtn = findViewById(R.id.bindBtn);
        mBindBtn.setOnClickListener(this);
        mUnbindBtn = findViewById(R.id.unbindBtn);
        mUnbindBtn.setOnClickListener(this);
        mAsynRequestBtn = findViewById(R.id.asynRequestBtn);
        mAsynRequestBtn.setOnClickListener(this);
        mSyncRequestBtn = findViewById(R.id.syncRequestBtn);
        mSyncRequestBtn.setOnClickListener(this);
        mResultTv = findViewById(R.id.resultTv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bindBtn:
                BridgeManager.getInstance().bindService(MainActivity.this,
                        "com.leo.aidlcallback", "com.leo.aidlTest",
                        new IRemoteCallback.Stub() {
                            @Override
                            public void onSuccess(final String func, final String params) throws RemoteException {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mResultTv.append("func：" + func + ";params：" + params + "\n");
                                    }
                                });
                            }

                            @Override
                            public void onError(String func, int errorCode) throws RemoteException {

                            }
                        });
                mResultTv.append("绑定服务\n");
                break;
            case R.id.unbindBtn:
                BridgeManager.getInstance().unbindService(MainActivity.this);
                mResultTv.append("取消绑定\n");
                break;
            case R.id.asynRequestBtn:
                // 异步
                BridgeManager.getInstance().send("test", "");
                break;
            case R.id.syncRequestBtn:
                // 同步
                String syncRequest = BridgeManager.getInstance().fetch("syncRequest");
                if (!TextUtils.isEmpty(syncRequest)) {
                    mResultTv.append(syncRequest);
                }
                break;
        }
    }
}
