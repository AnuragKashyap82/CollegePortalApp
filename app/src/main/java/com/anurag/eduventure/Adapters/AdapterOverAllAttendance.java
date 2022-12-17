package com.anurag.eduventure.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anurag.eduventure.Models.ModelAtteStud;
import com.anurag.eduventure.R;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterOverAllAttendance extends RecyclerView.Adapter<AdapterOverAllAttendance.HolderOverAllAttendance> {

    private Context context;
    private ArrayList<ModelAtteStud> atteStudArrayList;

    public AdapterOverAllAttendance(Context context, ArrayList<ModelAtteStud> atteStudArrayList) {
        this.context = context;
        this.atteStudArrayList = atteStudArrayList;
    }

    @NonNull
    @Override
    public HolderOverAllAttendance onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_over_all_attend_item, parent, false);
        return new HolderOverAllAttendance(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOverAllAttendance holder, int position) {
        ModelAtteStud modelAtteStud  = atteStudArrayList.get(position);
        String uid = modelAtteStud.getUid();
        String monthYear = modelAtteStud.getMonthYear();
        String presentCount = modelAtteStud.getPresentCount();
        String absentCount = modelAtteStud.getAbsentCount();
        String classCode = modelAtteStud.getClassCode();

        loadStudentDetails(uid, holder);
        if (presentCount.equals(null)){
            presentCount =  "0";
            holder.presentDaysTv.setText(presentCount);
            loadPercentPresent(presentCount, holder, classCode);
        }else {
            holder.presentDaysTv.setText(presentCount+" Day");
            loadPercentPresent(presentCount, holder, classCode);
        }



    }

    private void loadStudentDetails(String uid, HolderOverAllAttendance holder) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(uid);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                String name = snapshot.get("name").toString();

                holder.studentNameTv.setText(name);

            }
        });
    }
    private void loadPercentPresent(String presentCount, HolderOverAllAttendance holder, String classCode) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        String monthYear = ""+ currentMonth+currentYear;

        DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference("classroom");
        databaseReference.child(classCode).child("noOfClass").child(""+monthYear)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String totalNoClass = snapshot.child("noOfClass").getValue().toString();
                            int presentPercent = (Integer.parseInt(presentCount) * 100) / Integer.parseInt(totalNoClass);
                            holder.presentPercentTv.setText(presentPercent+"%");
                            if (presentPercent < 75){
                                holder.presentPercentTv.setTextColor(context.getResources().getColor(R.color.red));
                            }else {
                                holder.presentPercentTv.setTextColor(context.getResources().getColor(R.color.green));
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return atteStudArrayList.size();
    }

    public class HolderOverAllAttendance extends RecyclerView.ViewHolder {

        private TextView studentNameTv, presentDaysTv, presentPercentTv;

        public HolderOverAllAttendance(@NonNull View itemView) {
            super(itemView);
            studentNameTv = itemView.findViewById(R.id.studentNameTv);
            presentDaysTv = itemView.findViewById(R.id.presentDaysTv);
            presentPercentTv = itemView.findViewById(R.id.presentPercentTv);
        }
    }
}
