package com.afd.markmepresent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {
EditText email,password;
Button loginBtn;
String DeviceId;
TextView signup;
FirebaseDatabase database;
FirebaseAuth auth;
ProgressDialog pd;
String Email,Password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        pd = new ProgressDialog(LoginActivity.this);
        pd.setTitle("Please Wait");
        pd.setMessage("Logging In...");
        auth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        DeviceId=getDevice(LoginActivity.this);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        signup = findViewById(R.id.signUpTxt);

if(auth.getUid()!=null){
    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
    startActivity(intent);
    finish();
}
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Email = email.getText().toString();
                Password = password.getText().toString();
if(Email.length()<5){
    email.setError("Email is Required");
    return;
}
if(Password.length()<4){
    password.setError("Password is Required");
    return;
}
                pd.show();
                auth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
if(task.isSuccessful()){
    pd.setMessage("Verifying Device ID");
    database.getReference("Users").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Map<String, String> map = (Map<String, String>) snapshot.getValue();
                                        String di = map.get("DeviceId");
                                        if(di.equals(DeviceId)){
                                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else{
                                            pd.dismiss();
                                            Toast.makeText(LoginActivity.this, "This device already registered with Someone Else ", Toast.LENGTH_SHORT).show();
                                            auth.signOut();
                                        }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
}else{
    pd.dismiss();
    Toast.makeText(LoginActivity.this, "Error :"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
}
                    }
                });
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private String getDevice(Activity activity) {
        return Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}