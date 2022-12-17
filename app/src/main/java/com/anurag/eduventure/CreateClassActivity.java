package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.eduventure.Adapters.AdapterComment;
import com.anurag.eduventure.Models.ModelComment;
import com.anurag.eduventure.databinding.ActivityCreateClassBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.util.HashMap;
import java.util.Locale;

public class CreateClassActivity extends AppCompatActivity {
    ActivityCreateClassBinding binding;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private String className, subjectName, theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.createClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        binding.themeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showThemeDialog();
            }
        });
    }

    private void showThemeDialog() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(CreateClassActivity.this, R.style.BottomSheetStyle);
        LayoutInflater inflater = LayoutInflater.from(CreateClassActivity.this);
        View view1 = inflater.inflate(R.layout.select_theme_dialog, null);
        myDialog.setView(view1);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        ImageView themeOne = view1.findViewById(R.id.themeOne);
        ImageView themeTwo = view1.findViewById(R.id.themeTwo);
        ImageView themeThree = view1.findViewById(R.id.themeThree);
        ImageView themeFour = view1.findViewById(R.id.themeFour);
        ImageView themeFive = view1.findViewById(R.id.themeFive);

        themeOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                binding.themeEt.setText("1");
            }
        });
        themeTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                binding.themeEt.setText("2");
            }
        });
        themeThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                binding.themeEt.setText("3");
            }
        });
        themeFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                binding.themeEt.setText("4");
            }
        });
        themeFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                binding.themeEt.setText("5");
            }
        });
        dialog.show();
    }


    private void validateData() {
        subjectName = binding.subjectEt.getText().toString().trim();
        className = binding.classNameEt.getText().toString().trim();
        theme = binding.themeEt.getText().toString().trim();

        if (TextUtils.isEmpty(subjectName)) {
            Toast.makeText(CreateClassActivity.this, "Enter Subject Name....!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(className)) {
            Toast.makeText(CreateClassActivity.this, "Enter Class Name....!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(theme)) {
            Toast.makeText(CreateClassActivity.this, "Theme!!!!!", Toast.LENGTH_SHORT).show();
        } else {
            createClass();
        }
    }

    private void createClass() {
        progressDialog.setMessage("Creating ClassRoom");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("subjectName", "" + subjectName);
        hashMap.put("className", "" + className);
        hashMap.put("theme", "" + theme);
        hashMap.put("classCode", "" + timestamp);
        hashMap.put("uid", "" + firebaseAuth.getUid());
        hashMap.put("timestamp", "" + timestamp);

        DocumentReference documentReference = firebaseFirestore.collection("classroom").document("" + timestamp);
        documentReference.set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        clearText();
                        createOwnClass(timestamp, subjectName, className);
                        Toast.makeText(CreateClassActivity.this, "Class Created Successfully....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(CreateClassActivity.this, " Failed to Create Class to db due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createOwnClass(long timestamp, String subjectName, String className) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("subjectName", "" + subjectName);
        hashMap.put("className", "" + className);
        hashMap.put("theme", "" + theme);
        hashMap.put("classCode", "" + timestamp);
        hashMap.put("uid", "" + firebaseAuth.getUid());
        hashMap.put("timestamp", "" + timestamp);

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid()).collection("classroom").document("" + timestamp);
        documentReference.set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(CreateClassActivity.this, "Class Created Successfully....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(CreateClassActivity.this, " Failed to Create Class to db due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void clearText() {
        binding.classNameEt.setText("");
        binding.subjectEt.setText("");
        binding.themeEt.setText("");
    }
}