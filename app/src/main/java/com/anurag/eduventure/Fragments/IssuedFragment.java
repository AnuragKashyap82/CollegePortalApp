package com.anurag.eduventure.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anurag.eduventure.Adapters.AdapterIssuedBooks;
import com.anurag.eduventure.Models.ModelIssuedBooks;
import com.anurag.eduventure.R;
import com.anurag.eduventure.databinding.FragmentIssuedBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class IssuedFragment extends Fragment {

    public IssuedFragment() {
        // Required empty public constructor
    }

    private FragmentIssuedBinding binding;
    private FirebaseAuth firebaseAuth;
    private AdapterIssuedBooks adapterIssuedBooks;
    private ArrayList<ModelIssuedBooks> issuedBooksArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentIssuedBinding.inflate(getLayoutInflater());

        firebaseAuth = FirebaseAuth.getInstance();
        loadAllIssuedBooks();

        return binding.getRoot();
    }

    private void loadAllIssuedBooks() {
        binding.progressBar.setVisibility(View.VISIBLE);
        issuedBooksArrayList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("issuedBooks").document(firebaseAuth.getUid()).collection("Books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelIssuedBooks modelIssuedBooks = document.toObject(ModelIssuedBooks.class);
                                issuedBooksArrayList.add(modelIssuedBooks);
                            }

                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                            binding.issuedBooksRv.setLayoutManager(layoutManager);

                            binding.issuedBooksRv.setLayoutManager(new LinearLayoutManager(getActivity()));

                            adapterIssuedBooks = new AdapterIssuedBooks(getActivity(), issuedBooksArrayList, "USER");
                            binding.issuedBooksRv.setAdapter(adapterIssuedBooks);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}