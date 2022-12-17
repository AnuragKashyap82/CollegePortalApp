package com.anurag.eduventure.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anurag.eduventure.Adapters.AdapterAppliedBooks;
import com.anurag.eduventure.Adapters.AdapterIssuedBooks;
import com.anurag.eduventure.Models.ModelAppliedBooks;
import com.anurag.eduventure.Models.ModelIssuedBooks;
import com.anurag.eduventure.MyIssuedBookActivity;
import com.anurag.eduventure.R;
import com.anurag.eduventure.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }
    private FragmentHomeBinding binding;
    private AdapterAppliedBooks adapterAppliedBooks;
    private ArrayList<ModelAppliedBooks> appliedBooksArrayList;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater());

        firebaseAuth = FirebaseAuth.getInstance();
        loadAllAppliedBooks();

        return binding.getRoot();
    }

    private void loadAllAppliedBooks() {
        binding.progressBar.setVisibility(View.VISIBLE);
        appliedBooksArrayList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("issuedApplied").document(firebaseAuth.getUid()).collection("Books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelAppliedBooks modelAppliedBooks = document.toObject(ModelAppliedBooks.class);
                                appliedBooksArrayList.add(modelAppliedBooks);
                            }

                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                            binding.issuedBooksRv.setLayoutManager(layoutManager);

                            binding.issuedBooksRv.setLayoutManager(new LinearLayoutManager(getActivity()));

                            adapterAppliedBooks = new AdapterAppliedBooks(getActivity(), appliedBooksArrayList, "USER");
                            binding.issuedBooksRv.setAdapter(adapterAppliedBooks);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}