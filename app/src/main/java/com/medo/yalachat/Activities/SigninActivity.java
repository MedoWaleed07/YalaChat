package com.medo.yalachat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.medo.yalachat.MainActivity;
import com.medo.yalachat.R;

public class SigninActivity extends AppCompatActivity {
    TextInputEditText email_field,password_field;
    String email, password;

    FirebaseAuth auth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        initViews();
        initFirebase();
    }
    private void initFirebase() {
        auth                        = FirebaseAuth.getInstance();

        firebaseDatabase            = FirebaseDatabase.getInstance();
        databaseReference           = firebaseDatabase.getReference();
    }

    private void initViews() {
        email_field                 = findViewById(R.id.email_field);
        password_field              = findViewById(R.id.password_field);
    }

    public void signin(View view) {
        email           = email_field.getText().toString();
        password        = password_field.getText().toString();
        if(email == null){
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
            email_field.requestFocus();
            return;
        }
        if(password == null || password.length() < 6){
            Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show();
            password_field.requestFocus();
            return;
        }
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(), StartActivity.class));
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
