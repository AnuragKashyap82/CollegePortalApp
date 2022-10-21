package com.anurag.eduventure.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.anurag.eduventure.Filters.FilterNotice;
import com.anurag.eduventure.Models.ModelNotice;
import com.anurag.eduventure.NoticeViewActivity;
import com.anurag.eduventure.R;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterNotice extends RecyclerView.Adapter<AdapterNotice.HolderNotice> implements Filterable {

    private Context context;
    public ArrayList<ModelNotice> noticeList, filterList;
    private FilterNotice filter;

    public AdapterNotice(Context context, ArrayList<ModelNotice> noticeList) {
        this.context = context;
        this.noticeList = noticeList;
        this.filterList = noticeList;
    }

    @NonNull
    @Override
    public HolderNotice onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_notice, parent, false);
        return new HolderNotice(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderNotice holder, int position) {

        final ModelNotice modelNotice = noticeList.get(position);
        String noticeId = modelNotice.getNoticeId();
        String title = modelNotice.getTitle();
        String number = modelNotice.getNumber();
        String date = modelNotice.getTimestamp();
        String noticeUrl = modelNotice.getUrl();

        holder.noticeTitleTv.setText(title);
        holder.noticeNoTv.setText(number);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(date));
        String dateFormat = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.dateTv.setText(dateFormat);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NoticeViewActivity.class);
                intent.putExtra("noticeId", noticeId);
                intent.putExtra("noticeUrl", noticeUrl);
                intent.putExtra("title", title);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterNotice(this, filterList);

        }
        return filter;
    }

    class HolderNotice extends RecyclerView.ViewHolder {

        private TextView noticeTitleTv, noticeNoTv, dateTv;

        public HolderNotice(@NonNull View itemView) {
            super(itemView);

            noticeTitleTv = itemView.findViewById(R.id.noticeTitleTv);
            noticeNoTv = itemView.findViewById(R.id.noticeNoTv);
            dateTv = itemView.findViewById(R.id.dateTv);

        }
    }
}