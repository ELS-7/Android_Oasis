package com.scsa.andr.memoapp4;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MemoMainActivity extends AppCompatActivity {
    private ArrayList<MemoDto> memoList;
    private MemoAdapter adapter;
    private MemoDatabaseHelper dbHelper; // 1202 추가 - SQLite 데이터베이스 헬퍼
    private NfcAdapter nfcAdapter; // NFC 어댑터 선언
    private String lastProcessedTag = null; // 싱글탑 문제 해결용
    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_main);
        drawerLayout = findViewById(R.id.drawLayout);
        // NFC 어댑터 초기화
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "이 기기는 NFC를 지원하지 않습니다. NFC 없이 작동합니다.", Toast.LENGTH_SHORT).show();
        } else if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC를 활성화해주세요.", Toast.LENGTH_SHORT).show();
        }

        // Database Helper 초기화
        dbHelper = new MemoDatabaseHelper(this);

        // SQLite에서 데이터 가져오기
        memoList = dbHelper.getAllMemos();
        adapter = new MemoAdapter(this, memoList);

        ListView listView = findViewById(R.id.memo_list);
        listView.setAdapter(adapter);

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // Toolbar를 액션바로 설정
        getSupportActionBar().setTitle("Todo");
        // 사이드바
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.hamberger);

        // 항목 클릭 시 수정 페이지로 이동
        listView.setOnItemClickListener((parent, view, position, id) -> {
            MemoDto memo = memoList.get(position);  // 클릭한 메모 가져오기
            Intent intent = new Intent(MemoMainActivity.this, MemoEditActivity.class);
            intent.putExtra("memoId", memo.getId()); // 해당 메모의 ID 전달
            startActivityForResult(intent, 1); // 결과를 받을 수 있도록 startActivityForResult() 사용
        });

        // 콘텍스트 메뉴 등록
        registerForContextMenu(listView);

        // FAB 등록 버튼 처리
        FloatingActionButton fab = findViewById(R.id.fab_add_memo);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, MemoEditActivity.class);
            startActivityForResult(intent, 2); // 새로운 메모 작성 시에도 startActivityForResult() 사용
        });

        // 권한 확인 및 요청
        checkSmsPermissions();

        ((NavigationView)findViewById(R.id.layout)).setNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.main1){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(MemoMainActivity.this, MainActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.todo2){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
            } else if(item.getItemId() == R.id.news3){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(MemoMainActivity.this, NewsActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.alarm4){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(MemoMainActivity.this, AlarmMainActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.shell5){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(MemoMainActivity.this, GameMainActivity.class);
                startActivity(intent);
            } else if(item.getItemId() == R.id.dinner6){
                if(drawerLayout.isOpen()){
                    drawerLayout.closeDrawers();
                }
                Intent intent = new Intent(MemoMainActivity.this, WTE_MainActivity.class);
                startActivity(intent);
            }
            return false;
        });

    }

    // SMS 권한 확인 및 요청
    private void checkSmsPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

            // "다시 묻지 않기" 여부 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECEIVE_SMS) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_SMS)) {
                showPermissionExplanationDialog();
            } else {
                // 권한 요청
                requestSmsPermissions();
            }
        }
    }

    // 권한 요청
    private void requestSmsPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.READ_SMS
        }, 1);
    }

    // 권한 설명 다이얼로그 표시
    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("SMS 권한 필요")
                .setMessage("이 앱은 SMS를 수신하고 메모로 저장하기 위해 권한이 필요합니다. 계속하려면 권한을 허용해주세요.")
                .setPositiveButton("확인", (dialog, which) -> requestSmsPermissions())
                .setNegativeButton("취소", (dialog, which) -> {
                    Toast.makeText(this, "권한이 거부되어 SMS를 처리할 수 없습니다.", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    // 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                // "다시 묻지 않기"가 선택된 경우
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECEIVE_SMS)) {
                    showSettingsRedirectDialog();
                } else {
                    Toast.makeText(this, "SMS 권한이 거부되었습니다. 메시지를 처리할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 설정 화면으로 이동하는 다이얼로그 표시
    private void showSettingsRedirectDialog() {
        new AlertDialog.Builder(this)
                .setTitle("권한 필요")
                .setMessage("SMS 권한이 영구적으로 거부되었습니다. 앱 설정에서 권한을 허용해주세요.")
                .setPositiveButton("설정으로 이동", (dialog, which) -> {
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                })
                .setNegativeButton("취소", (dialog, which) -> {
                    Toast.makeText(this, "권한이 거부되어 앱 기능이 제한됩니다.", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    // 액티비티에서 돌아온 후 리스트 갱신
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // SQLite 데이터 갱신
            memoList.clear();
            memoList.addAll(dbHelper.getAllMemos());
            adapter.notifyDataSetChanged();
        }
    }

    private final BroadcastReceiver memoUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        // 로컬 브로드캐스트 등록
        LocalBroadcastManager.getInstance(this).registerReceiver(
                memoUpdateReceiver,
                new IntentFilter("UPDATE_MEMO_LIST")
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 로컬 브로드캐스트 해제
        LocalBroadcastManager.getInstance(this).unregisterReceiver(memoUpdateReceiver);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        MemoDto memo = memoList.get(info.position);  // 선택한 메모 가져오기
        if (item.getItemId() == R.id.menu_delete) {
            showDeleteConfirmationDialog(info.position);
            return true;
        } else if (item.getItemId() == R.id.menu_todo) {
            changeBold(memo, info.position);  // changeBold 수정
            return true;
        } else if (item.getItemId() == R.id.menu_must) {
            changeMust(memo, info.position);  // changeMust 수정
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("삭제 확인")
                .setMessage("정말로 메모를 삭제하시겠습니까?")
                .setPositiveButton("예", (dialog, which) -> {
                    MemoDto memo = memoList.get(position);
                    dbHelper.deleteMemo(memo.getId()); // SQLite에서 삭제
                    memoList.remove(position); // 리스트에서도 제거
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton("아니오", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close(); // 데이터베이스 닫기
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void changeBold(MemoDto memo, int position) {
        // Bold 상태 토글
        memo.setBold(!memo.isBold());

        // 데이터베이스 업데이트
        dbHelper.updateMemo(memo);

        // 리스트 갱신
        memoList.set(position, memo);
        adapter.notifyDataSetChanged();
    }

    private void changeMust(MemoDto memo, int position) {
        // Must 상태 토글
        memo.setMust(!memo.isMust());

        // 데이터베이스 업데이트
        dbHelper.updateMemo(memo);

        // 리스트 갱신
        memoList.set(position, memo);
        adapter.notifyDataSetChanged();
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

    //    강조표기 유지
    @Override
    protected void onResume() {
        super.onResume();
        enableNfcForegroundDispatch(); // NFC 태그 감지 활성화
        // 메모 리스트를 새로 로드하고 어댑터를 갱신
        memoList.clear();
        memoList.addAll(dbHelper.getAllMemos());  // DB에서 최신 메모 리스트 가져오기
        adapter.notifyDataSetChanged();  // 어댑터 갱신
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableNfcForegroundDispatch(); // NFC 태그 감지 비활성화
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) ||
                NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()) ||
                NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                // 태그 데이터 추출
                String tagId = bytesToHex(tag.getId());
                Log.d("NFC", "Detected Tag ID: " + tagId);

                // NFC 데이터 처리
                processNfcTag(intent);
            }
        }
    }

    private void enableNfcForegroundDispatch() {
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_MUTABLE);

        IntentFilter[] intentFilters = new IntentFilter[]{
                new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        };

        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private void disableNfcForegroundDispatch() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    private void processNfcTag(Intent intent) {
        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMessages != null) {
            for (Parcelable rawMessage : rawMessages) {
                NdefMessage ndefMessage = (NdefMessage) rawMessage;
                for (NdefRecord record : ndefMessage.getRecords()) {
                    String payload = new String(record.getPayload());

                    // ':' 뒤의 숫자값만 추출
                    String extractedValue = extractValueAfterColon(payload);

                    if (extractedValue != null) {
                        try {
                            int index = Integer.parseInt(extractedValue); // 숫자로 변환
                            if (index >= 1) {
                                index--; // 0 기반 인덱스로 조정
                            }

                            // 바로 메모 수정 또는 생성 페이지로 이동
                            handleMemoNavigation(index);
                            return; // 처리 후 종료
                        } catch (NumberFormatException e) {
                            createNewMemo();
                            Toast.makeText(this, "잘못된 숫자 형식입니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "유효한 데이터를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            Toast.makeText(this, "NFC 태그가 비어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }


    private void handleMemoNavigation(int index) {
        // 음수인 경우 바로 새 메모 생성 페이지로 이동
        if (index < 0) {
            createNewMemo();
            return;
        }

        // 리스트 범위 내에 해당 메모가 있는지 확인
        if (index >= 0 && index < memoList.size()) {
            MemoDto memo = memoList.get(index);
            Intent intent = new Intent(MemoMainActivity.this, MemoEditActivity.class);
            intent.putExtra("memoId", memo.getId()); // 해당 메모의 ID 전달
            startActivity(intent); // 수정 페이지로 이동
        } else {
            // 해당 인덱스에 메모가 없으면 새 메모 생성
            createNewMemo();
        }
    }

    private void createNewMemo() {
        Intent intent = new Intent(this, MemoEditActivity.class);
        startActivity(intent); // 새로운 메모 작성 페이지로 이동
    }

    private String extractValueAfterColon(String input) {
        if (input.contains(":")) {
            String[] parts = input.split(":"); // ':'로 문자열 분리
            if (parts.length > 1) {
                return parts[1]; // ':' 뒤의 값을 반환
            }
        }
        return null; // ':'가 없거나 유효하지 않은 경우 null 반환
    }


}
