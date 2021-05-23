package com.example.contacthandbook.fragment.classes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.contacthandbook.R;
import com.example.contacthandbook.firebaseManager.FirebaseCallBack;
import com.example.contacthandbook.firebaseManager.FirebaseManager;
import com.example.contacthandbook.model.Student;
import com.example.contacthandbook.model.Teacher;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ClassDetailActivity extends AppCompatActivity {

    TextView textViewTeacher;
    TextView textViewNumberStudent;
    TextView textViewYear;
    RecyclerView recyclerViewList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseManager firebaseManager = new FirebaseManager(this);
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String className = intent.getStringExtra("className");
        String teacherId = intent.getStringExtra("teacherId");
        setTitle("Class:" + className);
        setContentView(R.layout.activity_class_detail);
        textViewTeacher = findViewById(R.id.homeroomTeacher);

        firebaseManager.getTeacher(teacherId, new FirebaseCallBack.SingleTeacher() {
            @Override
            public void onCallback(Teacher teacher) {
                if (teacher.getName().equals("")) {
                    textViewTeacher.setText("No Teacher");
                }
                else {
                    textViewTeacher.setText( teacher.getName());
                }
            }
        });

        int year =Calendar.getInstance().get(Calendar.YEAR);
        recyclerViewList = findViewById(R.id.studentInClass);
        recyclerViewList.setLayoutManager(new LinearLayoutManager(this));
        textViewNumberStudent = findViewById(R.id.numberofStudent);
        textViewYear = findViewById(R.id.schoolyear);
        ArrayList<Student> arraymData = new ArrayList<Student>();
        firebaseManager.getAllStudent(new FirebaseCallBack.AllStudentCallBack() {
            @Override
            public void onCallback(List<Student> students) {

                    for (Student student : students) {
                        if (student.getClassName().contains(className)) {
                            arraymData.add(student);
                        }


                     }
                ClassDetailAdapter adapter = new ClassDetailAdapter(ClassDetailActivity.this,arraymData);
                recyclerViewList.setAdapter(adapter);
                textViewYear.setText(String.valueOf(year));
                textViewNumberStudent.setText(String.valueOf(arraymData.size()));

            }
        });



    }
    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }


}



