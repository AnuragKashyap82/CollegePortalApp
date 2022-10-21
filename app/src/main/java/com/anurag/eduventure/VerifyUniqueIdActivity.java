package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.anurag.eduventure.databinding.ActivityVerifyUniqueIdBinding;
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

public class VerifyUniqueIdActivity extends AppCompatActivity {

    private ActivityVerifyUniqueIdBinding binding;
    private FirebaseFirestore firebaseFirestore;
    String userType;
    private boolean isUniqueId = false;
    private boolean isRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyUniqueIdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(VerifyUniqueIdActivity.this, R.color.white));
        }

        firebaseFirestore = FirebaseFirestore.getInstance();

        binding.phoneEt.setEnabled(false);
        binding.sendOtpBtn.setEnabled(false);

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VerifyUniqueIdActivity.this, NeedHelpActivity.class));
            }
        });
    }
    String uniqueId;
    private void validateData() {
        uniqueId = binding.uniqueIdEt.getText().toString().trim();

        if (TextUtils.isEmpty(uniqueId)) {
            Toast.makeText(this, "Enter Your Unique Id....!", Toast.LENGTH_SHORT).show();
        } else {
            checkExistingUniqueId();
        }
    }

    private void checkExistingUniqueId() {
        binding.progressBar.setVisibility(View.VISIBLE);

        DocumentReference documentReference = firebaseFirestore.collection("RegisteredUniqueId").document(uniqueId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                isRegistered = value.exists();
                if (isRegistered){
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(VerifyUniqueIdActivity.this, "This Unique_Id is already Registered...!!", Toast.LENGTH_SHORT).show();
                }else {
                    verifyUniqueId();
                }
            }
        });
    }

    private void verifyUniqueId() {
        binding.progressBar.setVisibility(View.VISIBLE);
        DocumentReference documentReference = firebaseFirestore.collection("UniqueId").document(uniqueId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                isUniqueId = snapshot.exists();
                if (isUniqueId){
                    binding.progressBar.setVisibility(View.GONE);
                    verifyPhoneNumber();
                    binding.sendOtpBtn.setEnabled(true);
                    Toast.makeText(VerifyUniqueIdActivity.this, "Unique-Id Verified....!", Toast.LENGTH_SHORT).show();
                }
                else {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(VerifyUniqueIdActivity.this, "Unique-Id Not Found....!", Toast.LENGTH_SHORT).show();
                }
                String  phone = ""+snapshot.getString("phone");
                userType = ""+snapshot.getString("userType");
                binding.phoneEt.setText(phone);
            }
        });
    }

    private void verifyPhoneNumber() {
        binding.sendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePhoneNumber();
            }
        });
    }
    private String phoneNumber;
    private void validatePhoneNumber() {

        phoneNumber = binding.phoneEt.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Enter Your Phone Number....", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            Intent intent = new Intent(VerifyUniqueIdActivity.this, OtpActivity.class);
            intent.putExtra("phoneNumber", binding.phoneEt.getText().toString());
            intent.putExtra("uniqueId", binding.uniqueIdEt.getText().toString());
            intent.putExtra("userType", userType);
            startActivity(intent);
        }
    }
}
