package com.anurag.eduventure.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anurag.eduventure.Models.ModelAttendanceReport;
import com.anurag.eduventure.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterReportAttendance extends RecyclerView.Adapter<AdapterReportAttendance.HolderAttendanceReport> {

    private Context context;
    private ArrayList<ModelAttendanceReport> attendanceReportArrayList;

    public AdapterReportAttendance(Context context, ArrayList<ModelAttendanceReport> attendanceReportArrayList) {
        this.context = context;
        this.attendanceReportArrayList = attendanceReportArrayList;
    }

    @NonNull
    @Override
    public HolderAttendanceReport onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_attendance_date, parent, false);
        return new HolderAttendanceReport(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAttendanceReport holder, int position) {
        ModelAttendanceReport modelAttendanceReport  = attendanceReportArrayList.get(position);
        String uid = modelAttendanceReport.getUid();
        String attendance = modelAttendanceReport.getAttendance();

        loadUserDetails(uid, holder);
        if (attendance.equals("Present")){
            holder.presentTv.setVisibility(View.VISIBLE);
            holder.absentTv.setVisibility(View.GONE);
        }else if (attendance.equals("Absent")){
            holder.presentTv.setVisibility(View.GONE);
            holder.absentTv.setVisibility(View.VISIBLE);
        }

    }
    private void loadUserDetails(String uid, HolderAttendanceReport holder) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(uid);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                String name = snapshot.get("name").toString();
                String regNo = snapshot.get("regNo").toString();

                holder.studentNameTv.setText(name);
                holder.studentRegNoTv.setText(regNo);

            }
        });
    }

    @Override
    public int getItemCount() {
        return attendanceReportArrayList.size();
    }

    public class HolderAttendanceReport extends RecyclerView.ViewHolder {

        private TextView  studentNameTv, presentTv, absentTv, studentRegNoTv;

        public HolderAttendanceReport(@NonNull View itemView) {
            super(itemView);

            studentNameTv = itemView.findViewById(R.id.studentNameTv);
            studentRegNoTv = itemView.findViewById(R.id.studentRegNoTv);
            presentTv = itemView.findViewById(R.id.presentTv);
            absentTv = itemView.findViewById(R.id.absentTv);
        }
    }
}
