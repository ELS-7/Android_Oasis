package com.scsa.andr.memoapp4;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
//import android.os.CountDownTimer;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.Calendar;

import com.google.android.material.navigation.NavigationView;
import com.scsa.andr.memoapp4.databinding.ActivityAlarmMainBinding;

public class AlarmMainActivity extends AppCompatActivity {

    AlarmManager manager;

    Calendar cal = Calendar.getInstance();
    int year = 0;
    int month = 0;
    int day = 0;
    int hour = 0;
    int min = 0;

    private static String pad(int c) {
        return String.format("%02d", c);
    }
    private CountDownTimer countdownTimer; // 타이머를 멤버 변수로 저장
    private ActivityAlarmMainBinding binding;
    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        drawerLayout = findViewById(R.id.drawLayout);
        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // Toolbar를 액션바로 설정
        getSupportActionBar().setTitle("Clock");
        // 사이드바
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.hamberger);

        ((NavigationView)findViewById(R.id.layout)).setNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.main1){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(AlarmMainActivity.this, MainActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.todo2){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(AlarmMainActivity.this, MemoMainActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.news3){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(AlarmMainActivity.this, NewsActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.alarm4){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }

            } else if(item.getItemId() == R.id.shell5){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(AlarmMainActivity.this, GameMainActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.dinner6){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(AlarmMainActivity.this, WTE_MainActivity.class);
                startActivity(intent);
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkStartPermissionRequest();
    }

    // 다른앱위에 그리기 권한이 있어야 있어야 broadcast가 activity를 실행시킴.
    public void checkStartPermissionRequest() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1000);
            Toast.makeText(this, "권한을 허용해 주세요.", Toast.LENGTH_SHORT).show();
        } else {
            initEvent();
        }
    }

    private void initEvent() {
        binding.reg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int after = Integer.parseInt(binding.elap.getText().toString());

                long now = SystemClock.elapsedRealtime();
                long atTime = now + (after * 1000);

                Intent intent = new Intent();
                intent.setClass(AlarmMainActivity.this, AlarmReceiver.class);

                // 기존 알람 취소 및 새로운 PendingIntent 생성
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        AlarmMainActivity.this,
                        101,
                        intent,
//                        PendingIntent.FLAG_CANCEL_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

                // 기존 알람 취소
//                manager.cancel(pendingIntent);

                // 새 알람 등록
                manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, atTime, pendingIntent);

                // 기존 타이머 취소 및 초기화
                if (countdownTimer != null) {
                    Log.d("AlarmDebug", "카운트다운 타이머 이미있음" + countdownTimer);

                    countdownTimer.cancel();
                    countdownTimer = null;
                }
                binding.clockImage.setVisibility(View.GONE);
                binding.remainingTime.setVisibility(View.GONE);

                // 새 타이머 시작
                binding.clockImage.setVisibility(View.VISIBLE);
                binding.remainingTime.setVisibility(View.VISIBLE);
                startCountdownTimer(after);
                //디버깅
