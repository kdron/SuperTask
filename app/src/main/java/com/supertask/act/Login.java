package com.supertask.act;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.supertask.R;
import com.supertask.util.Constant;
import com.supertask.util.ConstantClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity implements View.OnClickListener {


    private ImageView imgBackground;
    private TextView txtLogin;

    private ImageView imgFb;
    Typeface font,font_bold;
    CallbackManager callbackManager;
    ConstantClass constantClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        FacebookSdk.sdkInitialize(getApplicationContext());
        constantClass = new ConstantClass(this);
        Cursor cursor = constantClass.getUser();
        if(cursor.getCount()>0)
        {
            nextActivity();
        }
                imgBackground = (ImageView) findViewById(R.id.imgBackground);
        txtLogin = (TextView) findViewById(R.id.txtLogin);

        imgFb = (ImageView) findViewById(R.id.imgFb);


        font = Typeface.createFromAsset(getAssets(), "fonts/Helvetica.otf");
        font_bold = Typeface.createFromAsset(getAssets(), "fonts/Helvetica Bold.ttf");
        txtLogin.setTypeface(font_bold);

        imgFb.setOnClickListener(this);

        initFacebook();
    }

    @Override   public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imgFb:
                LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList(Constant.permission_login));

                break;
        }

    }

    public void RequestData() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                JSONObject json = response.getJSONObject();
                try {
                    if (json != null) {


                        String fbemail = json.getString("email");

                        login(fbemail);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public void login(String email) {
        constantClass.insertUser(email);
        nextActivity();
    }
    public void nextActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void initFacebook() {
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = loginResult.getAccessToken();
                        RequestData();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(Login.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(Login.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
