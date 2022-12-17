package com.anurag.eduventure.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anurag.eduventure.Models.ModelAppliedUser;
import com.anurag.eduventure.R;
import com.anurag.eduventure.UsersAllAppliedBooksActivity;
import com.anurag.eduventure.UsersAllIssuedBooksActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterAppliedUser extends RecyclerView.Adapter<AdapterAppliedUser.HolderAppliedUser> {

    private Context context;
    private ArrayList<ModelAppliedUser> appliedUserArrayList;
    private String LAYOUT_CODE;

    public AdapterAppliedUser(Context context, ArrayList<ModelAppliedUser> appliedUserArrayList, String LAYOUT_CODE) {
        this.context = context;
        this.appliedUserArrayList = appliedUserArrayList;
        this.LAYOUT_CODE = LAYOUT_CODE;
    }

    @NonNull
    @Override
    public HolderAppliedUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_applied_book_user, parent, false);
        return new HolderAppliedUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAppliedUser holder, int position) {
        ModelAppliedUser modelAppliedUser = appliedUserArrayList.get(position);
        String uid = modelAppliedUser.getUid();
        loadUserDetails(uid, holder);

        if (LAYOUT_CODE.equals("ADMIN")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, UsersAllAppliedBooksActivity.class);
                    intent.putExtra("uid", uid);
                    context.startActivity(intent);
                }
            });
        }else if (LAYOUT_CODE.equals("USER")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, UsersAllIssuedBooksActivity.class);
                    intent.putExtra("uid", uid);
                    context.startActivity(intent);
                }
            });
        }

    }

    private void loadUserDetails(String uid, HolderAppliedUser holder) {
        DocumentReference documentReference  = FirebaseFirestore.getInstance().collection("Users").document(uid);
       documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
           @Override
           public void onEvent(@Nullable DocumentSnapshot ds, @Nullable FirebaseFirestoreException error) {
               String name = "" + ds.getString("name");
               String profileImage = "" + ds.getString("profileImage");
               String uniqueId = "" + ds.getString("uniqueId");

               holder.userNameTv.setText(name);
               holder.studentIdTv.setText(uniqueId);

               try {
                   Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(holder.profileIv);
               } catch (Exception e) {
                   holder.profileIv.setImageResource(R.drawable.ic_person_gray);
               }
           }
       });
    }

    @Override
    public int getItemCount() {
        return appliedUserArrayList.size();
    }

    public class HolderAppliedUser extends RecyclerView.ViewHolder {

        private TextView userNameTv, studentIdTv;
        private ImageView profileIv;

        public HolderAppliedUser(@NonNull View itemView) {
            super(itemView);

            userNameTv = itemView.findViewById(R.id.userNameTv);
            profileIv = itemView.findViewById(R.id.profileIv);
            studentIdTv = itemView.findViewById(R.id.studentIdTv);
        }
    }
}
