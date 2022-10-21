package com.anurag.eduventure;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

import androidx.annotation.NonNull;
import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class Constants {

    public static final String[] userType = {

            "user",
            "teachers",
    };
    public static final long MAX_BYTES_PDF = 50000000;

    public static final String[] branchCategories = {

            "CSE",
            "ME",
            "EE",
            "ECE",
            "CIVIL",
    };
    public static final String[] semesterCategories = {

            "1st Semester",
            "2nt Semester",
            "3rd Semester",
            "4th Semester",
            "5th Semester",
            "6th Semester",
            "7th Semester",
            "8th Semester",
    };
    public static final String[] sessionCategories = {

            "2010",
            "2011",
            "2012",
            "2013",
            "2014",
            "2015",
            "2016",
            "2017",
            "2018",
            "2019",
            "2020",
            "2021",
            "2022",
    };
    public static final String[] marksCategories = {

            "10",
            "20",
            "25",
            "40",
            "50",
            "75",
            "100",
    };
    public static final String[] subCategories = {

            "FLAT",
            "DAA",
            "DBMS",
            "DM",
            "DS",
            "OPP",
            "CYBER SECURITY",
    };
    public static void loadMaterialSize(String url, TextView sizeTv) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        double bytes = storageMetadata.getSizeBytes();

                        double kb = bytes / 1024;
                        double mb = kb / 1024;

                        if (mb >= 1) {
                            sizeTv.setText(String.format("%.2f", mb) + " MB");
                        } else if (kb >= 1) {
                            sizeTv.setText(String.format("%.2f", kb) + " KB");
                        } else {
                            sizeTv.setText(String.format("%.2f", bytes) + " bytes");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
}
