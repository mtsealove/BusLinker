package kr.ac.gachon.www.buslinker.Kakao;

import android.util.Log;

import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

public class SessionCallback implements ISessionCallback {

    @Override
    public void onSessionOpened() { //세션 오픈

        UserManagement userManagement=UserManagement.getInstance();
        userManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    //에러로 인한 로그인 실패
                    // finish();
                } else {
                    //redirectMainActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                //로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지url등을 리턴합니다.
                //사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공합니다.

                Log.e("UserProfile", userProfile.toString());
                Log.e("UserProfile", userProfile.getId() + "");


                long number = userProfile.getId();

                System.out.println();
                System.out.println("사용자 정보: " +userProfile.toString());

            }
        });

    }
    // 세션 실패시
    @Override
    public void onSessionOpenFailed(KakaoException exception) {
        System.out.println("세션 실패");
    }
}