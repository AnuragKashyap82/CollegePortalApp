package com.anurag.eduventure.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.anurag.eduventure.AssignmentViewActivity;
import com.anurag.eduventure.Models.ModelAssignment;
import com.anurag.eduventure.R;
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

public class AdapterAssignment extends RecyclerView.Adapter<AdapterAssignment.HolderAssignment> {

    private Context context;
    public ArrayList<ModelAssignment> assignmentList, filterList;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private boolean isCompletedAssignment = false;

    public AdapterAssignment(Context context, ArrayList<ModelAssignment> assignmentList) {
        this.context = context;
        this.assignmentList = assignmentList;
        this.filterList = assignmentList;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public HolderAssignment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_assignment, parent, false);
        return new HolderAssignment(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderAssignment holder, int position) {

        final ModelAssignment modelAssignment = assignmentList.get(position);
        String assignmentName = modelAssignment.getAssignmentName();
        String classCode = modelAssignment.getClassCode();
        String date = modelAssignment.getTimestamp();
        String assignmentUrl = modelAssignment.getUrl();
        String assignmentId = modelAssignment.getAssignmentId();
        String dueDate = modelAssignment.getDueDate();
        String fullMarks = modelAssignment.getFullMarks();
        String uid = modelAssignment.getUid();

        firebaseAuth = FirebaseAuth.getInstance();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(date));
        String dateFormat = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.dateTv.setText(dateFormat);
        holder.dueDateTv.setText(dueDate);
        holder.assignmentName.setText(assignmentName);

        checkSubmittedAss(modelAssignment, holder);
        checkTeacherDetails(modelAssignment, holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(context, AssignmentViewActivity.class);
                intent.putExtra("assignmentUrl", assignmentUrl);
                intent.putExtra("assignmentName", assignmentName);
                intent.putExtra("classCode", classCode);
                intent.putExtra("assignmentId", assignmentId);
                intent.putExtra("dueDate", dueDate);
                intent.putExtra("fullMarks", fullMarks);
                context.startActivity(intent);
            }
        });

    }

    private void checkTeacherDetails(ModelAssignment modelAssignment, HolderAssignment holder) {
        String uid = modelAssignment.getUid();

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(uid);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        String assignedBy = ""+value.getString("name");

                        holder.assignedByTv.setText(assignedBy);
            }
        });
    }

    private void checkSubmittedAss(ModelAssignment modelAssignment, HolderAssignment holder) {
        String classCode = modelAssignment.getClassCode();
        String assignmentId = modelAssignment.getAssignmentId();

        DocumentReference documentReference = firebaseFirestore.collection("classroom").document(classCode).collection("assignment").document(assignmentId).collection("Submission").document(firebaseAuth.getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                isCompletedAssignment = value.exists();
                if (isCompletedAssignment){
                    holder.profileIv.setVisibility(View.GONE);
                    holder.submittedIv.setVisibility(View.VISIBLE);
                }
                else{
                    holder.profileIv.setVisibility(View.VISIBLE);
                    holder.submittedIv.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }


    class HolderAssignment extends RecyclerView.ViewHolder {

        private TextView assignmentName, dueDateTv, dateTv, assignedByTv;
        private ImageView profileIv, submittedIv;

        public HolderAssignment(@NonNull View itemView) {
            super(itemView);

            assignmentName = itemView.findViewById(R.id.assignmentName);
            assignedByTv = itemView.findViewById(R.id.assignedByTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            dueDateTv = itemView.findViewById(R.id.dueDateTv);
            profileIv = itemView.findViewById(R.id.profileIv);
            submittedIv = itemView.findViewById(R.id.submittedIv);

        }
    }
}