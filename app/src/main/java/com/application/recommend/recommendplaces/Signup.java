package com.application.recommend.recommendplaces;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

//import com.basgeekball.awesomevalidation.AwesomeValidation;
//import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Signup extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    EditText t1,t2,t3,t4,t5,totp;
    Button b1;
    int i;
    String email,pass;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    FirebaseUser user;
    SmsManager smsManager;
     PendingIntent sendpi,delpi;
    public static final String SEND_SMS="send_sms";
   public static final String DEL_SMS="del_sms";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        email=getIntent().getExtras().getString("username");
        pass=getIntent().getExtras().getString("password");
        t1=findViewById(R.id.etName);
        t2=findViewById(R.id.etEmail);
        t3=findViewById(R.id.etDob);
        t4=findViewById(R.id.etPassword);
        t5=findViewById(R.id.etPhone);
        t2.setText(email);
        t4.setText(pass);
        b1=findViewById(R.id.bRegister);
        b1.setOnClickListener(this);
        auth=FirebaseAuth.getInstance();
        smsManager=SmsManager.getDefault();
        t3.setOnClickListener(this);
        t3.setOnFocusChangeListener(this);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Records");

        sendpi=PendingIntent.getBroadcast(this,0,new Intent(SEND_SMS),0);
       delpi=PendingIntent.getBroadcast(this,0,new Intent(DEL_SMS),0);
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        DatePickerDialog dialog;
        if(b == true){
            Calendar c = Calendar.getInstance();

            int y = c.get(Calendar.YEAR);
            int mo = c.get(Calendar.MONTH);
            int d = c.get(Calendar.DAY_OF_MONTH);
            dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    t3.setText( dayOfMonth + " / " + (monthOfYear + 1) + " / " + year);

                }
            }, y, mo, d);
            dialog.show();
        }
    }

    class MySend extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (this.getResultCode())
            {
                case RESULT_OK:
                    Toast.makeText(context, "Otp Sent to ur Mobile!!!", Toast.LENGTH_SHORT).show();
                   /* intent=new Intent(Signup.this,OTP.class);
                    intent.putExtra("otp",String.valueOf(i));
                    startActivity(intent);*/


                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(context, "No Service available!!!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class MyDel extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (this.getResultCode())
            {
                case RESULT_OK:
                    Toast.makeText(context, "Otp Delivered to ur Mobile!!!", Toast.LENGTH_SHORT).show();
                    //totp.setText(i);
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(context, "No Service available!!!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.registerReceiver(new MySend(),new IntentFilter(SEND_SMS));
        this.registerReceiver(new MyDel(),new IntentFilter(DEL_SMS));
    }

   @Override
   protected void onDestroy() {


       super.onDestroy();
       finish();

    }

    @Override
    public void onClick(View view) {
        String dob=t3.getText().toString();
        String [] dateParts = dob.split("/");
        String year = dateParts[2];
        year=year.replace(" ","");
        String name=t1.getText().toString();

        Calendar c=Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        String phone=t5.getText().toString();
        if(view==b1) {
            if(TextUtils.isEmpty(t1.getText().toString()) ||TextUtils.isEmpty(t2.getText().toString())||TextUtils.isEmpty(t3.getText().toString())||TextUtils.isEmpty(t4.getText().toString())||TextUtils.isEmpty(t5.getText().toString()))
            {
                Toast.makeText(this, "Fields cannot be left empty!!!!", Toast.LENGTH_SHORT).show();
            }
           else  if(Integer.parseInt(year)>y){
                Toast.makeText(this, "Enter a valid date!!!", Toast.LENGTH_SHORT).show();
                t3.requestFocus();
            }
           else  if(phone.length()<10)
                Toast.makeText(this, "Enter a phone number of 10 digits!!!", Toast.LENGTH_SHORT).show();
            else
            {
            Random r = new Random();
            i = r.nextInt(99999) + 100000;
            String str = Integer.toString(i);
            register();
            showOtpDialog();

            smsManager.sendTextMessage(t5.getText().toString(), null, str, sendpi, delpi);
        }}

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,
                MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void register()

    {

        //dialog.setMessage("Registering Please");
        final String key=databaseReference.push().getKey();
        String name=t1.getText().toString();
        String dob=t3.getText().toString();
        String phone=t5.getText().toString();
        final MyInfo myInfo=new MyInfo(name,email,dob,pass,phone,String.valueOf(i));
        databaseReference.child(key).setValue(myInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Signup.this, "Data Saved Succesfully!!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Signup.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showOtpDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater alertLayout = this.getLayoutInflater();
        final View dialogView = alertLayout.inflate(R.layout.activity_otp, null);
        builder.setView(dialogView);
        totp = dialogView.findViewById(R.id.eotp);
        final Button b1 =  dialogView.findViewById(R.id.bok);
        builder.setTitle("OTP!!!");
        //totp.setText(""+i);
        final AlertDialog dialog = builder.create();
        dialog.show();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              if(String.valueOf(i).contentEquals(totp.getText().toString())){

                Toast.makeText(Signup.this, "You are a valid user!!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Signup.this,MainActivity.class));
            }

        else
        {  Toast.makeText(Signup.this, "Wrong OTP!!!", Toast.LENGTH_SHORT).show();
           t1.setText("");

    }
        }});

    }

}

