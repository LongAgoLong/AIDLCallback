package com.leo.client.util;

import android.content.Context;

public class ContextHelp {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        ContextHelp.context = context.getApplicationContext();
    }
}
