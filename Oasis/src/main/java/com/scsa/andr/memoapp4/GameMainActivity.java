package com.scsa.andr.memoapp4;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
//import android.widget.Toast;
import android.widget.TextView; // 점수 표기용

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Random;

public class GameMainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity_SCSA";

    FrameLayout frameLayout;
    FrameLayout.LayoutParams params;
    int count = 0;   //잡은 쥐 개수를 저장할 변수
    int gameSpeed = 2000;  // 게임 속도 조절
    static boolean threadEndFlag = true; // 쓰레드 끄기
    MouseTask mouseTask;                // 쓰레드 구현

    int myWidth;  // 내 폰의 너비
    int myHeight; // 내 폰의 높이
    int imgWidth = 150;  //그림 크기
    int imgHeight = 150;//그림 크기
    Random random = new Random();  // 이미지 위치를 랜덤하게 발생시킬 객체
    int toolbarHeight; // 툴바의 높이
    SoundPool soundPool;   // 소리
    int killSound;    // 소리
    int killsearSound; //바다사자 소리
    MediaPlayer mediaPlayer;   // 소리

    int x = 200;        //시작위치
    int y = 200;        //시작위치
    ImageView[] imageViews; // 이미지들을 담아 놓을 배열
    ImageView[] backImageViews; // 죽었을때 이미지들을 담아 놓을 배열

    int level = 1;      // 게임 레벨
    int howManyMouse = 5;  //startLevel 5마리. 레벨마다 증가

    TextView scoreText; // 점수 표시용 TextView

    // 게임 시작 시간을 저장할 변수
    private long startTime;

    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity_main);

        frameLayout = findViewById(R.id.frame);
        scoreText = findViewById(R.id.scoreText); // 점수표기용 TextView 초기화

        // 초기 점수 설정
        updateScoreText();

        params = new FrameLayout.LayoutParams(1, 1);



        drawerLayout = findViewById(R.id.drawLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // Toolbar를 액션바로 설정
        getSupportActionBar().setTitle("Catch The Shell");
        // 사이드바
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.hamberger);


        // 툴바 높이 계산
        toolbar.post(() -> toolbarHeight = toolbar.getHeight());

        // 디스플레이 크기 체크
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        myWidth = metrics.widthPixels;
        myHeight = metrics.heightPixels;
        Log.d(TAG, "My Window " + myWidth + " : " + myHeight);

        // 사운드 셋팅
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(attributes)
                .build();
        killSound = soundPool.load(this, R.raw.chest, 1);
        killsearSound = soundPool.load(this, R.raw.seardie, 1);
        mediaPlayer = MediaPlayer.create(this, R.raw.aquaroad);
        mediaPlayer.setLooping(true);

        // XML에 정의된 버튼 가져오기
        Button startButton = findViewById(R.id.startButton);
        ImageView explainImage = findViewById(R.id.explanation);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(); // 게임 시작
                startButton.setVisibility(View.GONE); // 버튼 숨기기
                explainImage.setVisibility(View.GONE); // 설명 사진숨기기
            }
        });

        ((NavigationView)findViewById(R.id.layout)).setNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.main1){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(GameMainActivity.this, MainActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.todo2){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(GameMainActivity.this, MemoMainActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.news3){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(GameMainActivity.this, NewsActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.alarm4){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(GameMainActivity.this, AlarmMainActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.shell5){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                // 게임 정지?
            } else if(item.getItemId() == R.id.dinner6){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(GameMainActivity.this, WTE_MainActivity.class);
                startActivity(intent);
            }
            return false;
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

    private void startGame() {
        // 게임 초기화
        startTime = System.currentTimeMillis(); // 게임 시작 시간 기록
        init(howManyMouse);
        // 게임 시작 후 1초마다 점수와 시간 업데이트
        handler.post(updateRunnable);
    }

    public void init(int nums) {

        //초기화
        count = 0;
        startTime = System.currentTimeMillis(); // 게임 시작 시간 기록
        updateScoreText(); // 게임 초기화 시 점수 초기화
        threadEndFlag = true;
        this.howManyMouse = nums;
        gameSpeed = (int) (gameSpeed * (10 - level) / 10.);

        frameLayout.removeAllViews();

        //이미지 담을 배열 생성과 이미지 담기
        imageViews = new ImageView[nums+1];
        backImageViews = new ImageView[nums+1];
        for (int i = 0; i < nums; i++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.shell);  // 이미지 소스 설정
            iv.setTag(i);                   // 자기의 배열 위치를 저장.
            frameLayout.addView(iv, params);  // 화면에 표시
            imageViews[i] = iv;     // 배열에 담기

            ImageView backIv = new ImageView(this); // 잡았을때 이미지.
            backIv.setImageResource(R.drawable.shell_open);  // 이미지 소스 설정
            backIv.setVisibility(View.INVISIBLE);
            frameLayout.addView(backIv, params);  // 화면에 표시
            backImageViews[i] = backIv;     // 배열에 담기

            iv.setOnClickListener(h);  // 이벤트 등록
        }

        // 바다사자 이미지 추가 (sear.png)
        ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.sear);  // 새 이미지 소스 설정
        frameLayout.addView(iv, params);  // 화면에 추가
        imageViews[nums] = iv;  // 배열에 추가
        iv.setOnClickListener(searClickListener);  // 새 이미지 클릭 리스너 설정

        mouseTask = new MouseTask();  //일정 간격으로 이미지 위치를 바꿀 쓰레드 실행
        mouseTask.execute();
    }

    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // MouseTask 취소
        if (mouseTask != null) {
            mouseTask.cancel(true);
        }

        // MediaPlayer가 null이 아니고, 재생 중일 때만 일시 정지
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();  // MediaPlayer 해제
        }
        if (mouseTask != null) {
            mouseTask.cancel(true);
            handler.removeCallbacks(updateRunnable);  // 핸들러의 메시지 취소
        }
        threadEndFlag = false;
    }



    private void updateScoreText() {
        // 현재 시간이 0인 경우는 게임이 시작되기 전이므로, "00:00"으로 출력
        if (startTime == 0) {
            scoreText.setText("level : " + level + "  |  catch : " + count + " / " + howManyMouse + "  |  Time: 00:00");
            return;
        }


        // 현재 시간과 게임 시작 시간의 차이를 계산하여 경과 시간 구하기
        long elapsedTime = System.currentTimeMillis() - startTime;

        // 경과 시간을 초로 변환
        long elapsedSeconds = elapsedTime / 1000;
        long minutes = elapsedSeconds / 60;
        long seconds = elapsedSeconds % 60;

        // 점수 TextView 업데이트 "level 1  |  catch : 0 / 0"
        String scoreDisplay = "level : " + level + "  |  catch : " + count + " / " + howManyMouse +
                "  |  Time: " + String.format("%02d:%02d", minutes, seconds);
        scoreText.setText(scoreDisplay);
    }

    View.OnClickListener h = new View.OnClickListener() {
        public void onClick(View v) {   // 쥐를 잡았을 때
            count++;
            updateScoreText(); // 점수 업데이트
            ImageView iv = (ImageView) v;
            soundPool.play(killSound, 1, 1, 0, 0, 1);  // 소리 내기
//            iv.setImageResource(R.drawable.shell_open);  // 이미지 소스 설정
            iv.setVisibility(View.INVISIBLE);          // 이미지(쥐) 제거

            int position = (int)v.getTag(); // imageview에 저장해놓은 자신의 위치를 꺼내옴.
            Log.d(TAG, "onClick: "+position);
            ImageView backImage = backImageViews[position];
            backImage.setVisibility(View.VISIBLE);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                backImage.setVisibility(View.INVISIBLE);
            }, 500);  // 500ms 후 제거


            // 잡힌 조개 - 열린 조개 표시
            // 클릭한 위치를 가져와 새로운 이미지 위치 설정
