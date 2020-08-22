package com.leo.client.ui;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.leo.aidlcallback.IRemoteCallback;
import com.leo.client.util.BridgeManager;
import com.leo.client.util.ContextHelp;
import com.leo.client.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mBindBtn;
    private Button mUnbindBtn;
    private Button mRequestBtn;
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
        mRequestBtn = findViewById(R.id.requestBtn);
        mRequestBtn.setOnClickListener(this);
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
                                Log.i("LEO", func + params);
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
            case R.id.requestBtn:
                BridgeManager.getInstance().send("test", "");
                break;
        }
    }
}
