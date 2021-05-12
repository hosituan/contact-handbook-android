package com.example.contacthandbook.firebaseManager;

import com.example.contacthandbook.model.Notification;
import com.example.contacthandbook.model.Student;
import com.example.contacthandbook.model.Teacher;
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

    interface AddStudentCallBack {
        void onCallback(boolean success);
    }


    interface AllTeacherCallBack {
        void onCallback(List<Teacher> teachers);
    }

    interface AddTeacherCallBack {
        void onCallback(boolean success);
    }
    interface AddMessageCallBack {
        void onCallback(boolean success);
    }

    interface AllNotificationCallBack {
        void onCallback(List<Notification> notifications);
    }

}


