package com.afd.markmepresent;

import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class TakeAttendanceActivity extends AppCompatActivity {
EditText headingET;
Button startBtn;
ImageView qrcode;
String dateToStr;
FirebaseAuth auth;
FirebaseDatabase database;
    String key,heading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        setContentView(R.layout.activity_take_attendance);
headingET = findViewById(R.id.nameET);
startBtn= findViewById(R.id.startBtn);
qrcode= findViewById(R.id.qrCode);
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        dateToStr = format.format(today);
startBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        int key1 = (int)(Math.random()*2645);
        key = Integer.toString(key1);
        heading = headingET.getText().toString();
        if(heading.length()<3){
            headingET.setError("Heading length should be atleast 3 characters");
            return;
        }
        else{

database.getReference("ActiveKey").child(auth.getUid()).child("key").setValue(key);
            startBtn.setVisibility(View.GONE);
            headingET.setVisibility(View.GONE);
            qrcode.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle(heading);
            Toast.makeText(TakeAttendanceActivity.this, "Attendance Mode Initiated", Toast.LENGTH_SHORT).show();
            takeAtn();

        }
    }
});


    }
    public void takeAtn(){




        MultiFormatWriter writer = new MultiFormatWriter();
        try{
            BitMatrix matrix = writer.encode("anilfromdit/"+auth.getUid()+"/"+key+"/"+heading+"/"+dateToStr+"/"+Math.random()*10, BarcodeFormat.QR_CODE,256,256);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            qrcode.setImageBitmap(bitmap);
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        catch (WriterException e){
            Toast.makeText(TakeAttendanceActivity.this, "An Error Occurred\nAttendance Mode Finished", Toast.LENGTH_SHORT).show();
            startBtn.setVisibility(View.VISIBLE);
            headingET.setVisibility(View.VISIBLE);
            qrcode.setVisibility(View.GONE);
        }


        database.getReference("ActiveKey").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String , String> map = (Map<String, String>) snapshot.getValue();
String keyI = map.get("key");

                if(Integer.parseInt(key)+1==Integer.parseInt(keyI)){
                    Toast.makeText(TakeAttendanceActivity.this, "Attendance Marked", Toast.LENGTH_SHORT).show();
                    key = keyI;
                    takeAtn();
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
        Intent intent = new Intent(TakeAttendanceActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}