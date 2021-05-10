package com.example.contacthandbook.firebaseManager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.contacthandbook.MainActivity;
import com.example.contacthandbook.fragment.home.HomeFragment;
import com.example.contacthandbook.model.Notification;
import com.example.contacthandbook.model.Student;
import com.example.contacthandbook.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FirebaseManager {
    private static final String TAG = "Firebase manager";
    private static final String USERS_CHILD = "users";
    private static final String STUDENT_CHILD = "students";
    private static final String CLASS_CHILD = "classes";
    private static final String TEACHER_CHILD = "teachers";
    private static final String NOTIFICATION_CHILD = "notifications";
    private static final String FEEDBACK_CHILD = "feedback";
    private static final String CLASSES_CHILD = "classes";

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

    }


    public void addMessage(Notification notification, FirebaseCallBack.AddMessageCallBack callBack ) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(NOTIFICATION_CHILD).child(new Date().toString());

        myRef.addValueEventListener(new ValueEventListener() {
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

    public void loadNotification(FirebaseCallBack.AllNotificationCallBack callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query studentQuery = database.getReference(NOTIFICATION_CHILD).limitToLast(1000);
        studentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Notification> notifications = new ArrayList<>();
                for (DataSnapshot notiSnapshot: snapshot.getChildren()) {
                    Notification notification = notiSnapshot.getValue(Notification.class);
                    notifications.add(notification);
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

}



