package com.riantservices.riant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;

import com.riantservices.riant.R;
import com.riantservices.riant.helpers.SessionManager;

public class AccountActivity extends AppCompatActivity {

    ViewSwitcher name,number,pass;
    EditText ETname,ETnumber,ETpass;
    TextView Tname,Tnumber,Tpass,Temail;
    ImageButton editName,editNumber,editPass;
    Button save,logout;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        if(getSupportActionBar()!=null)getSupportActionBar().hide();
        name = findViewById(R.id.nameSwitcher);
        number = findViewById(R.id.numberSwitcher);
        pass = findViewById(R.id.passSwitcher);
        ETname = findViewById(R.id.ETName);
        ETnumber = findViewById(R.id.ETNumber);
        ETpass = findViewById(R.id.ETPass);
        Tname = findViewById(R.id.name);
        Tnumber = findViewById(R.id.number);
        Temail = findViewById(R.id.email);
        Tpass = findViewById(R.id.pass);
        editName = findViewById(R.id.editName);
        editName.setOnClickListener(iconClickListener);
        editNumber = findViewById(R.id.editNumber);
        editNumber.setOnClickListener(iconClickListener);
        editPass = findViewById(R.id.editPass);
        editPass.setOnClickListener(iconClickListener);
        save = findViewById(R.id.save);
        save.setOnClickListener(iconClickListener);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(iconClickListener);
        //loadData();
        fetchOffline();
    }

    View.OnClickListener iconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.editName:
                    name.showNext();
                    break;
                case R.id.editNumber:
                    number.showNext();
                    break;
                case R.id.editPass:
                    pass.showNext();
                    break;
                case R.id.save:
                    if (ETname != null && ETnumber != null && ETpass != null) {
                        saveData(ETname.getText().toString(), ETnumber.getText().toString(), ETpass.getText().toString());
                    }
                    break;
                case R.id.logout:
                    sessionManager.logoutUser();
                    Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    break;
            }
        }
    };

    public void saveData(String name,String number,String password){
        boolean sendSuccess;
        //send data to server
        sendSuccess=true;
        //
        if(this.name.getNextView().getId()==R.id.name) this.name.showNext();
        if(this.number.getNextView().getId()==R.id.number) this.number.showNext();
        if(this.pass.getNextView().getId()==R.id.pass) this.pass.showNext();
        if(sendSuccess){
            Tname.setText(ETname.getText());
            Tnumber.setText(ETnumber.getText());
        }
    }

    public void loadData(){
        try{
            //fetchFromServer
        }
        catch (Exception e){
            fetchOffline();
        }
    }

    public void fetchOffline(){
        sessionManager = new SessionManager(getApplicationContext());
        Temail.setText(sessionManager.getEmail());
    }
}
