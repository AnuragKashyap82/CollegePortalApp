package com.anurag.eduventure;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.anurag.eduventure.databinding.ActivityContentBinding;

import androidx.appcompat.app.AppCompatActivity;

public class ContentActivity extends AppCompatActivity {

    ActivityContentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}