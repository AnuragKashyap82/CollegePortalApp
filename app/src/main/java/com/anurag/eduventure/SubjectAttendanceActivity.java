package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.anurag.eduventure.databinding.ActivitySubjectAttendanceBinding;
import com.anurag.eduventure.databinding.ActivitySubmittedAssignmentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SubjectAttendanceActivity extends AppCompatActivity {

    ActivitySubjectAttendanceBinding binding;
    boolean isPresent = false;
    FirebaseAuth firebaseAuth;
    private String name ,uniqueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubjectAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        int[] allDay = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31};

        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH) + 1;
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DecimalFormat mFormat = new DecimalFormat("00");
        String day  =mFormat.format(mDay);
        String year  =mFormat.format(mYear);
        String month  =mFormat.format(mMonth);

        checkAttendancePercentage(day, month, year);

    }

    private void checkAttendancePercentage(String day, String month, String year) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Attendance")
                .child("4th Semester").child("CSE").child("DM").child(day).child(month).child(year).child("present")
                .child("0011");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isPresent = dataSnapshot.exists();
                if(isPresent){
                    binding.presentTv.setVisibility(View.VISIBLE);
                    binding.absentTv.setVisibility(View.GONE);
                }else {
                    binding.presentTv.setVisibility(View.GONE);
                    binding.absentTv.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void checkUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                    String userType = "" + ds.child("userType").getValue();
                                     name = "" + ds.child("name").getValue();
                                     uniqueId = "" + ds.child("uniqueId").getValue();

                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                }

}