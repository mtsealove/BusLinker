package kr.ac.gachon.www.buslinker.Entity;

import java.util.ArrayList;
import java.util.Date;

public class Member {
    public static final int KAKAO_MEMBER = 1, NORMAL_MEMBER = 2, FACEBOOK_MEMBER = 3;
    public static Member user;   //현재 로그인된 계정
    String email;   //메일 주소
    String name;    //이름
    String gender;  //성별
    String birth;   //생일
    String profilePath; //프로필 사진 경로
    Object profileThumbPath;    //프로필 사진 썸네일 경로
    int category;   //사용자 종류
    ArrayList<DealLog> dealLogs;    //거래내역
    ArrayList<DealLog> RecentDealLogs;  //최근 거래내역

    public Member() {
        dealLogs = new ArrayList<>();
        RecentDealLogs = new ArrayList<>();
    }

    public Member(String email, String name, String gender, String birth, int category, String profilePath) {
        this();
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.birth = birth;
        this.category = category;
        this.profilePath = profilePath;
    }


    public Member(String email, String name, int category) {  //프로필 사진이 있을 경우
        this();
        this.email=email;
        this.name=name;
        this.category=category;
    }

    public Member(String email, String name, String profilePath, String profileThumbPath, int category) {  //카카오 계정으로 프로필 사진이 있을 경우
        this();
        this.email=email;
        this.name=name;
        this.profilePath=profilePath;
        this.category=category;
        this.profileThumbPath=profileThumbPath;
    }

    public Member(Object profilePath, int category) {
        this();
        this.category = category;
        this.profileThumbPath = profilePath;
    }

    public void AddDealLog(DealLog dealLog) {   //거래내역 추가
        dealLogs.add(dealLog);
        IsRecent(dealLog);  //최근이면 추가
    }

    private void IsRecent(DealLog dealLog) { //91일 이하인지 계산
        Date date = new Date();
        long diff = date.getTime() - dealLog.getPayTime().getTime();
        long diffDay = diff / (24 * 60 * 60 * 1000);
        if (diffDay <= 91) RecentDealLogs.add(dealLog);    //3개월 내면 추가
    }

    public int GetRecentDealCount() {
        return RecentDealLogs.size();
    }

    public ArrayList<DealLog> getDealLogs() {
        return dealLogs;
    }

    public ArrayList<DealLog> getRecentDealLogs() {
        return RecentDealLogs;
    }

    @Override
    public String toString() {
        String result = "사용자: 이름: " + name + " 이메일: " + email + " 프로필 사진 경로: " + profilePath + "거래내역: " + dealLogs.size() + " 건";
        return  result;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public int getCategory() {
        return category;
    }

    public Object getProfileThumbPath() {
        return profileThumbPath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public void setProfileThumbPath(String profileThumbPath) {
        this.profileThumbPath = profileThumbPath;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }
}
