package com.scsa.andr.memoapp4;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent i) {
        Toast.makeText(context, "Alarm!!", Toast.LENGTH_LONG).show();

        Intent it = new Intent(context, AlarmActivity.class);
        //BR의 context는 TASK정보가 없으므로 새로 만든다.
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(it);

    }

}
