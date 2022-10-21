package com.anurag.eduventure.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.eduventure.Constants;
import com.anurag.eduventure.MaterialDetailsActivity;
import com.anurag.eduventure.Models.ModelFav;
import com.anurag.eduventure.databinding.RowFavBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterFav extends RecyclerView.Adapter<AdapterFav.HolderFav> {

    private Context context;
    private ArrayList<ModelFav> favArrayList;
    private RowFavBinding binding;

    private static final String TAG = "FAV_BOOK_TAG";

    public AdapterFav(Context context, ArrayList<ModelFav> favArrayList) {
        this.context = context;
        this.favArrayList = favArrayList;
    }

    @NonNull
    @Override
    public HolderFav onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowFavBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderFav(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderFav holder, int position) {

        ModelFav model = favArrayList.get(position);
        String branch = model.getBranch();
        String semester = model.getSemester();
        String materialId = model.getMaterialId();
        String topicName = model.getTopicName();
        String materialUrl = model.getUrl();


        loadMaterialDetails(model, holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MaterialDetailsActivity.class);
                intent.putExtra("materialId", model.getMaterialId());
                intent.putExtra("semester", model.getSemester());
                intent.putExtra("branch", model.getBranch());
                intent.putExtra("materialUrl", model.getUrl());
                intent.putExtra("topicName", model.getTopicName());
                context.startActivity(intent);
            }
        });

        holder.removeFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Remove From Favourite")
                        .setMessage("Are you sure want to Remove Favourite?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(FirebaseAuth.getInstance().getUid()).child("Favorites").child(materialId)
                                        .removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(context, "Removed from  your favorites list....", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure( Exception e) {
                                                Toast.makeText(context, "Failed to remove from favorites due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

    }

    private void loadMaterialDetails(ModelFav model, HolderFav holder) {
        String branch = model.getBranch();
        String semester = model.getSemester();
        String materialId = model.getMaterialId();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Material");
        reference.child(branch).child(semester).child(materialId)

                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String materialId = "" + snapshot.child("materialId").getValue();
                        String subjectName = "" + snapshot.child("subjectName").getValue();
                        String topicName = "" + snapshot.child("topicName").getValue();
                        String branch = "" + snapshot.child("branch").getValue();
                        String semester = "" + snapshot.child("semester").getValue();
                        String viewsCount = "" + snapshot.child("viewsCount").getValue();
                        String downloadsCount = "" + snapshot.child("downloadsCount").getValue();
                        String timestamp = "" + snapshot.child("timestamp").getValue();
                        String url = "" + snapshot.child("url").getValue();

                        model.setFavorite(true);
                        model.setSubjectName(subjectName);
                        model.setTopicName(topicName);
                        model.setTimestamp(Long.parseLong(timestamp));
                        model.setBranch(branch);
                        model.setUrl(url);
                        model.setSemester(semester);
                        model.setMaterialId(materialId);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(timestamp));
                        String dateFormat = DateFormat.format("dd/MM/yyyy", calendar).toString();

                        Constants.loadMaterialSize(""+url, holder.sizeTv);

                        holder.subjectTitleTv.setText(subjectName);
                        holder.topicTv.setText(topicName);
                        holder.dateTv.setText(dateFormat);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return favArrayList.size();
    }

    class HolderFav extends RecyclerView.ViewHolder{

        TextView subjectTitleTv, topicTv, sizeTv, dateTv;
        ImageButton removeFavBtn;


        public HolderFav(@NonNull View itemView) {
            super(itemView);

            subjectTitleTv = binding.subjectTitleTv;
            removeFavBtn = binding.removeFavBtn;
            topicTv = binding.topicTv;
            sizeTv = binding.sizeTv;
            dateTv = binding.dateTv;
        }
    }

}
