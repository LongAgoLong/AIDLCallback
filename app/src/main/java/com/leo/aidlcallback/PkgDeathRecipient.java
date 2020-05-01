package com.leo.aidlcallback;

import android.os.IBinder;

public class PkgDeathRecipient implements IBinder.DeathRecipient {
    private String pkgName;

    public PkgDeathRecipient(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getPkgName() {
        return pkgName;
    }

    @Override
    public void binderDied() {

    }
}
