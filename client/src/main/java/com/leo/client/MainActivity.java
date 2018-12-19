package com.leo.client;

import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
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
                    public void onSuccess(String func, String params) throws RemoteException {
                        Log.i("LEO", func + params);
                        mResultTv.append("func：" + func + "\n");
                        mResultTv.append("params：" + params + "\n");
                    }

                    @Override
                    public void onError(String func, int errorCode) throws RemoteException {

                    }
                });
                mResultTv.append("绑定服务");
                mResultTv.append("\n");
                break;
            case R.id.unbindBtn:
                AIDLUtil.getInstance().unbindService(MainActivity.this);
                mResultTv.append("取消绑定");
                mResultTv.append("\n");
                break;
            case R.id.requestBtn:
                AIDLUtil.getInstance().send(MainActivity.this);
                break;
        }
    }
}
