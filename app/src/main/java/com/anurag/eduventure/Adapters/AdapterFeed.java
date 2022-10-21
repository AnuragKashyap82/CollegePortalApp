package com.anurag.eduventure.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import com.anurag.eduventure.AssignmentViewActivity;
import com.anurag.eduventure.Constants;
import com.anurag.eduventure.EditPostActivity;
import com.anurag.eduventure.MainUsersActivity;
import com.anurag.eduventure.Models.ModelFeed;
import com.anurag.eduventure.R;

import com.anurag.eduventure.VerifyUniqueIdActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.HolderFeed> {

    private Context context;
    public ArrayList<ModelFeed> feedArrayList;
    boolean isLiked = false;

    public AdapterFeed(Context context, ArrayList<ModelFeed> feedArrayList) {
        this.context = context;
        this.feedArrayList = feedArrayList;

    }

    @NonNull
    @Override
    public AdapterFeed.HolderFeed onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_feeds, parent, false);
        return new AdapterFeed.HolderFeed(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFeed.HolderFeed holder, int position) {

        final ModelFeed modelFeed = feedArrayList.get(position);
        String postId = modelFeed.getPostId();
        String uid = modelFeed.getUid();
        String postText = modelFeed.getPostText();

        loadUserDetails(modelFeed, holder);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(postId));
        String dateFormat = DateFormat.format("dd/MM/yyyy  K:mm a", calendar).toString();

        holder.dateTv.setText(dateFormat);
        holder.expressionTv.setText(postText);

        checkImageAvailable(modelFeed, holder);

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet(modelFeed, holder);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent =new Intent(context, AssignmentViewActivity.class);
//                intent.putExtra("assignmentUrl", assignmentUrl);
//                intent.putExtra("assignmentId", assignmentId);
//                intent.putExtra("branch", branch);
//                intent.putExtra("semester", semester);
//                intent.putExtra("dueDate", dueDate);
//                intent.putExtra("session", session);
//                intent.putExtra("fullMarks", fullMarks);
//                context.startActivity(intent);
            }
        });

    }


    private void checkImageAvailable(ModelFeed modelFeed, HolderFeed holder) {
        String postId = modelFeed.getPostId();
        String postImage = modelFeed.getPostImage();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Feeds");
        reference.child(postId).child("postImage")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            holder.postIv.setVisibility(View.VISIBLE);
                            try {
                                Picasso.get().load(postImage).placeholder(R.drawable.ic_person_gray).into(holder.postIv);
                            } catch (Exception e) {
                                holder.postIv.setImageResource(R.drawable.ic_person_gray);
                            }
                        } else {
                            holder.postIv.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadUserDetails(ModelFeed modelFeed, AdapterFeed.HolderFeed holder) {
        String uid = modelFeed.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = "" + snapshot.child("name").getValue();
                        String profileImage = "" + snapshot.child("profileImage").getValue();

                        holder.nameTv.setText(name);
                        try {
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(holder.profileIv);
                        } catch (Exception e) {
                            holder.profileIv.setImageResource(R.drawable.ic_person_gray);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showBottomSheet(ModelFeed modelFeed, HolderFeed holder) {
        String postId = modelFeed.getPostId();

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bs_more_feed_options);

        LinearLayout editPostLL = dialog.findViewById(R.id.editPostLL);
        LinearLayout deletePostLL = dialog.findViewById(R.id.deletePostLL);
        LinearLayout sharePostLL = dialog.findViewById(R.id.sharePostLL);
        LinearLayout favPostTv = dialog.findViewById(R.id.favPostLL);
        LinearLayout blockPostLL = dialog.findViewById(R.id.blockPostLL);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        checkUsers(modelFeed, holder, editPostLL, deletePostLL);

        editPostLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(context, EditPostActivity.class);
                intent.putExtra("postId", postId);
                context.startActivity(intent);
            }
        });

        deletePostLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure want to Delete This Post" + " ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deletePost(postId);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
                dialog.dismiss();
            }
        });
        sharePostLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
        favPostTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
        blockPostLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
    }

    private void deletePost(String postId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Feeds");
        reference.child(postId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Post Deleted...!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    String uid;

    private void checkUsers(ModelFeed modelFeed, HolderFeed holder, LinearLayout editPostLL, LinearLayout deletePostLL) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        }
        String userUid = modelFeed.getUid();

        if (uid.equals(userUid)) {
            editPostLL.setVisibility(View.VISIBLE);
            deletePostLL.setVisibility(View.VISIBLE);
        } else {
            editPostLL.setVisibility(View.GONE);
            deletePostLL.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return feedArrayList.size();
    }

    class HolderFeed extends RecyclerView.ViewHolder {

        private TextView nameTv, dateTv, expressionTv;
        private ImageView profileIv, postIv, moreBtn;

        public HolderFeed(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.nameTv);
            expressionTv = itemView.findViewById(R.id.expressionTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            profileIv = itemView.findViewById(R.id.profileIv);
            postIv = itemView.findViewById(R.id.postIv);
            moreBtn = itemView.findViewById(R.id.moreBtn);

        }
    }
}
