package com.scsa.andr.memoapp4;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.scsa.andr.memoapp4.databinding.ActivityAlarmBinding;

public class AlarmActivity extends AppCompatActivity {
    MediaPlayer player;
    private ActivityAlarmBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 홈 아이콘 클릭 처리
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 홈 아이콘 활성화
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.img_oasis); // 커스텀 홈 아이콘
        }

        // TextView의 배경색에 애니메이션 적용
        TextView textView = findViewById(R.id.arlamID);

        // Animator XML을 사용해 ObjectAnimator 설정
        ObjectAnimator colorAnimator = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.color_animator);
        colorAnimator.setTarget(textView); // 애니메이션 대상 설정
        colorAnimator.start(); // 애니메이션 시작

        // 알람 화면 활성화 설정
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        // 알람 소리 재생
        player = MediaPlayer.create(this, R.raw.alaram2_iphone);
        player.start();

        // 알람 종료 버튼 클릭 이벤트
        binding.btnAlarmOff.setOnClickListener(v -> {
            releasePlayer();
            finish(); // 알람 종료 후 Activity 종료
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    // MediaPlayer 해제
    private void releasePlayer() {
        if (player != null && player.isPlaying()) {
            player.stop();
            player.release();
            player = null;
        }
    }

    // 홈 아이콘 클릭 시 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // 홈 아이콘 클릭 시 MainActivity로 돌아가기
            finish(); // 현재 알람 화면 종료
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
