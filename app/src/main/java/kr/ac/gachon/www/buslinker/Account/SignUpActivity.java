package kr.ac.gachon.www.buslinker.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import kr.ac.gachon.www.buslinker.R;
import kr.ac.gachon.www.buslinker.Security.AES256Util;
import kr.ac.gachon.www.buslinker.UploadDataActivity;
import kr.ac.gachon.www.buslinker.Views.SystemUiTuner;

public class SignUpActivity extends AppCompatActivity {
    Button signUpBtn;
    EditText nameET, emailET, passwordET, passwordCheckET, birthET;
    RadioGroup genderGroup;
    CheckBox acceptAllCB, accept1CB, accept2CB, accept3CB;
    private String gender = "";
    String mjsonString;
    private static final String TAG_JSON = ",iser";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_NAME = "name";
    private static final String IPAddress = "http://192.168.10.26";
    private boolean EmailExist = true;   //메일이 존재하는지 확인할 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        SystemUiTuner systemUiTuner = new SystemUiTuner(SignUpActivity.this);
        systemUiTuner.setStatusBarYellow();

        //회원정보
        nameET = findViewById(R.id.nameET);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        passwordCheckET = findViewById(R.id.checkPasswordET);
        birthET = findViewById(R.id.birthET);
        genderGroup = findViewById(R.id.genderRG);
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

            }
        });
        signUpBtn = findViewById(R.id.signUpBtn);
        //약관 체크박스들
        acceptAllCB = findViewById(R.id.acceptAllCB);
        accept1CB = findViewById(R.id.accept1CB);
        accept2CB = findViewById(R.id.accept2CB);
        accept3CB = findViewById(R.id.accept3CB);

        acceptAllCB.setOnCheckedChangeListener(AllCheckListener);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp();
            }
        });
    }

    protected void SignUp() {   //회원가입
        if (CheckData() && IsAcceptChecked()) {    //데이터 입력 및 약관 동의 확인
            String email = emailET.getText().toString();
            GetIDExist getIDExist = new GetIDExist();
            getIDExist.execute(email);
        }
    }

    protected boolean CheckData() {    //모든 정보가 제대로 입력되었는지 확인
        boolean result = false;
        String email = emailET.getText().toString();
        String name = nameET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordCheck = passwordCheckET.getText().toString();
        String birth = birthET.getText().toString();
        try {
            int radioButtonId = genderGroup.getCheckedRadioButtonId();
            RadioButton radioButton = findViewById(radioButtonId);
            gender = radioButton.getText().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (email.length() == 0) toast("이메일을 입력해주세요");
        else if ((!email.contains("@")) || ((!email.contains(".com")) && (!email.contains(".kr")) && (!email.contains(".net"))))
            toast("올바른 이메일을 입력해주세요");
        else if (name.length() == 0) toast("이름을 입력해주세요");
        else if (birth.length() == 0) toast("생년월일을 입력해주세요");
        else if (birth.length() != 8) toast("생년월일은 8자리여야 합니다");
        else if (gender.length() == 0) toast("성별을 선택해주세요");
        else if (password.length() == 0) toast("비밀번호를 입력해주세요");
        else if (passwordCheck.length() == 0) toast("비밀번호를 확인해주세요");
        else if (password.length() < 8 || password.length() > 15) toast("비밀번호는 8자 이상 15자 이하여야 합니다");
        else if (!CheckPasswordRight(password)) toast("비밀번호는 대/소문자 숫자가 모두 포함되어야 합니다");
        else if (!password.equals(passwordCheck)) toast("비밀번호가 일치하지 않습니다");
        else result = true;   //모든 정보가 제대로 입력되었을 경우

        return result;
    }

    protected boolean CheckPasswordRight(String password) { //대/소문자 및 숫자가 모두 입력되었는지 확인
        boolean Upper = false;
        boolean Lower = false;
        boolean Digit = false;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(password.charAt(i))) Upper = true;
            else if (Character.isLowerCase(password.charAt(i))) Lower = true;
            else if (Character.isDigit(password.charAt(i))) Digit = true;
        }

        return Upper && Lower && Digit;
    }

    protected boolean IsAcceptChecked() {   //모든 필수 약관이 체크되었는지 확인
        if (accept1CB.isChecked() && accept2CB.isChecked()) return true;
        else {
            toast("필수 약관에 동의해주세요");
            return false;
        }
    }

    CompoundButton.OnCheckedChangeListener AllCheckListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            //모든 체크를 체크하게 설정
            accept1CB.setChecked(b);
            accept2CB.setChecked(b);
            accept3CB.setChecked(b);
        }
    };

    protected void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void Post() {
        String email = emailET.getText().toString();
        String name = nameET.getText().toString();
        String birth = birthET.getText().toString();
        birth = birth.substring(0, 4) + "-" + birth.substring(4, 6) + "-" + birth.substring(6, 8);
        String password = passwordET.getText().toString();

        PostMember postMember = new PostMember();
        postMember.execute(email, name, birth, gender, password);
    }


    private class GetIDExist extends AsyncTask<String, Void, String> {
        //ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() { //연결 준비 중
            super.onPreExecute();
            //progressDialog = ProgressDialog.show(SignUpActivity.this, "ID가 ", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {   //결과를 가지고 왔을 떄
            //progressDialog.dismiss();
            super.onPostExecute(result);
            if (result.equals("NoExist")) {    //존재하지 않음을 반환하면
                Post();
            } else {    //ID가 존재하면
                toast("이미 존재하는 이메일입니다");
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String email = strings[0];
            String serverUrl = "http://192.168.10.26/GetIDExist.php";   //ID가 존재하는지만 확인
            String parameter = "email=" + email;
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

    class PostMember extends AsyncTask<String, Void, String> {
        final String TAG = "POST";
        final String IP = "http://192.168.10.26";
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(SignUpActivity.this,
                    "잠시만 기다려주세요", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            // mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
            if (result.equals("RESULT_OK.")) {
                Intent intent = new Intent(SignUpActivity.this, SignUpResultActivity.class);
                startActivity(intent);
                finish();
            }
        }


        @Override
        protected String doInBackground(String... params) {
            String Email = params[0];
            String Name = params[1];
            String Birth = params[2];
            String Gender = params[3];
            String Password = params[4];

            AES256Util aes256Util = new AES256Util(); //비밀번호 암호화
            try {
                Password = aes256Util.encrypt(Password);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }


            String serverURL = IP + "/InsertMember.php";
            String postParameters = "email=" + Email
                    + "&name=" + Name
                    + "&birth=" + Birth
                    + "&category=2"
                    + "&gender=" + Gender
                    + "&password=" + Password;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
}
