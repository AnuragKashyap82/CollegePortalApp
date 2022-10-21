package com.anurag.eduventure.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.eduventure.Constants;
import com.anurag.eduventure.Models.ModelStudent;
import com.anurag.eduventure.R;
import com.anurag.eduventure.StudentListActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterStudent extends RecyclerView.Adapter<AdapterStudent.HolderStudent> {

    private Context context;
    public ArrayList<ModelStudent> studentList;

    public AdapterStudent(Context context, ArrayList<ModelStudent> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public HolderStudent onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_student, parent, false);
        return new HolderStudent(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderStudent holder, @SuppressLint("RecyclerView") int position) {

        final ModelStudent modelStudent = studentList.get(position);
        String name = modelStudent.getName();
        String branch = modelStudent.getBranch();
        String semester = modelStudent.getSemester();
        String uniqueId = modelStudent.getUniqueId();

//        holder.studentIdTv.setText(studentId);
        holder.studentNameTv.setText(name);
        holder.studentIdTv.setText(uniqueId);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent =new Intent(context, AssignmentViewActivity.class);
//                intent.putExtra("branch", branch);
//                intent.putExtra("semester", semester);
//                intent.putExtra("name", name);
//                context.startActivity(intent);
//
//            }
//        });
        holder.presentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.presentBtn.setVisibility(View.GONE);
                holder.absentBtn.setVisibility(View.VISIBLE);
                addToPresent(name, branch, semester, uniqueId);
            }
        });
        holder.absentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                holder.presentBtn.setVisibility(View.VISIBLE);
//                holder.absentBtn.setVisibility(View.GONE);
////                markAbsent(name, branch, semester, uniqueId);
//                EasyDB easyDB = EasyDB.init(context, "ITEMS_DB")
//                        .setTableName("ITEMS_TABLE")
//                        .addColumn(new Column("Name", new String[]{"text", "not null"}))
//                        .addColumn(new Column("Branch", new String[]{"text", "not null"}))
//                        .addColumn(new Column("Semester", new String[]{"text", "not null"}))
//                        .addColumn(new Column("uniqueId", new String[]{"text", "unique"}))
//                        .doneTableColumn();
//
//                easyDB.deleteRow(1, uniqueId);
//                Toast.makeText(context, "Marked as Absent", Toast.LENGTH_SHORT).show();
//
//                studentList.remove(position);
//                notifyItemChanged(position);
//                notifyDataSetChanged();
//                easyDB.deleteAllDataFromTable();
//                ((StudentListActivity) context).presentCount();
            }
        });
    }

    private void addToPresent(String name, String branch, String semester, String uniqueId) {
        EasyDB easyDB = EasyDB.init(context, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Branch", new String[]{"text", "not null"}))
                .addColumn(new Column("Semester", new String[]{"text", "not null"}))
                .addColumn(new Column("uniqueId", new String[]{"text", "unique"}))
                .doneTableColumn();

        Boolean b = easyDB.addData("Name", name)
                .addData("Branch", branch)
                .addData("Semester", semester)
                .addData("uniqueId", uniqueId)
                .doneDataAdding();

        Toast.makeText(context, "Marked as Present", Toast.LENGTH_SHORT).show();

        ((StudentListActivity)context).presentCount();
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    class HolderStudent extends RecyclerView.ViewHolder {

        private TextView studentIdTv, studentNameTv, absentBtn, presentBtn;

        public HolderStudent(@NonNull View itemView) {
            super(itemView);

            studentIdTv = itemView.findViewById(R.id.studentIdTv);
            studentNameTv = itemView.findViewById(R.id.studentNameTv);
            absentBtn = itemView.findViewById(R.id.absentBtn);
            presentBtn = itemView.findViewById(R.id.presentBtn);

        }
    }
}
