package com.ysw.placeserach;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import kotlinx.android.extensions.ContainerOptions;

public class EmailSignInActivity extends AppCompatActivity {

    Button clickSignin;
    EditText etEmail;
    EditText etPassword;

    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sign_in);

        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_circle_left_24);

        clickSignin = findViewById(R.id.clickSignin);
        clickSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etEmail = findViewById(R.id.et_email);
                etPassword = findViewById(R.id.et_password);

                email = etEmail.getText().toString();
                password = etPassword.getText().toString();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("emailusers").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(DocumentSnapshot document : queryDocumentSnapshots ) {
                            if (email.equals(document.getData().get("email").toString()) && password.equals(document.getData().get("password").toString())) {
                                Toast.makeText(EmailSignInActivity.this, "Email 로그인", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EmailSignInActivity.this, MainActivity.class));
                                finish();
                                return;
                            }else{
                                new AlertDialog.Builder(EmailSignInActivity.this).setMessage("Email 또는 Password가 잘못 되었습니다.").show();
                                return;
                            }
                        }//for

                    }//onSuccess
                });//db.collection("emailusers").get().addOnSuccessListener


            }//onClick
        });//setOnClickListener

    } //onCreate
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){finish();}
        return super.onOptionsItemSelected(item);
    } //onOptionitemselected
}//EmailSignInActivity