package kr.ac.gachon.www.buslinker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import kr.ac.gachon.www.buslinker.Entity.Member;

public class UploadDataActivity extends AppCompatActivity { //데이터를 DB에 업로드할 액티비티
    //이전 액티비티로 넘겨받을 정보들
    private String DepTerminal, ArrTerminal, SendPersonName, SendPersonNumber, ReceivePersonName, ReceivePersonNumber, DeliveryTime, Message, PayMethod, PayTime;
    private int Side, Weight, Price;
    //그 외 정보들
    private String MemberID;

    final String TAG = "태그";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_data);

        getParameter();
    }

    private void getParameter() {   //파라미터들을 받아옴
        Intent intent = getIntent();
        DepTerminal = intent.getStringExtra("DepTerminal").replace("터미널", "");
        ArrTerminal = intent.getStringExtra("ArrTerminal").replace("터미널", "");
        SendPersonName = intent.getStringExtra("SendPersonName");
        SendPersonNumber = intent.getStringExtra("SendPersonNumber");
        ReceivePersonName = intent.getStringExtra("ReceivePersonName");
        ReceivePersonNumber = intent.getStringExtra("ReceivePersonNumber");
        DeliveryTime = intent.getStringExtra("DeliveryTime");
        Message = intent.getStringExtra("Message");
        PayMethod = intent.getStringExtra("PayMethod");
        Side = intent.getIntExtra("Side", 0);
        Weight = intent.getIntExtra("Weight", 0);
        Price = intent.getIntExtra("Price", 0);


        //만들기
        MemberID = Member.user.getEmail();
        if (MemberID == null) MemberID = "anonymous";    //로그인되지 않았으면 익명

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PayTime = simpleDateFormat.format(date);

        Post();
    }

    private void Post() {
        InsertData insertData = new InsertData();
        insertData.execute(MemberID, DepTerminal, ArrTerminal, SendPersonName, SendPersonNumber, ReceivePersonName, ReceivePersonNumber, DeliveryTime, Integer.toString(Side), Integer.toString(Weight), PayMethod, Message, PayTime, Integer.toString(Price));
    }

    class InsertData extends AsyncTask<String, Void, String> {
        final String IP = "http://192.168.10.26";
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(UploadDataActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            // mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String MemberID = params[0];
            String DepTerminal = params[1];
            String ArrTerminal = params[2];
            String SendPersonName = params[3];
            String SendPersonNumber = params[4];
            String ReceivePersonName = params[5];
            String ReceivePersonNumber = params[6];
            String DeliveryTime = params[7];
            String Side = params[8];
            String Weight = params[9];
            String PayMethod = params[10];
            String Message = params[11];
            String PayTime = params[12];
            String Price = params[13];

            String serverURL = IP + "/InsertLog.php";
            String postParameters = "MemberID=" + MemberID
                    + "&DepTerminal=" + DepTerminal
                    + "&ArrTerminal=" + ArrTerminal
                    + "&SendPersonName=" + SendPersonName
                    + "&SendPersonNumber=" + SendPersonNumber
                    + "&ReceivePersonName=" + ReceivePersonName
                    + "&ReceivePersonNumber=" + ReceivePersonNumber
                    + "&DeliveryTime=" + DeliveryTime
                    + "&Side=" + Side
                    + "&Weight=" + Weight
                    + "&PayMethod=" + PayMethod
                    + "&Message=" + Message
                    + "&PayTime=" + PayTime
                    + "&Price=" + Price;
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
