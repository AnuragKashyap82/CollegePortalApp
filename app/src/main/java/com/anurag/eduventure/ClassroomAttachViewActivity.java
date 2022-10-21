package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.anurag.eduventure.databinding.ActivityClassroomAttachViewBinding;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static com.anurag.eduventure.Constants.MAX_BYTES_PDF;

public class ClassroomAttachViewActivity extends AppCompatActivity {
    ActivityClassroomAttachViewBinding binding;
    private String classCode, timestamp, classMsg, url, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClassroomAttachViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        classCode = getIntent().getStringExtra("classCode");
        timestamp = getIntent().getStringExtra("timestamp");
        classMsg = getIntent().getStringExtra("classMsg");
        url = getIntent().getStringExtra("url");
        uid = getIntent().getStringExtra("uid");

        loadAttachmentFromUrl(url);

    }

    private void loadAttachmentFromUrl(String url) {

        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        reference.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {

                        binding.attachmentPdfView.fromBytes(bytes)
                                .swipeHorizontal(false)
                                .onPageChange(new OnPageChangeListener() {
                                    @Override
                                    public void onPageChanged(int page, int pageCount) {
                                        int currentPage = (page + 1);
                                        binding.toolbarSubtitleTv.setText(currentPage + "/" + pageCount);

                                    }
                                })
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        Toast.makeText(ClassroomAttachViewActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        Toast.makeText(ClassroomAttachViewActivity.this, "Error on page" + page + " " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .load();
                        binding.progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }
}