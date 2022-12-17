package com.anurag.eduventure.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anurag.eduventure.Adapters.AdapterAppliedBooks;
import com.anurag.eduventure.Adapters.AdapterAppliedUser;
import com.anurag.eduventure.Models.ModelAppliedBooks;
import com.anurag.eduventure.Models.ModelAppliedUser;
import com.anurag.eduventure.R;
import com.anurag.eduventure.databinding.FragmentAppliedBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AppliedFragment extends Fragment {

    public AppliedFragment() {
        // Required empty public constructor
    }

    private FragmentAppliedBinding binding;
    private AdapterAppliedUser adapterAppliedUser;
    private ArrayList<ModelAppliedUser> appliedUserArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentAppliedBinding.inflate(getLayoutInflater());

        loadAllAppliedUsers();

        return binding.getRoot();
    }

    private void loadAllAppliedUsers() {
        binding.progressBar.setVisibility(View.VISIBLE);
        appliedUserArrayList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("issuedApplied")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelAppliedUser modelAppliedUser = document.toObject(ModelAppliedUser.class);
                                appliedUserArrayList.add(modelAppliedUser);
                            }

                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                            binding.issuedBooksRv.setLayoutManager(layoutManager);

                            binding.issuedBooksRv.setLayoutManager(new LinearLayoutManager(getActivity()));

                            adapterAppliedUser = new AdapterAppliedUser(getActivity(), appliedUserArrayList, "ADMIN");
                            binding.issuedBooksRv.setAdapter(adapterAppliedUser);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}