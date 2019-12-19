package com.riantservices.riant.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.riantservices.riant.R;

public class RegisterActivity extends Activity{
    EditText UserName, Email, Phone, Pass1, Pass2;
    Button BtnRegister;
    String strName, strEmail, strPhone,strPass, strPass2;
    public static String rslt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        UserName = findViewById(R.id.RegUsername);
        Email = findViewById(R.id.RegEmail);
        Phone = findViewById(R.id.RegPhone);
        Pass1 = findViewById(R.id.RegPass);
        Pass2 = findViewById(R.id.RegPass2);
        BtnRegister = findViewById(R.id.BtnRegister);
        BtnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                strName = UserName.getText().toString();
                strEmail = Email.getText().toString();
                strPhone = Phone.getText().toString();
                strPass = Pass1.getText().toString();
                strPass2 = Pass2.getText().toString();
                if (strName.matches("")) {
                    alertDialog("Username is required");
                }else if(strEmail.matches("")) {
                    alertDialog("Email is required");
                }else if(strPhone.matches("")) {
                    alertDialog("Phone is required");
                }else if(strPass.matches("")) {
                    alertDialog("Pasword is required");
                }
                else if(!isValidEmailAddress(strEmail)) {
                    alertDialog("Introduce a valid email format");
                }
                else if(!strPass.equals(strPass2)) {
                    alertDialog("Password not match");
                }
                else{
                    Register(strName, strEmail, strPhone, strPass);
                }
            }
        });
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
        UserName.setOnFocusChangeListener(onFocusChangeListener);
        Email.setOnFocusChangeListener(onFocusChangeListener);
        Phone.setOnFocusChangeListener(onFocusChangeListener);
        Pass1.setOnFocusChangeListener(onFocusChangeListener);
        Pass2.setOnFocusChangeListener(onFocusChangeListener);

    }
    public void alertDialog(String Message){
        new AlertDialog.Builder(this)
                .setTitle("Riant Message")
                .setMessage(Message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert).show();

    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    protected void Register(final String Name, final String Email,final String Phone,final String Password) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Connecting");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        try{
            Thread.sleep(1000);
        }catch(Exception ignored) {}
        progressDialog.dismiss();
        alertDialog("User Succcessfully Registered");
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        intent.putExtra("Email",Email);
        intent.putExtra("Pass",Password);
        startActivity(intent);
    }
}