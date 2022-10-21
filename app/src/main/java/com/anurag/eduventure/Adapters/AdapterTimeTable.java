package com.anurag.eduventure.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.anurag.eduventure.EditTimeTableActivity;
import com.anurag.eduventure.Models.ModelTimeTable;
import com.anurag.eduventure.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterTimeTable extends RecyclerView.Adapter<AdapterTimeTable.HolderTimeTable> {
    private Context context;
    public ArrayList<ModelTimeTable> timeTableArrayList;
    private FirebaseAuth firebaseAuth;

    public AdapterTimeTable(Context context, ArrayList<ModelTimeTable> timeTableArrayList) {
        this.context = context;
        this.timeTableArrayList = timeTableArrayList;
    }

    @NonNull
    @Override
    public AdapterTimeTable.HolderTimeTable onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_timetable, parent, false);
        return new AdapterTimeTable.HolderTimeTable(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTimeTable.HolderTimeTable holder, int position) {

        final ModelTimeTable modelTimeTable = timeTableArrayList.get(position);
        String subName = modelTimeTable.getSubName();
        String teacherName = modelTimeTable.getTeacherName();
        String startTime = modelTimeTable.getStartTime();
        String endTime = modelTimeTable.getEndTime();
        String day = modelTimeTable.getDay();
        String ongoingTopic = modelTimeTable.getOngoingTopic();
        String percentSylComp = modelTimeTable.getPercentSylComp();

        holder.subNameTv.setText(subName);
        holder.teacherNameTv.setText(teacherName);
        holder.startTimeTv.setText(startTime+"\n" + "Hr");
        holder.endTimeTv.setText(endTime+"\n" + "Hr");
        holder.ongoingTopicTv.setText(ongoingTopic);
        holder.syllabusCompletedTv.setText(percentSylComp);

        firebaseAuth = FirebaseAuth.getInstance();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog(modelTimeTable, holder);
            }
        });

    }

    private void showBottomSheetDialog(ModelTimeTable modelTimeTable, HolderTimeTable holder) {
        String day = modelTimeTable.getDay();
        String subName = modelTimeTable.getSubName();

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bs_more_feed_options);

        LinearLayout editPostLL = dialog.findViewById(R.id.editPostLL);
        LinearLayout deletePostLL = dialog.findViewById(R.id.deletePostLL);
        LinearLayout sharePostLL = dialog.findViewById(R.id.sharePostLL);
        LinearLayout favPostLL = dialog.findViewById(R.id.favPostLL);
        LinearLayout blockPostLL = dialog.findViewById(R.id.blockPostLL);

        editPostLL.setVisibility(View.VISIBLE);
        deletePostLL.setVisibility(View.VISIBLE);
        sharePostLL.setVisibility(View.GONE);
        favPostLL.setVisibility(View.GONE);
        blockPostLL.setVisibility(View.GONE);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        editPostLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(context, EditTimeTableActivity.class);
                intent.putExtra("day", day);
                intent.putExtra("subName", subName);
                context.startActivity(intent);
            }
        });
        deletePostLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure want to Delete this class")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("timeTable");
                                reference.child(firebaseAuth.getUid()).child(day).child(subName)
                                        .removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(context, "Class Deleted...!", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public int getItemCount() {
        return timeTableArrayList.size();
    }

    class HolderTimeTable extends RecyclerView.ViewHolder {

        private TextView subNameTv, teacherNameTv, startTimeTv, endTimeTv, ongoingTopicTv, syllabusCompletedTv;

        public HolderTimeTable(@NonNull View itemView) {
            super(itemView);

            subNameTv = itemView.findViewById(R.id.subNameTv);
            teacherNameTv = itemView.findViewById(R.id.teacherNameTv);
            startTimeTv = itemView.findViewById(R.id.startTimeTv);
            endTimeTv = itemView.findViewById(R.id.endTimeTv);
            ongoingTopicTv = itemView.findViewById(R.id.ongoingTopicTv);
            syllabusCompletedTv = itemView.findViewById(R.id.syllabusCompletedTv);

        }
    }
}
