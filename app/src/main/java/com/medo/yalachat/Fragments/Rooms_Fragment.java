package com.medo.yalachat.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.medo.yalachat.Activities.Add_new_Activity;
import com.medo.yalachat.Activities.StartActivity;
import com.medo.yalachat.MainActivity;
import com.medo.yalachat.Models.RoomModel;
import com.medo.yalachat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class Rooms_Fragment extends Fragment {
    FloatingActionButton add_btn,logout_btn;
   RecyclerView recyclerView;
   RecyclerView.LayoutManager layoutManager;
   DividerItemDecoration dividerItemDecoration;
   View view;
   AlertDialog passwordDialog;
   String dialog_password;

   List<RoomModel> rooms;
   
   FirebaseAuth auth;

   FirebaseDatabase firebaseDatabase;
   DatabaseReference databaseReference;

   FirebaseStorage firebaseStorage;
   StorageReference storageReference;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.rooms_fragment,null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initFireBase();
        getRooms();
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), Add_new_Activity.class));
            }
        });
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
       databaseReference.child("Rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                rooms.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    RoomModel room = dataSnapshot1.getValue(RoomModel.class);
                    rooms.add(room);
                }
                RoomsAdapter adapter = new RoomsAdapter(rooms);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initFireBase() {
        auth                        = FirebaseAuth.getInstance();

        firebaseDatabase            = FirebaseDatabase.getInstance();
        databaseReference           = firebaseDatabase.getReference();

        firebaseStorage             = FirebaseStorage.getInstance();
        storageReference            = firebaseStorage.getReference();
    }

    private void initViews() {
        recyclerView            = view.findViewById(R.id.recyclerview);
        layoutManager           = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        dividerItemDecoration   = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        rooms                   = new ArrayList<>();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        add_btn                 = view.findViewById(R.id.add);
        logout_btn              = view.findViewById(R.id.logout);
    }

    public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomsVH>{

        List<RoomModel> rooms;

        public RoomsAdapter(List<RoomModel> rooms) {
            this.rooms = rooms;
        }

        @NonNull
        @Override
        public RoomsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.rooms_item,null);
            return new RoomsVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RoomsVH holder, int position) {
            RoomModel model         = rooms.get(position);
            holder.room_name.setText(model.getRoom_name());

            Picasso.get().load(model.getImage()).into(holder.room_img);

            if(!model.getPassword().equals("")){
                holder.lock_img.setImageResource(R.drawable.ic_lock_outline_black_24dp);
            }
            String id               = model.getId();
            holder.check(id,model);
        }

        @Override
        public int getItemCount() {
            return rooms.size();
        }

        public class RoomsVH extends RecyclerView.ViewHolder{

            ImageView room_img,lock_img;
            TextView room_name;
            Button action;
            public RoomsVH(@NonNull View itemView) {
                super(itemView);
                room_img            = itemView.findViewById(R.id.room_img);
                lock_img            = itemView.findViewById(R.id.lock);
                room_name           = itemView.findViewById(R.id.room_name);
                action              = itemView.findViewById(R.id.action);
            }
            void check (final String id, final RoomModel roomModel){
                databaseReference.child("MyRooms").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.child(getUID()).hasChild(id)){
                            action.setText("Add");
                            action.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if(!roomModel.getPassword().equals("")){
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        View passwordView = LayoutInflater.from(getContext()).inflate(R.layout.password_dialog,null);

                                        final EditText dialog_password_field = passwordView.findViewById(R.id.room_password);
                                        Button dialog_confirm_btn = passwordView.findViewById(R.id.confirm_btn);

                                        dialog_confirm_btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog_password = dialog_password_field.getText().toString();
                                                if(dialog_password.isEmpty()){
                                                    Toast.makeText(getContext(), "Enter Password", Toast.LENGTH_SHORT).show();
                                                    dialog_password_field.requestFocus();
                                                    return;
                                                }
                                                if(dialog_password.equals(roomModel.getPassword())){
                                                    databaseReference.child("MyRooms").child(getUID()).child(id).setValue(roomModel);
                                                    passwordDialog.dismiss();
                                                    Toast.makeText(getContext(), "Added", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Toast.makeText(getContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                                                    dialog_password_field.setText("");
                                                    dialog_password_field.requestFocus();
                                                    return;
                                                }
                                            }
                                        });
                                        builder.setView(passwordView);
                                        passwordDialog = builder.create();

                                        passwordDialog.show();
                                    }else{
                                        databaseReference.child("MyRooms").child(getUID()).child(id).setValue(roomModel);
                                        Toast.makeText(getContext(), "Added", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            action.setText("Remove");
                            action.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    databaseReference.child("MyRooms").child(getUID()).child(id).removeValue();

                                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private String getUID() {
        String id = auth.getCurrentUser().getUid();
        return id;
    }
}
