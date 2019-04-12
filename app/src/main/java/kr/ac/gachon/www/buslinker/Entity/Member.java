package kr.ac.gachon.www.buslinker.Entity;

public class Member {
    public static final int KAKAO_MEMBER = 1, NORMAL_MEMBER = 2, FACEBOOK_MEMBER = 3;
    public static Member user;   //현재 로그인된 계정
    String email;
    String name;
    String profilePath;
    Object profileThumbPath;
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

    public Member(Object profilePath, int category) {
        this.category = category;
        this.profileThumbPath = profilePath;
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
