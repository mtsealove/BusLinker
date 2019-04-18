package kr.ac.gachon.www.buslinker.Views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.ac.gachon.www.buslinker.Entity.CustomAlert;
import kr.ac.gachon.www.buslinker.Entity.Member;
import kr.ac.gachon.www.buslinker.Account.LoginActivity;
import kr.ac.gachon.www.buslinker.R;

public class SlideMenu extends RelativeLayout {
    LinearLayout myAccountLayout;
    TextView recentDealTV, nameTV, prepareShipTV, shippingTV, shippedTV;    //사용자 정보로 표시될 텍스트
    RelativeLayout loginLayout, inquireDealLayout, publicNoticeLayout, checkChargeLayout, faqLayout, memberLayout, logoutLayout;  //하단에 표시될 레이아웃
    ImageView myAccountIcon;    //프로필 사진
    Button closeBtn;
    private Context context;
    public SlideMenu(Context context) {
        super(context);
        this.context=context;
        init();
    }
    public SlideMenu(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context=context;
        init();
    }

    public SlideMenu(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        this.context=context;
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SlideMenu(Context context, AttributeSet attributeSet, int defStyleAttr, int defStyleRes) {
        super(context, attributeSet, defStyleAttr, defStyleRes);
        this.context=context;
        init();
    }

    private void init() {
        String inflaterService=Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(inflaterService);
        View view=inflater.inflate(R.layout.slide_layout, SlideMenu.this, false);
        addView(view);

        //뷰 매칭
        closeBtn=view.findViewById(R.id.closeBtn);
        //로그인 했을 떄 보이는 뷰들
        myAccountLayout=view.findViewById(R.id.myAccountLayout);
        myAccountIcon=view.findViewById(R.id.myAccountIcon);
        recentDealTV=view.findViewById(R.id.recentDealTV);
        nameTV=view.findViewById(R.id.nameTV);
        prepareShipTV=view.findViewById(R.id.prepareShipTV);
        shippingTV=view.findViewById(R.id.shippingTV);
        shippedTV=view.findViewById(R.id.shippedTV);
        //하단 뷰
        loginLayout=view.findViewById(R.id.loginLayout);
        memberLayout=view.findViewById(R.id.memberLayout);
        inquireDealLayout=view.findViewById(R.id.inquireLayout);
        publicNoticeLayout=view.findViewById(R.id.noticeLayout);
        checkChargeLayout=view.findViewById(R.id.check_chargeLayout);
        faqLayout=view.findViewById(R.id.faqLayout);
        logoutLayout = view.findViewById(R.id.logoutLayout);

        loginLayout.setVisibility(View.VISIBLE);
        logoutLayout.setVisibility(GONE);
        memberLayout.setVisibility(View.GONE);
        myAccountLayout.setVisibility(GONE);

        loginLayout.setOnClickListener(new OnClickListener() {  //로그인 버튼 눌렀을 때
            @Override
            public void onClick(View view) {
                Login();
            }
        });
        logoutLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout();
            }
        });
        checkLogin();
    }

    private void Login() {  //로그인 액티비티로 이동
        Intent intent=new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public void checkLogin() {    //로그인 확인
        if(Member.user!=null) { //로그인 되었을 때
            memberLayout.setVisibility(View.VISIBLE);
            myAccountLayout.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.GONE);
            logoutLayout.setVisibility(VISIBLE);

            nameTV.setText(Member.user.getName());
            setMyAccountIcon(Member.user.getProfileThumbPath());
        } else {    //로그인 되지 않았을 때
            memberLayout.setVisibility(View.GONE);
            myAccountLayout.setVisibility(View.GONE);
            loginLayout.setVisibility(View.VISIBLE);
            logoutLayout.setVisibility(GONE);
        }
    }

    public void Logout() {  //로그아웃
        final CustomAlert customAlert = new CustomAlert(context);
        customAlert.DialogChoice("로그아웃", "로그아웃하시겠습니까?", new OnClickListener() {
            @Override
            public void onClick(View view) {
                Member.user = null;   //로그인된 객체 삭제
                checkLogin();
                customAlert.cancelDialog();
            }
        });
    }

    Bitmap bitmap=null;

    private void setMyAccountIcon(final Object urlStr) { //URL에서 파일을 다운받아 실행

        if (urlStr instanceof String) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        URL url = new URL((String) urlStr);    //URL변환
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();

                        InputStream is = connection.getInputStream(); //읽어옴
                        bitmap = BitmapFactory.decodeStream(is); //이미지로 변환

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            try {
                thread.join();
                myAccountIcon.setBackgroundDrawable(context.getDrawable(R.drawable.round_rectangle));
                myAccountIcon.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (urlStr instanceof Uri) {
            System.out.print("URI입니다");
            myAccountIcon.setImageURI((Uri) urlStr);
        }
    }
}