//            int[] location = new int[2];
//            iv.getLocationOnScreen(location);
//
//            // 새로운 ImageView를 클릭 위치에 추가
//            ImageView shellImageView = new ImageView(RatMainActivity.this);
//            shellImageView.setImageResource(R.drawable.shell_open);  // shell_open.png 이미지 설정
//            FrameLayout.LayoutParams shellParams = new FrameLayout.LayoutParams(imgWidth, imgHeight);
//            shellParams.leftMargin = location[0];
//            shellParams.topMargin = location[1] - toolbarHeight*2 ; // 툴바 높이를 보정
//            shellImageView.setLayoutParams(shellParams);
//
//            frameLayout.addView(shellImageView);  // 해당 위치에 새 이미지 추가
////
////            // 일정 시간 후에 shell_open.png 이미지를 제거
//            new Handler(Looper.getMainLooper()).postDelayed(() -> iv.setVisibility(View.INVISIBLE), 500);  // 500ms 후 제거
//
//            iv.setVisibility(View.INVISIBLE);          // 이미지(쥐) 제거

            if (count == howManyMouse) {   // 쥐를 다 잡았을때
                threadEndFlag = false;
                mouseTask.cancel(true);

                // 경과 시간 계산
                long elapsedTime = System.currentTimeMillis() - startTime;
                long elapsedSeconds = elapsedTime / 1000;
                long minutes = elapsedSeconds / 60;
                long seconds = elapsedSeconds % 60;

                String grade = "A";

                if (minutes * 60 + seconds <= (level + 10)) {
                    grade = "S";
                } else if (minutes * 60 + seconds <= (level + 5) * 2.5) {
                    grade = "A";
                } else if (minutes * 60 + seconds <= (level + 5) * 3L) {
                    grade = "B";
                } else {
                    grade = "C";
                }

                // 다이얼로그에서 점수만 크게 표시
                String message = "점수: " + grade + "\n" + "클리어 시간: " + String.format("%02d:%02d", minutes, seconds) + "\n" + (level + 1) + "레벨에 도전하시겠습니까?";

                // 점수 부분을 크게 표시하기 위해 SpannableString 사용
                SpannableString spannableMessage = new SpannableString(message);
                int scoreStart = message.indexOf("점수: ") + 4; // "점수: " 위치 계산
                int scoreEnd = scoreStart + grade.length(); // 점수 끝 위치

                // 점수 부분만 크게 표시
                spannableMessage.setSpan(new RelativeSizeSpan(6.0f), scoreStart, scoreEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                // 레벨 업 다이얼로그
                AlertDialog.Builder dialog = new AlertDialog.Builder(GameMainActivity.this);
                dialog.setMessage(spannableMessage);
                dialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        level++;
                        init(++howManyMouse);
                    }
                });
                dialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        showHomeConfirmDialog();
                    }
                });

                // 다이얼로그 외부를 클릭했을 때 이벤트 처리
                AlertDialog alertDialog = dialog.create();
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // 외부 클릭 시 동작
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        showHomeConfirmDialog();
                    }
                });
                alertDialog.show();
            }
        }
    };


    //바다사자 온클릭
    View.OnClickListener searClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (count!=0){
                count--;  // count 감소
            }

            soundPool.play(killsearSound, 1, 1, 0, 0, 1);  // 소리 내기
            updateScoreText(); // 점수 업데이트

            for (ImageView img : imageViews) {
                if (img.getVisibility() == View.INVISIBLE){
                    img.setVisibility(View.VISIBLE);
                    break;
                }
            }

            // 점수가 차감될 때마다 쥐의 개수 증가
