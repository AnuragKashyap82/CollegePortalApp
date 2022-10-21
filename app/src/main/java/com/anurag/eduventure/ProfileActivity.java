package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.anurag.eduventure.Adapters.AdapterNotice;
import com.anurag.eduventure.Models.ModelNotice;
import com.anurag.eduventure.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        loadMyInfo();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.profileEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class));
            }
        });
    }

    private void loadMyInfo() {
        DocumentReference documentReference  = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot ds, @Nullable FirebaseFirestoreException error) {

                String userType = "" + ds.getString("userType");
                String email = "" + ds.getString("email");
                String name = "" + ds.getString("name");
                String address = "" + ds.getString("address");
                String city = "" + ds.getString("city");
                String state = "" + ds.getString("state");
                String country = "" + ds.getString("country");
                String uniqueId = "" + ds.getString("uniqueId");
                String online = "" + ds.getString("online");
                String phone = "" + ds.getString("phone");
                String profileImage = "" + ds.getString("profileImage");
                String timestamp = "" + ds.getString("timestamp");
                String uid = "" + ds.getString("uid");
                String regNo = "" + ds.getString("regNo");
                String dob = "" + ds.getString("dob");
                String fatherName = "" + ds.getString("fatherName");
                String motherName = "" + ds.getString("motherName");
                String branch = "" + ds.getString("branch");
                String semester = "" + ds.getString("semester");
                String session = "" + ds.getString("session");
                String seatType = "" + ds.getString("seatType");
                String hostler = "" + ds.getString("hostler");

                binding.nameTv.setText(name);
                binding.phoneTv.setText("+91" + phone);
                binding.emailTv.setText(email);
                binding.addressTv.setText(address);
                binding.cityTv.setText(city);
                binding.stateTv.setText(state);
                binding.countryTv.setText(country);
                binding.uniqueIdTv.setText(uniqueId);
                binding.regNoTv.setText(regNo);
                binding.dobTv.setText(dob);
                binding.fatherNameTv.setText(fatherName);
                binding.motherNameTv.setText(motherName);
                binding.branchTv.setText(branch);
                binding.semesterTv.setText(semester);
                binding.sessionTv.setText(session);
                binding.seatTypeTv.setText(seatType);
                binding.hostlerTv.setText(hostler);

                if (userType.equals("user")) {
                    binding.adminVector.setVisibility(View.GONE);
                } else if (userType.equals("teachers")) {
                    binding.adminVector.setVisibility(View.VISIBLE);
                } else if (userType.equals("anurag")) {
                    binding.adminVector.setVisibility(View.VISIBLE);
                }

                try {
                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(binding.profileIv);
                } catch (Exception e) {
                    binding.profileIv.setImageResource(R.drawable.ic_person_gray);
                }
            }
        });
    }

//    private void loadFavoriteMaterial() {
//        materialFavArrayList = new ArrayList<>();
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.child(firebaseAuth.getUid()).child("Favorites")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        materialFavArrayList.clear();
//                        for (DataSnapshot ds : snapshot.getChildren()) {
//                            String materialId = "" + ds.child("materialId").getValue();
//                            String branch = "" + ds.child("branch").getValue();
//                            String semester = "" + ds.child("semester").getValue();
//                            String schoolId = "" + ds.child("schoolId").getValue();
//                            String topic = "" + ds.child("topic").getValue();
//
//                            ModelMaterialFav modelMaterialFav = new ModelMaterialFav();
//                            modelMaterialFav.setMaterialId(materialId);
//                            modelMaterialFav.setSchoolId(schoolId);
//                            modelMaterialFav.setBranch(branch);
//                            modelMaterialFav.setSemester(semester);
//                            modelMaterialFav.setMaterialId(materialId);
//                            modelMaterialFav.setTopic(topic);
//
//                            materialFavArrayList.add(modelMaterialFav);
//                        }
//
//                        binding.favoriteMaterialCountTv.setText("" + materialFavArrayList.size());
//                        adapterMaterialFavorite = new AdapterMaterialFavorite(ProfileActivity.this, materialFavArrayList);
//                        binding.FabMaterialsRv.setAdapter(adapterMaterialFavorite);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }
}