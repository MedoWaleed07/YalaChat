package com.medo.yalachat.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medo.yalachat.Activities.Chat_Activity;
import com.medo.yalachat.MainActivity;
import com.medo.yalachat.Models.RoomModel;
import com.medo.yalachat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyRoomsFragment extends Fragment {
    FloatingActionButton logout_btn;
    View view;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    DividerItemDecoration dividerItemDecoration;
    List<RoomModel> myRooms;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.myrooms_fragment,null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initFireBase();
        getRooms();
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setMessage("Are you Sure to Logout")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getContext(), MainActivity.class));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();
            }
        });
    }

    private void getRooms() {
        databaseReference.child("MyRooms").child(getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myRooms.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    RoomModel roomModel = dataSnapshot1.getValue(RoomModel.class);
                    myRooms.add(roomModel);
                }
                MyRoomsAdapter adapter = new MyRoomsAdapter(myRooms);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initFireBase() {
        firebaseDatabase            = FirebaseDatabase.getInstance();
        databaseReference           = firebaseDatabase.getReference();
    }

    private void initViews() {
        recyclerView                = view.findViewById(R.id.recyclerview);
        layoutManager               = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        dividerItemDecoration       = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        myRooms                     = new ArrayList<>();
        logout_btn                  = view.findViewById(R.id.logout);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    public class MyRoomsAdapter extends RecyclerView.Adapter<MyRoomsAdapter.MyRoomsVH>{
        List<RoomModel> model;

        public MyRoomsAdapter(List<RoomModel> model) {
            this.model = model;
        }

        @NonNull
        @Override
        public MyRoomsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.myrooms_item,null);
            return new MyRoomsVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyRoomsVH holder, int position) {
            RoomModel roomModel = model.get(position);

            holder.room_name.setText(roomModel.getRoom_name());
            Picasso.get().load(roomModel.getImage()).into(holder.room_image);

            final String id = roomModel.getId();

            holder.join_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(),Chat_Activity.class);
                    intent.putExtra("key",id);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return model.size();
        }

        public class MyRoomsVH extends RecyclerView.ViewHolder {
            TextView room_name;
            ImageView room_image;
            Button join_btn;
            public MyRoomsVH(@NonNull View itemView) {
                super(itemView);
                room_name                   = itemView.findViewById(R.id.myroom_name);
                room_image                  = itemView.findViewById(R.id.myroom_img);
                join_btn                    = itemView.findViewById(R.id.join_btn);
            }
        }
    }
    private String getUID() {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return id;
    }
}
