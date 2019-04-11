package kr.ac.gachon.www.buslinker.Entity;

public class Member {
    public static final int KAKAO_MEMBER=1, NORMAL_MEMBER=2;
    public static Member user;   //현재 로그인된 계정
    String email;
    String name;
    String profilePath;
    String profileThumbPath;
    int category;

    public Member(String email, String name, int category) {  //프로필 사진이 있을 경우
        this.email=email;
        this.name=name;
        this.category=category;
    }

    public Member(String email, String name, String profilePath, String profileThumbPath, int category) {  //카카오 계정으로 프로필 사진이 있을 경우
        this.email=email;
        this.name=name;
        this.profilePath=profilePath;
        this.category=category;
        this.profileThumbPath=profileThumbPath;
    }

    @Override
    public String toString() {
        String result="사용자: 이름: "+name+" 이메일: "+email+" 프로필 사진 경로: "+profilePath;
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

    public String getProfileThumbPath() {
        return profileThumbPath;
    }
}
