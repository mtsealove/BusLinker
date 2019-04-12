package kr.ac.gachon.www.buslinker.Facebook;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import kr.ac.gachon.www.buslinker.Entity.Member;

public class FacebookLoginCallback implements FacebookCallback<LoginResult> {

    // 로그인 성공 시 호출 됩니다. Access Token 발급 성공.

    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.e("Callback :: ", "onSuccess");
        requestMe(loginResult.getAccessToken());
    }

    // 로그인 창을 닫을 경우, 호출됩니다.
    @Override
    public void onCancel() {
        Log.e("Callback :: ", "onCancel");
    }

    // 로그인 실패 시에 호출됩니다.
    @Override
    public void onError(FacebookException error) {
        Log.e("Callback :: ", "onError : " + error.getMessage());
    }

    // 사용자 정보 요청
    public void requestMe(AccessToken token) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.e("result", object.toString());
                        try {
                            String name = object.getString("name");
                            String email = object.getString("email");
                            Member.user.setName(name);
                            Member.user.setEmail(email);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,email");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();

    }

}

