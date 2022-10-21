package com.anurag.eduventure;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.anurag.eduventure.databinding.ActivityMaterialBinding;

public class MaterialActivity extends AppCompatActivity {
    ActivityMaterialBinding binding;
    private String branch, semester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMaterialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.semTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MaterialActivity.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(MaterialActivity.this);
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
        binding.searchMaterialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        binding.addMaterialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MaterialActivity.this, AddMaterialActivity.class));
            }
        });
    }

    private void validateData() {

        branch = binding.branchTv.getText().toString().trim();
        semester = binding.semTv.getText().toString().trim();

        if (TextUtils.isEmpty(branch)){
            Toast.makeText(MaterialActivity.this, "Select Your Branch!!!!!", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(semester)){
            Toast.makeText(MaterialActivity.this, "Select Your Semester!!!!!", Toast.LENGTH_SHORT).show();
        }else {
            searchMaterial();
        }
    }

    private void searchMaterial() {
        Intent intent = new Intent(MaterialActivity.this, AllMaterialActivity.class);
        intent.putExtra("branch", branch);
        intent.putExtra("semester", semester);
        startActivity(intent);
    }
}