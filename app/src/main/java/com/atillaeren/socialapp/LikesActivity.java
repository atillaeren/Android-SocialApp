package com.atillaeren.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.atillaeren.socialapp.adapters.AdapterUsers;
import com.atillaeren.socialapp.models.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LikesActivity extends AppCompatActivity {

    String pId;

    private RecyclerView recyclerView;

    private List<ModelUser> userList;
    private AdapterUsers adapterUsers;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);

        //Action bar
        ActionBar actionBar= getSupportActionBar();
        actionBar.setTitle("Likes");

        //back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);

        firebaseAuth = FirebaseAuth.getInstance();

        actionBar.setSubtitle(firebaseAuth.getCurrentUser().getEmail());

        //get the post id
        Intent intent = getIntent();
        pId = intent.getStringExtra("postId");

        userList = new ArrayList<>();

        //get each users uid who liked the post
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes");
        reference.child(pId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String hisUid = "" + ds.getRef().getKey();
                    //get user info
                    getUsers(hisUid);
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }

    private void getUsers(String hisUid) {
        //get all users information using uid then add to rw
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(hisUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            ModelUser mUser = ds.getValue(ModelUser.class);
                            userList.add(mUser);
                        }
                        //setup adapter
                        adapterUsers = new AdapterUsers(LikesActivity.this, userList);
                        // set adapter to recycler
                        recyclerView.setAdapter(adapterUsers);
                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //back to previous activity
        return super.onSupportNavigateUp();
    }
}