//            init(howManyMouse-count);  // 쥐의 개수를 새로 설정하여 다시 시작

            ImageView iv = (ImageView) v;
            iv.setVisibility(View.INVISIBLE);  // 클릭한 이미지 숨기기
        }
    };


    // '홈으로 돌아가시겠습니까?' 알림
    private void showHomeConfirmDialog() {
        AlertDialog.Builder homeDialog = new AlertDialog.Builder(GameMainActivity.this);
        homeDialog.setMessage("홈으로 돌아가시겠습니까?");
        homeDialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish(); // 앱 종료
            }
        });
        homeDialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 레벨 업 다이얼로그 다시 띄우기
                showLevelUpDialog();
            }
        });

        // AlertDialog 객체 생성 후 외부 터치 비활성화 설정
        AlertDialog alertDialog = homeDialog.create();
        alertDialog.setCanceledOnTouchOutside(false); // 외부 터치 기본 동작 막기
        alertDialog.show();
    }


    // 레벨 업 다이얼로그 다시 띄우기
    private void showLevelUpDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(GameMainActivity.this);
        dialog.setMessage((level + 1) + "레벨에 도전하시겠습니까?");
        dialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                level++;
                init(++howManyMouse);
            }
        });
        dialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.show();
    }


    // 쥐 위치 이동하여 다시 그리기
    public void update() {
        if (!threadEndFlag) return;
        Log.d(TAG, "update:");
        for (int i = 0; i < imageViews.length; i++) {
            ImageView img = imageViews[i];
            ImageView backImg = backImageViews[i];

            x = random.nextInt(myWidth - imgWidth);
            y = random.nextInt(myHeight - imgHeight - toolbarHeight - 100);

            img.layout(x, y, x + imgWidth, y + imgHeight);
            if(backImg != null){
                backImg.layout(x, y, x + imgWidth, y + imgHeight);
            }
            img.invalidate();
        }

    }

    // 일정 시간 간격으로 쥐를 다시 그리도록 update()를 호출하는 쓰레드
    class MouseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {// 다른 쓰레드
            while (threadEndFlag) {
                //다른 쓰레드에서는 UI를 접근할 수 없으므로
                publishProgress();    //자동으로 onProgressUpdate() 가 호출된다.
                try {
                    Thread.sleep(gameSpeed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            update();
        }
    }

    private Handler handler = new Handler();
    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            // 점수와 경과 시간을 업데이트
            updateScoreText();
            // 1초마다 갱신
            handler.postDelayed(this, 1000);
        }

    };
}
