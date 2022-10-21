package com.anurag.eduventure.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.anurag.eduventure.Filters.FilterNotice;
import com.anurag.eduventure.Models.ModelAllPresent;
import com.anurag.eduventure.Models.ModelNotice;
import com.anurag.eduventure.NoticeViewActivity;
import com.anurag.eduventure.R;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterAllPresent extends RecyclerView.Adapter<AdapterAllPresent.HolderAllPresent> {

    private Context context;
    public ArrayList<ModelAllPresent> allPresentList;

    public AdapterAllPresent(Context context, ArrayList<ModelAllPresent> allPresentList) {
        this.context = context;
        this.allPresentList = allPresentList;
    }

    @NonNull
    @Override
    public AdapterAllPresent.HolderAllPresent onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_student, parent, false);
        return new HolderAllPresent(view);
    }


    @Override
    public void onBindViewHolder(@NonNull HolderAllPresent holder, int position) {

        final ModelAllPresent modelAllPresent = allPresentList.get(position);
        String name = modelAllPresent.getName();
        String branch = modelAllPresent.getBranch();
        String semester = modelAllPresent.getSemester();
        String uniqueId = modelAllPresent.getUniqueId();
        String date = modelAllPresent.getDate();

        holder.studentIdTv.setText(uniqueId);
        holder.studentNameTv.setText(name);

//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(Long.parseLong(date));
//        String dateFormat = DateFormat.format("dd/MM/yyyy", calendar).toString();

//        holder.dateTv.setText(dateFormat);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, NoticeViewActivity.class);
//                intent.putExtra("noticeId", noticeId);
//                intent.putExtra("noticeUrl", noticeUrl);
//                intent.putExtra("title", title);
//                context.startActivity(intent);
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return allPresentList.size();
    }


    class HolderAllPresent extends RecyclerView.ViewHolder {

        private TextView studentIdTv, studentNameTv, presentBtn, absentBtn;

        public HolderAllPresent(@NonNull View itemView) {
            super(itemView);

            studentIdTv = itemView.findViewById(R.id.studentIdTv);
            studentNameTv = itemView.findViewById(R.id.studentNameTv);
            presentBtn = itemView.findViewById(R.id.presentBtn);
            absentBtn = itemView.findViewById(R.id.absentBtn);

        }
    }
}
