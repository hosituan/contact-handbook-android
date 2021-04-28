package com.example.contacthandbook.firebaseManager;

import com.example.contacthandbook.model.Student;
import com.example.contacthandbook.model.User;

import java.util.List;

public interface FirebaseCallBack {
    interface UserCallBack {
        void onCallback(User user);

    }

    interface ValidateCallBack {
        void onCallBack(boolean isValidate, User user);
    }

    interface AllStudentCallBack {
        void onCallback(List<Student> students);
    }
}


