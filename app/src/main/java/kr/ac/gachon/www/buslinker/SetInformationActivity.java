package kr.ac.gachon.www.buslinker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kr.ac.gachon.www.buslinker.Entity.Member;
import kr.ac.gachon.www.buslinker.Views.SlideMenu;
import kr.ac.gachon.www.buslinker.Views.SystemUiTuner;

public class SetInformationActivity extends AppCompatActivity {
    private String arrTmnNm, depTmnNm, depTime, arrTime, registrationDate;
    private double X, Y, Z, weight;
    EditText depPersonNameET, depPersonContactET, arrPersonNameET, arrPersonContactET;
    TextView arrTmnNmTV, depTmnNmTV, timeTV, freightInfoTV;
    SlideMenu slideMenu;
    RelativeLayout checkPayLayout;


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

        SystemUiTuner systemUiTuner=new SystemUiTuner(SetInformationActivity.this);
        systemUiTuner.setStatusBarWhite();

        getParameter();
        showParameter();
        checkPayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPayMethod();
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

    private void setPayMethod() {   //결제 방법 선택 다이얼로그 출력
        AlertDialog.Builder builder=new AlertDialog.Builder(SetInformationActivity.this);
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_pay, null);
        builder.setView(view);

        AlertDialog dialog=builder.create();
        dialog.show();
    }

    private void ReadPhoneNumber() {    //전화번호 읽어서 입력
        TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String PhoneNum = telManager.getLine1Number();
        if(PhoneNum.startsWith("+82")){
            PhoneNum = PhoneNum.replace("+82", "0");
        }
        depPersonContactET.setText(PhoneNum);
    }

    @Override
    public void onResume() {
        super.onResume();
        slideMenu.checkLogin();
    }
}
