package com.ennjapps.yesms.Activity;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ennjapps.yesms.DbHelper;
import com.ennjapps.yesms.MyPreferences;
import com.ennjapps.yesms.R;
import com.ennjapps.yesms.SmsModel;

import java.util.Calendar;


public class SmsListActivity extends ListActivity {
    private static long back_pressed_time;
    private static long PERIOD = 2000;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setListAdapter(getSmsListAdapter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
        adapter.getCursor().close();
        DbHelper.closeDbHelper();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
        adapter.getCursor().close();
        DbHelper.closeDbHelper();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater lf = getLayoutInflater();
        View headerView = lf.inflate(R.layout.item_add, getListView(), false);
        getListView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        headerView.setClickable(true);
        getListView().addHeaderView(headerView);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), AddSmsActivity.class);
                intent.putExtra(DbHelper.COLUMN_TIMESTAMP_CREATED, String.valueOf(id));
                startActivity(intent);
            }
        });
        Calendar c=Calendar.getInstance();
        int hours=c.get(Calendar.HOUR_OF_DAY);

        String greeting=null;
        if(hours>=0 && hours<12) {
            greeting = "Good Morning";
        }else if(hours>=12 && hours<16) {
            greeting = "Good Afternoon";
        }else if (hours>=16 && hours<21){
            greeting="Good Evening";
        }else if (hours>=21 && hours<24) {
            greeting = "Good Night";
        }
        MyPreferences pref = new MyPreferences(SmsListActivity.this);
        if (pref.isFirstTime()) {
            displayToast( "Hi" +" "+ pref.getUsername());

            pref.setOld(true);
        } else {
            displayToast( greeting + " " + pref.getUsername());

        }

    }

    public void gotoNextActivity(View view) {
        startActivity(new Intent(getApplicationContext(), AddSmsActivity.class));
    }

    private SimpleCursorAdapter getSmsListAdapter() {
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                DbHelper.getDbHelper(this).getCursor(),
                new String[] { DbHelper.COLUMN_MESSAGE, DbHelper.COLUMN_RECIPIENT_NAME },
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                TextView textView = (TextView) view;
                if (textView.getId() == android.R.id.text2) {
                    textView.setText(getFormattedSmsInfo(cursor));
                    return true;
                }
                return false;
            }
        });
        return adapter;
    }

    private String getFormattedSmsInfo(Cursor cursor) {
        String recipient, recipientName, recipientNumber, status, datetime;
        recipientName = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_RECIPIENT_NAME));
        recipientNumber = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_RECIPIENT_NUMBER));
        recipient = recipientName.length() > 0 ? recipientName : recipientNumber;
        switch (cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_STATUS))) {
            case (SmsModel.STATUS_PENDING):
                status = getString(R.string.list_status_pending);
                break;
            case (SmsModel.STATUS_SENT):
                status = getString(R.string.list_status_sent);
                break;
            case (SmsModel.STATUS_DELIVERED):
                status = getString(R.string.list_status_delivered);
                break;
            default:
                status = getString(R.string.list_status_failed);
        }
        datetime = DateUtils.formatDateTime(
            this,
            cursor.getLong(cursor.getColumnIndex(DbHelper.COLUMN_TIMESTAMP_SCHEDULED)),
            DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME
        );
        return getString(R.string.list_sms_info_template, status, recipient, datetime);
    }
    @Override
    public void onBackPressed() {
        if (back_pressed_time + PERIOD > System.currentTimeMillis()) super.onBackPressed();
        else
            displayToast("Press once again to exit!");
        //Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed_time = System.currentTimeMillis();
    }
    public void displayToast(String message) {
        // Inflate toast XML layout
        View layout = getLayoutInflater().inflate(R.layout.toast_layout,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        // Fill in the message into the textview
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        // Construct the toast, set the view and display
        Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
}
