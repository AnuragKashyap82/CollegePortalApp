package com.anurag.eduventure.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.anurag.eduventure.Models.ModelSubmittedAss;
import com.anurag.eduventure.R;
import com.anurag.eduventure.SubmittedAssViewActivity;
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

public class AdapterSubmittedAss extends RecyclerView.Adapter<AdapterSubmittedAss.HolderSubmittedAss> {

    private Context context;
    public ArrayList<ModelSubmittedAss> assignmentList;
    private FirebaseFirestore firebaseFirestore;

    public AdapterSubmittedAss(Context context, ArrayList<ModelSubmittedAss> assignmentList) {
        this.context = context;
        this.assignmentList = assignmentList;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public HolderSubmittedAss onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_submitted_ass, parent, false);
        return new HolderSubmittedAss(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderSubmittedAss holder, int position) {

        final ModelSubmittedAss modelAssignment = assignmentList.get(position);
        String date = modelAssignment.getTimestamp();
        String classCode = modelAssignment.getClassCode();
        String submittedAssUrl = modelAssignment.getUrl();
        String assignmentId = modelAssignment.getAssignmentId();
        String assignmentName = modelAssignment.getAssignmentName();
        String dueDate = modelAssignment.getDueDate();
        String uid = modelAssignment.getUid();
        String fullMarks = modelAssignment.getFullMarks();
        String marksObtained = modelAssignment.getMarksObtained();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(date));
        String dateFormat = DateFormat.format("dd/MM/yyyy", calendar).toString();

        loadStudentsDetails(modelAssignment, holder);

        holder.dateTv.setText(dateFormat);
        holder.obtainedMarksTv.setText(marksObtained);
        holder.fullMarksTv.setText(fullMarks);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(context, SubmittedAssViewActivity.class);
                intent.putExtra("submittedAssUrl", submittedAssUrl);
                intent.putExtra("assignmentId", assignmentId);
                intent.putExtra("dueDate", dueDate);
                intent.putExtra("assignmentName", assignmentName);
                intent.putExtra("classCode", classCode);
                intent.putExtra("uid", uid);
                context.startActivity(intent);

            }
        });

    }

    private void loadStudentsDetails(ModelSubmittedAss modelAssignment, HolderSubmittedAss holder) {
        String uid = modelAssignment.getUid();

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(uid);
        documentReference.addSnapshotListener((Activity) context, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot ds, @Nullable FirebaseFirestoreException error) {
                String name = ""+ds.getString("name");
                String profileImage = ""+ds.getString("profileImage");

                holder.nameTv.setText(name);
                try {
                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(holder.profileTv);
                }
                catch (Exception e){
                    holder.profileTv.setImageResource(R.drawable.ic_person_gray);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }


    class HolderSubmittedAss extends RecyclerView.ViewHolder {

        private TextView nameTv, dateTv, obtainedMarksTv, fullMarksTv;
        private ImageView profileTv;

        public HolderSubmittedAss(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.nameTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            profileTv = itemView.findViewById(R.id.profileTv);
            obtainedMarksTv = itemView.findViewById(R.id.obtainedMarksTv);
            fullMarksTv = itemView.findViewById(R.id.fullMarksTv);

        }
    }
}