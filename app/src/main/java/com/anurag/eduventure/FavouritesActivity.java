package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.anurag.eduventure.Adapters.AdapterFav;
import com.anurag.eduventure.Models.ModelFav;
import com.anurag.eduventure.databinding.ActivityFavouritesBinding;
import com.anurag.eduventure.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavouritesActivity extends AppCompatActivity {

    ActivityFavouritesBinding binding;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelFav> favArrayList;
    private AdapterFav adapterFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavouritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        loadFavoriteMaterial();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String userType = "" + snapshot.child("userType").getValue();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadFavoriteMaterial() {
        favArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favorites")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        favArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String materialId = "" + ds.child("materialId").getValue();
                            String branch = "" + ds.child("branch").getValue();
                            String semester = "" + ds.child("semester").getValue();
                            String topicName = "" + ds.child("topicName").getValue();

                            ModelFav modelMaterialFav = new ModelFav();
                            modelMaterialFav.setMaterialId(materialId);
                            modelMaterialFav.setBranch(branch);
                            modelMaterialFav.setSemester(semester);
                            modelMaterialFav.setMaterialId(materialId);
                            modelMaterialFav.setTopicName(topicName);

                            favArrayList.add(modelMaterialFav);
                        }

//                        binding.favoriteMaterialCountTv.setText("" + favArrayList.size());
                        adapterFav = new AdapterFav(FavouritesActivity.this, favArrayList);
                        binding.favRv.setAdapter(adapterFav);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}