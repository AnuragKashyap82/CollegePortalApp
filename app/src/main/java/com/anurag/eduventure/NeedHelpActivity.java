package com.anurag.eduventure;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.anurag.eduventure.databinding.ActivityNeedHelpBinding;

public class NeedHelpActivity extends AppCompatActivity {

    private ActivityNeedHelpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNeedHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(NeedHelpActivity.this, R.color.white));
        }

        binding.sendMailBtn.setOnClickListener(new View.OnClickListener() {
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
    }

    private void validateData() {
        String problemDetails;

        problemDetails = binding.problemEt.getText().toString().trim();

        if (TextUtils.isEmpty(problemDetails)) {
            Toast.makeText(this, "Describe Yours Problem....", Toast.LENGTH_SHORT).show();
            return;
        } else {
            String[] addresses = {"anuragkashyap0802@gmail.com"};
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("gmail/*");
            intent.putExtra(Intent.EXTRA_EMAIL, addresses);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Problem related SignIn/SignUp");
            intent.putExtra(Intent.EXTRA_TEXT, problemDetails);

            if (intent.resolveActivity(NeedHelpActivity.this.getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }
}
