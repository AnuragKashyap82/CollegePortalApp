package com.anurag.eduventure.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.eduventure.Models.ModelAtteStud;
import com.anurag.eduventure.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import static com.anurag.eduventure.AttendanceActivity.joiningCode;

public class AdapterAttenStud extends RecyclerView.Adapter<AdapterAttenStud.HolderStudents> {

    private Context context;
    private ArrayList<ModelAtteStud> atteStudArrayList;

    public AdapterAttenStud(Context context, ArrayList<ModelAtteStud> atteStudArrayList) {
        this.context = context;
        this.atteStudArrayList = atteStudArrayList;
    }

    @NonNull
    @Override
    public HolderStudents onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_attendance_item, parent, false);
        return new HolderStudents(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderStudents holder, int position) {
        ModelAtteStud modelAtteStud = atteStudArrayList.get(position);
        String uid = modelAtteStud.getUid();

        loadUserDetails(uid, holder);
        checkForCheckBox(uid, holder);

        holder.absentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markAttendanceAsAbsent(uid, holder);
                holder.absentBtn.setChecked(true);
                if (holder.presentBtn.isChecked()) {
                    holder.presentBtn.setChecked(false);
                }

            }
        });
        holder.presentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markAttendanceAsPresent(uid, holder);
                holder.presentBtn.setChecked(true);
                if (holder.absentBtn.isChecked()) {
                    holder.absentBtn.setChecked(false);

                }
            }
        });
    }

    private void markAttendanceAsAbsent(String uid, HolderStudents holder) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        String date = currentDay + "-" + currentMonth + "-" + currentYear;
        String monthYear = ""+ currentMonth+currentYear;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("date", "" + date);
        hashMap.put("monthYear", "" + currentMonth + currentYear);
        hashMap.put("uid", "" + uid);
        hashMap.put("Attendance", "Absent");

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("classroom")
                .document(joiningCode).collection("Attendance").document("Date").collection("" + date).document(uid);
        documentReference.set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Classroom");
                        databaseReference.child(joiningCode).child("Students").child(""+monthYear).child(uid)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String absentCount = "" + snapshot.child("absentCount").getValue();
                                            if (absentCount.equals("") || absentCount.equals("null")) {
                                                absentCount = "0";
                                            }
                                            long newAbsentCount = Long.parseLong(absentCount) + 1;
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("absentCount", ""+newAbsentCount);
                                            hashMap.put("monthYear", ""+monthYear);
                                            hashMap.put("classCode", ""+joiningCode);
                                            hashMap.put("uid", ""+uid);

                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Classroom");
                                            reference.child(joiningCode).child("Students").child(""+monthYear).child(uid).updateChildren(hashMap)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                checkForCheckBox(uid, holder);
                                                                Toast.makeText(context, "Absent!!!", Toast.LENGTH_SHORT).show();
                                                            }else {

                                                            }
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });

                                        }else{
                                            long newPresentCount = 1;
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("absentCount", ""+newPresentCount);
                                            hashMap.put("monthYear", ""+monthYear);
                                            hashMap.put("classCode", ""+joiningCode);
                                            hashMap.put("uid", ""+uid);

                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Classroom");
                                            reference.child(joiningCode).child("Students").child(""+monthYear).child(uid).setValue(hashMap)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                checkForCheckBox(uid, holder);
                                                                Toast.makeText(context, "Absent!!!", Toast.LENGTH_SHORT).show();
                                                            }else {

                                                            }
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void markAttendanceAsPresent(String uid, HolderStudents holder) {

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        String date = currentDay + "-" + currentMonth + "-" + currentYear;
        String monthYear = ""+ currentMonth+currentYear;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("date", "" + date);
        hashMap.put("monthYear", "" + currentMonth + currentYear);
        hashMap.put("uid", "" + uid);
        hashMap.put("Attendance", "Present");

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("classroom")
                .document(joiningCode).collection("Attendance").document("Date").collection("" + date).document(uid);
        documentReference.set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Classroom");
                        databaseReference.child(joiningCode).child("Students").child(""+monthYear).child(uid)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String presentCount = "" + snapshot.child("presentCount").getValue();
                                            if (presentCount.equals("") || presentCount.equals("null")) {
                                                presentCount = "0";
                                            }

                                            long newPresentCount = Long.parseLong(presentCount) + 1;
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("presentCount", ""+newPresentCount);
                                            hashMap.put("monthYear", ""+monthYear);
                                            hashMap.put("classCode", ""+joiningCode);
                                            hashMap.put("uid", ""+uid);

                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Classroom");
                                            reference.child(joiningCode).child("Students").child(""+monthYear).child(uid).updateChildren(hashMap)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                checkForCheckBox(uid, holder);
                                                                Toast.makeText(context, "Present!!!", Toast.LENGTH_SHORT).show();
                                                            }else {

                                                            }
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });
                                        }else {
                                            long newPresentCount = 1;

                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("presentCount", ""+newPresentCount);
                                            hashMap.put("monthYear", ""+monthYear);
                                            hashMap.put("classCode", ""+joiningCode);
                                            hashMap.put("uid", ""+uid);

                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Classroom");
                                            reference.child(joiningCode).child("Students").child(""+monthYear).child(uid).setValue(hashMap)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                checkForCheckBox(uid, holder);
                                                                Toast.makeText(context, "Present!!!", Toast.LENGTH_SHORT).show();
                                                            }else {

                                                            }
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    private void checkForCheckBox(String uid, HolderStudents holder) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        String date = currentDay + "-" + currentMonth + "-" + currentYear;

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("classroom")
                .document(joiningCode).collection("Attendance").document("Date").collection("" + date).document(uid);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot.exists()){
                    String attendance = snapshot.getString("Attendance");
                    if (attendance.equals("Present")){
                        holder.presentBtn.setChecked(true);
                        if (holder.absentBtn.isChecked()) {
                            holder.absentBtn.setChecked(false);

                        }
                    }else if (attendance.equals("Absent")){
                        holder.absentBtn.setChecked(true);
                        if (holder.presentBtn.isChecked()) {
                            holder.presentBtn.setChecked(false);
                        }

                    }
                }

            }
        });
    }

    private void loadUserDetails(String uid, HolderStudents holder) {
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
        return atteStudArrayList.size();
    }

    public class HolderStudents extends RecyclerView.ViewHolder {

        private TextView studentNameTv, studentRegNoTv;
        private CheckBox presentBtn, absentBtn;

        public HolderStudents(@NonNull View itemView) {
            super(itemView);

            studentNameTv = itemView.findViewById(R.id.studentNameTv);
            studentRegNoTv = itemView.findViewById(R.id.studentRegNoTv);
            presentBtn = itemView.findViewById(R.id.presentBtn);
            absentBtn = itemView.findViewById(R.id.absentBtn);
        }
    }
}
