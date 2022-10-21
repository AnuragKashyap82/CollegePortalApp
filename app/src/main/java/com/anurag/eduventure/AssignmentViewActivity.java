package com.anurag.eduventure;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.eduventure.databinding.ActivityAssignmentViewBinding;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import static com.anurag.eduventure.Constants.MAX_BYTES_PDF;

public class AssignmentViewActivity extends AppCompatActivity {

    ActivityAssignmentViewBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private String assignmentId, assignmentName, assignmentUrl, dueDate, fullMarks, classCode;
    private Uri pdfUri = null;
    boolean isCompletedAssignment = false;

    private static final int PDF_PICK_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = ActivityAssignmentViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        assignmentUrl = getIntent().getStringExtra("assignmentUrl");
        assignmentName = getIntent().getStringExtra("assignmentName");
        classCode = getIntent().getStringExtra("classCode");
        assignmentId = getIntent().getStringExtra("assignmentId");
        dueDate = getIntent().getStringExtra("dueDate");
        fullMarks = getIntent().getStringExtra("fullMarks");

        checkUser();
        loadAssignment();

        binding.deleteAssignmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AssignmentViewActivity.this);
                builder.setTitle("Delete")
                        .setMessage("Are you sure want to Delete Assignment: " + assignmentId + " ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteAssignment();
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
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.editAssignmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssignmentViewActivity.this, EditAssignmentActivity.class);
                intent.putExtra("assignmentId", assignmentId);
                intent.putExtra("classCode", classCode);
                startActivity(intent);
                Toast.makeText(AssignmentViewActivity.this, "Edit Material Btn Clicked....!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.downloadAssignmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(AssignmentViewActivity.this);
                builder.setTitle("Download")
                        .setMessage("Do you want to Download Notice: " + assignmentId + " ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (ContextCompat.checkSelfPermission(AssignmentViewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    downloadAssignment(AssignmentViewActivity.this, "" + classCode, "" + assignmentId, "" + assignmentUrl);

                                } else {

                                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                }
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
        binding.addAssBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailsBottomSheet(dueDate, fullMarks);
            }
        });
    }

    private void detailsBottomSheet(String dueDate, String fullMarks) {

        final Dialog dialog = new Dialog(AssignmentViewActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bs_add_assignment);

        TextView dueDateTv = dialog.findViewById(R.id.dueDateTv);
        Button submitAssignmentBtn = dialog.findViewById(R.id.submitAssignmentBtn);
        Button submittedAssignmentBtn = dialog.findViewById(R.id.submittedAssignmentBtn);
        Button assWorkViewBtn = dialog.findViewById(R.id.assWorkViewBtn);
        ImageButton pdfPickBtn = dialog.findViewById(R.id.pdfPickBtn);
        TextView completedTv = dialog.findViewById(R.id.completedTv);
        TextView notCompletedTv = dialog.findViewById(R.id.notCompletedTv);
        TextView fullMarksTv = dialog.findViewById(R.id.fullMarksTv);
        TextView obtainedMarksTv = dialog.findViewById(R.id.obtainedMarksTv);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        dueDateTv.setText(dueDate);
        fullMarksTv.setText(fullMarks);

        loadMarks(obtainedMarksTv);

        completedTv.setVisibility(View.GONE);
        checkCompleted(completedTv, notCompletedTv, submitAssignmentBtn, assWorkViewBtn);

        pdfPickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
            }
        });

        submitAssignmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
                dialog.dismiss();
            }
        });
        assWorkViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssignmentViewActivity.this, YourAssWorkActivity.class);
                intent.putExtra("assignmentId", assignmentId);
                intent.putExtra("assignmentName", assignmentName);
                intent.putExtra("classCode", classCode);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        submittedAssignmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssignmentViewActivity.this, SubmittedAssignmentActivity.class);
                intent.putExtra("assignmentId", assignmentId);
                intent.putExtra("classCode", classCode);
                intent.putExtra("assignmentName", assignmentName);
                startActivity(intent);
                dialog.dismiss();
            }
        });
    }
    private void checkCompleted(TextView completedTv, TextView notCompletedTv, Button submitAssignmentBtn, Button assWorkViewBtn) {

        DocumentReference documentReference = firebaseFirestore.collection("classroom").document(classCode).collection("assignment").document(assignmentId).collection("Submission").document(firebaseAuth.getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                isCompletedAssignment = value.exists();
                if (isCompletedAssignment){
                    completedTv.setVisibility(View.VISIBLE);
                    notCompletedTv.setVisibility(View.GONE);
                    submitAssignmentBtn.setText("Replace Your Work");
                    assWorkViewBtn.setVisibility(View.VISIBLE);
                }
                else{
                    completedTv.setVisibility(View.GONE);
                    notCompletedTv.setVisibility(View.VISIBLE);
                    submitAssignmentBtn.setVisibility(View.VISIBLE);
                    assWorkViewBtn.setVisibility(View.GONE);
                }
            }
        });
    }

    private void pdfPickIntent() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PDF_PICK_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PDF_PICK_CODE) {

                pdfUri = data.getData();
            }
        } else {
            Toast.makeText(this, "Cancelled picking pdf", Toast.LENGTH_SHORT).show();

        }
    }

    private void validateData() {
        if (pdfUri == null) {
            Toast.makeText(this, "Pick Your Assignment....!", Toast.LENGTH_SHORT).show();
        } else {
            uploadAssignmentToStorage();
        }
    }

    private void uploadAssignmentToStorage() {
        binding.progressBar.setVisibility(View.VISIBLE);

        long timestamp = System.currentTimeMillis();
        String filePathAndName = "Assignment/" + timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String uploadedAssignmentUrl = "" + uriTask.getResult();

                        uploadAssignmentWorkInfoToDb(uploadedAssignmentUrl, timestamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(AssignmentViewActivity.this, "Assignment Work upload failed due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void uploadAssignmentWorkInfoToDb(String uploadedAssignmentUrl, long timestamp) {

        binding.progressBar.setVisibility(View.VISIBLE);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("assignmentId", ""+assignmentId);
        hashMap.put("classCode", ""+classCode);
        hashMap.put("dueDate", ""+dueDate);
        hashMap.put("fullMarks", ""+fullMarks);
        hashMap.put("assignmentName", ""+assignmentName);
        hashMap.put("marksObtained", "");
        hashMap.put("timestamp", ""+timestamp);
        hashMap.put("uid", ""+firebaseAuth.getUid());
        hashMap.put("url", ""+uploadedAssignmentUrl);

        DocumentReference documentReference = firebaseFirestore.collection("classroom").document(classCode).collection("assignment").document(assignmentId).collection("Submission").document(firebaseAuth.getUid());
        documentReference
                .set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(AssignmentViewActivity.this, "Work Successfully uploaded....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(AssignmentViewActivity.this, " Failed to upload to db due to"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }


    private void deleteAssignment() {

        DocumentReference documentReference = firebaseFirestore.collection("classroom").document(classCode).collection("assignment").document(assignmentId);
        documentReference
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AssignmentViewActivity.this, "Assignment Deleted...!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AssignmentViewActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void loadAssignment() {

        DocumentReference documentReference = firebaseFirestore.collection("classroom").document(classCode).collection("assignment").document(assignmentId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String assignmentUrl = "" + value.getString("url");
                String assignmentName = "" + value.getString("assignmentName");
                dueDate = "" + value.getString("dueDate");

                binding.assignmentNameTv.setText(assignmentName);
                loadMaterialFromUrl(assignmentUrl);
            }
        });
    }

    private void loadMaterialFromUrl(String assignmentUrl) {

        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(assignmentUrl);
        reference.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {

                        binding.assignmentPdfView.fromBytes(bytes)
                                .swipeHorizontal(false)
                                .onPageChange(new OnPageChangeListener() {
                                    @Override
                                    public void onPageChanged(int page, int pageCount) {
                                        int currentPage = (page + 1);
                                        binding.toolbarSubtitleTv.setText(currentPage + "/" + pageCount);

                                    }
                                })
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        Toast.makeText(AssignmentViewActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        Toast.makeText(AssignmentViewActivity.this, "Error on page" + page + " " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .load();
                        binding.progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }

        private void loadMarks(TextView obtainedMarksTv) {

        DocumentReference documentReference = firebaseFirestore.collection("classroom").document(classCode).collection("assignment").document(assignmentId).collection("Submission").document(firebaseAuth.getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String marksObtained = ""+value.getString("marksObtained");
                obtainedMarksTv.setText(marksObtained);
            }
        });
    }
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    downloadAssignment(AssignmentViewActivity.this, "" + classCode, "" + assignmentId, "" + assignmentUrl);
                } else {
                    Toast.makeText(this, "Permission was denied...", Toast.LENGTH_SHORT).show();
                }
            });

    private void downloadAssignment(AssignmentViewActivity assignmentViewActivity, String classCode, String assignmentId, String assignmentUrl) {
        String nameWithExtension = classCode + ".pdf";

        ProgressDialog progressDialog = new ProgressDialog(AssignmentViewActivity.this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Downloading" + nameWithExtension + "....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(assignmentUrl);
        storageReference.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        saveDownloadedBook(AssignmentViewActivity.this, progressDialog, bytes, nameWithExtension, classCode, assignmentId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AssignmentViewActivity.this, "Failed to download due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveDownloadedBook(AssignmentViewActivity assignmentViewActivity, ProgressDialog progressDialog, byte[] bytes, String nameWithExtension, String classCode, String assignmentId) {
        try {
            File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            downloadsFolder.mkdir();

            String filePath = downloadsFolder.getPath() + "/" + nameWithExtension;

            FileOutputStream out = new FileOutputStream(filePath);
            out.write(bytes);
            out.close();

            Toast.makeText(AssignmentViewActivity.this, "Saved to Download Folder", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

        } catch (Exception e) {
            Toast.makeText(AssignmentViewActivity.this, "Failed saving to download folder due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }


    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String userType = "" + snapshot.child("userType").getValue();

//                        if (userType.equals("user")){
//                            binding.fb.setVisibility(View.GONE);
//                        }
//                        else if (userType.equals("admin")){
//                            binding.fb.setVisibility(View.VISIBLE);
//                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}