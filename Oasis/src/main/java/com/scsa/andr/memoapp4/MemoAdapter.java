package com.scsa.andr.memoapp4;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;

public class MemoAdapter extends android.widget.BaseAdapter {
    private final Context context;
    private final List<MemoDto> memoList;

    public MemoAdapter(Context context, List<MemoDto> memoList) {
        this.context = context;
        this.memoList = memoList;
    }

    @Override
    public int getCount() {
        return memoList.size();
    }

    @Override
    public Object getItem(int position) {
        return memoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.memo_item, parent, false);
        }

        // memo_item.xml에서 텍스트뷰 찾기
        TextView title = convertView.findViewById(R.id.memo_title);
        TextView memoTime = convertView.findViewById(R.id.memo_time); // 작성 시간
        TextView memoText = convertView.findViewById(R.id.memo_text); // 미리보기 텍스트
        MemoDto memo = memoList.get(position);

        // 제목 설정
        String titleText = memo.getTitle();

        // 강조 상태가 true일 경우 title을 붉은 색과 별표로 설정
        if (memo.isMust()) {
            title.setTextColor(Color.RED);  // 붉은 글씨로 설정
            titleText += "   ★";  // 제목 옆에 별표 추가
            title.setTextSize(24);  // 제목 글씨 크기 크게 설정
            memoText.setTypeface(null, Typeface.BOLD);  // 상세 내용도 굵게
            memoText.setTextSize(18);  // 내용 글씨 크기도 크게 설정
        } else {
            title.setTextColor(Color.BLACK);  // 기본 색상으로 설정
            memoText.setTypeface(null, Typeface.NORMAL);  // 상세 내용 기본 글꼴
            title.setTextSize(18);  // 제목 글씨 크기 크게 설정
            memoText.setTextSize(12);  // 내용 글씨 크기도 크게 설정
        }

        // 제목 텍스트 설정
        title.setText(titleText);

        // isBold()에 따른 제목의 스타일 설정
        if (memo.isBold()) {
            title.setTextColor(Color.GRAY);  // 회색으로 설정
            title.setTypeface(null, Typeface.BOLD);  // 볼드로 설정
        } else {
            title.setTypeface(null, Typeface.NORMAL);  // 기본 글꼴로 설정
        }

        // 작성 시간 설정
        memoTime.setText(memo.getRegDate());  // 예시: "2024-12-06 12:30"

        // 메모 내용 설정 (미리보기)
        String previewText = memo.getBody();  // getText() 메서드로 본문 내용만 사용
        if (previewText.length() > 100) {
            previewText = previewText.substring(0, 100) + "..."; // 내용이 길 경우 100자까지만 보이도록
        }
        memoText.setText(previewText);

        // 짝수/홀수 행에 따라 배경색 설정
        if (position % 2 == 0) {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.even_row_color));
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.odd_row_color));
        }

        return convertView;
    }





    // ViewHolder 클래스 정의
    static class ViewHolder {
        TextView title;
        TextView text;
    }
}
