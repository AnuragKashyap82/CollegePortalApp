package com.anurag.eduventure.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anurag.eduventure.Adapters.AdapterAppliedBooks;
import com.anurag.eduventure.Adapters.AdapterReturn;
import com.anurag.eduventure.Models.ModelAppliedBooks;
import com.anurag.eduventure.Models.ModelReturnBook;
import com.anurag.eduventure.R;
import com.anurag.eduventure.databinding.FragmentReturnedBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class ReturnedFragment extends Fragment {

    public ReturnedFragment() {
        // Required empty public constructor
    }
    private FragmentReturnedBinding binding;
    private FirebaseAuth firebaseAuth;
    private AdapterReturn adapterReturn;
    private ArrayList<ModelReturnBook> returnBookArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentReturnedBinding.inflate(getLayoutInflater());

        firebaseAuth = FirebaseAuth.getInstance();
        loadAllReturnedBooks();


        return binding.getRoot();
    }

    private void loadAllReturnedBooks() {
        binding.progressBar.setVisibility(View.VISIBLE);
        returnBookArrayList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("returnedBooks").document(firebaseAuth.getUid()).collection("Books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelReturnBook modelReturnBook = document.toObject(ModelReturnBook.class);
                                returnBookArrayList.add(modelReturnBook);
                            }

                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                            binding.returnedBookRv.setLayoutManager(layoutManager);

                            binding.returnedBookRv.setLayoutManager(new LinearLayoutManager(getActivity()));

                            adapterReturn = new AdapterReturn(getActivity(), returnBookArrayList);
                            binding.returnedBookRv.setAdapter(adapterReturn);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}