package com.anurag.eduventure.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.eduventure.Models.ModelPresentStudent;
import com.anurag.eduventure.R;
import com.anurag.eduventure.StudentListActivity;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterPresentStudent extends RecyclerView.Adapter<AdapterPresentStudent.HolderPresentStudent>{

    private Context context;
    private ArrayList<ModelPresentStudent> presentStudents;

    public AdapterPresentStudent(Context context, ArrayList<ModelPresentStudent> presentStudents) {
        this.context = context;
        this.presentStudents = presentStudents;
    }

    @NonNull
    @Override
    public HolderPresentStudent onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_presentstudent, parent, false);
        return new HolderPresentStudent(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPresentStudent holder, @SuppressLint("RecyclerView") int position) {

        ModelPresentStudent modelPresentStudent = presentStudents.get(position);

        String name = modelPresentStudent.getName();
        String branch = modelPresentStudent.getBranch();
        String semester = modelPresentStudent.getSemester();
        String uniqueId = modelPresentStudent.getUniqueId();

        holder.studentNameTv.setText(name);
        holder.studentIdTv.setText(uniqueId);

        holder.absentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyDB easyDB = EasyDB.init(context, "ITEMS_DB")
                        .setTableName("ITEMS_TABLE")
                        .addColumn(new Column("Name", new String[]{"text", "not null"}))
                        .addColumn(new Column("Branch", new String[]{"text", "not null"}))
                        .addColumn(new Column("Semester", new String[]{"text", "not null"}))
                        .addColumn(new Column("uniqueId", new String[]{"text", "unique"}))
                        .doneTableColumn();

                easyDB.deleteRow(1, uniqueId);
                Toast.makeText(context, "Marked as Absent", Toast.LENGTH_SHORT).show();

                presentStudents.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();

                ((StudentListActivity) context).presentCount();
            }
        });
    }
    @Override
    public int getItemCount() {
        return presentStudents.size();
    }

    class HolderPresentStudent extends RecyclerView.ViewHolder{

        private TextView studentIdTv, studentNameTv, absentBtn;

        public HolderPresentStudent(@NonNull View itemView) {
            super(itemView);

            studentIdTv = itemView.findViewById(R.id.studentIdTv);
            studentNameTv = itemView.findViewById(R.id.studentNameTv);
            absentBtn = itemView.findViewById(R.id.absentBtn);

        }
    }
}
