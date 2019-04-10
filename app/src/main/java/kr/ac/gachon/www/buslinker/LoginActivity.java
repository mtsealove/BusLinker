package kr.ac.gachon.www.buslinker;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kr.ac.gachon.www.buslinker.Kakao.SessionCallback;
import kr.ac.gachon.www.buslinker.Views.SystemUiTuner;

public class LoginActivity extends AppCompatActivity {
    EditText IDET, passwordET;
    Button loginBtn, signUpBtn;
    SessionCallback callback;
    com.kakao.usermgmt.LoginButton kakaoLoginBtn;

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

        kakaoLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Session.getCurrentSession().addCallback(callback);
                requestMe();
            }
        });
    }

    private void signUp() {
        Intent intent=new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    protected void requestMe() {
        UserManagement.getInstance().requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                System.out.println("오류: "+errorResult);
            }

            @Override
            public void onNotSignedUp() {
                System.out.println("회원 아님");
            }

            @Override
            public void onSuccess(UserProfile result) {
                System.out.println("유저 프로필"+result.toString());
                System.out.println("ID: "+result.getId());
                Toast.makeText(LoginActivity.this, "사용자 ID:"+result.getUUID(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
