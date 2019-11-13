package com.medo.yalachat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.medo.yalachat.Models.RoomModel;
import com.medo.yalachat.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class Add_new_Activity extends AppCompatActivity {
    CheckBox add_pass;
    TextInputEditText room_name_field,password_field;
    CircleImageView room_pic;

    Uri picture;

    FirebaseAuth auth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseStorage storage;

    String room_name,room_password,id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_);

        initViews();
        initFireBase();
        check();
    }

    private void initFireBase() {
        auth                        = FirebaseAuth.getInstance();

        firebaseDatabase            = FirebaseDatabase.getInstance();
        databaseReference           = firebaseDatabase.getReference();

        storage                     = FirebaseStorage.getInstance();
    }

    private void initViews() {
        add_pass                = findViewById(R.id.checkbox);
        room_name_field         = findViewById(R.id.roomname_field);
        password_field          = findViewById(R.id.password_field);
        room_pic                = findViewById(R.id.room_pic);

        room_pic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(1,1)
                        .start(Add_new_Activity.this);
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
                            .placeholder(R.drawable.ic_speech_bubble)
                            .error(R.drawable.ic_speech_bubble)
                            .into(room_pic);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void check() {
        password_field.setEnabled(false);
        password_field.setBackgroundResource(R.drawable.gray_edittext);
        add_pass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!compoundButton.isChecked()){
                    password_field.setEnabled(false);
                    password_field.setFocusable(false);
                    password_field.setBackgroundResource(R.drawable.gray_edittext);
                    password_field.setText("");
                }else{
                    password_field.setEnabled(true);
                    password_field.setFocusable(true);
                    password_field.setFocusableInTouchMode(true);
                    password_field.setBackgroundResource(R.drawable.edittext);
                }
            }
        });
    }

    public void create(View view) {

        Toast.makeText(this, "Please Wait Don't Press Again", Toast.LENGTH_LONG).show();
        room_name               = room_name_field.getText().toString();
        room_password           = password_field.getText().toString();

        if(room_name == null){
            Toast.makeText(this, "Invalid name", Toast.LENGTH_SHORT).show();
            room_name_field.requestFocus();
            return;
        }
        if(add_pass.isChecked() && password_field == null){
            Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show();
            password_field.requestFocus();
            return;
        }
        if(picture == null){
            Toast.makeText(this, "Choose Picture", Toast.LENGTH_SHORT).show();
            return;
        }
        uploadImg(room_name,picture);
    }

    private void uploadImg(final String room_name, Uri picture) {
        UploadTask uploadTask;
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("rooms_images/" + picture.getLastPathSegment());
        uploadTask = storageReference.putFile(picture);

        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
        {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
            {
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>()
        {
            @Override
            public void onComplete(@NonNull Task<Uri> task)
            {
                if (task.isSuccessful())
                {
                    Uri image = task.getResult();
                    String photoUrl = image.toString();

                    addTodb(room_name,room_password,photoUrl);
                } else
                {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addTodb(String room_name, String room_password, String photoUrl) {
        id = databaseReference.child("Rooms").push().getKey();
        RoomModel room = new RoomModel(room_name,room_password,photoUrl,id);
        databaseReference.child("Rooms").child(id).setValue(room);
        startActivity(new Intent(getApplicationContext(), StartActivity.class));
        finish();
    }
}
