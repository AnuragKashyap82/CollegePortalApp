package com.anurag.eduventure.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anurag.eduventure.Models.ModelReturnBook;
import com.anurag.eduventure.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterReturn extends RecyclerView.Adapter<AdapterReturn.HolderReturn> {

    private Context context;
    private ArrayList<ModelReturnBook> returnBookArrayList;

    public AdapterReturn(Context context, ArrayList<ModelReturnBook> returnBookArrayList) {
        this.context = context;
        this.returnBookArrayList = returnBookArrayList;
    }

    @NonNull
    @Override
    public HolderReturn onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_issued_books, parent, false);
        return new HolderReturn(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderReturn holder, int position) {
        ModelReturnBook modelReturnBook = returnBookArrayList.get(position);
        String timestamp = modelReturnBook.getTimestamp();
        String returnDate = modelReturnBook.getReturnDate();
        String uid = modelReturnBook.getUid();

        holder.yd.setText("Returned Date:");

        holder.issuedDateTv.setText(returnDate);
        loadBooksDetails(timestamp, holder);
    }

    private void loadBooksDetails(String timestamp, HolderReturn holder) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Books").document(timestamp);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                String subjectName = snapshot.get("subjectName").toString();
                String bookName = snapshot.get("bookName").toString();
                String authorName = snapshot.get("authorName").toString();
                String bookId = snapshot.get("bookId").toString();

                holder.subjectNameTv.setText(subjectName);
                holder.bookNameTv.setText(bookName);
                holder.authorNameTv.setText(authorName);
                holder.bookId.setText("Book No: " + bookId);

            }
        });

    }

    @Override
    public int getItemCount() {
        return returnBookArrayList.size();
    }

    public class HolderReturn extends RecyclerView.ViewHolder {

        private TextView subjectNameTv, bookNameTv, bookId, authorNameTv, issuedDateTv, yd;

        public HolderReturn(@NonNull View itemView) {
            super(itemView);

            subjectNameTv = itemView.findViewById(R.id.subjectNameTv);
            bookNameTv = itemView.findViewById(R.id.bookNameTv);
            bookId = itemView.findViewById(R.id.bookId);
            authorNameTv = itemView.findViewById(R.id.AuthorNameTv);
            issuedDateTv = itemView.findViewById(R.id.issuedDateTv);
            yd = itemView.findViewById(R.id.yd);
        }
    }
}
