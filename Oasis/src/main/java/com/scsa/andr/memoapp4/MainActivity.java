package com.scsa.andr.memoapp4;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity_SCSA";
    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawLayout);

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Oasis in Life");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.hamberger);

        // TodoList 버튼 클릭 시 MemoMainActivity로 이동
        Button todoListButton = findViewById(R.id.button_todo_list);
        todoListButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MemoMainActivity.class);
            startActivity(intent);
        });

        // 알람관리 버튼 클릭 시 AlarmActivity로 이동
        Button AlarmButton = findViewById(R.id.button_alarm);
        AlarmButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AlarmMainActivity.class);
            startActivity(intent);
        });


        //   뉴스 버튼 button_news -> newsActivity
        Button NewsButton = findViewById(R.id.button_news);
        NewsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NewsActivity.class);
            startActivity(intent);
        });



        // 쥐 잡기 게임 버튼 클릭 시 RatMainActivity로 이동
        Button RatButton = findViewById(R.id.button_catch_rat);
        RatButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameMainActivity.class);
            startActivity(intent);
        });

        // 저녁메뉴 버튼 클릭 시 RatMainActivity로 이동
        Button RecommandButton = findViewById(R.id.button_recommend);
        RecommandButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WTE_MainActivity.class);
            startActivity(intent);
        });

        ((NavigationView)findViewById(R.id.layout)).setNavigationItemSelectedListener( item -> {
            if(item.getItemId() == R.id.main1){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
            } else if(item.getItemId() == R.id.todo2){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(MainActivity.this, MemoMainActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.news3){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.alarm4){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(MainActivity.this, AlarmMainActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.shell5){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(MainActivity.this, GameMainActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.dinner6){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(MainActivity.this, WTE_MainActivity.class);
                startActivity(intent);
            }
            return false;
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: ");

        if(item.getItemId() == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }
}
