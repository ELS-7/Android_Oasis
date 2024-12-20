package com.scsa.andr.memoapp4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class MemoDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "memo.db";
    private static final int DATABASE_VERSION = 2; // 버전 변경

    public static final String TABLE_MEMO = "memos";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_IS_BOLD = "isBold"; // 새 열 추가
    public static final String COLUMN_IS_MUST = "isMust"; // 새 열 추가

    public MemoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_MEMO + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_IS_BOLD + " INTEGER DEFAULT 0, " + // 기본값 설정
                COLUMN_IS_MUST + " INTEGER DEFAULT 0)";   // 기본값 설정
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_MEMO + " ADD COLUMN " + COLUMN_IS_BOLD + " INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_MEMO + " ADD COLUMN " + COLUMN_IS_MUST + " INTEGER DEFAULT 0");
        }
    }

    // 메모 전체 가져오기
    public ArrayList<MemoDto> getAllMemos() {
        ArrayList<MemoDto> memoList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_MEMO, null, null, null, null, null, COLUMN_DATE + " ASC");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
            boolean isBold = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_BOLD)) == 1;
            boolean isMust = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_MUST)) == 1;

            memoList.add(new MemoDto(id, title, content, date, isBold, isMust));
        }
        cursor.close();
        return memoList;
    }

    // 메모 저장
    public void saveMemo(MemoDto memo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, memo.getTitle());
        values.put(COLUMN_CONTENT, memo.getBody());
        values.put(COLUMN_DATE, memo.getRegDate());
        values.put(COLUMN_IS_BOLD, memo.isBold() ? 1 : 0);
        values.put(COLUMN_IS_MUST, memo.isMust() ? 1 : 0);
        db.insert(TABLE_MEMO, null, values);
    }

    // 메모 수정
    public void updateMemo(MemoDto memo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, memo.getTitle());
        values.put(COLUMN_CONTENT, memo.getBody());
        values.put(COLUMN_DATE, memo.getRegDate());
        values.put(COLUMN_IS_BOLD, memo.isBold() ? 1 : 0);
        values.put(COLUMN_IS_MUST, memo.isMust() ? 1 : 0);
        db.update(TABLE_MEMO, values, COLUMN_ID + " = ?", new String[]{String.valueOf(memo.getId())});
    }

    // 특정 ID의 메모 가져오기
    public MemoDto getMemoById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        MemoDto memo = null;

        Cursor cursor = db.query(TABLE_MEMO, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
            boolean isBold = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_BOLD)) == 1;
            boolean isMust = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_MUST)) == 1;

            memo = new MemoDto(id, title, content, date, isBold, isMust);
        }
        cursor.close();
        return memo;
    }

    // 메모 삭제
    public void deleteMemo(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_MEMO, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }
}
