package com.ennjapps.yesms;

import android.content.Context;
import android.content.Intent;

public class SmsSentReceiver extends WakefulBroadcastReceiver {

    static public final String RESULT_CODE = "resultCode";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, SmsSentService.class);
        service.putExtras(intent.getExtras());
        service.putExtra(RESULT_CODE, getResultCode());
        startWakefulService(context, service);
    }
}