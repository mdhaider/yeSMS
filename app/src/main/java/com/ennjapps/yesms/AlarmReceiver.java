package com.ennjapps.yesms;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    public static final String INTENT_FILTER = "com.github.yeriomin.smsscheduler.AlarmReceiver.INTENT_FILTER";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, SmsSenderService.class);
        service.putExtras(intent.getExtras());
        startWakefulService(context, service);
    }

}