//                Log.d("AlarmDebug", "카운트다운 타이머" + countdownTimer);
//                Log.d("AlarmDebug", "Alarm set for (elapsedRealtime): " + atTime);
//                Log.d("AlarmDebug", "Current elapsedRealtime: " + SystemClock.elapsedRealtime());

                Toast.makeText(AlarmMainActivity.this, after + "초 후 알람등록", Toast.LENGTH_SHORT).show();
            }
        });



        binding.cancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AlarmMainActivity.this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmMainActivity.this, 101, intent, PendingIntent.FLAG_IMMUTABLE);
                Toast.makeText(AlarmMainActivity.this, "알람 해제", Toast.LENGTH_SHORT).show();
                manager.cancel(pendingIntent);
                // 기존 타이머가 실행 중이라면 취소
                if (countdownTimer != null) {
                    countdownTimer.cancel();
                    countdownTimer = null;
                }
                // 시계 아이콘 숨기기
                binding.clockImage.setVisibility(View.GONE);
                binding.remainingTime.setVisibility(View.GONE);

            }
        });

        binding.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(
                        AlarmMainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                binding.date.setText(year + "-" + pad(monthOfYear + 1) + "-" + pad(dayOfMonth));
                                AlarmMainActivity.this.year = year;
                                AlarmMainActivity.this.month = monthOfYear;
                                AlarmMainActivity.this.day = dayOfMonth;
                            }
                        },
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        binding.time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(
                        AlarmMainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                binding.time.setText(pad(hourOfDay) + ":" + pad(minute));
                                AlarmMainActivity.this.hour = hourOfDay;
                                AlarmMainActivity.this.min = minute;
                            }
                        },
                        Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                        Calendar.getInstance().get(Calendar.MINUTE),
                        true
                ).show();
            }
        });

        binding.reg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AlarmMainActivity.this.day == 0 || AlarmMainActivity.this.min == 0) {
                    Toast.makeText(AlarmMainActivity.this, "날짜와 시간을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 설정된 날짜와 시간으로 Calendar 업데이트
                cal.set(Calendar.YEAR, AlarmMainActivity.this.year);
                cal.set(Calendar.MONTH, AlarmMainActivity.this.month);
                cal.set(Calendar.DAY_OF_MONTH, AlarmMainActivity.this.day);
                cal.set(Calendar.HOUR_OF_DAY, AlarmMainActivity.this.hour);
                cal.set(Calendar.MINUTE, AlarmMainActivity.this.min);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                long atTime = cal.getTimeInMillis();
                long now = System.currentTimeMillis();
                int after = (int) ((atTime - now) / 1000); // 현재 시간과의 차이를 초 단위로 계산

                Intent intent = new Intent();
                intent.setClass(AlarmMainActivity.this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmMainActivity.this, 102, intent, PendingIntent.FLAG_IMMUTABLE);
                manager.set(AlarmManager.RTC_WAKEUP, atTime, pendingIntent);
                Toast.makeText(AlarmMainActivity.this, "알람등록", Toast.LENGTH_SHORT).show();

                // 기존 타이머 취소 및 초기화
                if (countdownTimer != null) {
                    Log.d("AlarmDebug", "카운트다운 타이머 이미있음" + countdownTimer);
                    countdownTimer.cancel();
                    countdownTimer = null;
                }

                // 알람 시계 아이콘 및 남은 시간 표시
                binding.clockImage.setVisibility(View.VISIBLE);
                binding.remainingTime.setVisibility(View.VISIBLE);
                startCountdownTimer(after);
            }
        });

        binding.cancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlarmMainActivity.this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmMainActivity.this, 102, intent, PendingIntent.FLAG_IMMUTABLE);
                Toast.makeText(AlarmMainActivity.this, "알람 해제", Toast.LENGTH_SHORT).show();
                manager.cancel(pendingIntent);

                // 기존 타이머가 실행 중이라면 취소
                if (countdownTimer != null) {
                    countdownTimer.cancel();
                    countdownTimer = null;
                }

                // 알람 시계 아이콘 및 남은 시간 제거
                binding.clockImage.setVisibility(View.GONE);
                binding.remainingTime.setVisibility(View.GONE);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            // 홈 아이콘 클릭 시 메인 액티비티로 이동
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        }

        if(item.getItemId() == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    // 카운트다운 타이머
    private void startCountdownTimer(int seconds) {
        // 기존 타이머 취소
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }

        // 새 타이머 생성
        countdownTimer = new CountDownTimer(seconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long remainingSeconds = millisUntilFinished / 1000;
                int minutes = (int) remainingSeconds / 60;
                int seconds = (int) remainingSeconds % 60;
                String timeText = String.format("남은 시간: %02d:%02d", minutes, seconds);

                binding.remainingTime.setText(timeText);
                Log.d("CountDownTimer", "Remaining time: " + timeText);
            }

            @Override
            public void onFinish() {

                binding.remainingTime.setText("알람 울림!");
            }
        }.start();
    }


}
