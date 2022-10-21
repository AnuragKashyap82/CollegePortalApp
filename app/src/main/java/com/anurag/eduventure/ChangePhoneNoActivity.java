package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.anurag.eduventure.Adapters.AdapterFeed;
import com.anurag.eduventure.Models.ModelFeed;
import com.anurag.eduventure.databinding.ActivityChangePhoneNoBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChangePhoneNoActivity extends AppCompatActivity {
    ActivityChangePhoneNoBinding binding;

    private ArrayList<ModelFeed> feedArrayList;
    private AdapterFeed adapterFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePhoneNoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadFeeds();
        binding.createPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
            }
        });
    }
    private void loadFeeds() {
        feedArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Feeds");
        reference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        feedArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelFeed model = ds.getValue(ModelFeed.class);
                            feedArrayList.add(model);
                        } Collections.sort(feedArrayList, new Comparator<ModelFeed>() {
                            @Override
                            public int compare(ModelFeed t1, ModelFeed t2) {
                                return t1.getPostId().compareToIgnoreCase(t2.getPostId());
                            }
                        });
                        Collections.reverse(feedArrayList);
                        adapterFeed = new AdapterFeed(ChangePhoneNoActivity.this, feedArrayList);
                        binding.feedsRv.setAdapter(adapterFeed);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void showBottomSheet() {

        final Dialog dialog = new Dialog(ChangePhoneNoActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bs_post_type_options);

        LinearLayout postImageLl = dialog.findViewById(R.id.postImageLl);
        LinearLayout postVideoLl = dialog.findViewById(R.id.postVideoLl);
        LinearLayout askDoubtLl = dialog.findViewById(R.id.askDoubtLl);
        LinearLayout addAchievementLl = dialog.findViewById(R.id.addAchievementLl);
        LinearLayout addDocLl = dialog.findViewById(R.id.addDocLl);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


        postImageLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(ChangePhoneNoActivity.this, CreatePostActivity.class));
            }
        });

        postVideoLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChangePhoneNoActivity.this, AddLectureActivity.class));
                dialog.dismiss();
            }
        });
        askDoubtLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        addAchievementLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
        addDocLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
    }
}