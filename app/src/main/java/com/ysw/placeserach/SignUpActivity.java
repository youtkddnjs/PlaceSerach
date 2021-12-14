package com.ysw.placeserach;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    Button clickSignup;
    EditText etEmail;
    EditText etPassword;
    EditText etPasswordConfirm;

    String email;
    String password;
    String passwordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_circle_left_24);


        clickSignup = findViewById(R.id.clickSignup);
        clickSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEmail = findViewById(R.id.et_email);
                etPassword = findViewById(R.id.et_password);
                etPasswordConfirm = findViewById(R.id.et_password_confirm);

                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                passwordConfirm = etPasswordConfirm.getText().toString();

                if (!password.equals(passwordConfirm)) {
                    new AlertDialog.Builder(SignUpActivity.this).setMessage("패스워드확인 문제가 있습니다. 다시 확인하여 입력해주시기 바랍니다.").show();
                    etPasswordConfirm.requestFocus();
                    etPasswordConfirm.selectAll();
                    return;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("emailusers").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot document : queryDocumentSnapshots ){
                            if(email.equals(document.getData().get("email").toString())){
                                new AlertDialog.Builder(SignUpActivity.this).setMessage("E메일 중복됨").show();
                                etEmail.requestFocus();
                                etEmail.setSelection(etEmail.length());
                                return;
                            }//if
                        }//for

                        Map<String, String> user = new HashMap<>();
                        user.put("email",email);
                        user.put("password", password);

                        db.collection("emailusers").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(SignUpActivity.this, "회원가입완료", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });//addOnSuccessListener
                    }
                });//get().addOnSuccessListener


            }//onClick
        });//setOnClickListener




    }//Create

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){finish();}
        return super.onOptionsItemSelected(item);
    } //onOptionitemselected




}//SignUpActivity