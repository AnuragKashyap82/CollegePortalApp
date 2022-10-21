package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.anurag.eduventure.Adapters.AdapterMaterial;
import com.anurag.eduventure.Models.ModelMaterial;
import com.anurag.eduventure.databinding.ActivityAllMaterialBinding;
import com.anurag.eduventure.databinding.ActivityMaterialBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AllMaterialActivity extends AppCompatActivity {

    ActivityAllMaterialBinding binding;
    private String branch, semester;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelMaterial> materialList;
    private AdapterMaterial adapterMaterial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllMaterialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        branch = getIntent().getStringExtra("branch");
        semester = getIntent().getStringExtra("semester");

        binding.semesterTv.setText(semester);
        binding.branchTv.setText(branch);

        firebaseAuth = FirebaseAuth.getInstance();
        loadAllMaterial();

        binding.searchMaterialEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterMaterial.getFilter().filter(charSequence);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void loadAllMaterial() {
        materialList =new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Material");
        reference.child(branch).child(semester)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        materialList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelMaterial modelMaterial = ds.getValue(ModelMaterial.class);
                            materialList.add(modelMaterial);
                        }
                        Collections.sort(materialList, new Comparator<ModelMaterial>() {
                            @Override
                            public int compare(ModelMaterial t1, ModelMaterial t2) {
                                return t1.getTimestamp().compareToIgnoreCase(t2.getTimestamp());
                            }
                        });
                        Collections.reverse(materialList);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(AllMaterialActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        binding.materialRv.setLayoutManager(layoutManager);

                        binding.materialRv.setLayoutManager(new LinearLayoutManager(AllMaterialActivity.this));

                        adapterMaterial = new AdapterMaterial(AllMaterialActivity.this, materialList);
                        binding.materialRv.setAdapter(adapterMaterial);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}