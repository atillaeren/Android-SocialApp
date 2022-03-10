package com.atillaeren.socialapp.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atillaeren.socialapp.AddParticipantActivity;
import com.atillaeren.socialapp.DashboardActivity;
import com.atillaeren.socialapp.GroupInfoActivity;
import com.atillaeren.socialapp.R;
import com.atillaeren.socialapp.ThereProfileActivity;
import com.atillaeren.socialapp.models.ModelUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import static androidx.core.content.ContextCompat.startActivity;

public class AdapterAddParticipant extends RecyclerView.Adapter<AdapterAddParticipant.HolderAddParticipant> {

    private Context context;
    private ArrayList<ModelUser> userArrayList;
    private String groupId, myGroupRole;

    public AdapterAddParticipant(Context context, ArrayList<ModelUser> userArrayList, String groupId, String myGroupRole) {
        this.context = context;
        this.userArrayList = userArrayList;
        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
    }

    @NonNull
    @NotNull
    @Override
    public HolderAddParticipant onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_add_participant, parent, false);
        return new HolderAddParticipant(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterAddParticipant.HolderAddParticipant holder, int position) {

        //get data
        ModelUser modelUser = userArrayList.get(position);
        String name = modelUser.getName();
        String email = modelUser.getEmail();
        String image = modelUser.getImage();
        String uid = modelUser.getUid();



        //set data
        holder.nameTv.setText(name);
        holder.emailTv.setText(email);
        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_default_img).into(holder.avatarIv);
        }
        catch (Exception e) {
            holder.avatarIv.setImageResource(R.drawable.ic_default_img);
        }

        checkExistOrNot(modelUser, holder);

        //handle click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* check user added or not
                * if added = show delete user / change role option (admin can't change creator role
                * if not = show add participant option*/
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                reference.child(groupId).child("Participants").child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    //user exists / participant
                                    String hisPreRole = ""+snapshot.child("role").getValue();

                                    //options dialog
                                    String[] options;

                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Choose Option");
                                    if (myGroupRole.equals("creator")) {
                                        if (hisPreRole.equals("admin")) {
                                            //im creator, he/she is admin
                                            options = new String[] {"Remove Admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //item clicks
                                                    if (which==0) {
                                                        //remove admin
                                                        removeAdmin(modelUser);
                                                        notifyDataSetChanged();
                                                    }
                                                    else {
                                                        //remove participant
                                                        removeParticipant(modelUser);
                                                        notifyDataSetChanged();

                                                        Intent intent = new Intent(context, GroupInfoActivity.class);
                                                        intent.putExtra("groupId", groupId);
                                                        context.startActivity(intent);
                                                    }
                                                }
                                            }).show();
                                        }
                                        else if (hisPreRole.equals("participant")) {
                                            //im creator, he/she is participant
                                            options = new String[]{"Make Admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //item clicks
                                                    if (which==0) {
                                                        //make admin
                                                        makeAdmin(modelUser);
                                                        notifyDataSetChanged();
                                                    }
                                                    else {
                                                        //remove participant
                                                        removeParticipant(modelUser);

                                                        notifyDataSetChanged();
                                                        notifyItemRangeChanged(0, userArrayList.size());
                                                        Intent intent = new Intent(context, GroupInfoActivity.class);
                                                        intent.putExtra("groupId", groupId);
                                                        context.startActivity(intent);
                                                    }

                                                }
                                            }).show();
                                        }
                                    }
                                    else if (myGroupRole.equals("admin")) {
                                        if (hisPreRole.equals("creator")) {
                                            //im admin, he/she is creator
                                            Toast.makeText(context, "Creator of Group...", Toast.LENGTH_SHORT).show();
                                        }
                                        else if (hisPreRole.equals("admin")) {
                                            //im admin, he / she admin
                                            options = new String[] {"Remove Admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //item clicks
                                                    if (which==0) {
                                                        //remove admin
                                                        removeAdmin(modelUser);
                                                        notifyDataSetChanged();
                                                    }
                                                    else {
                                                        //remove participant
                                                        removeParticipant(modelUser);

                                                        notifyDataSetChanged();
                                                        Intent intent = new Intent(context, GroupInfoActivity.class);
                                                        intent.putExtra("groupId", groupId);
                                                        context.startActivity(intent);
                                                    }
                                                }
                                            }).show();
                                        }
                                        else if (hisPreRole.equals("participant")) {
                                            //im admin, he/she is participant
                                            options = new String[]{"Make Admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //item clicks
                                                    if (which==0) {
                                                        //make admin
                                                        makeAdmin(modelUser);
                                                        notifyDataSetChanged();
                                                    }
                                                    else {
                                                        //remove participant
                                                        removeParticipant(modelUser);

                                                        notifyDataSetChanged();
                                                        Intent intent = new Intent(context, GroupInfoActivity.class);
                                                        intent.putExtra("groupId", groupId);
                                                        context.startActivity(intent);
                                                    }
                                                }
                                            }).show();
                                        }
                                    }
                                }
                                else {
                                    //user does not exists / not participant then add to group
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Add Participant")
                                            .setMessage("Do you want to add this user to group?")
                                            .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //add user
                                                    addParticipant(modelUser);

                                                    notifyDataSetChanged();
                                                }
                                            })
                                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                }
                            }


                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
            }
        });
    }


    private void addParticipant(ModelUser modelUser) {
        //get user data / add participant
        String timestamp = ""+System.currentTimeMillis();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", modelUser.getUid());
        hashMap.put("role", "participant");
        hashMap.put("timestamp", ""+timestamp);

        //add user to groups>groupId>participants node
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //success
                        Toast.makeText(context, "The user added to the group successfully...", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        //failed
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeParticipant(ModelUser modelUser) {
        //remove participant from group
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getUid()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //success
                        Toast.makeText(context, "The user deleted from the group successfully...", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        //failed
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void makeAdmin(ModelUser modelUser) {
        //setup user data /make role admin
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "admin");

        //update role
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //success
                        Toast.makeText(context, "The user's role is now Admin...", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        //failed
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeAdmin(ModelUser modelUser) {
        //setup user data / remove admin role
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "participant");

        //update role
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //success
                        Toast.makeText(context, "The user's role is now Participant...", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        //failed
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkExistOrNot(ModelUser modelUser, HolderAddParticipant holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            //exists
                            String hisRole = ""+snapshot.child("role").getValue();
                            holder.roleTv.setText(hisRole);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        //not exists
                        holder.roleTv.setText("");
                    }
                });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    class HolderAddParticipant extends RecyclerView.ViewHolder {

        private ImageView avatarIv;
        private TextView nameTv, emailTv, roleTv;

        public HolderAddParticipant(@NonNull @NotNull View itemView) {
            super(itemView);

            avatarIv = itemView.findViewById(R.id.avatarIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            emailTv = itemView.findViewById(R.id.emailTv);
            roleTv = itemView.findViewById(R.id.roleTv);
        }
    }

}
