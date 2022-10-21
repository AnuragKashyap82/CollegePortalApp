package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

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

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUser();
            }
        }, 2000);
    }

    private void checkUser() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();

        } else {

            DocumentReference documentReference  = firebaseFirestore.collection("Users").document(firebaseUser.getUid());
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                    String userType = "" + value.getString("userType");

                    if (userType.equals("teachers")) {
                        if (!firebaseUser.isEmailVerified()) {
                            startActivity(new Intent(SplashActivity.this, VerifyEmailActivity.class));
                            finishAffinity();
                        }else {
                            startActivity(new Intent(SplashActivity.this, MainUsersActivity.class));
                            finish();
                        }
                    } else if (userType.equals("user")) {
                        if (!firebaseUser.isEmailVerified()) {
                            startActivity(new Intent(SplashActivity.this, VerifyEmailActivity.class));
                            finishAffinity();
                        }else {
                            startActivity(new Intent(SplashActivity.this, MainUsersActivity.class));
                            finish();
                        }
                    } else if (userType.equals("anurag")) {
                        if (!firebaseUser.isEmailVerified()) {
                            startActivity(new Intent(SplashActivity.this, VerifyEmailActivity.class));
                            finishAffinity();
                        }else {
                            startActivity(new Intent(SplashActivity.this, MainUsersActivity.class));
                            finish();
                        }
                    }

                }
            });
        }
    }
}