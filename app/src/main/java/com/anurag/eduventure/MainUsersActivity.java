package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.eduventure.Adapters.AdapterClassroom;
import com.anurag.eduventure.Adapters.AdapterFeed;
import com.anurag.eduventure.Adapters.AdapterNotice;
import com.anurag.eduventure.Adapters.AdapterTimeTable;
import com.anurag.eduventure.Models.ModelClassroom;
import com.anurag.eduventure.Models.ModelFeed;
import com.anurag.eduventure.Models.ModelNotice;
import com.anurag.eduventure.Models.ModelTimeTable;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainUsersActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView profilePic, profileIv;
    FloatingActionButton createPostBtn;
    TextView headerStudentIdTv, headerEmailTv, nameTv, seeAllTv;
    Toolbar drawerBtn;
    RecyclerView timeTableRv;
    RelativeLayout noticeRl, classroomRl, timeTableRl;
    private ArrayList<ModelTimeTable> timeTableArrayList;
    private AdapterTimeTable adapterTimeTable;
    FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ProgressDialog progressDialog;
    ImageSlider slider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_users);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadMyInfo();
        loadAllClasses();
        loadSlider();

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        createPostBtn = findViewById(R.id.createPostBtn);
        drawerBtn = findViewById(R.id.drawerBtn);
        nameTv = findViewById(R.id.nameTv);
        profileIv = findViewById(R.id.profileIv);
        seeAllTv = findViewById(R.id.seeAllTv);
        timeTableRv = findViewById(R.id.timeTableRv);
        slider = findViewById(R.id.slider);
        noticeRl = findViewById(R.id.noticeRl);
        classroomRl = findViewById(R.id.classroomRl);
        timeTableRl = findViewById(R.id.timeTableRl);


        View headerView = navigationView.getHeaderView(0);
        headerStudentIdTv = (TextView) headerView.findViewById(R.id.headerStudentIdTv);
        headerEmailTv = (TextView) headerView.findViewById(R.id.headerEmailTv);
        profilePic = (ImageView) headerView.findViewById(R.id.profileIv);

        seeAllTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainUsersActivity.this, DaysActivity.class));
            }
        });

        headerStudentIdTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainUsersActivity.this, "Name Clicked.....!!!!", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainUsersActivity.this, ProfileActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        noticeRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainUsersActivity.this, NoticeActivity.class));
            }
        });
        classroomRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainUsersActivity.this, ClassroomActivity.class));
            }
        });
        timeTableRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);

                switch (day) {
                    case Calendar.SUNDAY:
                        // Current day is Sunday
                        break;
                    case Calendar.MONDAY:
                        // Current day is Monday
                        Intent myIntent = new Intent(MainUsersActivity.this, TimeTableActivity.class);
                        myIntent.putExtra("text", "Monday");
                        startActivity(myIntent);
                        break;
                    case Calendar.TUESDAY:
                        Intent myIntent1 = new Intent(MainUsersActivity.this, TimeTableActivity.class);
                        myIntent1.putExtra("text", "Tuesday");
                        startActivity(myIntent1);
                        break;
                    case Calendar.WEDNESDAY:
                        Intent myIntent2 = new Intent(MainUsersActivity.this, TimeTableActivity.class);
                        myIntent2.putExtra("text", "Wednesday");
                        startActivity(myIntent2);
                        break;
                    case Calendar.THURSDAY:
                        Intent myIntent3 = new Intent(MainUsersActivity.this, TimeTableActivity.class);
                        myIntent3.putExtra("text", "Thursday");
                        startActivity(myIntent3);
                        break;
                    case Calendar.FRIDAY:
                        Intent myIntent4 = new Intent(MainUsersActivity.this, TimeTableActivity.class);
                        myIntent4.putExtra("text", "Friday");
                        startActivity(myIntent4);
                        break;
                    case Calendar.SATURDAY:
                        Intent myIntent5 = new Intent(MainUsersActivity.this, TimeTableActivity.class);
                        myIntent5.putExtra("text", "Saturday");
                        startActivity(myIntent5);
                        break;
                }
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, drawerBtn, R.string.OpenDrawer, R.string.CloseDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Fragment fragment = null;

                switch (id) {
                    case R.id.optClassroom:
                        startActivity(new Intent(MainUsersActivity.this, ClassroomActivity.class));
                        break;
                    case R.id.optTimeTable:
//                        startActivity(new Intent(MainUsersActivity.this, DaysActivity.class));
                        Calendar calendar = Calendar.getInstance();
                        int day = calendar.get(Calendar.DAY_OF_WEEK);

                        switch (day) {
                            case Calendar.SUNDAY:
                                // Current day is Sunday
                                break;
                            case Calendar.MONDAY:
                                // Current day is Monday
                                Intent myIntent = new Intent(MainUsersActivity.this, TimeTableActivity.class);
                                myIntent.putExtra("text", "Monday");
                                startActivity(myIntent);
                                break;
                            case Calendar.TUESDAY:
                                Intent myIntent1 = new Intent(MainUsersActivity.this, TimeTableActivity.class);
                                myIntent1.putExtra("text", "Tuesday");
                                startActivity(myIntent1);
                                break;
                            case Calendar.WEDNESDAY:
                                Intent myIntent2 = new Intent(MainUsersActivity.this, TimeTableActivity.class);
                                myIntent2.putExtra("text", "Wednesday");
                                startActivity(myIntent2);
                                break;
                            case Calendar.THURSDAY:
                                Intent myIntent3 = new Intent(MainUsersActivity.this, TimeTableActivity.class);
                                myIntent3.putExtra("text", "Thursday");
                                startActivity(myIntent3);
                                break;
                            case Calendar.FRIDAY:
                                Intent myIntent4 = new Intent(MainUsersActivity.this, TimeTableActivity.class);
                                myIntent4.putExtra("text", "Friday");
                                startActivity(myIntent4);
                                break;
                            case Calendar.SATURDAY:
                                Intent myIntent5 = new Intent(MainUsersActivity.this, TimeTableActivity.class);
                                myIntent5.putExtra("text", "Saturday");
                                startActivity(myIntent5);
                                break;
                        }
                        break;
                    case R.id.optFav:
                        startActivity(new Intent(MainUsersActivity.this, FavouritesActivity.class));
                        break;
                    case R.id.optNotes:
                        startActivity(new Intent(MainUsersActivity.this, NotesActivity.class));
                        break;
                    case R.id.optNotice:
                        startActivity(new Intent(MainUsersActivity.this, NoticeActivity.class));
                        break;
                    case R.id.optLibrary:
                        break;
                    case R.id.optAttendance:
                        startActivity(new Intent(MainUsersActivity.this, PresentStudentViewActivity.class));
                        break;
                    case R.id.optMaterial:
                        startActivity(new Intent(MainUsersActivity.this, MaterialActivity.class));
                        break;
                    case R.id.optAss:

                        break;
                    case R.id.optLectures:
                        startActivity(new Intent(MainUsersActivity.this, LectureActivity.class));
                        break;
                    case R.id.optProfile:
                        startActivity(new Intent(MainUsersActivity.this, ProfileActivity.class));
                        break;
                    case R.id.optChangePass:
                        startActivity(new Intent(MainUsersActivity.this, ChangePasswordActivity.class));
                        break;
                    case R.id.optChangePhone:
                        startActivity(new Intent(MainUsersActivity.this, ChangePhoneNoActivity.class));
                        break;
                    case R.id.optSetting:
                        startActivity(new Intent(MainUsersActivity.this, SettingActivity.class));
                        break;
                    case R.id.optAddSliderBtn:
                        startActivity(new Intent(MainUsersActivity.this, AddSliderActivity.class));
                        break;
                    case R.id.optAddUniqueIdBtn:
                        startActivity(new Intent(MainUsersActivity.this, AddUniqueIdActivity.class));
                        break;
                    case R.id.optAddStudentBtn:
                        startActivity(new Intent(MainUsersActivity.this, AddStudentActivity.class));
                        break;
                    case R.id.optAboutUs:
                        startActivity(new Intent(MainUsersActivity.this, AboutUsActivity.class));
                        break;
                    case R.id.optLogout:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainUsersActivity.this);
                        builder.setTitle("Delete")
                                .setMessage("Are you sure want to Logout")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        makeMeOffline();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment).commit();
        drawerLayout.closeDrawer(GravityCompat.START);
        fragmentTransaction.addToBackStack(null);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void loadMyInfo() {

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot ds, @Nullable FirebaseFirestoreException error) {
                String profileImage = "" + ds.getString("profileImage");
                String userName = "" + ds.getString("name");
                String userEmail = "" + ds.getString("email");
                String uniqueId = "" + ds.getString("uniqueId");

                headerStudentIdTv.setText(uniqueId);
                headerEmailTv.setText(userEmail);
                nameTv.setText(userName);

                try {
                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(profilePic);
                } catch (Exception e) {
                    profilePic.setImageResource(R.drawable.ic_person_gray);
                }
                try {
                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(profileIv);
                } catch (Exception e) {
                    profileIv.setImageResource(R.drawable.ic_person_gray);
                }
            }
        });
    }

    private void makeMeOffline() {
        progressDialog.setMessage("Logging out...");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "false");

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.setMessage("Logging Out...!");
                        progressDialog.show();
                        firebaseAuth.signOut();
                        startActivity(new Intent(MainUsersActivity.this, LoginActivity.class));
                        finish();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainUsersActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadAllClasses() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                // Current day is Sunday
                break;
            case Calendar.MONDAY:
                timeTableArrayList = new ArrayList<>();
                FirebaseFirestore.getInstance().collection("timeTable").document(firebaseAuth.getUid()).collection("Monday")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        ModelTimeTable modelTimeTable = document.toObject(ModelTimeTable.class);
                                        timeTableArrayList.add(modelTimeTable);
                                    }
                                    Collections.sort(timeTableArrayList, new Comparator<ModelTimeTable>() {
                                        @Override
                                        public int compare(ModelTimeTable t1, ModelTimeTable t2) {
                                            return t1.getStartTime().compareToIgnoreCase(t2.getStartTime());
                                        }
                                    });

                                    adapterTimeTable = new AdapterTimeTable(MainUsersActivity.this, timeTableArrayList);
                                    timeTableRv.setAdapter(adapterTimeTable);
                                }
                            }
                        });
                break;
            case Calendar.TUESDAY:
                timeTableArrayList = new ArrayList<>();
                    FirebaseFirestore.getInstance().collection("timeTable").document(firebaseAuth.getUid()).collection("Tuesday")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        ModelTimeTable modelTimeTable = document.toObject(ModelTimeTable.class);
                                        timeTableArrayList.add(modelTimeTable);
                                    }
                                    Collections.sort(timeTableArrayList, new Comparator<ModelTimeTable>() {
                                        @Override
                                        public int compare(ModelTimeTable t1, ModelTimeTable t2) {
                                            return t1.getStartTime().compareToIgnoreCase(t2.getStartTime());
                                        }
                                    });

                                    adapterTimeTable = new AdapterTimeTable(MainUsersActivity.this, timeTableArrayList);
                                    timeTableRv.setAdapter(adapterTimeTable);
                                }
                            }
                        });
                break;
            case Calendar.WEDNESDAY:
                timeTableArrayList = new ArrayList<>();
                FirebaseFirestore.getInstance().collection("timeTable").document(firebaseAuth.getUid()).collection("Wednesday")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        ModelTimeTable modelTimeTable = document.toObject(ModelTimeTable.class);
                                        timeTableArrayList.add(modelTimeTable);
                                    }
                                    Collections.sort(timeTableArrayList, new Comparator<ModelTimeTable>() {
                                        @Override
                                        public int compare(ModelTimeTable t1, ModelTimeTable t2) {
                                            return t1.getStartTime().compareToIgnoreCase(t2.getStartTime());
                                        }
                                    });

                                    adapterTimeTable = new AdapterTimeTable(MainUsersActivity.this, timeTableArrayList);
                                    timeTableRv.setAdapter(adapterTimeTable);
                                }
                            }
                        });
                break;
            case Calendar.THURSDAY:
                timeTableArrayList = new ArrayList<>();
                FirebaseFirestore.getInstance().collection("timeTable").document(firebaseAuth.getUid()).collection("Thursday")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        ModelTimeTable modelTimeTable = document.toObject(ModelTimeTable.class);
                                        timeTableArrayList.add(modelTimeTable);
                                    }
                                    Collections.sort(timeTableArrayList, new Comparator<ModelTimeTable>() {
                                        @Override
                                        public int compare(ModelTimeTable t1, ModelTimeTable t2) {
                                            return t1.getStartTime().compareToIgnoreCase(t2.getStartTime());
                                        }
                                    });

                                    adapterTimeTable = new AdapterTimeTable(MainUsersActivity.this, timeTableArrayList);
                                    timeTableRv.setAdapter(adapterTimeTable);
                                }
                            }
                        });
                break;
            case Calendar.FRIDAY:
                timeTableArrayList = new ArrayList<>();
                FirebaseFirestore.getInstance().collection("timeTable").document(firebaseAuth.getUid()).collection("Friday")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        ModelTimeTable modelTimeTable = document.toObject(ModelTimeTable.class);
                                        timeTableArrayList.add(modelTimeTable);
                                    }
                                    Collections.sort(timeTableArrayList, new Comparator<ModelTimeTable>() {
                                        @Override
                                        public int compare(ModelTimeTable t1, ModelTimeTable t2) {
                                            return t1.getStartTime().compareToIgnoreCase(t2.getStartTime());
                                        }
                                    });

                                    adapterTimeTable = new AdapterTimeTable(MainUsersActivity.this, timeTableArrayList);
                                    timeTableRv.setAdapter(adapterTimeTable);
                                }
                            }
                        });
                break;
            case Calendar.SATURDAY:
                timeTableArrayList = new ArrayList<>();
                FirebaseFirestore.getInstance().collection("timeTable").document(firebaseAuth.getUid()).collection("Saturday")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        ModelTimeTable modelTimeTable = document.toObject(ModelTimeTable.class);
                                        timeTableArrayList.add(modelTimeTable);
                                    }
                                    Collections.sort(timeTableArrayList, new Comparator<ModelTimeTable>() {
                                        @Override
                                        public int compare(ModelTimeTable t1, ModelTimeTable t2) {
                                            return t1.getStartTime().compareToIgnoreCase(t2.getStartTime());
                                        }
                                    });

                                    adapterTimeTable = new AdapterTimeTable(MainUsersActivity.this, timeTableArrayList);
                                    timeTableRv.setAdapter(adapterTimeTable);
                                }
                            }
                        });
                break;
        }
    }

    final List<SlideModel> schoolBanner = new ArrayList<>();

    private void loadSlider() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Slider");
        reference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot data : snapshot.getChildren())
                            schoolBanner.add(new SlideModel(data.child("url").getValue().toString(), ScaleTypes.CENTER_CROP));

                        slider.setImageList(schoolBanner, ScaleTypes.CENTER_CROP);
                        slider.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onItemSelected(int i) {
                                showBottomSheetDialog();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showBottomSheetDialog() {

        final Dialog dialog = new Dialog(MainUsersActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bs_more_feed_options);

        LinearLayout editPostLL = dialog.findViewById(R.id.editPostLL);
        LinearLayout deletePostLL = dialog.findViewById(R.id.deletePostLL);
        LinearLayout sharePostLL = dialog.findViewById(R.id.sharePostLL);
        LinearLayout favPostLL = dialog.findViewById(R.id.favPostLL);
        LinearLayout blockPostLL = dialog.findViewById(R.id.blockPostLL);

        editPostLL.setVisibility(View.VISIBLE);
        deletePostLL.setVisibility(View.VISIBLE);
        sharePostLL.setVisibility(View.GONE);
        favPostLL.setVisibility(View.GONE);
        blockPostLL.setVisibility(View.GONE);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        editPostLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(MainUsersActivity.this, AddSliderActivity.class);
                startActivity(intent);
            }
        });
        deletePostLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainUsersActivity.this);
                builder.setTitle("Delete")
                        .setMessage("Are you sure want to Delete this Slider Image")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressDialog.setMessage("Deleting Slider");
                                progressDialog.show();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Slider");
                                ref
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressDialog.dismiss();
                                                Toast.makeText(MainUsersActivity.this, "Slider Deleted...!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(MainUsersActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
}