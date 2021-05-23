package com.example.contacthandbook.firebaseManager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.contacthandbook.MainActivity;
import com.example.contacthandbook.fragment.home.HomeFragment;
import com.example.contacthandbook.model.Classes;
import com.example.contacthandbook.model.Notification;
import com.example.contacthandbook.model.NotifyDestination;
import com.example.contacthandbook.model.Student;
import com.example.contacthandbook.model.Teacher;
import com.example.contacthandbook.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager {
    private static final String TAG = "Firebase manager";
    private static final String USERS_CHILD = "users";
    private static final String STUDENT_CHILD = "students";
    private static final String CLASS_CHILD = "classes";
    private static final String TEACHER_CHILD = "teachers";
    private static final String NOTIFICATION_CHILD = "notifications";
    private static final String FEEDBACK_CHILD = "feedback";

    private Context context;

    public FirebaseManager(Context context){
        this.context=context;
    }
    
    public void getUser(String username, FirebaseCallBack.UserCallBack callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference(USERS_CHILD).child(username);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                callBack.onCallback(user);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void checkUser(String username, String password, String role, FirebaseCallBack.ValidateCallBack callBack) {
        getUser(username, new FirebaseCallBack.UserCallBack() {
            @Override
            public void onCallback(User user) {
                if (user != null && username.equals(user.getUsername()) && password.equals(user.getPassword()) && role.equals(user.getRole())) {
                    callBack.onCallBack(true, user);
                }
                else {
                    callBack.onCallBack(false, user);
                }
            }

        });

    }

    public void getAllStudent(FirebaseCallBack.AllStudentCallBack callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query studentQuery = database.getReference(STUDENT_CHILD).limitToLast(1000);
        studentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Student> students = new ArrayList<>();
                for (DataSnapshot studentSnapshot: snapshot.getChildren()) {
                    Student student = studentSnapshot.getValue(Student.class);
                    students.add(student);
                }
                callBack.onCallback(students);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void addStudent(Student student, FirebaseCallBack.AddStudentCallBack callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(STUDENT_CHILD).child(student.getId());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myRef.setValue(student);

                callBack.onCallback(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onCallback(false);
            }
        });

        DatabaseReference addStudentAccountRef = database.getReference(USERS_CHILD).child(student.getId());

        addStudentAccountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User stu = new User(student.getId(), "1", student.getName(), "Student");
                addStudentAccountRef.setValue(stu);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onCallback(false);
            }
        });

        DatabaseReference addStudentClass = database.getReference(CLASS_CHILD).child(student.getClassName()).child(student.getId());
        addStudentClass.setValue("student");


    }

    public void getAllTeacher(FirebaseCallBack.AllTeacherCallBack callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query teacherQuery = database.getReference(TEACHER_CHILD).limitToLast(1000);
        teacherQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Teacher> teachers = new ArrayList<>();
                for (DataSnapshot teacherSnapshot: snapshot.getChildren()) {
                    Teacher teacher = teacherSnapshot.getValue(Teacher.class);
                    teachers.add(teacher);
                }
                callBack.onCallback(teachers);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


    public void addTeacher(Teacher teacher, FirebaseCallBack.AddTeacherCallBack callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(TEACHER_CHILD).child(teacher.getId());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myRef.setValue(teacher);

                callBack.onCallback(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onCallback(false);
            }
        });

        DatabaseReference addTeacherAccountRef = database.getReference(USERS_CHILD).child(teacher.getId());

        addTeacherAccountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User tea = new User(teacher.getId(), "1", teacher.getName(), "Teacher");
                addTeacherAccountRef.setValue(tea);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onCallback(false);
            }
        });



    }

    public void addMessage(Notification notification, FirebaseCallBack.AddMessageCallBack callBack ) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(NOTIFICATION_CHILD).child(new Date().toString());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myRef.setValue(notification);
                callBack.onCallback(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onCallback(false);
            }
        });
    }

    public void loadNotification(NotifyDestination destination, FirebaseCallBack.AllNotificationCallBack callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query studentQuery = database.getReference(NOTIFICATION_CHILD).limitToLast(1000);
        studentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                List<Notification> notifications = new ArrayList<>();
                for (DataSnapshot notiSnapshot: snapshot.getChildren()) {

                    Notification notification = notiSnapshot.getValue(Notification.class);
                    if (notification.getDesitnation()== destination || destination == NotifyDestination.ALL )
                    {
                        notifications.add(notification);
                    }

                }
                callBack.onCallback(notifications);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void loadClasses(FirebaseCallBack.AllClassName callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query classQuery = database.getReference(CLASS_CHILD).limitToLast(1000);
        classQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Classes> classes = new ArrayList<>();

                for (DataSnapshot classSnapshot: snapshot.getChildren()) {
                    String className = classSnapshot.getKey();
                    Teacher teacher = new Teacher();
                    List<Student> studentList = new ArrayList<>();
                    for (DataSnapshot child: classSnapshot.getChildren()) {
                        Log.w("CLASSS", child.getValue().toString());
                        if (child.getKey().toString().equals("Teacher")) {
                            teacher = new Teacher(child.getValue().toString());
                        }
                        else {
                            Student student = new Student(child.getKey());
                            studentList.add(student);
                        }
                    }
                    Classes classes1 = new Classes(className, teacher, studentList);
                    classes.add(classes1);

                }
                callBack.onCallback(classes);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public  void addTeacherToClass(String className, Teacher teacher, FirebaseCallBack.AddTeacherCallBack callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference addTeacherRef = database.getReference(CLASS_CHILD).child(className).child("Teacher");
        addTeacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                addTeacherRef.setValue(teacher.getId());
                callBack.onCallback(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onCallback(false);
            }
        });
    }

    public void getTeacher(String id, FirebaseCallBack.SingleTeacher callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference getTeacherRef = database.getReference(TEACHER_CHILD).child(id);
        getTeacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Teacher teacher = snapshot.getValue(Teacher.class);
                callBack.onCallback(teacher);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.onCallback(new Teacher());
            }
        });
    }


    /*public void getAllStudentByClass(String className,FirebaseCallBack.AllStudentCallBack callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query studentQuery = database.getReference(STUDENT_CHILD).child("className").child(className);
        studentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Student> students = new ArrayList<>();
                for (DataSnapshot studentSnapshot: snapshot.getChildren()) {
                    Student student = studentSnapshot.getValue(Student.class);
                    students.add(student);
                }
                callBack.onCallback(students);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

     */


}



