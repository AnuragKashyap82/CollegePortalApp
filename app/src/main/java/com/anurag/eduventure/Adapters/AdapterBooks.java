package com.anurag.eduventure.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.anurag.eduventure.BookViewActivity;
import com.anurag.eduventure.Filters.FilterBooks;
import com.anurag.eduventure.Filters.FilterNotice;
import com.anurag.eduventure.Models.ModelBooks;
import com.anurag.eduventure.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterBooks extends RecyclerView.Adapter<AdapterBooks.HolderBooks> implements Filterable {

    private Context context;
    public ArrayList<ModelBooks> booksArrayList;
    private FilterBooks filter;

    public AdapterBooks(Context context, ArrayList<ModelBooks> booksArrayList) {
        this.context = context;
        this.booksArrayList = booksArrayList;
    }

    @NonNull
    @Override
    public HolderBooks onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_books_items, parent, false);
        return new HolderBooks(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderBooks holder, int position) {
        ModelBooks modelBooks = booksArrayList.get(position);
        String bookName = modelBooks.getBookName();
        String authorName = modelBooks.getAuthorName();
        String subjectName = modelBooks.getSubjectName();
        String bookId = modelBooks.getBookId();
        String timestamp = modelBooks.getTimestamp();

        holder.bookNameTv.setText(bookName);
        holder.subjectNameTv.setText(subjectName);
        holder.authorNameTv.setText(authorName);
        holder.bookId.setText("Book No: "+bookId);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BookViewActivity.class);
                intent.putExtra("timestamp", ""+timestamp);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return booksArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterBooks(this, booksArrayList);

        }
        return filter;
    }

    public class HolderBooks extends RecyclerView.ViewHolder {

        private TextView subjectNameTv, bookNameTv, bookId, authorNameTv;

        public HolderBooks(@NonNull View itemView) {
            super(itemView);

            subjectNameTv = itemView.findViewById(R.id.subjectNameTv);
            bookNameTv = itemView.findViewById(R.id.bookNameTv);
            bookId = itemView.findViewById(R.id.bookId);
            authorNameTv = itemView.findViewById(R.id.AuthorNameTv);
        }
    }
}
