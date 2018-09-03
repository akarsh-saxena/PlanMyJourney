package com.application.recommend.recommendplaces;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.basgeekball.awesomevalidation.AwesomeValidation;
//import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {
    EditText etuser,etpass;
    Button bsignup,bsignin;
    FirebaseAuth auth;
    FirebaseUser user;
    ProgressDialog dialog;
    TextToSpeech tts;
    DatabaseReference reference;
    String email,password;
    TextView tvlogin;
    ImageView img;
    boolean hidden =true;
    boolean Success;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    int i=0;
 //   AwesomeValidation validation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_ACTIVITY_CLEAR_TOP);
        setContentView(R.layout.activity_main);
        etuser=findViewById(R.id.etuser);
        etpass=findViewById(R.id.etpass);
        img=findViewById(R.id.imageButton3);
        img.setOnClickListener(this);
       // bsignin=findViewById(R.id.bsignin);
        bsignup=findViewById(R.id.bsignup);
        bsignup.setOnClickListener(this);
       // bsignin.setOnClickListener(this);
        tvlogin=findViewById(R.id.tvlogin);
        tvlogin.setOnClickListener(this);
        dialog=new ProgressDialog(this);
        auth=FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        tts=new TextToSpeech(this,this);
        reference= FirebaseDatabase.getInstance().getReference();
        reference.child("Records").child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Toast.makeText(MainActivity.this, "Username already exists!!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            // reference.addValueEventListener(this);

    });
    }

    @Override
    protected void onPause() {
        super.onPause();
        tts.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.stop();
    }
    @Override
    public void onBackPressed() {
        long t=System.currentTimeMillis();
        if(i==0 ){
            i=1;
            Toast.makeText(this, "Press again to exit!!", Toast.LENGTH_SHORT).show();
        }
        else if(System.currentTimeMillis()-t<1000)
        {
            super.onBackPressed();
            //System.exit(0);
           /* Intent intent = new Intent(this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);*/
           finish();
          // return;

        }
       // i=0;
    }


    @Override
    public void onClick(View view) {
        final String str = etuser.getText().toString();

        if (view == bsignup) {
            //final String use=etuser.getText().toString();
            //String pass=etpass.getText().toString();
            if (TextUtils.isEmpty(etuser.getText().toString()) || TextUtils.isEmpty(etpass.getText().toString())) {
                Toast.makeText(this, "Fill the fields..then only you can proceed further!!!!", Toast.LENGTH_SHORT).show();
                tts.speak("Fill the fields..then only you can proceed further", TextToSpeech.QUEUE_FLUSH, null);
            } else if (!str.contains("@") || !str.contains(".com"))
                Toast.makeText(this, "Invalid Email!!! ", Toast.LENGTH_SHORT).show();
            else if (etpass.length() < 6)
                Toast.makeText(this, R.string.pass, Toast.LENGTH_SHORT).show();
            /* else  if(user.getEmail().toString().equals(etuser.getText().toString()))
                Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();*/


            else {
                registerUser();

            }


        }
        if (view == tvlogin) {


            Intent in = new Intent(this, SignIn.class);
            in.putExtra("username", etuser.getText().toString());
            in.putExtra("password", etpass.getText().toString());
            startActivity(in);
        }

        if (view == img) {
            if (hidden) {
                etpass.setTransformationMethod(null);
                img.setImageResource(R.mipmap.green);
                hidden=false;
            }
            else {
                etpass.setTransformationMethod(new PasswordTransformationMethod());
                img.setImageResource(R.mipmap.red);
                hidden=true;
            }
        }
    }


    private void registerUser() {

        //final boolean Success;
        String user=etuser.getText().toString();
        String pass=etpass.getText().toString();

        if(TextUtils.isEmpty(user)|| TextUtils.isEmpty(pass))
            Toast.makeText(this, "All fields are Mandatory!!!", Toast.LENGTH_SHORT).show();
        dialog.setMessage("Registering in firebase...PLease Wait");
        dialog.show();
        auth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                     Toast.makeText(MainActivity.this, "Successfully Registered!!!", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                    //startActivity(new Intent(MainActivity.this,Login.class));
                    Intent in = new Intent(MainActivity.this, Signup.class);
                    in.putExtra("username", etuser.getText().toString());
                    in.putExtra("password", etpass.getText().toString());
                    startActivity(in);
                    Log.d("Suces","in sucees"+String.valueOf(Success));


                }

            }

        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

               Toast.makeText(MainActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                etuser.setText("");
                etpass.setText("");
                etuser.requestFocus();

            }
        });
            //return Success;
    }

    @Override
    public void onInit(int i) {

    }



}


