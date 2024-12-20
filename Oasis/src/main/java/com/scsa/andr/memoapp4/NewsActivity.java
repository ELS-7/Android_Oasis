package com.scsa.andr.memoapp4;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.android.material.navigation.NavigationView;
import com.scsa.andr.memoapp4.databinding.NewsrowBinding;

public class NewsActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity_SCSA";
    ListView listView;
    MyAdapter adapter;
    List<NewsHaniItem> list = new ArrayList<>();
    Button btnHani, btnChosun, btnYna; // 버튼색 변경
    DrawerLayout drawerLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity_main);
        drawerLayout = findViewById(R.id.drawLayout);
        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // Toolbar를 액션바로 설정
        getSupportActionBar().setTitle("News");
        // 사이드바
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.hamberger);


        listView = findViewById(R.id.result);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);

//        버튼색
        btnHani = findViewById(R.id.btn_hani);
        btnChosun = findViewById(R.id.btn_chosun);
        btnYna = findViewById(R.id.btn_yna);

//        기본세팅 한겨례
        new MyAsyncTask().execute("https://www.hani.co.kr/rss/");
        setSelectedButton(btnHani);

        // 버튼 클릭 이벤트 설정
        findViewById(R.id.btn_hani).setOnClickListener(v -> {
            loadNews("https://www.hani.co.kr/rss/");
            setSelectedButton(btnHani);
        });
        findViewById(R.id.btn_chosun).setOnClickListener(v -> {
            loadNews("https://www.chosun.com/arc/outboundfeeds/rss/?outputType=xml");
            setSelectedButton(btnChosun);
        });
        findViewById(R.id.btn_yna).setOnClickListener(v -> {
            loadNews("https://www.yna.co.kr/rss/news.xml");
            setSelectedButton(btnYna);
        });


        listView.setOnItemClickListener((parent, view, position, id) -> {
            NewsHaniItem item = list.get(position); //받아온 데이터
            // 다이얼로그 생성
            new android.app.AlertDialog.Builder(NewsActivity.this)
                    .setTitle("해당 뉴스 페이지로 이동하시겠습니까?")
                    .setMessage("카테고리: " + (item.category != null ? item.category : "-") + "\n"
                            + "발행일: " + (item.pubDate != null ? item.pubDate.toString() : "-"))
                    .setPositiveButton("예", (dialog, which) -> {
                        // '예' 버튼 클릭 시 링크 열기
                        String url = item.link;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    })
                    .setNegativeButton("아니오", (dialog, which) -> {
                        // '아니오' 버튼 클릭 시 아무 작업도 하지 않음
                        dialog.dismiss();
                    })
                    .show();
        });


        ((NavigationView)findViewById(R.id.layout)).setNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.main1){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(NewsActivity.this, MainActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.todo2){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(NewsActivity.this, MemoMainActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.news3){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }

            } else if(item.getItemId() == R.id.alarm4){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(NewsActivity.this, AlarmMainActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.shell5){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(NewsActivity.this, GameMainActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.dinner6){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(NewsActivity.this, WTE_MainActivity.class);
                startActivity(intent);
            }
            return false;
        });
    }



    private void setSelectedButton(Button selectedButton) {
        // 모든 버튼의 배경 색을 원래대로 초기화
        resetButtonColors();

        // 클릭된 버튼의 배경 색을 진하게 변경
        selectedButton.setBackgroundColor(getResources().getColor(R.color.selected));
    }

    private void resetButtonColors() {
        // 각 버튼의 배경 색을 기본 색으로 설정
        btnHani.setBackgroundColor(getResources().getColor(R.color.odd_row_color));
        btnChosun.setBackgroundColor(getResources().getColor(R.color.odd_row_color));
        btnYna.setBackgroundColor(getResources().getColor(R.color.odd_row_color));
    }


    // 뉴스 로드 메서드
    private void loadNews(String rssUrl) {
        list.clear();
        new MyAsyncTask().execute(rssUrl);

    }

    class MyAsyncTask extends AsyncTask<String, String, List<NewsHaniItem>> {

        @Override
        protected List<NewsHaniItem> doInBackground(String... arg) {
            try {
                Log.d(TAG, "connection start....");
                Log.d(TAG, "RSS : Connecting to URL: " + arg[0]); // 여기에서 URL을 로그로 출력
                InputStream input = new URL(arg[0]).openConnection().getInputStream();
                Log.d(TAG, "connection ok....");

                parsing(new BufferedReader(new InputStreamReader(input)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return list;
        }

        //data 조회 완료.
        protected void onPostExecute(List<NewsHaniItem> result) {
            Log.d(TAG, "onPostExecute: "+list.size());
            adapter.notifyDataSetChanged();
        }

        XmlPullParser parser = Xml.newPullParser();
        private void parsing(Reader reader) throws Exception {
            parser.setInput(reader);
            Log.d(TAG, "parsing: " + parser.getNamespace());
            int eventType = parser.getEventType();
            NewsHaniItem item = null;
            long id = 0;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        Log.d(TAG, "RSS: START_TAG: " + name); // 태그 이름 로그 추가
                        if (name.equalsIgnoreCase("item")) {
                            item = new NewsHaniItem();
                            item.id = ++id;
                        } else if (item != null) {
                            if (name.equalsIgnoreCase("title")) {
                                item.title = parser.nextText();

                                Log.d(TAG, "중앙일보 RSS 데이터: " + item.title);

                            } else if (name.equalsIgnoreCase("link")) {
                                item.link = parser.nextText();
                            } else if (name.equalsIgnoreCase("description")) {
                                item.description = parser.nextText();
                            } else if (name.equalsIgnoreCase("pubDate")) {
                                item.pubDate = new Date(parser.nextText());
                            } else if("dc".equalsIgnoreCase(parser.getPrefix())){   // namespace
                                if (name.equalsIgnoreCase("subject")) {
                                    item.subject = parser.nextText();
                                }else if (name.equalsIgnoreCase("category")) {
                                    item.category = parser.nextText();
                                }
                            }
//                            else if (name.equalsIgnoreCase("subject")) { // namespace를 무시해도 이슈없음.
//                                item.subject = parser.nextText();
//                            }
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        Log.d(TAG, "RSS : END_TAG: " + name); // END_TAG 로그 추가
                        if (name.equalsIgnoreCase("item") && item != null) {
                            list.add(item);
                            Log.d(TAG, "RSS : Item added: " + item.title);  // item이 list에 추가될 때 로그 추가
                        }
                        break;
                }
                eventType = parser.next();
            }
        }
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;

            if (convertView == null) {
                NewsrowBinding binding = NewsrowBinding.inflate(LayoutInflater.from(NewsActivity.this), viewGroup, false);
//                RowBinding binding = RowBinding.inflate(LayoutInflater.from(MainActivity.this), viewGroup, false);
                convertView = binding.getRoot();

                holder = new ViewHolder();
                holder.title = binding.title;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            NewsHaniItem item = list.get(position);

            holder.title.setText(item.title);

            return convertView;
        }

        class ViewHolder {
            TextView title;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return list.get(i).id;
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
        if(item.getItemId() == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
        }
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
