package com.afd.markmepresent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseDatabase database,database1;
ProgressDialog pd;
EditText name,email,course,year,sap,rollno,password;
TextView login;
Spinner gender;
Button signup;
String Name,Email,Gender,Course,Year,Sap,Rollno,Password;
String DeviceId;
int response = 1;
    String [] list = {"Choose Your Gender","Male","Female","Other"};

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        database1 = FirebaseDatabase.getInstance();
        pd = new ProgressDialog(SignupActivity.this);
        name = findViewById(R.id.name);
    email = findViewById(R.id.email);
    course = findViewById(R.id.course);
    year = findViewById(R.id.year);
    sap = findViewById(R.id.sap);
    rollno = findViewById(R.id.rollno);
    password = findViewById(R.id.password);
    login = findViewById(R.id.loginBtn);
    gender = findViewById(R.id.gender);
    signup = findViewById(R.id.signup);
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
    gender.setAdapter(adapter);
    login.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
            startActivity(intent);
        }
    });
    DeviceId=getDevice(SignupActivity.this);
    Query deviceIdQuery=database.getReference().child("Users").orderByChild("DeviceId").equalTo(DeviceId);
    deviceIdQuery.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.getChildrenCount()>0){
                pd.dismiss();
                Toast.makeText(SignupActivity.this, "Device Already Registered", Toast.LENGTH_SHORT).show();
                response=0;
            }
        }

        @Override
        public void onCancelled(@NonNull  DatabaseError error) {

        }
    });
    signup.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(response==0){
                Toast.makeText(SignupActivity.this, "Device Already Register", Toast.LENGTH_SHORT).show();
            }else{
                work();
            }
        }
    });

    }

    public void work(){
        final int[] response = {1};
        pd.setTitle("Please Wait");
        pd.setMessage("Creating Your Account");
                pd.show();
                Name = name.getText().toString();
                Email = email.getText().toString();
                Gender =  gender.getSelectedItem().toString();
                Course = course.getText().toString();
                Year = year.getText().toString();
                Sap = sap.getText().toString();
                Rollno = rollno.getText().toString();
                Password = password.getText().toString();
                if(Name.length()==0){
                    name.setError("Please Enter Your Name");
                    response[0] =0;
                }
                if(Email.length()==0){
                    email.setError("Please Enter Your Email");
                    response[0] =0;
                }

                if(Gender.equals("Choose Your Gender")){
                    ((TextView)gender.getSelectedView()).setError("Choose Your Gender.");
                    response[0] =0;
                }
                if(Course.length()==0){
                    course.setError("Please Enter Your Course");
                    response[0] =0;
                }
                if(Year.length()==0 || Integer.parseInt(Year)>5 || Integer.parseInt(Year)<1){
                    year.setError("Please Enter Valid Academic Year");
                    response[0] =0;
                }
                if(Sap.length()==0  || Double.parseDouble(Sap)<1000000000){
                    sap.setError("Please Enter Valid SAP ID");
                    response[0] =0;
                }
                if(Rollno.length()==0 || Double.parseDouble(Rollno)<200000000){
                    Rollno="NA";
                }
                if(Password.length()==0){
                    password.setError("Please Choose Your Password");
                    response[0] =0;
                }
                if(response[0] ==1) {
                    auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                String id = task.getResult().getUser().getUid();
                                hashMap.put("Name", Name);
                                hashMap.put("Email", Email);
                                hashMap.put("Gender", Gender);
                                hashMap.put("Course", Course);
                                hashMap.put("Year", Year);
                                hashMap.put("Sap", Sap);
                                hashMap.put("Rollno", Rollno);
                                hashMap.put("Password", Password);
                                hashMap.put("Uid", id);
                                hashMap.put("DeviceId", DeviceId);

                                database1.getReference("Users").child(id).setValue(hashMap);
                                Toast.makeText(SignupActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                pd.dismiss();
                                Toast.makeText(SignupActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{

                    pd.dismiss();
                }



    }
    private String getDevice(Activity activity) {
        return Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}