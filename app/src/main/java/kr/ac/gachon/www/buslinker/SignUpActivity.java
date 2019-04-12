package kr.ac.gachon.www.buslinker;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import kr.ac.gachon.www.buslinker.Entity.Member;
import kr.ac.gachon.www.buslinker.Views.SystemUiTuner;

public class SignUpActivity extends AppCompatActivity {
    Button checkReuseBtn, signUpBtn;
    EditText nameET, emailET, passwordET, passwordCheckET;
    String mjsonString;
    private static final String TAG_JSON = ",iser";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_NAME = "name";
    private static final String IPAddress = "http://192.168.10.26";
    private boolean EmailExist = false;   //메일이 존재하는지 확인할 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameET = findViewById(R.id.nameET);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        passwordCheckET = findViewById(R.id.passwordCheckET);
        checkReuseBtn = findViewById(R.id.checkReuseBtn);
        signUpBtn = findViewById(R.id.signUpBtn);

        SystemUiTuner systemUiTuner = new SystemUiTuner(SignUpActivity.this);
        systemUiTuner.setStatusBarYellow();

        checkReuseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ID = emailET.getText().toString();
                if (ID.contains("@") && (ID.contains(".com") || ID.contains(".net") || ID.contains(".kr")))    //메일이 정상적으로 입력됬는지 확인
                    CheckIDReuse(ID);
                else
                    Toast.makeText(SignUpActivity.this, "올바른 이메일을 입력하세요", Toast.LENGTH_SHORT).show();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp();
            }
        });
    }

    protected void SignUp() {
        String email = emailET.getText().toString();
        String name = nameET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordCheck = passwordCheckET.getText().toString();

        if (!EmailExist)
            Toast.makeText(SignUpActivity.this, "중복확인을 해주세요", Toast.LENGTH_SHORT).show();
        else if (name.length() == 0)
            Toast.makeText(SignUpActivity.this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
        else if (password.length() == 0)
            Toast.makeText(SignUpActivity.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
        else if (!password.equals(passwordCheck))
            Toast.makeText(SignUpActivity.this, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
        else {
            InsertData insertData = new InsertData();
            insertData.execute(email, name, password);
        }

    }

    protected void CheckIDReuse(String ID) {    //이메일 사용중인지 확인
        GetData getData = new GetData();
        getData.execute(ID);
    }

    private class GetData extends AsyncTask<String, Void, String> {
        String errString = null;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() { //준비중
            super.onPreExecute();
            progressDialog = ProgressDialog.show(SignUpActivity.this, "잠시만 기다려주세요", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {   //실행 완료
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (result == null) {
                //Toast.makeText(SignUpActivity.this, "오류", Toast.LENGTH_SHORT).show();
            } else {
                mjsonString = result;
                System.out.println("결과:" + result);
                getResult();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            String searchKeyword = strings[0];
            String serverUrl = IPAddress + "/GetIDExist.php";
            String postParameter = "email=" + searchKeyword;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(serverUrl);
                connection = (HttpURLConnection) url.openConnection();

                connection.setReadTimeout(5000);
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.connect();

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(postParameter.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                outputStream.close();

                int responseStateCocde = connection.getResponseCode();

                InputStream is;
                if (responseStateCocde == HttpURLConnection.HTTP_OK) {
                    is = connection.getInputStream();
                } else {
                    is = connection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();
                inputStreamReader.close();
                is.close();
                connection.disconnect();
                return sb.toString().trim();
            } catch (Exception e) {
                connection.disconnect();
                e.printStackTrace();
                return null;
            }
        }
    }

    private class InsertData extends AsyncTask<String, Void, String> {      //데이터 삽입 클래스
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(SignUpActivity.this, "잠시만 기다려주세요", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("삽입 결과", result);
            progressDialog.dismiss();
            finish();
        }

        @Override
        protected String doInBackground(String... strings) {
            String email = strings[0];
            String name = strings[1];
            String password = strings[2];

            Log.e("insert", "메일: " + email + " 이름: " + name + " 비번: " + password);

            String parameter = "EMAIL=" + email + "&NAME=" + name + "&PW=" + password;
            try {
                URL url = new URL(IPAddress + "/insertMember.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setReadTimeout(5000);
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("POST");
                connection.connect();

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(parameter.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                outputStream.close();

                int requestStatusCode = connection.getResponseCode();
                Log.e("PostResponeCode", "코드" + requestStatusCode);

                InputStream inputStream;
                if (requestStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = connection.getInputStream();
                } else {
                    inputStream = connection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                inputStreamReader.close();
                inputStream.close();
                bufferedReader.close();
                connection.disconnect();
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "err: " + e.getMessage();
            }
        }
    }

    private void getResult() {
        try {
            JSONObject jsonObject = new JSONObject(mjsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            boolean exist = false;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String email = item.getString(TAG_EMAIL);
                if (email != null) exist = true;
            }
            if (exist) {
                EmailExist = false;
                Toast.makeText(SignUpActivity.this, "메일 사용 불가", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            EmailExist = true;
            Toast.makeText(SignUpActivity.this, "메일 사용 가능", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
