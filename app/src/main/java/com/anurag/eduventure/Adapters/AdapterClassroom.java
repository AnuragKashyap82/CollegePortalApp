package com.anurag.eduventure.Adapters;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.eduventure.AddLectureActivity;
import com.anurag.eduventure.AssignmentViewActivity;
import com.anurag.eduventure.ClassroomViewActivity;
import com.anurag.eduventure.CreatePostActivity;
import com.anurag.eduventure.MainUsersActivity;
import com.anurag.eduventure.Models.ModelClassroom;
import com.anurag.eduventure.Models.ModelComment;
import com.anurag.eduventure.NoticeViewActivity;
import com.anurag.eduventure.R;
import com.anurag.eduventure.databinding.RowClassBinding;
import com.anurag.eduventure.databinding.RowCommentBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.CLIPBOARD_SERVICE;

public class AdapterClassroom extends RecyclerView.Adapter<AdapterClassroom.HolderClassroom> {
    private Context context;
    private ArrayList<ModelClassroom> classroomArrayList;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private RowClassBinding binding;

    public AdapterClassroom(Context context, ArrayList<ModelClassroom> classroomArrayList) {
        this.context = context;
        this.classroomArrayList = classroomArrayList;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public AdapterClassroom.HolderClassroom onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowClassBinding.inflate(LayoutInflater.from(context), parent, false);
        return new AdapterClassroom.HolderClassroom(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterClassroom.HolderClassroom holder, int position) {
        ModelClassroom modelClassroom = classroomArrayList.get(position);
        String classCode = modelClassroom.getClassCode();
        String className = modelClassroom.getClassName();
        String subjectName = modelClassroom.getSubjectName();
        String theme = modelClassroom.getTheme();
        String uid = modelClassroom.getUid();
        String timestamp = modelClassroom.getTimestamp();

        loadClassDetails(modelClassroom, holder);
        holder.classCode.setText(classCode);

        holder.copyClassCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("classCode", holder.classCode.getText());
                if (clipboard == null || clip == null) {
                    holder.copyClassCodeBtn.setVisibility(View.VISIBLE);
                    holder.copiedClassCodeBtn.setVisibility(View.GONE);
                    return;
                }
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Class Code copied...!!!!", Toast.LENGTH_SHORT).show();
                holder.copyClassCodeBtn.setVisibility(View.GONE);
                holder.copiedClassCodeBtn.setVisibility(View.VISIBLE);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ClassroomViewActivity.class);
                intent.putExtra("classCode", classCode);
                context.startActivity(intent);
            }
        });

    }

    private void showBottomSheetDialog(ModelClassroom modelClassroom, HolderClassroom holder) {
        String classCode = modelClassroom.getClassCode();
        String uid = modelClassroom.getUid();

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bs_post_type_options);

        LinearLayout postImageLl = dialog.findViewById(R.id.postImageLl);
        LinearLayout postVideoLl = dialog.findViewById(R.id.postVideoLl);
        LinearLayout askDoubtLl = dialog.findViewById(R.id.askDoubtLl);
        LinearLayout addAchievementLl = dialog.findViewById(R.id.addAchievementLl);
        LinearLayout addDocLl = dialog.findViewById(R.id.addDocLl);
        LinearLayout unEnrollClassroomLl = dialog.findViewById(R.id.unEnrollClassroomLl);

        postImageLl.setVisibility(View.GONE);
        postVideoLl.setVisibility(View.GONE);
        askDoubtLl.setVisibility(View.GONE);
        addAchievementLl.setVisibility(View.GONE);
        addDocLl.setVisibility(View.GONE);
        unEnrollClassroomLl.setVisibility(View.VISIBLE);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


        unEnrollClassroomLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("UnEnroll")
                        .setMessage("Are you sure want to UnEnroll")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                unEnrollClassroom(modelClassroom, holder);
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
    }

    private void unEnrollClassroom(ModelClassroom modelClassroom, HolderClassroom holder) {

        DocumentReference documentReference  = firebaseFirestore.collection("Users").document(firebaseAuth.getUid()).collection("classroom").document(modelClassroom.getClassCode());
        documentReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Class UnEnroll...!", Toast.LENGTH_SHORT).show();
//                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("classroom");
//                        databaseReference.child(modelClassroom.getClassCode()).child("Students").child(firebaseAuth.getUid()).removeValue();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadClassDetails(ModelClassroom modelClassroom, HolderClassroom holder) {
        String classCode = modelClassroom.getClassCode();
        DocumentReference documentReference  = firebaseFirestore.collection("classroom").document(classCode);
        documentReference.addSnapshotListener((Activity) context, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {

                String subjectName = "" + snapshot.getString("subjectName");
                String className = "" + snapshot.getString("className");
                String classCode = "" + snapshot.getString("classCode");
                String uid = "" + snapshot.getString("uid");
                String theme = "" + snapshot.getString("theme");
                String timestamp = "" + snapshot.getString("timestamp");

                holder.classNameTv.setText(className);
                holder.subjectNameTv.setText(subjectName);
                loadClassTeacherDetails(uid, holder);

                holder.moreBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (uid.equals(firebaseAuth.getUid())){

                        }else {
                            showBottomSheetDialog(modelClassroom, holder);
                        }
                    }
                });

                if (theme.equals("1")){
                    holder.classBg.setImageResource(R.drawable.class_bg_one);
                }else if (theme.equals("2")){
                    holder.classBg.setImageResource(R.drawable.class_bg_two);
                }else if (theme.equals("3")){
                    holder.classBg.setImageResource(R.drawable.class_bd_three);
                }else if (theme.equals("4")){
                    holder.classBg.setImageResource(R.drawable.class_bg_four);
                }else if (theme.equals("5")){
                    holder.classBg.setImageResource(R.drawable.class_bg_five);
                }

            }
        });
    }

    private void loadClassTeacherDetails(String uid, HolderClassroom holder){
        DocumentReference documentReference  = firebaseFirestore.collection("Users").document(uid);
        documentReference.addSnapshotListener((Activity) context, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot ds, @Nullable FirebaseFirestoreException error) {

                String name = "" + ds.getString("name");
                String profileImage = "" + ds.getString("profileImage");

                try {
                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(holder.profileIv);
                }
                catch (Exception e){
                    holder.profileIv.setImageResource(R.drawable.ic_person_gray);
                }

                holder.classTeacherTv.setText("("+name+")");
            }
        });
    }

    @Override
    public int getItemCount() {
        return classroomArrayList.size();
    }

    class HolderClassroom extends RecyclerView.ViewHolder {

        TextView classNameTv, subjectNameTv, classCode, classTeacherTv, assNotCompView;
        ImageView copyClassCodeBtn, copiedClassCodeBtn, moreBtn, classBg, profileIv;

        public HolderClassroom(@NonNull View itemView) {
            super(itemView);

            classNameTv = binding.classNameTv;
            subjectNameTv = binding.subjectNameTv;
            classCode = binding.classCode;
            classTeacherTv = binding.classTeacherTv;
            copyClassCodeBtn = binding.copyClassCodeBtn;
            copiedClassCodeBtn = binding.copiedClassCodeBtn;
            moreBtn = binding.moreBtn;
            classBg = binding.classBg;
            profileIv = binding.profileIv;

        }
    }
}