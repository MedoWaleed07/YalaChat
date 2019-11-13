package com.medo.yalachat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.UploadTask;
import com.medo.yalachat.Activities.SigninActivity;
import com.medo.yalachat.Activities.StartActivity;
import com.medo.yalachat.Models.UserModel;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    TextInputEditText username_field,email_field,password_field,cpassword_field;
    CircleImageView user_pic;
    String username, email, password, cpassword,id;
    boolean doubleBackToExitPressedOnce = false;
    Uri picture;

    FirebaseAuth auth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseUser user;

    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initFirebase();

        if(user != null){
            startActivity(new Intent(getApplicationContext(),StartActivity.class));
            finish();
        }
    }

    private void initFirebase() {
        auth                        = FirebaseAuth.getInstance();

        firebaseDatabase            = FirebaseDatabase.getInstance();
        databaseReference           = firebaseDatabase.getReference();

        user = auth.getCurrentUser();

        storage                     = FirebaseStorage.getInstance();
    }

    private void initViews() {
        username_field              = findViewById(R.id.username_field);
        email_field                 = findViewById(R.id.email_field);
        password_field              = findViewById(R.id.password_field);
        cpassword_field             = findViewById(R.id.cpassword_field);
        user_pic                    = findViewById(R.id.user_pic);
        user_pic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(1,1)
                        .start(MainActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == Activity.RESULT_OK)
            {
                if (result != null)
                {
                    picture = result.getUri();

                    Picasso.get()
                            .load(picture)
                            .placeholder(R.drawable.ic_boy)
                            .error(R.drawable.ic_boy)
                            .into(user_pic);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void register(View view) {
        username        = username_field.getText().toString();
        email           = email_field.getText().toString();
        password        = password_field.getText().toString();
        cpassword       = cpassword_field.getText().toString();
        if(username == null){
            Toast.makeText(this, "Enter Username", Toast.LENGTH_SHORT).show();
            username_field.requestFocus();
            return;
        }
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
        if(!cpassword.equals(password)){
            Toast.makeText(this, "Passwords not match", Toast.LENGTH_SHORT).show();
            cpassword_field.requestFocus();
            return;
        }
        if (picture == null) {
            Toast.makeText(this, "Choose Picture", Toast.LENGTH_SHORT).show();
            return;
        }
        createUser(username,email,password);
    }

    private void createUser(final String username, final String email, final String password) {
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            id = task.getResult().getUser().getUid();
                            uploadPicture(username,email,picture,id);
                        }else{
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadPicture(final String username, final String email, Uri picture, final String id) {
        UploadTask uploadTask;
        storageReference = FirebaseStorage.getInstance().getReference().child("images/" + picture.getLastPathSegment());
        uploadTask = storageReference.putFile(picture);
        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri image = task.getResult();
                String imageURL = image.toString();

                saveDB(username,email,imageURL,id);
            }
        });
    }

    private void saveDB(String username, String email, String imageURL, String id) {
        UserModel user = new UserModel(username,email,imageURL);
        databaseReference.child("Users").child(id).setValue(user);

        startActivity(new Intent(getApplicationContext(), StartActivity.class));
    }

    public void already(View view) {
        startActivity(new Intent(getApplicationContext(), SigninActivity.class));
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
