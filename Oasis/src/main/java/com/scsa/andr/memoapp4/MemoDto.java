package com.scsa.andr.memoapp4;

public class MemoDto {
    private int id; // 데이터베이스의 고유 ID 필드
    private String title;
    private String body;
    private String regDate;
    private boolean isBold; // 새로 추가된 필드
    private boolean isMust; // 새로 추가된 필드

    // 기본 생성자
    public MemoDto() {}

    // 전체 필드를 사용하는 생성자
    public MemoDto(int id, String title, String body, String regDate, boolean isBold, boolean isMust) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.regDate = regDate;
        this.isBold = isBold;
        this.isMust = isMust;
    }

    // ID 없이 사용하는 생성자 (새 메모 추가 시 사용)
    public MemoDto(String title, String body, String regDate, boolean isBold, boolean isMust) {
        this.title = title;
        this.body = body;
        this.regDate = regDate;
        this.isBold = isBold;
        this.isMust = isMust;
    }

    // Getter and Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean isBold) {
        this.isBold = isBold;
    }

    public boolean isMust() {
        return isMust;
    }

    public void setMust(boolean isMust) {
        this.isMust = isMust;
    }
}
