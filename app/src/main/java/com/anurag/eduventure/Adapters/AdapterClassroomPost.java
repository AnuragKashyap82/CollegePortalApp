package com.anurag.eduventure.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateFormat;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.eduventure.ClassroomMsgViewActivity;
import com.anurag.eduventure.ClassroomViewActivity;
import com.anurag.eduventure.JoinClassActivity;
import com.anurag.eduventure.Models.ModelClassroom;
import com.anurag.eduventure.Models.ModelClassroomPost;

import com.anurag.eduventure.Models.ModelComment;
import com.anurag.eduventure.NoticeViewActivity;
import com.anurag.eduventure.PostClassroomMsgActivity;
import com.anurag.eduventure.PostClassroomMsgEditActivity;
import com.anurag.eduventure.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterClassroomPost extends RecyclerView.Adapter<AdapterClassroomPost.HolderClassroomPost> {
    private Context context;
    public ArrayList<ModelClassroomPost> classroomPostArrayList;
    private FirebaseFirestore firebaseFirestore;

    public AdapterClassroomPost(Context context, ArrayList<ModelClassroomPost> classroomPostArrayList) {
        this.context = context;
        this.classroomPostArrayList = classroomPostArrayList;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public AdapterClassroomPost.HolderClassroomPost onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_classpost, parent, false);
        return new AdapterClassroomPost.HolderClassroomPost(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterClassroomPost.HolderClassroomPost holder, int position) {

        final ModelClassroomPost modelClassroomPost = classroomPostArrayList.get(position);
        String classCode = modelClassroomPost.getClassCode();
        String classMsg = modelClassroomPost.getClassMsg();
        String timestamp = modelClassroomPost.getTimestamp();
        String url = modelClassroomPost.getUrl();
        String uid = modelClassroomPost.getUid();
        Boolean isAttachmentExist = modelClassroomPost.isAttachmentExist();


        loadMsgSenderDetails(modelClassroomPost, holder);
        checkAttachment(modelClassroomPost, holder);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateFormat = DateFormat.format("dd/MM/yyyy  K:mm a", calendar).toString();

        holder.dateTv.setText(dateFormat);
        holder.postTextTv.setText(classMsg);

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUser(modelClassroomPost, holder);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ClassroomMsgViewActivity.class);
                intent.putExtra("classCode", classCode);
                intent.putExtra("classMsg", classMsg);
                intent.putExtra("timestamp", timestamp);
                intent.putExtra("url", url);
                intent.putExtra("uid", uid);
                intent.putExtra("isAttachmentExist", isAttachmentExist);
                intent.putExtra("msgCode", timestamp);
                context.startActivity(intent);
            }
        });
    }

    private void checkUser(ModelClassroomPost modelClassroomPost, HolderClassroomPost holder) {
        String uid = modelClassroomPost.getUid();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (uid.equals(firebaseUser.getUid())) {
            showBottomSheetDialog(modelClassroomPost, holder);
        } else {

        }
    }

    private void showBottomSheetDialog(ModelClassroomPost modelClassroomPost, AdapterClassroomPost.HolderClassroomPost holder) {
        String classCode = modelClassroomPost.getClassCode();
        String msgCode = modelClassroomPost.getTimestamp();

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bs_post_type_options);

        LinearLayout postImageLl = dialog.findViewById(R.id.postImageLl);
        LinearLayout postVideoLl = dialog.findViewById(R.id.postVideoLl);
        LinearLayout askDoubtLl = dialog.findViewById(R.id.askDoubtLl);
        LinearLayout addAchievementLl = dialog.findViewById(R.id.addAchievementLl);
        LinearLayout addDocLl = dialog.findViewById(R.id.addDocLl);
        LinearLayout unEnrollClassroomLl = dialog.findViewById(R.id.unEnrollClassroomLl);
        LinearLayout deleteClassroomPostLl = dialog.findViewById(R.id.deleteClassroomPostLl);
        LinearLayout editClassroomPostLl = dialog.findViewById(R.id.editClassroomPostLl);

        postImageLl.setVisibility(View.GONE);
        postVideoLl.setVisibility(View.GONE);
        askDoubtLl.setVisibility(View.GONE);
        addAchievementLl.setVisibility(View.GONE);
        addDocLl.setVisibility(View.GONE);
        unEnrollClassroomLl.setVisibility(View.GONE);
        deleteClassroomPostLl.setVisibility(View.VISIBLE);
        editClassroomPostLl.setVisibility(View.VISIBLE);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


        deleteClassroomPostLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure want to Delete Post")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                DocumentReference documentReference = firebaseFirestore.collection("classroom").document(classCode).collection("classMsg").document(msgCode);
                                documentReference
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(context, "Msg Deleted...!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });
        editClassroomPostLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(context, PostClassroomMsgEditActivity.class);
                intent.putExtra("classCode", classCode);
                intent.putExtra("msgCode", msgCode);
                context.startActivity(intent);
            }
        });
    }

    private void checkAttachment(ModelClassroomPost modelClassroomPost, AdapterClassroomPost.HolderClassroomPost holder) {
        String classCode = modelClassroomPost.getClassCode();
        String msgCode = modelClassroomPost.getTimestamp();

        DocumentReference documentReference = firebaseFirestore.collection("classroom").document(classCode).collection("classMsg").document(msgCode);
        documentReference.addSnapshotListener((Activity) context, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot ds, @Nullable FirebaseFirestoreException error) {

                if (modelClassroomPost.isAttachmentExist()) {
                    holder.attachmentTv.setVisibility(View.VISIBLE);
                } else {
                    holder.attachmentTv.setVisibility(View.GONE);
                }

            }
        });
    }

    private void loadMsgSenderDetails(ModelClassroomPost modelClassroomPost, AdapterClassroomPost.HolderClassroomPost holder) {
        String uid = modelClassroomPost.getUid();

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(uid);
        documentReference.addSnapshotListener((Activity) context, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot ds, @Nullable FirebaseFirestoreException error) {

                String name = "" + ds.getString("name");
                String profileImage = "" + ds.getString("profileImage");


                holder.nameTv.setText(name);
                try {
                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(holder.profileIv);
                } catch (Exception e) {
                    holder.profileIv.setImageResource(R.drawable.ic_person_gray);
                }

            }
        });

    }


    @Override
    public int getItemCount() {
        return classroomPostArrayList.size();
    }

    class HolderClassroomPost extends RecyclerView.ViewHolder {

        TextView nameTv, dateTv, postTextTv, attachmentTv;
        ImageView moreBtn, profileIv;

        public HolderClassroomPost(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.nameTv);
            postTextTv = itemView.findViewById(R.id.postTextTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            profileIv = itemView.findViewById(R.id.profileIv);
            attachmentTv = itemView.findViewById(R.id.attachmentTv);
        }
    }
}