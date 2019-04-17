package kr.ac.gachon.www.buslinker;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import kr.ac.gachon.www.buslinker.Views.SlideMenu;
import kr.ac.gachon.www.buslinker.Views.SystemUiTuner;

public class SetInformationActivity extends AppCompatActivity {
    private String arrTmnNm, depTmnNm, depTime, arrTime, registrationDate, MSG;
    private double X, Y, Z, weight;
    EditText depPersonNameET, depPersonContactET, arrPersonNameET, arrPersonContactET;
    TextView arrTmnNmTV, depTmnNmTV, timeTV, freightInfoTV, inputMsgTV, showMsgTV;
    SlideMenu slideMenu;
    Button panelBtn;
    RelativeLayout checkPayLayout;
    android.support.v4.widget.DrawerLayout drawerLayout;
    final String TAG_PRICE_WEIGHT = "무게 가격정보", TAG_PRICE_SIDE = "세 변의 합 가격정보", TAG_FINAL_PRICE = "최종 가격";

    int PriceSide = 0, PriceWeight = 0; //세 변의 합에 의한 가격정보와 무게에 따른 가격정보
    int FinalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_information);
        arrTmnNmTV=findViewById(R.id.arrTmnNmTV);
        depTmnNmTV=findViewById(R.id.depTmnNmTV);
        timeTV=findViewById(R.id.deliveryTimeTV);
        freightInfoTV=findViewById(R.id.freightInfoTV);
        checkPayLayout=findViewById(R.id.checkPayLayout);
        depPersonNameET=findViewById(R.id.depPersonNameET);
        depPersonContactET=findViewById(R.id.depPersonContactET);
        arrPersonNameET=findViewById(R.id.arrPersonNameET);
        arrPersonContactET=findViewById(R.id.arrPersonContactET);
        slideMenu=findViewById(R.id.slideMenu);
        panelBtn = findViewById(R.id.panelBtn);
        drawerLayout = findViewById(R.id.drawerLayout);
        inputMsgTV = findViewById(R.id.inputMSGTV);
        showMsgTV = findViewById(R.id.showMsgTV);

        //하얀 상태바
        SystemUiTuner systemUiTuner=new SystemUiTuner(SetInformationActivity.this);
        systemUiTuner.setStatusBarWhite();

        getParameter(); //이전 액티비티에서 정보 받아옴
        showParameter();    //받아온 정보를 화면에 표시
        checkPayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPayMethod();
            }
        }); //결제 방법 선택 다이얼로그 출력

        //패널 버튼
        panelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });

        //메시지 입력
        inputMsgTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMsg();
            }
        });
        //전화번호 양식
        depPersonContactET.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        arrPersonContactET.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        if(Member.user!=null)   //만약 로그인되어 있다면
            depPersonNameET.setText(Member.user.getName()); //사용자 이름을 우선적으로 설정
        ReadPhoneNumber();

        depPersonNameET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(depPersonNameET.getText().toString().length()!=0) {
                    depPersonNameET.setText("");
                    depPersonNameET.setOnClickListener(null);
                }
            }
        });

        depPersonContactET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(depPersonContactET.getText().toString().length()!=0) {
                    depPersonContactET.setText("");
                    depPersonContactET.setOnClickListener(null);
                }
            }
        });

        ReadPrice();
    }

    private void getParameter(){    //이전액티비티에서 정보를 읽어옴
        Intent intent=getIntent();
        arrTime=intent.getStringExtra("arrTime");
        depTime=intent.getStringExtra("depTime");
        arrTmnNm=intent.getStringExtra("arrTmnNm");
        depTmnNm=intent.getStringExtra("depTmnNm");
        X=intent.getDoubleExtra("X", 0);
        Y=intent.getDoubleExtra("Y", 0);
        Z=intent.getDoubleExtra("Z", 0);
        weight=intent.getDoubleExtra("weight", 0);
        registrationDate=intent.getStringExtra("registrationDate");
    }

    private void showParameter() {  //얻어온 정보들을 출력
        arrTmnNmTV.setText(arrTmnNm);
        depTmnNmTV.setText(depTmnNm);

        String Date4Show=registrationDate.substring(0, 4)+"년 "+registrationDate.substring(4, 6)+"월 "+registrationDate.substring(6, 8)+"일";
        arrTime=arrTime.replace("시 ", ":");
        arrTime=arrTime.replace("분", "");
        depTime=depTime.replace("시 ", ":");
        depTime=depTime.replace("분", "");
        timeTV.setText(Date4Show+" "+depTime+" ~ "+arrTime);

        String freightInfo="가로: "+(int)X+"cm 세로: "+(int)Y+"cm 높이: "+(int)Z+"cm\n무게: "+weight+"kg";
        freightInfoTV.setText(freightInfo);
    }

    private boolean ReusePayMethod = false;   //결제 수단을 다시 사용할 것인지 선택

    private void setPayMethod() {   //결제 방법 선택 다이얼로그 출력
        AlertDialog.Builder builder=new AlertDialog.Builder(SetInformationActivity.this);
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_pay, null);
        TextView priceTV = view.findViewById(R.id.priceTV);
        priceTV.setText(FinalPrice + "원");    //최종 가격을 화면에 표시

        CheckBox reuseCB = view.findViewById(R.id.reuseCB);
        reuseCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ReusePayMethod = b;
            }
        });
        Button mobileCardBtn = view.findViewById(R.id.mobileCardBtn);
        Button virtualAccountBtn = view.findViewById(R.id.virtualAccountBtn);
        Button noAccountBtn = view.findViewById(R.id.noAccountBtn);
        mobileCardBtn.setOnClickListener(PayMethodClickListener);
        virtualAccountBtn.setOnClickListener(PayMethodClickListener);
        noAccountBtn.setOnClickListener(PayMethodClickListener);
        builder.setView(view);

        AlertDialog dialog=builder.create();
        dialog.show();
    }

    View.OnClickListener PayMethodClickListener = new View.OnClickListener() {    //결제 방법 선택 버튼 리스너
        @Override
        public void onClick(View view) {
            //먼저 모든 정보가 입력되었는지 확인
            String SendPersonName = depPersonNameET.getText().toString();
            String SendPersonNumber = depPersonContactET.getText().toString();
            String ReceivePersonName = arrPersonNameET.getText().toString();
            String ReceivePersonNumber = arrPersonContactET.getText().toString();
            String DeliveryTime = registrationDate.substring(0, 4) + "-" + registrationDate.substring(4, 6) + "-" + registrationDate.substring(6, 8) + " " + depTime + ":00";
            int Side = (int) (X + Y + Z);
            int Weight = (int) weight;
            int Price = FinalPrice;

            if (SendPersonName.length() == 0)
                Toast.makeText(SetInformationActivity.this, "보내는 사람의 이름을 적어주세요", Toast.LENGTH_SHORT).show();
            else if (SendPersonNumber.length() == 0)
                Toast.makeText(SetInformationActivity.this, "보내는 사람의 연락처를 적어주세요", Toast.LENGTH_SHORT).show();
            else if (ReceivePersonName.length() == 0)
                Toast.makeText(SetInformationActivity.this, "받는 사람의 이름을 적어주세요", Toast.LENGTH_SHORT).show();
            else if (ReceivePersonNumber.length() == 0)
                Toast.makeText(SetInformationActivity.this, "받는 사람의 연락처를 적어주세요", Toast.LENGTH_SHORT).show();
            else {
                Intent intent = new Intent(SetInformationActivity.this, UploadDataActivity.class);
                intent.putExtra("SendPersonName", SendPersonName);
                intent.putExtra("SendPersonNumber", SendPersonNumber);
                intent.putExtra("ReceivePersonName", ReceivePersonName);
                intent.putExtra("ReceivePersonNumber", ReceivePersonNumber);
                intent.putExtra("DeliveryTime", DeliveryTime);
                intent.putExtra("Side", Side);
                intent.putExtra("Weight", Weight);
                intent.putExtra("Price", Price);
                intent.putExtra("Message", MSG);
                intent.putExtra("DepTerminal", depTmnNm);
                intent.putExtra("ArrTerminal", arrTmnNm);
                switch (view.getId()) {
                    case R.id.mobileCardBtn:    //모바일 카드결제
                        intent.putExtra("PayMethod", "신용/체크카드");
                        break;
                    case R.id.virtualAccountBtn:    //가상계좌
                        intent.putExtra("PayMethod", "가상계좌");
                        break;
                    case R.id.noAccountBtn: //무통장 입금
                        intent.putExtra("PayMethod", "무통장 입금");
                        break;
                }
                startActivity(intent);
            }
        }
    };

    private void ReadPhoneNumber() {    //전화번호 읽어서 입력
        TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String PhoneNum = telManager.getLine1Number();
        if(PhoneNum.startsWith("+82")){
            PhoneNum = PhoneNum.replace("+82", "0");
        }
        depPersonContactET.setText(PhoneNum);
    }

    private void InputMsg() {   //메시지 입력 다이얼로그 출력
        AlertDialog.Builder builder = new AlertDialog.Builder(SetInformationActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_input_msg, null);
        Button confirmBtn = view.findViewById(R.id.confirmBtn);
        final EditText msgET = view.findViewById(R.id.MsgET);
        msgET.setText(MSG);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MSG = msgET.getText().toString();
                if (MSG.length() != 0) {    //글자수가 0이 아니면
                    showMsgTV.setText(MSG);
                    showMsgTV.setVisibility(View.VISIBLE);
                } else {
                    showMsgTV.setVisibility(View.GONE);
                }
                dialog.cancel();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        slideMenu.checkLogin();
    }

    private void ReadPrice() {  //가격 정보를 읽어서 설정
        //세 변의 합에 대한
        GetPriceSideData getPriceSideData = new GetPriceSideData();
        getPriceSideData.execute(depTmnNm, arrTmnNm, Integer.toString((int) (X + Y + Z)));
        //무게에 대한
        GetPriceWeightData getPriceWeightData = new GetPriceWeightData();
        getPriceWeightData.execute(depTmnNm, arrTmnNm, Double.toString(weight));
    }

    private class GetPriceSideData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() { //연결 준비 중
            super.onPreExecute();
            progressDialog = ProgressDialog.show(SetInformationActivity.this, "가격정보를 가져오는 중입니다", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {   //결과를 가지고 왔을 떄
            progressDialog.dismiss();
            super.onPostExecute(result);
            GetSideResult(result);
        }

        @Override
        protected String doInBackground(String... strings) {
            String start = strings[0].replace("터미널", ""); //출발 터미널
            String destination = strings[1].replace("터미널", "");   //도착 터미널
            String side = strings[2]; //세 변의 합
            String serverUrl = "http://192.168.10.26/GetPriceBySide.php";   //세 변의 합을 기준으로 가격을 받아옴
            String parameter = "start=" + start + "&destination=" + destination + "&side=" + side;    //파라미터 설정
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

    private void GetSideResult(String result) { //결과로 데이터 파싱
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray(",iser");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String price = item.getString("price"); //가격 정보 파싱
                try {
                    PriceSide = Integer.parseInt(price);
                    Log.e(TAG_PRICE_SIDE, "" + PriceSide);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GetPriceWeightData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() { //연결 준비 중
            super.onPreExecute();
            progressDialog = ProgressDialog.show(SetInformationActivity.this, "가격정보를 가져오는 중입니다", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {   //결과를 가지고 왔을 떄
            progressDialog.dismiss();
            super.onPostExecute(result);
            GetWeightResult(result);
        }

        @Override
        protected String doInBackground(String... strings) {
            String start = strings[0].replace("터미널", ""); //출발 터미널
            String destination = strings[1].replace("터미널", "");   //도착 터미널
            String weight = strings[2]; //무게
            String serverUrl = "http://192.168.10.26/GetPriceByWeight.php";   //무게를 기준으로 가격을 받아옴
            String parameter = "start=" + start + "&destination=" + destination + "&weight=" + weight;    //파라미터 설정
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

    private void GetWeightResult(String result) { //결과로 데이터 파싱
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray(",iser");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String price = item.getString("price"); //가격 정보 파싱
                try {
                    PriceWeight = Integer.parseInt(price);
                    Log.e(TAG_PRICE_WEIGHT, "" + PriceWeight);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //둘 중에 높은 것으로 설정
        if (PriceWeight > PriceSide) FinalPrice = PriceWeight;
        else FinalPrice = PriceSide;
        Log.e(TAG_FINAL_PRICE, "" + FinalPrice);
    }

}