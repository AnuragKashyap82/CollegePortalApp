package com.anurag.eduventure.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.anurag.eduventure.EditPostActivity;
import com.anurag.eduventure.Models.ModelFeed;
import com.anurag.eduventure.Models.ModelLecture;
import com.anurag.eduventure.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterLecture extends RecyclerView.Adapter<AdapterLecture.HolderLecture> {

    private Context context;
    public ArrayList<ModelLecture> lectureArrayList;

    public AdapterLecture(Context context, ArrayList<ModelLecture> lectureArrayList) {
        this.context = context;
        this.lectureArrayList = lectureArrayList;

    }

    @NonNull
    @Override
    public AdapterLecture.HolderLecture onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_lectures, parent, false);
        return new AdapterLecture.HolderLecture(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLecture.HolderLecture holder, int position) {

        final ModelLecture modelLecture = lectureArrayList.get(position);
        String lectureId = modelLecture.getLectureId();
        String uid = modelLecture.getUid();
        String lectureText = modelLecture.getLectureText();
        String lectureVideo = modelLecture.getLectureVideo();

        loadUserDetails(modelLecture, holder);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(lectureId));
        String dateFormat = DateFormat.format("dd/MM/yyyy K:mm a", calendar).toString();

        holder.dateTv.setText(dateFormat);
        holder.lectureTextTv.setText(lectureText);

        setLectureVideoUrl(modelLecture, holder);

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet(modelLecture, holder);
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

    private void setLectureVideoUrl(ModelLecture modelLecture, HolderLecture holder) {
        holder.progressBar.setVisibility(View.VISIBLE);

        String lectureVideoUrl = modelLecture.getLectureVideo();
        MediaController mediaController = new MediaController(context);
        mediaController.setAnchorView(holder.lectureVideoView);

        Uri lectureUri = Uri.parse(lectureVideoUrl);
        holder.lectureVideoView.setMediaController(mediaController);
        holder.lectureVideoView.setVideoURI(lectureUri);

        holder.lectureVideoView.requestFocus();
        holder.lectureVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        holder.lectureVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer ap, int what, int extra) {

                switch (what){
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:{
                        holder.progressBar.setVisibility(View.GONE);

                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:{
                        holder.progressBar.setVisibility(View.GONE);
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:{
                        holder.progressBar.setVisibility(View.GONE);
                        return true;
                    }
                }
                return false;
            }
        });
        holder.lectureVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.pause();
            }
        });
    }

    private void loadUserDetails(ModelLecture modelFeed, HolderLecture holder) {
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

    private void showBottomSheet(ModelLecture modelLecture, HolderLecture holder) {
        String postId = modelLecture.getLectureId();

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

        checkUsers(modelLecture, holder, editPostLL, deletePostLL);

        editPostLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                dialog.dismiss();
//                Intent intent = new Intent(context, EditPostActivity.class);
//                intent.putExtra("postId", postId);
//                context.startActivity(intent);
            }
        });

        deletePostLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure want to Delete This Lecture" + " ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteLecture(postId);
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

    private void deleteLecture(String postId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("lectures");
        reference.child(postId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Lecture Deleted...!", Toast.LENGTH_SHORT).show();
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

    private void checkUsers(ModelLecture modelFeed, HolderLecture holder, LinearLayout editPostLL, LinearLayout deletePostLL) {
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
        return lectureArrayList.size();
    }

    class HolderLecture extends RecyclerView.ViewHolder {

        private TextView nameTv, dateTv, lectureTextTv;
        private ImageView profileIv, postIv, moreBtn;
        private VideoView lectureVideoView;
        private ProgressBar progressBar;

        public HolderLecture(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.nameTv);
            lectureTextTv = itemView.findViewById(R.id.lectureTextTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            profileIv = itemView.findViewById(R.id.profileIv);
            postIv = itemView.findViewById(R.id.postIv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            lectureVideoView = itemView.findViewById(R.id.lectureVideoView);
            progressBar = itemView.findViewById(R.id.progressBar);

        }
    }
}