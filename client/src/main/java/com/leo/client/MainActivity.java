package com.leo.client;

import android.os.RemoteException;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.leo.aidlcallback.IRemoteCallback;

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
                AIDLUtil.getInstance().bindService(MainActivity.this, new IRemoteCallback.Stub() {
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
                AIDLUtil.getInstance().unbindService(MainActivity.this);
                mResultTv.append("取消绑定\n");
                break;
            case R.id.requestBtn:
                AIDLUtil.getInstance().send();
                break;
        }
    }
}
