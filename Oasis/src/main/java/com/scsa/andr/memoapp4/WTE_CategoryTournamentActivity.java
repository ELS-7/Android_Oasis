package com.scsa.andr.memoapp4;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class WTE_CategoryTournamentActivity extends AppCompatActivity {
    private ArrayList<String> categories; // 카테고리 데이터를 담는 리스트
    private TextView txtCategory1, txtCategory2;
    private Button btnCategory1, btnCategory2, btnComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wte_activity_category_tournament); // 새 XML 레이아웃

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // Toolbar를 액션바로 설정

        // 데이터 받기
        categories = getIntent().getStringArrayListExtra("categories");
        if (categories == null || categories.size() < 2) {
            finish();
            return;
        }

        // View 초기화
        txtCategory1 = findViewById(R.id.txt_option1);
        txtCategory2 = findViewById(R.id.txt_option2);
        btnCategory1 = findViewById(R.id.btn_option1);
        btnCategory2 = findViewById(R.id.btn_option2);
        btnComplete = findViewById(R.id.btn_complete);

        btnComplete.setEnabled(false);
        btnComplete.setVisibility(View.GONE);

        // 첫 번째 대결 세팅
        updateCategoryTournament();

        // 버튼 클릭 이벤트
        btnCategory1.setOnClickListener(v -> handleSelection(0));
        btnCategory2.setOnClickListener(v -> handleSelection(1));

        btnComplete.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("selectedCategory", categories.get(0)); // 최종 선택된 카테고리
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void updateCategoryTournament() {
        if (categories.size() == 1) {
            // 최종 선택 완료
            txtCategory1.setText("최종 선택: " + categories.get(0));
            txtCategory2.setText("");
            btnCategory1.setVisibility(View.GONE);
            btnCategory2.setVisibility(View.GONE);

            // 텍스트 크기 조정
            txtCategory1.setTextSize(18); // 원하는 크기로 설정 (단위: sp)


            btnComplete.setEnabled(true);
            btnComplete.setVisibility(View.VISIBLE);
        } else {
            // 대결 업데이트
            txtCategory1.setText(categories.get(0));
            txtCategory2.setText(categories.get(1));
        }
    }

    private void handleSelection(int selectedIndex) {
        // 선택되지 않은 항목 제거
        categories.remove(1 - selectedIndex);

        // 다음 대결 준비
        updateCategoryTournament();
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
        return super.onOptionsItemSelected(item);
    }
}
