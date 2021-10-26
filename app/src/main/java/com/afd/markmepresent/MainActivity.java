package com.afd.markmepresent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
Button logOutBtn,takeAtn,markAtn,downloadAtn,taUsingQR,taUsingId,MmpUsingQR,MmpUsingId;
FirebaseAuth auth;
LinearLayout ll1,ll2;
FirebaseDatabase database,db1;
String MySap,MyName;
    int n = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        db1 = FirebaseDatabase.getInstance();
        takeAtn = findViewById(R.id.takeattendance);
        markAtn = findViewById(R.id.markattandance);
        ll1= findViewById(R.id.linearLayout);
        ll2= findViewById(R.id.linearLayout1);
        taUsingQR = findViewById(R.id.using_qr);
        MmpUsingId = findViewById(R.id.mmp_using_id);
        MmpUsingQR = findViewById(R.id.mmp_using_qr);
        taUsingId = findViewById(R.id.using_id);
       downloadAtn = findViewById(R.id.downloadattendance);
        logOutBtn = findViewById(R.id.logout);
        database.getReference("ActiveKey").child(auth.getUid()).child("key").setValue(0);
db1.getReference("Users").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        Map <String,String> map = (Map<String, String>) snapshot.getValue();
   MyName = map.get("Name");
   MySap = map.get("Sap");
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});

takeAtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        if(ll1.getVisibility() == View.VISIBLE){
            ll1.setVisibility(View.GONE);
        }
        else{
            ll1.setVisibility(View.VISIBLE);
        }

        if(ll2.getVisibility() == View.VISIBLE){
            ll2.setVisibility(View.GONE);
        }

    }
});

taUsingQR.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this,TakeAttendanceActivity.class);
        startActivity(intent);
    }
});
taUsingId.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this,AttendanceUsingIdPassActivity.class);
        startActivity(intent);
    }
});


markAtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        if(ll2.getVisibility() == View.VISIBLE){
            ll2.setVisibility(View.GONE);
        }
        else{
            ll2.setVisibility(View.VISIBLE);
        }

        if(ll1.getVisibility() == View.VISIBLE){
            ll1.setVisibility(View.GONE);
        }


    }
});
MmpUsingQR.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        n=0;
        IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
        intentIntegrator.setPrompt("Align QR Code in Square Box");
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setCaptureActivity(read.class);
        intentIntegrator.initiateScan();
    }
});

MmpUsingId.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
//        Toast.makeText(MainActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
Intent intent = new Intent(MainActivity.this,MmpUsingIdPassActivity.class);
startActivity(intent);

    }
});

downloadAtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Toast.makeText(MainActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
    }
});


        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        database.getReference("ActiveKey").child(auth.getUid()).child("key").setValue(0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult =  IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult.getContents()!=null){
            try {

                String[] arrOfStr = intentResult.getContents().split("/", 6);
                String Usd,key,name,dateTime;
                Usd = arrOfStr[1];
                key = arrOfStr[2];
                name = arrOfStr[3];
                dateTime = arrOfStr[4];
                if (!arrOfStr[0].equals("anilfromdit")) {
                    Toast.makeText(MainActivity.this, "This QR code does not belong to this app", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                    builder2.setTitle("Invalid QR Code");
                    builder2.setMessage("This is an invalid qr code for attendance");
                    builder2.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder2.show();
                    return;
                }


                database.getReference("ActiveKey").child(Usd).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, String> map = (Map<String, String>) snapshot.getValue();
                        String check = map.get("key");


                        if (n == 0) {
                            if (check.equals(key)) {
                                n++;
                                HashMap<Object, String> hashMap = new HashMap<>();
                                hashMap.put(auth.getUid(), "SapID");
                                try {
                                    database.getReference("AttendanceList").child(Usd).child(name + " " + dateTime).child(MyName).setValue(MySap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            int newKey = Integer.parseInt(key) + 1;
                                            String newKeyFinal = Integer.toString(newKey);
                                            HashMap<Object, String> hashMap2 = new HashMap<>();
                                            hashMap2.put("key", newKeyFinal);
                                            database.getReference("ActiveKey").child(Usd).setValue(hashMap2);

                                            Toast.makeText(MainActivity.this, MyName + " Marked Present", Toast.LENGTH_SHORT).show();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                                            builder.setTitle(name);
                                            builder.setMessage(MyName + " You Have Been Marked Present");
                                            builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            builder.show();

                                        }
                                    });
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(MainActivity.this, "Key Does Not Match", Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder builder3 = new AlertDialog.Builder(MainActivity.this);
                                builder3.setTitle("Reporting");
                                builder3.setMessage("This Incident Will Be Reported");
                                builder3.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder3.show();
                                HashMap<Object, String> reportMap = new HashMap<>();
                                reportMap.put("Lecture", name);
                                reportMap.put("Sap Id", MySap);
                                reportMap.put("Msg", "Used Old QR Code for Attendance");
                                database.getReference("Reports").child(Usd).child(MySap).setValue(reportMap);


                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            catch (Exception e ){
            Toast.makeText(MainActivity.this, "Invalid QR Code for Attendance", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(MainActivity.this, "Could Not Read Any QR CODE", Toast.LENGTH_SHORT).show();
        }

    }
}