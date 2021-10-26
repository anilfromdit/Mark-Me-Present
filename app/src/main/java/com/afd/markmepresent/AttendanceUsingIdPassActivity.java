package com.afd.markmepresent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class AttendanceUsingIdPassActivity extends AppCompatActivity {
EditText heading,taId,taPassword;
Button taStartBtn;
TextView counter,counterTag;
RelativeLayout myLayout;
FirebaseDatabase database;
FirebaseAuth auth;
String myHeading,myID,myPassword;
ProgressDialog pd ;
String dateToStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_using_id_pass);

    heading = findViewById(R.id.nameET);
    taId = findViewById(R.id.ta_id);
    taPassword = findViewById(R.id.ta_pass);
    taStartBtn = findViewById(R.id.start_btn);
    database = FirebaseDatabase.getInstance();
    myLayout = findViewById(R.id.my_R_layout);
    counter = findViewById(R.id.count);
    counterTag = findViewById(R.id.a_text);
    auth = FirebaseAuth.getInstance();
pd = new ProgressDialog(AttendanceUsingIdPassActivity.this);
pd.setTitle("Please Wait");
pd.setMessage("Initializing Attendance Mode");
    taStartBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            myHeading = heading.getText().toString();
            myID = taId.getText().toString();
            myPassword = taPassword.getText().toString();

            if(myHeading.length()<6){
                heading.setError("Heading length should be at least 6 char ");
                return;
            }
            if(myID.length()<4){
                taId.setError("Id length should be at least 4 char ");
                return;
            }
            if(myPassword.length()<4){
                taPassword.setError("Password length should be at least 4 char ");
                return;
            }
            pd.show();
            Date today = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
            dateToStr = format.format(today);
            HashMap<Object,String> hashMap = new HashMap<>();
            hashMap.put("Usd",auth.getUid());
            hashMap.put("Password",myPassword);
            hashMap.put("Heading",myHeading +" "+ dateToStr);
            database.getReference("ActiveIdPass").child(myID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                 if(task.isSuccessful()){
                     pd.dismiss();
                     Toast.makeText(AttendanceUsingIdPassActivity.this, "Attendance Mode Initialized", Toast.LENGTH_SHORT).show();
                     AttendanceMode();
                 }
                 else{
                     pd.dismiss();
                     Toast.makeText(AttendanceUsingIdPassActivity.this, "Task Failed: \n"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                 }
                }


            });




        }
    });




    }
    void AttendanceMode() {
        getSupportActionBar().hide();
        heading.setVisibility(View.GONE);
        taPassword.setVisibility(View.GONE);
        taId.setVisibility(View.GONE);
        taStartBtn.setVisibility(View.GONE);
        myLayout.setBackgroundColor(getResources().getColor(R.color.black));
        counter.setVisibility(View.VISIBLE);
        counterTag.setVisibility(View.VISIBLE);
//        myHeading +" "+ dateToStr
        database.getReference("AttendanceList").child(auth.getUid()).child(myHeading +" "+ dateToStr).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
           if(snapshot.exists()) {
               int countA = (int)snapshot.getChildrenCount();
               counter.setText(String.valueOf(countA));
           }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(heading.length()>6){
            database.getReference("ActiveIdPass").child(myID).removeValue();
        }
    }
}