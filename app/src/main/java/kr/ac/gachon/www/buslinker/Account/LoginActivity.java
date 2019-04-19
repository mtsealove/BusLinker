package kr.ac.gachon.www.buslinker.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.Profile;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import kr.ac.gachon.www.buslinker.Entity.DealLog;
import kr.ac.gachon.www.buslinker.Entity.Member;
import kr.ac.gachon.www.buslinker.Facebook.FacebookLoginCallback;
import kr.ac.gachon.www.buslinker.R;
import kr.ac.gachon.www.buslinker.Views.SystemUiTuner;

public class LoginActivity extends AppCompatActivity {
    EditText IDET, passwordET;
    Button loginBtn, signUpBtn;
    com.kakao.usermgmt.LoginButton kakaoLoginBtn;
    com.facebook.login.widget.LoginButton facebookLoginBtn;
    private SessionCallback KakaoCallback;
    private FacebookLoginCallback facebookLoginCallback;
    private com.facebook.CallbackManager facebookCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        IDET=findViewById(R.id.IDET);
        passwordET=findViewById(R.id.passwordET);
        loginBtn=findViewById(R.id.loginBtn);
        signUpBtn=findViewById(R.id.signUpBtn);
        kakaoLoginBtn=findViewById(R.id.kakaoLoginBtn);
        facebookLoginBtn = findViewById(R.id.facebookLoginBtn);

        SystemUiTuner systemUiTuner=new SystemUiTuner(LoginActivity.this);
        systemUiTuner.setStatusBarWhite();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        KakaoCallback = new SessionCallback();

        //카카오 콜백 설정
        Session.getCurrentSession().addCallback(KakaoCallback);
        Session.getCurrentSession().close();

        //페이스북 콜백 설정
        facebookCallbackManager = com.facebook.CallbackManager.Factory.create();
        facebookLoginCallback = new FacebookLoginCallback();
        facebookLoginBtn.setReadPermissions(Arrays.asList("public_profile"));
        facebookLoginBtn.registerCallback(facebookCallbackManager, facebookLoginCallback);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginAsBusLinker();
            }
        });
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

    String password;

    protected void LoginAsBusLinker() { //버스링커 계정으로 로그인
        String ID = IDET.getText().toString();
        password = passwordET.getText().toString();
        //ID, 비밀번호 입력 체크
        if (ID.length() == 0) Toast.makeText(this, "ID를 입력하세요", Toast.LENGTH_SHORT).show();
        else if (password.length() == 9)
            Toast.makeText(this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
        else {
            GetData getData = new GetData();
            getData.execute(ID, password);
        }
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
        Session.getCurrentSession().removeCallback(KakaoCallback);
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
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            Uri profilePath = profile.getProfilePictureUri(100, 100);
            Member.user = new Member(profilePath, Member.FACEBOOK_MEMBER);
            finish();
        }
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            System.out.println("액티비티 결과");
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() { //연결 준비 중
            super.onPreExecute();
            progressDialog = ProgressDialog.show(LoginActivity.this, "로그인 중입니다", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {   //결과를 가지고 왔을 떄
            progressDialog.dismiss();
            super.onPostExecute(result);

            if (result.equals("NoExist"))
                Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다", Toast.LENGTH_SHORT).show();
            else GetMember(result);
        }

        @Override
        protected String doInBackground(String... strings) {
            String Email = strings[0];
            String Password = strings[1];
            String serverUrl = "http://192.168.10.26/GetMember.php";
            String parameter = "Email=" + Email + "&Password=" + Password;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(serverUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(5000);
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("POST");
                connection.connect();

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(parameter.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                outputStream.close();

                int responseCode = connection.getResponseCode();
                InputStream inputStream;
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = connection.getInputStream();
                } else inputStream = connection.getErrorStream();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
                connection.disconnect();
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "Err: " + e.getMessage();
            }
        }
    }

    private void GetMember(String result) { //리턴 결과로 계정 생성
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray(",iser");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String email = item.getString("Email");
                String name = item.getString("Name");
                String profilePath = item.getString("ProfilePath");
                String birth = item.getString("Birth");
                String gender = item.getString("Gender");
                int category = Integer.parseInt(item.getString("Category"));
                Member.user = new Member(email, name, gender, birth, category, profilePath);
                ExecuteLog(email);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void ExecuteLog(String email) { //거래내역 쿼리 실행
        GetLog getLog = new GetLog();
        getLog.execute(email);
    }

    private class GetLog extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() { //연결 준비 중
            super.onPreExecute();
            progressDialog = ProgressDialog.show(LoginActivity.this, "거래내역을 조회중입니다", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {   //결과를 가지고 왔을 떄
            progressDialog.dismiss();
            super.onPostExecute(result);

            if (result.equals("NoExist"))
                Toast.makeText(LoginActivity.this, "거래내역 없음", Toast.LENGTH_SHORT).show();
            else CreateLog(result);
        }

        @Override
        protected String doInBackground(String... strings) {
            String Email = strings[0];
            String serverUrl = "http://192.168.10.26/GetLogByID.php";
            String parameter = "Email=" + Email;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(serverUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(5000);
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("POST");
                connection.connect();

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(parameter.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                outputStream.close();

                int responseCode = connection.getResponseCode();
                InputStream inputStream;
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = connection.getInputStream();
                } else inputStream = connection.getErrorStream();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
                connection.disconnect();
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "Err: " + e.getMessage();
            }
        }
    }

    private void CreateLog(String result) { //리턴 결과로 거래내역 생성
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray(",iser");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (int i = 0; i < jsonArray.length(); i++) {
                //데이터 파싱
                JSONObject item = jsonArray.getJSONObject(i);
                int LogID = item.getInt("LogID");
                String MemberID = item.getString("MemberID");
                String DepTerminal = item.getString("DepTerminal");
                String ArrTerminal = item.getString("ArrTerminal");
                String SendPersonName = item.getString("SendPersonName");
                String SendPersonNumber = item.getString("SendPersonNumber");
                String ReceivePersonName = item.getString("ReceivePersonName");
                String ReceivePersonNumber = item.getString("ReceivePersonNumber");
                Date DeliveryTime = simpleDateFormat.parse(item.getString("DeliveryTime"));
                int Side = item.getInt("Side");
                int Weight = item.getInt("Weight");
                String PayMethod = item.getString("PayMethod");
                String Message = null;
                try {
                    Message = item.getString("Message");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Date PayTime = simpleDateFormat.parse(item.getString("PayTime"));
                int Price = item.getInt("Price");
                String State = null;
                try {
                    State = item.getString("State");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                DealLog dealLog = new DealLog(LogID, MemberID, DepTerminal, ArrTerminal, SendPersonName, SendPersonNumber, ReceivePersonName, ReceivePersonNumber, DeliveryTime, Side, Weight, PayMethod, Message, PayTime, Price, State);
                Member.user.AddDealLog(dealLog);    //거래내역 객체 생성 및 추가
                Toast.makeText(getApplicationContext(), "객체 " + i + "개 생성", Toast.LENGTH_SHORT).show();
            }
            Log.e("계정정보: ", Member.user.toString());
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}