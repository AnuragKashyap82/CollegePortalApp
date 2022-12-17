package com.anurag.eduventure.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.eduventure.Models.ModelComment;
import com.anurag.eduventure.R;
import com.anurag.eduventure.databinding.RowCommentBinding;
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

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.HolderComment> {

    private Context context;
    private ArrayList<ModelComment> commentArrayList;
    private FirebaseAuth firebaseAuth;
    private RowCommentBinding binding;

    public AdapterComment(Context context, ArrayList<ModelComment> commentArrayList) {
        this.context = context;
        this.commentArrayList = commentArrayList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowCommentBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderComment(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderComment holder, int position) {
        ModelComment modelComment = commentArrayList.get(position);
        String commentId = modelComment.getCommentId();
        String materialId = modelComment.getMaterialId();
        String comment = modelComment.getComment();
        String uid = modelComment.getUid();
        String timestamp = modelComment.getTimestamp();

        loadUserDetails(modelComment, holder);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateFormat = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.dateTv.setText(dateFormat);
        holder.commentTv.setText(comment);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseAuth.getCurrentUser() != null) {
//                    deleteComment(modelComment, holder);
                    Toast.makeText(context, "Comments on This Book....!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loadUserDetails(ModelComment modelComment, HolderComment holder) {
        String uid = modelComment.getUid();

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(modelComment.getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                String name = snapshot.getString("name");
                String profileImage = snapshot.getString("profileImage");


            }
        });
    }

//    private void deleteComment(ModelComment modelComment, HolderComment holder) {
//
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Delete Comment")
//                .setMessage("Are you sure want to delete this comment")
//                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
//                        ref.child(modelComment.getBookId())
//                                .child("Comments")
//                                .child(modelComment.getCommentId())
//                                .removeValue()
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        Toast.makeText(context, "Deleted....", Toast.LENGTH_SHORT).show();
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Toast.makeText(context, "Failed to delete due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                    }
//                })
//                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                })
//                .show();
//    }

        @Override
        public int getItemCount () {
            return commentArrayList.size();
        }

        class HolderComment extends RecyclerView.ViewHolder {

            ShapeableImageView profileTv;
            TextView nameTv, dateTv, commentTv;

            public HolderComment(@NonNull View itemView) {
                super(itemView);

                profileTv = binding.profileTv;
                nameTv = binding.nameTv;
                dateTv = binding.dateTv;
                commentTv = binding.commentTv;
            }
        }

    }