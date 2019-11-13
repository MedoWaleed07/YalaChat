package com.medo.yalachat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medo.yalachat.Models.MessageModel;
import com.medo.yalachat.Models.UserModel;
import com.medo.yalachat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat_Activity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    EditText message_field;
    FloatingActionButton sent;
    List<MessageModel> messages;

    String name,img,message_txt,key,msgKey;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_);

        key = getIntent().getStringExtra("key");

        initViews();
        initFireBase();
        getUserData(getUID());
        sentbtn();
        getMessages(key);
    }

    private void getMessages(String roomkey) {

        databaseReference.child("Messages").child(roomkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    MessageModel messageModel = dataSnapshot1.getValue(MessageModel.class);
                    messages.add(messageModel);
                }
                ChatAdapter adapter = new ChatAdapter(messages);
                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sentbtn() {
        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message_txt                     = message_field.getText().toString();
                if(message_txt.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Write Message", Toast.LENGTH_SHORT).show();
                    message_field.requestFocus();
                    return;
                }
                sentMsg(message_txt,name,img,getUID());
            }
        });
    }

    private void initFireBase() {
        firebaseDatabase                = FirebaseDatabase.getInstance();
        databaseReference               = firebaseDatabase.getReference();
    }
    private void sentMsg(String message_txt, String name, String img, String uid) {
        MessageModel messageModel = new MessageModel(name,message_txt,img,uid);
        msgKey = databaseReference.child("Messages").child(key).push().getKey();
        if(msgKey!= null)
            databaseReference.child("Messages").child(key).child(msgKey).setValue(messageModel);
        message_field.setText("");
    }

    private void getUserData(String uid) {
        databaseReference.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel user      = dataSnapshot.getValue(UserModel.class);

                if(user != null){
                    name            = user.getUsername();
                    img             = user.getImage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initViews() {
        recyclerView                    = findViewById(R.id.recyclerview);
        layoutManager                   = new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
        sent                            = findViewById(R.id.sent_btn);
        message_field                   = findViewById(R.id.message_field);
        messages                        = new ArrayList<>();

        recyclerView.setLayoutManager(layoutManager);
    }

    public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatVH>{
        List<MessageModel> model;

        public ChatAdapter(List<MessageModel> model) {
            this.model = model;
        }

        @NonNull
        @Override
        public ChatVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.message_item,parent,false);
            return new ChatVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatVH holder, int position) {
            MessageModel messageModel = model.get(position);

            holder.name.setText(messageModel.getName());
            holder.msg.setText(messageModel.getMessage());
            String id = messageModel.getId();
            Picasso.get().load(messageModel.getImage()).into(holder.img);

            if(id.equals(getUID())){
                holder.linearLayout.setGravity(Gravity.END);
                holder.inner.setBackgroundResource(R.color.colorAccent);
                holder.name.setTextColor(Color.WHITE);
                holder.msg.setTextColor(Color.WHITE);
            }
        }

        @Override
        public int getItemCount() {
            return model.size();
        }

        public class ChatVH extends RecyclerView.ViewHolder {
            TextView name,msg;
            CircleImageView img;
            LinearLayout linearLayout;
            LinearLayout inner;
            public ChatVH(@NonNull View itemView) {
                super(itemView);
                name                = itemView.findViewById(R.id.msg_name_txt);
                msg                 = itemView.findViewById(R.id.msg_txt);
                img                 = itemView.findViewById(R.id.msg_img);
                linearLayout        = itemView.findViewById(R.id.linear);
                inner               = itemView.findViewById(R.id.innerLayout);
            }
        }
    }


    private String getUID() {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return id;
    }
}
