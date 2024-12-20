package com.scsa.andr.memoapp4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class MemoSingleton {
    private static MemoSingleton instance;
    private final MemoDatabaseHelper dbHelper;

    private MemoSingleton(Context context) {
        dbHelper = new MemoDatabaseHelper(context.getApplicationContext());
    }

    public static MemoSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new MemoSingleton(context);
        }
        return instance;
    }

    // SQLite에서 메모 리스트 가져오기
    public ArrayList<MemoDto> getMemoList() {
        ArrayList<MemoDto> memoList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                MemoDatabaseHelper.TABLE_MEMO,
                null,
                null,
                null,
                null,
                null,
                MemoDatabaseHelper.COLUMN_DATE + " DESC"
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MemoDatabaseHelper.COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MemoDatabaseHelper.COLUMN_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(MemoDatabaseHelper.COLUMN_CONTENT));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(MemoDatabaseHelper.COLUMN_DATE));
            boolean isBold = cursor.getInt(cursor.getColumnIndexOrThrow(MemoDatabaseHelper.COLUMN_IS_BOLD)) == 1;
            boolean isMust = cursor.getInt(cursor.getColumnIndexOrThrow(MemoDatabaseHelper.COLUMN_IS_MUST)) == 1;

            memoList.add(new MemoDto(id, title, content, date, isBold, isMust));
        }
        cursor.close();
        return memoList;
    }

    // SQLite에 새 메모 추가
    public void addMemo(MemoDto memo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MemoDatabaseHelper.COLUMN_TITLE, memo.getTitle());
        values.put(MemoDatabaseHelper.COLUMN_CONTENT, memo.getBody());
        values.put(MemoDatabaseHelper.COLUMN_DATE, memo.getRegDate());
        values.put(MemoDatabaseHelper.COLUMN_IS_BOLD, memo.isBold() ? 1 : 0);
        values.put(MemoDatabaseHelper.COLUMN_IS_MUST, memo.isMust() ? 1 : 0);

        db.insert(MemoDatabaseHelper.TABLE_MEMO, null, values);
    }

    // SQLite에서 메모 업데이트
    public void updateMemo(int id, MemoDto memo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MemoDatabaseHelper.COLUMN_TITLE, memo.getTitle());
        values.put(MemoDatabaseHelper.COLUMN_CONTENT, memo.getBody());
        values.put(MemoDatabaseHelper.COLUMN_DATE, memo.getRegDate());
        values.put(MemoDatabaseHelper.COLUMN_IS_BOLD, memo.isBold() ? 1 : 0);
        values.put(MemoDatabaseHelper.COLUMN_IS_MUST, memo.isMust() ? 1 : 0);

        db.update(
                MemoDatabaseHelper.TABLE_MEMO,
                values,
                MemoDatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // SQLite에서 메모 삭제
    public void deleteMemo(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(
                MemoDatabaseHelper.TABLE_MEMO,
                MemoDatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }
}
