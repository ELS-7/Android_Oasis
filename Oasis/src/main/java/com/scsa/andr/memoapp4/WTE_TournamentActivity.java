package com.scsa.andr.memoapp4;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class WTE_TournamentActivity extends AppCompatActivity {
    private ArrayList<String> items;
    private TextView txtOption1, txtOption2;
    private Button btnOption1, btnOption2, btnComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wte_activity_tournament);

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // Toolbar를 액션바로 설정

        // 데이터 받기
        items = getIntent().getStringArrayListExtra("items");
        if (items == null || items.size() < 2) {
            finish();
            return;
        }

        // View 초기화
        txtOption1 = findViewById(R.id.txt_option1);
        txtOption2 = findViewById(R.id.txt_option2);
        btnOption1 = findViewById(R.id.btn_option1);
        btnOption2 = findViewById(R.id.btn_option2);

        btnComplete = findViewById(R.id.btn_complete);

        btnComplete.setEnabled(false);
        btnComplete.setVisibility(View.GONE);

        // 첫 번째 대결 세팅
        updateTournament();

        // 버튼 클릭 이벤트
        btnOption1.setOnClickListener(v -> handleSelection(0));
        btnOption2.setOnClickListener(v -> handleSelection(1));

        btnComplete.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("selectedItem", items.get(0)); // 최종 선택된 항목
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void updateTournament() {
        if (items.size() == 1) {
            // 최종 선택 완료
            txtOption1.setText("최종 선택: " + items.get(0));
            txtOption2.setText("");
            btnOption1.setVisibility(View.GONE);
            btnOption2.setVisibility(View.GONE);

            // 텍스트 크기 조정
            txtOption1.setTextSize(18); // 원하는 크기로 설정 (단위: sp)

            btnComplete.setEnabled(true);
            btnComplete.setVisibility(View.VISIBLE);
        } else {
            // 대결 업데이트
            txtOption1.setText(items.get(0));
            txtOption2.setText(items.get(1));
        }
    }

    private void handleSelection(int selectedIndex) {
        // 선택되지 않은 항목 제거
        items.remove(1 - selectedIndex);

        // 다음 대결 준비
        updateTournament();
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
