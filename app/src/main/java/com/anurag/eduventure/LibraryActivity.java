package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.anurag.eduventure.Adapters.AdapterBooks;
import com.anurag.eduventure.Models.ModelBooks;
import com.anurag.eduventure.databinding.ActivityLibraryBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LibraryActivity extends AppCompatActivity {

    private ActivityLibraryBinding binding;
    private AdapterBooks adapterBooks;
    private ArrayList<ModelBooks> booksArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLibraryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadAllBooks();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.addBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LibraryActivity.this, AddBooksActivity.class));
            }
        });
        binding.applyIssueViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LibraryActivity.this, LibraryManagementActivity.class));
            }
        });
        if (!binding.searchEt.getText().toString().isEmpty()){
            binding.searchEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        adapterBooks.getFilter().filter(charSequence);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
        binding.issuedBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LibraryActivity.this, MyIssuedBookActivity.class));
            }
        });

    }

    private void loadAllBooks() {
        booksArrayList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("Books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelBooks modelBooks = document.toObject(ModelBooks.class);
                                booksArrayList.add(modelBooks);
                            }

                            binding.booksRv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

                            adapterBooks = new AdapterBooks(LibraryActivity.this, booksArrayList);
                            binding.booksRv.setAdapter(adapterBooks);
                        }
                    }
                });
    }
}