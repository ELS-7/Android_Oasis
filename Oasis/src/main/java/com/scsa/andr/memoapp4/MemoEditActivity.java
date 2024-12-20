package com.scsa.andr.memoapp4;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MemoEditActivity extends AppCompatActivity {
    private int position = -1;
    private int memoId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_edit);

        MemoDatabaseHelper dbHelper = new MemoDatabaseHelper(this);

        EditText editTitle = findViewById(R.id.edit_title);
        EditText editBody = findViewById(R.id.edit_body);
        TextView tvMemoTime = findViewById(R.id.tv_memo_time);
        CheckBox checkBold = findViewById(R.id.check_bold);  // isBold 체크박스
        CheckBox checkMust = findViewById(R.id.check_must);  // isMust 체크박스
        Button btnSave = findViewById(R.id.btn_save);
        Button btnDelete = findViewById(R.id.btn_delete);
        Button btnCancel = findViewById(R.id.btn_cancel);

        Intent intent = getIntent();
        int memoId = intent.getIntExtra("memoId", -1);

        if (memoId != -1) {
            // 해당 ID의 메모 불러오기
            MemoDto memo = dbHelper.getMemoById(memoId);
            editTitle.setText(memo.getTitle());
            editBody.setText(memo.getBody());
            tvMemoTime.setText("작성 시간: " + memo.getRegDate());
            checkBold.setChecked(memo.isBold());  // isBold 상태 설정
            checkMust.setChecked(memo.isMust());  // isMust 상태 설정
        } else {
            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            tvMemoTime.setText("작성 시간: " + currentTime);
        }

        // 저장 버튼
        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            String body = editBody.getText().toString();
            String regDate = tvMemoTime.getText().toString().replace("작성 시간: ", "");
            boolean isBold = checkBold.isChecked();
            boolean isMust = checkMust.isChecked();

            if (memoId == -1) {
                dbHelper.saveMemo(new MemoDto(title, body, regDate, isBold, isMust));
            } else {
                dbHelper.updateMemo(new MemoDto(memoId, title, body, regDate, isBold, isMust)); // 수정된 메모 업데이트
            }

            setResult(RESULT_OK);
            finish();
        });

        // 삭제 버튼
        btnDelete.setOnClickListener(v -> {
            if (memoId != -1) {
                dbHelper.deleteMemo(memoId);
            }
            setResult(RESULT_OK);
            finish();
        });

        // 취소 버튼
        btnCancel.setOnClickListener(v -> finish());
    }
}
