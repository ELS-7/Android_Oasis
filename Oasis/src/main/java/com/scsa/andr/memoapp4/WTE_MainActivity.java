package com.scsa.andr.memoapp4;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class WTE_MainActivity extends AppCompatActivity {
    private Spinner categorySpinner, typeSpinner;
    private ArrayAdapter<String> typeAdapter;
    private HashMap<String, List<String>> recommend;

    private void initializeRecommendationData() {
        recommend = new HashMap<>();

        // 한식
        recommend.put("한식-빵", Arrays.asList("호떡", "찹쌀도넛", "팥빵", "꿀떡", "약과", "설기떡", "찹쌀떡", "모찌"));
        recommend.put("한식-떡", Arrays.asList("떡국", "인절미", "송편", "무지개떡", "절편", "경단", "백설기", "쑥떡"));
        recommend.put("한식-면", Arrays.asList("냉면", "잔치국수", "칼국수", "콩국수", "비빔냉면", "온면", "막국수", "수제비"));
        recommend.put("한식-쌀", Arrays.asList("비빔밥", "돌솥밥", "밥버거", "쌈밥", "영양밥", "주먹밥", "곤드레밥", "솥밥"));
        recommend.put("한식-고기", Arrays.asList("불고기", "갈비찜", "삼겹살", "제육볶음", "닭갈비", "소고기장조림", "보쌈", "돼지갈비"));
        recommend.put("한식-생선", Arrays.asList("조기구이", "고등어조림", "간장게장", "굴비찜", "임연수구이", "꽁치조림", "연어장", "매운탕"));
        recommend.put("한식-야채", Arrays.asList("나물비빔밥", "김치전", "미역무침", "파전", "감자조림", "오이무침", "고사리볶음", "참나물무침"));

        // 일식
        recommend.put("일식-빵", Arrays.asList("멜론빵", "카레빵", "단팥빵", "소세지빵", "슈크림빵", "치즈롤", "도넛", "스콘"));
        recommend.put("일식-떡", Arrays.asList("모찌", "경단", "미타라시당고", "유키미다이후쿠", "아마자케", "우이로", "다이후쿠", "보타모찌"));
        recommend.put("일식-면", Arrays.asList("우동", "소바", "라멘", "야키소바", "모밀소바", "냉라멘", "탄탄멘", "기리메시"));
        recommend.put("일식-쌀", Arrays.asList("스시", "오니기리", "덮밥", "카츠동", "텐동", "장어덮밥", "회덮밥", "오야코동"));
        recommend.put("일식-고기", Arrays.asList("규동", "돈가스", "닭꼬치", "스키야키", "샤부샤부", "고기덮밥", "고기튀김", "니쿠자가"));
        recommend.put("일식-생선", Arrays.asList("사시미", "생선초밥", "니시키리", "튀김생선", "고등어초밥", "명태알튀김", "훈제연어", "간장생선조림"));
        recommend.put("일식-야채", Arrays.asList("야채덴푸라", "고보무침", "시금치나물", "가지튀김", "무샐러드", "연근튀김", "야채스프", "도토리묵"));

        // 중식
        recommend.put("중식-빵", Arrays.asList("화과자", "만두", "단팥찐빵", "파이", "흑설탕빵", "버터롤", "꿀빵", "호두빵"));
        recommend.put("중식-떡", Arrays.asList("펑궈", "니엔까오", "차오니엔까오", "찹쌀떡", "우마이떡", "떡고기찜", "홍두니엔까오", "참깨떡"));
        recommend.put("중식-면", Arrays.asList("짜장면", "짬뽕", "우육면", "마라탕면", "탄탄면", "비빔냉면", "라미엔", "유린면"));
        recommend.put("중식-쌀", Arrays.asList("볶음밥", "마파두부밥", "탕수육밥", "오므라이스", "사천볶음밥", "짜장밥", "고추잡채밥", "굴밥"));
        recommend.put("중식-고기", Arrays.asList("탕수육", "라조기", "꿔바로우", "마라샹궈", "고추잡채", "중국식닭튀김", "소고기볶음", "돼지고기찜"));
        recommend.put("중식-생선", Arrays.asList("칠리새우", "생선찜", "레몬생선", "사천식생선", "간장생선찜", "어향생선", "생선완자", "생선튀김"));
        recommend.put("중식-야채", Arrays.asList("청경채볶음", "마파가지", "야채스프", "토마토볶음", "중국식샐러드", "깐풍가지", "생강야채볶음", "야채만두"));

        // 양식
        recommend.put("양식-빵", Arrays.asList("크로와상", "갈릭브레드", "치아바타", "프레첼", "포카치아", "브리오슈", "버터롤", "베이글"));
        recommend.put("양식-떡", Arrays.asList("뇨끼", "라이스페이퍼롤", "떡볶이 응용", "호박떡", "두부떡", "밥떡", "베이컨떡말이", "치즈떡"));
        recommend.put("양식-면", Arrays.asList("스파게티", "페투치니 알프레도", "카펠리니", "라자냐", "마카로니", "볼로네제", "카르보나라", "뇨끼파스타"));
        recommend.put("양식-쌀", Arrays.asList("리조또", "파에야", "버섯밥", "오므라이스", "칠리라이스", "치킨라이스", "해산물라이스", "타이프라이드라이스"));
        recommend.put("양식-고기", Arrays.asList("스테이크", "미트로프", "바비큐 립", "햄버거 스테이크", "양갈비", "치킨커틀릿", "소고기구이", "터키로스트"));
        recommend.put("양식-생선", Arrays.asList("훈제연어", "피쉬앤칩스", "그릴드새우", "참치스테이크", "연어스테이크", "생선파이", "바삭생선튀김", "참치샌드위치"));
        recommend.put("양식-야채", Arrays.asList("샐러드", "라따뚜이", "야채스튜", "구운야채", "바질페스토샐러드", "비네거샐러드", "시금치그라탱", "찐야채"));

    }
    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wte_activity_main);
        initializeRecommendationData();

        // Spinner 초기화
        categorySpinner = findViewById(R.id.spinner_category);
        typeSpinner = findViewById(R.id.spinner_type);
        drawerLayout = findViewById(R.id.drawLayout);
        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // Toolbar를 액션바로 설정
        getSupportActionBar().setTitle("그래서 저녁 뭐먹음?");
        // 사이드바
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.hamberger);

        String[] categories = {"아무거나","한식", "일식", "중식", "양식"};
        String[] types = {"아무거나","빵", "떡", "면", "쌀", "고기", "생선", "야채"};


        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);


        // "카테고리 탐색" 버튼 클릭
        findViewById(R.id.button_category).setOnClickListener(v -> {
            Intent intent = new Intent(WTE_MainActivity.this, WTE_CategoryTournamentActivity.class);
            intent.putStringArrayListExtra("categories", new ArrayList<>(Arrays.asList(categories)));
            startActivityForResult(intent, 100);
        });


        // "종류 탐색" 버튼 클릭
        findViewById(R.id.button_type).setOnClickListener(v -> {
            Intent intent = new Intent(WTE_MainActivity.this, WTE_TournamentActivity.class);
            intent.putStringArrayListExtra("items", new ArrayList<>(Arrays.asList(types)));
            startActivityForResult(intent, 100);
        });

        findViewById(R.id.button_recommend).setOnClickListener(v -> {
            String selectedCategory = categorySpinner.getSelectedItem().toString();
            String selectedType = typeSpinner.getSelectedItem().toString();

            List<String> recommendResult = new ArrayList<>();

            if (selectedCategory.equals("아무거나") && selectedType.equals("아무거나")) {
                // 아무거나-아무거나: 모든 메뉴
                for (List<String> value : recommend.values()) {
                    recommendResult.addAll(value);
                }
            } else if (selectedCategory.equals("아무거나")) {
                // 아무거나-특정 타입: 해당 타입의 모든 카테고리 메뉴
                for (String key : recommend.keySet()) {
                    if (key.endsWith("-" + selectedType)) {
                        recommendResult.addAll(recommend.get(key));
                    }
                }
            } else if (selectedType.equals("아무거나")) {
                // 특정 카테고리-아무거나: 해당 카테고리의 모든 타입 메뉴
                for (String key : recommend.keySet()) {
                    if (key.startsWith(selectedCategory + "-")) {
                        recommendResult.addAll(recommend.get(key));
                    }
                }
            } else {
                // 특정 카테고리-특정 타입: 해당 조합 메뉴
                String key = selectedCategory + "-" + selectedType;
                recommendResult = recommend.getOrDefault(key, Arrays.asList("추천 결과가 없습니다."));
            }

            // ListView에 결과를 표시
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recommendResult);
            ListView listRecommend = findViewById(R.id.list_recommend);
            listRecommend.setAdapter(adapter);
        });


        ((NavigationView)findViewById(R.id.layout)).setNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.main1){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(WTE_MainActivity.this, MainActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.todo2){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(WTE_MainActivity.this, MemoMainActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.news3){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(WTE_MainActivity.this, NewsActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.alarm4){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(WTE_MainActivity.this, AlarmMainActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.shell5){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(WTE_MainActivity.this, GameMainActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.dinner6){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }

            }
            return false;
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            String selectedItem = data.getStringExtra("selectedItem");
            String selectedCategory = data.getStringExtra("selectedCategory");

            if (selectedItem != null) {
                // 선택된 항목을 두 번째 드롭다운에 반영
                int position = typeAdapter.getPosition(selectedItem);
                if (position >= 0) {
                    typeSpinner.setSelection(position);
                    Toast.makeText(this, "선택된 항목: " + selectedItem, Toast.LENGTH_SHORT).show();
                }
            } else if (selectedCategory != null) {
                // 선택된 카테고리를 첫 번째 드롭다운에 반영
                ArrayAdapter<String> categoryAdapter = (ArrayAdapter<String>) categorySpinner.getAdapter();
                int position = categoryAdapter.getPosition(selectedCategory);
                if (position >= 0) {
                    categorySpinner.setSelection(position);
                    Toast.makeText(this, "선택된 카테고리: " + selectedCategory, Toast.LENGTH_SHORT).show();
                }
            }
        }
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

}
