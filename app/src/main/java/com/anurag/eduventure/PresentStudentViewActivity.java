package com.anurag.eduventure;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.anurag.eduventure.databinding.ActivityPresentStudentBinding;
import com.anurag.eduventure.databinding.ActivityPresentStudentViewBinding;

import java.text.DecimalFormat;
import java.util.Calendar;

public class PresentStudentViewActivity extends AppCompatActivity {
    ActivityPresentStudentViewBinding binding;
    private String sub, semester, branch, date;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPresentStudentViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.subTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PresentStudentViewActivity.this);
                builder.setTitle("Select Subject:")
                        .setItems(Constants.subCategories, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selectedUser = Constants.subCategories[i];
                                binding.subTv.setText(selectedUser);
                            }
                        }).show();
            }
        });

        binding.semTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PresentStudentViewActivity.this);
                builder.setTitle("Select Semester:")
                        .setItems(Constants.semesterCategories, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selectedUser = Constants.semesterCategories[i];
                                binding.semTv.setText(selectedUser);
                            }
                        }).show();
            }
        });
        binding.branchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PresentStudentViewActivity.this);
                builder.setTitle("Select Branch:")
                        .setItems(Constants.branchCategories, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selectedUser = Constants.branchCategories[i];
                                binding.branchTv.setText(selectedUser);
                            }
                        }).show();
            }
        });
        binding.dateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickDialog();
            }
        });
        binding.checkAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        binding.attendanceIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PresentStudentViewActivity.this, AttendanceActivity.class));

            }
        });
    }

    private void datePickDialog() {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH) + 1;
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                DecimalFormat mFormat = new DecimalFormat("00");
                String pDay = mFormat.format(dayOfMonth);
                String pMonth = mFormat.format(monthOfYear + 1);
                String pYear = "" + year;
                String pDate = pDay + "/" + pMonth + "/" + pYear;


                binding.dateTv.setText(pDate);
            }
        }, mYear, mMonth - 1, mDay);

        datePickerDialog.show();
        datePickerDialog.getDatePicker();
    }

    private void validateData() {
        sub = binding.subTv.getText().toString().trim();
        semester = binding.semTv.getText().toString().trim();
        branch = binding.branchTv.getText().toString().trim();
        date = binding.dateTv.getText().toString().trim();

        if (TextUtils.isEmpty(sub)) {
            Toast.makeText(this, "Select Subject....!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(semester)) {
            Toast.makeText(this, "Select Semester....!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(branch)) {
            Toast.makeText(this, "Select branch....!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(date)) {
            Toast.makeText(this, "Select Today's Date....!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(PresentStudentViewActivity.this, PresentStudentActivity.class);
            intent.putExtra("branch", branch);
            intent.putExtra("semester", semester);
            intent.putExtra("date", date);
            intent.putExtra("sub", sub);
            startActivity(intent);
        }

    }
}