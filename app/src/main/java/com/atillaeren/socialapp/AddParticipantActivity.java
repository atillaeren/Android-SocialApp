package com.atillaeren.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.atillaeren.socialapp.adapters.AdapterAddParticipant;
import com.atillaeren.socialapp.models.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AddParticipantActivity extends AppCompatActivity {

    //init views
    private FirebaseAuth firebaseAuth;

    private RecyclerView usersRv;
    private ActionBar actionBar;
    private String groupId, myGroupRole;

    private ArrayList<ModelUser> userList;
    private AdapterAddParticipant adapterAddParticipant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_participant);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Add Participants");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();

        usersRv = findViewById(R.id.usersRv);
        groupId = getIntent().getStringExtra("groupId");
        loadGroupInfo();


    }

    private void getAllUsers() {
        //create list
        userList = new ArrayList<>();
        //get users from db
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    //get all users except signed in user
                    if (!firebaseAuth.getUid().equals(modelUser.getUid())) {
                        userList.add(modelUser);
                    }
                }
                //set adapter
                adapterAddParticipant = new AdapterAddParticipant(AddParticipantActivity.this, userList, ""+groupId, ""+myGroupRole);
                //add information to rw
                usersRv.setAdapter(adapterAddParticipant);
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }

    private void loadGroupInfo() {
        final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String groupId = "" + ds.child("groupId").getValue();
                    final String groupTitle = "" + ds.child("groupTitle").getValue();
                    String groupDesc = "" + ds.child("groupDesc").getValue();
                    String groupIcon = "" + ds.child("groupIcon").getValue();
                    String createdBy = "" + ds.child("createdBy").getValue();
                    String timestamp = "" + ds.child("timestamp").getValue();
                    actionBar.setTitle("Add Participants");

                    ref1.child(groupId).child("Participants").child(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        myGroupRole = ""+snapshot.child("role").getValue();
                                        actionBar.setTitle(groupTitle + "("+myGroupRole+")");

                                        getAllUsers();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}