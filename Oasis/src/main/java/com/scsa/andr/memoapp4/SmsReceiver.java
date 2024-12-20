package com.scsa.andr.memoapp4;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 권한 확인
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            // 권한 없음 - 알림이나 다른 처리 없이 작업 종료
            return;
        }

        // SMS 메시지 처리
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            StringBuilder messageBuilder = new StringBuilder();

            // 수신된 SMS 메시지 처리
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                messageBuilder.append(smsMessage.getMessageBody());
            }

            String message = messageBuilder.toString();
            String regDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            // SMS 메시지에서 특정 키워드를 기반으로 isBold와 isMust 설정
            boolean isBold = message.toLowerCase().contains("important"); // 예: 'important' 키워드가 포함된 경우
            boolean isMust = message.toLowerCase().contains("urgent");    // 예: 'urgent' 키워드가 포함된 경우

            // MemoDatabaseHelper를 사용하여 메모 추가
            MemoDatabaseHelper dbHelper = new MemoDatabaseHelper(context);
            MemoDto newMemo = new MemoDto("SMS Memo", message, regDate, isBold, isMust);
            dbHelper.saveMemo(newMemo);

            // 리스트 업데이트 브로드캐스트 송신
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("UPDATE_MEMO_LIST"));
        }
    }
}
