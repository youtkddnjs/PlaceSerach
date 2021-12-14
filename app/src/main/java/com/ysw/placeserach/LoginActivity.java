package com.ysw.placeserach;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.util.Utility;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String keyHash = Utility.INSTANCE.getKeyHash(this);
        Log.i("###keyhash :", keyHash);
    }

    public void clickGo(View view) {
        //MainActivity로 이동
        Intent intent= new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void clickSignUp(View view) {
        //회원가입 화면(액티비티)로 이동
        startActivity(new Intent(this,SignUpActivity.class));
    }

    public void clickLoginEmail(View view) {
        //이메일 로그인 화면(액티비티)로 이동
        startActivity(new Intent(this,EmailSignInActivity.class));
    }

    public void clickLoginKakao(View view) {
        UserApiClient.getInstance().loginWithKakaoAccount(this, new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                if(oAuthToken != null){
                    Toast.makeText(getApplication(), "Login", Toast.LENGTH_SHORT).show();
                    UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
                        @Override
                        public Unit invoke(User user, Throwable throwable) {

                            if(user!=null){
                                String userid=user.getId()+"";
                                String email = user.getKakaoAccount().getEmail();

                                G.user=new UserAccount(userid,email);
//                                new AlertDialog.Builder(LoginActivity.this).setMessage(email+"").show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }


                            return null;
                        }
                    }); //me
                }//if oAuthToken
                return null;
            }
        });//loginWithKakaoAccount
    }//clickLoginKakao

    public void clickLoginGoogle(View view) {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestId().requestEmail().build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent intent =googleSignInClient.getSignInIntent();
        startActivityForResult(intent,100);


    }//clickLoginGoogle

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100){
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);

                if(account != null){
                    Toast.makeText(LoginActivity.this, "Google Login", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

            } catch (ApiException e) {
                e.printStackTrace();
            }
        }

    }//onActivityResult

    public void clickLoginNaver(View view) {
        OAuthLogin oAuthLogin = OAuthLogin.getInstance();
        oAuthLogin.init(this, "YyYJzMCUpm81ae3RDt1N","4C72lS2_Pb","My Location");

        oAuthLogin.startOauthLoginActivity(this, new OAuthLoginHandler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void run(boolean success) {
                if(success){
                    Toast.makeText(LoginActivity.this,"Naver Login",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

            }
        });

    }//clickLoginNaver
}