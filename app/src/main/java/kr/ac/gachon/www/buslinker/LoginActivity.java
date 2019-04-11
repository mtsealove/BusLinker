package kr.ac.gachon.www.buslinker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import kr.ac.gachon.www.buslinker.Entity.Member;
import kr.ac.gachon.www.buslinker.Kakao.KakaoLoginActivity;
import kr.ac.gachon.www.buslinker.Views.SystemUiTuner;

public class LoginActivity extends AppCompatActivity {
    EditText IDET, passwordET;
    Button loginBtn, signUpBtn;
    com.kakao.usermgmt.LoginButton kakaoLoginBtn;
    private SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        IDET=findViewById(R.id.IDET);
        passwordET=findViewById(R.id.passwordET);
        loginBtn=findViewById(R.id.loginBtn);
        signUpBtn=findViewById(R.id.signUpBtn);
        kakaoLoginBtn=findViewById(R.id.kakaoLoginBtn);

        SystemUiTuner systemUiTuner=new SystemUiTuner(LoginActivity.this);
        systemUiTuner.setStatusBarWhite();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        callback=new SessionCallback();

        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().close();
    }

    private void signUp() {
        Intent intent=new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private void redirectActivity() {
        Intent intent=new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    protected void requestKakaoAccount() {  //카카오 계정 요청
        System.out.println("세션 열림");
        UserManagement.getInstance().requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                System.out.println("세션 오류");
            }

            @Override
            public void onNotSignedUp() {
                System.out.println("회원 아님");
            }

            @Override
            public void onSuccess(UserProfile result) {
                System.out.println("성공 "+result.toString());
                String email=result.getEmail();
                String name=result.getNickname();
                String profilePath=result.getProfileImagePath();
                String profileThumbPath=result.getThumbnailImagePath();
                boolean verified=result.getEmailVerified();
                if(verified) {  //이메일 인증이 된 사용자라면
                    Member.user=new Member(email, name, profilePath,profileThumbPath, Member.KAKAO_MEMBER);
                    System.out.println(Member.user.toString());
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this,"카카오톡 이메일 인증이 되지 않았습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            requestKakaoAccount();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            System.out.println("세션 열기 실패");
            if(exception != null) {
                Logger.e(exception);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            System.out.println("액티비티 결과");
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
