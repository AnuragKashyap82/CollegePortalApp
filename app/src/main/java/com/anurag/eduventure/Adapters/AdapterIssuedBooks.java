package com.anurag.eduventure.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.eduventure.Models.ModelIssuedBooks;
import com.anurag.eduventure.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterIssuedBooks extends RecyclerView.Adapter<AdapterIssuedBooks.HolderIssuedBook> {

    private Context context;
    private ArrayList<ModelIssuedBooks> issuedBooksArrayList;
    private String LAYOUT_CODE;

    public AdapterIssuedBooks(Context context, ArrayList<ModelIssuedBooks> issuedBooksArrayList, String LAYOUT_CODE) {
        this.context = context;
        this.issuedBooksArrayList = issuedBooksArrayList;
        this.LAYOUT_CODE = LAYOUT_CODE;
    }

    @NonNull
    @Override
    public HolderIssuedBook onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.row_issued_books, parent, false);
        return new HolderIssuedBook(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderIssuedBook holder, int position) {
        ModelIssuedBooks modelIssuedBooks  = issuedBooksArrayList.get(position);
        String timestamp = modelIssuedBooks.getTimestamp();
        String issueDate = modelIssuedBooks.getIssueDate();
        String uid = modelIssuedBooks.getUid();

        holder.issuedDateTv.setText(issueDate);
        loadBooksDetails(timestamp, holder);

        if (LAYOUT_CODE.equals("ADMIN")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showReturnBookDialog(timestamp, uid);
                }
            });
        }else if (LAYOUT_CODE.equals("USER")){

        }
    }

    private void loadBooksDetails(String timestamp, HolderIssuedBook holder) {
        DocumentReference documentReference  = FirebaseFirestore.getInstance().collection("Books").document(timestamp);
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
                holder.bookId.setText("Book No: "+bookId);

            }
        });
    }
    private void showReturnBookDialog(String timestamp, String uid) {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(context, R.style.BottomSheetStyle);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view1 = inflater.inflate(R.layout.confirm_issue_layout, null);
        myDialog.setView(view1);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        Button cancelBtn = view1.findViewById(R.id.cancelBtn);
        Button issueBookBtn = view1.findViewById(R.id.issueBookBtn);
        TextView bookNameTv = view1.findViewById(R.id.bookNameTv);
        TextView tv3 = view1.findViewById(R.id.tv3);
        TextView bookIdTv = view1.findViewById(R.id.bookIdTv);
        ProgressBar progressBar = view1.findViewById(R.id.progressBar);

        tv3.setText("Return Book");

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Books").document(timestamp);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                String subjectName = snapshot.get("subjectName").toString();
                String bookName = snapshot.get("bookName").toString();
                String authorName = snapshot.get("authorName").toString();
                String bookId = snapshot.get("bookId").toString();

                bookNameTv.setText(bookName);
                bookIdTv.setText(bookId);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        issueBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnBook(timestamp, uid, progressBar, dialog);
            }
        });
        dialog.show();
    }

    private void returnBook(String timestamp, String uid, ProgressBar progressBar, AlertDialog dialog) {
        progressBar.setVisibility(View.VISIBLE);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        String date = currentDay + "-" + currentMonth + "-" + currentYear;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("timestamp", "" + timestamp);
        hashMap.put("returnDate", "" + date);
        hashMap.put("uid", "" + uid);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("returnedBooks").document(uid);
        documentReference.collection("Books").document(timestamp)
                .set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            HashMap<String, Object> hashMap1 = new HashMap<>();
                            hashMap1.put("uid", "" + uid);
                            DocumentReference documentReference1 = FirebaseFirestore.getInstance().collection("returnedBooks").document(uid);
                            documentReference1.set(hashMap1)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        deleteIssuedApplied(timestamp, uid, progressBar, dialog);
                                                    }
                                                }, 2000);
                                            } else {

                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        } else {
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void deleteIssuedApplied(String timestamp, String uid, ProgressBar progressBar, AlertDialog dialog) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("issuedBooks").document(uid);
        documentReference.collection("Books").document(timestamp).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            checkAllBookIssued(uid, dialog, progressBar);
                        } else {
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void checkAllBookIssued(String uid, AlertDialog dialog, ProgressBar progressBar) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("issuedBooks").document(uid);
        documentReference.collection("Books")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (snapshot.isEmpty()) {
                            DocumentReference documentReference1 = FirebaseFirestore.getInstance().collection("issuedBooks").document(uid);
                            documentReference1.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressBar.setVisibility(View.GONE);
                                            dialog.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        } else {
                            Toast.makeText(context, "Snapshot exists!!!!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            dialog.dismiss();
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return issuedBooksArrayList.size();
    }

    public class HolderIssuedBook extends RecyclerView.ViewHolder {

        private TextView subjectNameTv, bookNameTv, bookId, authorNameTv, issuedDateTv;

        public HolderIssuedBook(@NonNull View itemView) {
            super(itemView);

            subjectNameTv = itemView.findViewById(R.id.subjectNameTv);
            bookNameTv = itemView.findViewById(R.id.bookNameTv);
            bookId = itemView.findViewById(R.id.bookId);
            authorNameTv = itemView.findViewById(R.id.AuthorNameTv);
            issuedDateTv = itemView.findViewById(R.id.issuedDateTv);
        }
    }
}
