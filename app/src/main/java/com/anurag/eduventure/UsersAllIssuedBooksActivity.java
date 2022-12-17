package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.anurag.eduventure.Adapters.AdapterAppliedBooks;
import com.anurag.eduventure.Adapters.AdapterAppliedUser;
import com.anurag.eduventure.Adapters.AdapterIssuedBooks;
import com.anurag.eduventure.Models.ModelAppliedBooks;
import com.anurag.eduventure.Models.ModelAppliedUser;
import com.anurag.eduventure.Models.ModelIssuedBooks;
import com.anurag.eduventure.databinding.ActivityUsersAllIssuedBooksBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAllIssuedBooksActivity extends AppCompatActivity {

    private ActivityUsersAllIssuedBooksBinding binding;
    private String uid;
    private AdapterIssuedBooks adapterIssuedBooks;
    private ArrayList<ModelIssuedBooks> issuedBooksArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersAllIssuedBooksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        uid = getIntent().getStringExtra("uid");
        loadUserDetails(uid);
        loadAllUsersBooks(uid);
    }

    private void loadUserDetails(String uid) {
        DocumentReference documentReference  = FirebaseFirestore.getInstance().collection("Users").document(uid);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot ds, @Nullable FirebaseFirestoreException error) {
                String name = "" + ds.getString("name");
                String profileImage = "" + ds.getString("profileImage");
                String uniqueId = "" + ds.getString("uniqueId");

                binding.studentNameTv.setText(name);
                binding.studentIdTv.setText(uniqueId);

                try {
                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(binding.profileIv);
                } catch (Exception e) {
                    binding.profileIv.setImageResource(R.drawable.ic_person_gray);
                }
            }
        });
    }
    private void loadAllUsersBooks(String uid) {
        binding.progressBar.setVisibility(View.VISIBLE);
        issuedBooksArrayList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("issuedBooks").document(uid).collection("Books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelIssuedBooks modelIssuedBooks = document.toObject(ModelIssuedBooks.class);
                                issuedBooksArrayList.add(modelIssuedBooks);
                            }

                            LinearLayoutManager layoutManager = new LinearLayoutManager(UsersAllIssuedBooksActivity.this, LinearLayoutManager.HORIZONTAL, false);
                            binding.usersBooksRv.setLayoutManager(layoutManager);

                            binding.usersBooksRv.setLayoutManager(new LinearLayoutManager(UsersAllIssuedBooksActivity.this));

                            adapterIssuedBooks = new AdapterIssuedBooks(UsersAllIssuedBooksActivity.this, issuedBooksArrayList, "ADMIN");
                            binding.usersBooksRv.setAdapter(adapterIssuedBooks);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}