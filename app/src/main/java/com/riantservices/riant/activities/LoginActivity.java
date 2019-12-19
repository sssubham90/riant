package com.riantservices.riant.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.riantservices.riant.R;
import com.riantservices.riant.helpers.SessionManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends Activity implements OnClickListener  {

    private EditText EtLogEmail, EtLogPass;
    static int rslt;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button btnSignin, btnRegister;
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        setContentView(R.layout.activity_login);
        Intent intent = getIntent();
        EtLogEmail = findViewById(R.id.LogEmail);
        EtLogPass = findViewById(R.id.LogPass);
        if(intent.hasExtra("Email")) {
            String Email_User_From_register = intent.getStringExtra("Email");
            String PassUser_From_register = intent.getStringExtra("Pass");
            EtLogEmail.setText(Email_User_From_register);
            EtLogPass.setText(PassUser_From_register);
        }

        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    TextView textView = (TextView) v;
                    textView.setBackground(getResources().getDrawable(R.drawable.textboxselected));
                    textView.setHintTextColor(getResources().getColor(R.color.colorBlack));
                    textView.setTextColor(getResources().getColor(R.color.colorBlack));
                }
                else{
                    TextView textView = (TextView) v;
                    textView.setBackground(getResources().getDrawable(R.drawable.textboxborder));
                    textView.setHintTextColor(getResources().getColor(R.color.colorWhite));
                    textView.setTextColor(getResources().getColor(R.color.colorWhite));
                }
            }
        };
        EtLogEmail.setOnFocusChangeListener(onFocusChangeListener);
        EtLogPass.setOnFocusChangeListener(onFocusChangeListener);
        btnSignin = findViewById(R.id.btnSignin);
        btnRegister = findViewById(R.id.btnRegister);
        btnSignin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        EtLogPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    @Override
    public void onClick(View view) {
        String strEmail, strPass;
        switch (view.getId()) {
            case R.id.btnSignin:
                strEmail = EtLogEmail.getText().toString();
                strPass = EtLogPass.getText().toString();
                if (strEmail.matches("")) {
                    alertDialog("Email is required");
                    break;
                } else if (strPass.matches("")) {
                    alertDialog("Password is required");
                    break;
                } else if (!isValidEmailAddress(strEmail)) {
                    alertDialog("Invalid Email Id");
                    break;
                } else {
                    Login(strEmail, strPass);
                }
                break;
            case R.id.btnRegister:
                Intent register = new Intent(this, RegisterActivity.class);
                startActivity(register);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
        }
    }

    public void alertDialog(String Message) {

        new AlertDialog.Builder(this).setTitle("Riant Alert").setMessage(Message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    protected void Login(final String email, final String pwd) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Connecting");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        try{
            Thread.sleep(1000);
        }catch(Exception ignored) {}
        progressDialog.dismiss();
        if(true){
            sessionManager.createLoginSession(email);
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }
        else
        {
            alertDialog("Invalid Username or Password, Try again!!");
        }
    }
}