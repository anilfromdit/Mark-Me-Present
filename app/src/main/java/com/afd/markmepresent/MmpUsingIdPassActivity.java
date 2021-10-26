package com.afd.markmepresent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class MmpUsingIdPassActivity extends AppCompatActivity {
EditText mmpId, mmpPassword;
Button mmpBtn;
FirebaseAuth auth;
FirebaseDatabase database;

String myId,myPass;
ProgressDialog pd ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mmp_using_id_pass);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mmpId = findViewById(R.id.mmp_id);
        mmpPassword = findViewById(R.id.mmp_pass);
        mmpBtn = findViewById(R.id.mmp_start_btn);
        pd = new ProgressDialog(MmpUsingIdPassActivity.this);
        pd.setTitle("Please Wait");
        pd.setMessage("please wait while we mark you present");
        mmpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myId = mmpId.getText().toString();
                myPass = mmpPassword.getText().toString();
                if(myId.length()<4){
                    mmpId.setError("Invalid ID");
                    return;
                }
                if(myPass.length()<4){
                    mmpPassword.setError("Invalid Password");
                    return;
                }
pd.show();
                database.getReference("ActiveIdPass").child(myId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Map<String , String> map = (Map<String, String>) snapshot.getValue();
                            String sPass=map.get("Password");
                            if(myPass.equals(sPass)){
                                String sUsd = map.get("Usd");
                                String sHeading = map.get("Heading");
                                markMe(sUsd,sHeading);
                            }
                            else{
                                pd.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(MmpUsingIdPassActivity.this);
                                builder.setTitle("Invalid Credentials");
                                builder.setMessage("Oops,Invalid ID or Password");
                                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(MmpUsingIdPassActivity.this, MmpUsingIdPassActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                builder.show();
                            }
                        }
                        else{

                            AlertDialog.Builder builder = new AlertDialog.Builder(MmpUsingIdPassActivity.this);
                            builder.setTitle("Invalid Credentials");
                            builder.setMessage("Oops,Invalid ID or Password");
                            builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(MmpUsingIdPassActivity.this, MmpUsingIdPassActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            builder.show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }


        });
    }
     void markMe(String sUsd, String sHeading) {
        database.getReference("Users").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String , String> map1 = (Map<String, String>) snapshot.getValue();
                String mySap = map1.get("Sap");
                String myName = map1.get("Name");
                database.getReference("AttendanceList").child(sUsd).child(sHeading).child(myName).setValue(mySap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                   if(task.isSuccessful()){
                       AlertDialog.Builder builder = new AlertDialog.Builder(MmpUsingIdPassActivity.this);
                       builder.setTitle("Success");
                       builder.setMessage(myName+" You're Marked Present in "+ sHeading);
                       builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               Intent intent = new Intent(MmpUsingIdPassActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                           }
                       });
                       builder.show();
                   }
                   else{
                       AlertDialog.Builder builder = new AlertDialog.Builder(MmpUsingIdPassActivity.this);
                       builder.setTitle("Failed");
                       builder.setMessage("Oops, There was an error while marking you present");
                       builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               Intent intent = new Intent(MmpUsingIdPassActivity.this, MmpUsingIdPassActivity.class);
                               startActivity(intent);
                               finish();
                           }
                       });
                       builder.show();
                   }
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}