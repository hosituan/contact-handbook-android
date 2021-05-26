package com.example.contacthandbook.fragment.home;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.contacthandbook.R;
import com.example.contacthandbook.firebaseManager.FirebaseCallBack;
import com.example.contacthandbook.firebaseManager.FirebaseManager;
import com.example.contacthandbook.fragment.notification.NotificationAdapter;
import com.example.contacthandbook.model.Notification;
import com.example.contacthandbook.model.NotifyDestination;
import com.example.contacthandbook.model.User;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private static final String PREFS_NAME = "USER_INFO";
    FirebaseManager firebaseManager = new FirebaseManager(getContext());
    HomeAdapter adapter;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel

        loadList();
    }

    void loadList() {
        NotifyDestination role = NotifyDestination.ALL;
        User user = getSavedInfo();
        if (user.getRole().equals("Admin")) {
            role = NotifyDestination.ALL;
        }
        if (user.getRole().equals("Student")) {
            return;
        }
        if (user.getRole().equals("Parent")) {
            role=NotifyDestination.PARENT;
        }
        if (user.getRole().equals("Teacher")) {
            role=NotifyDestination.TEACHER;
        }
        //show progressHUD
        KProgressHUD hud = KProgressHUD.create(getContext())
                .setDetailsLabel("Loading notification")
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        RecyclerView recyclerView = getView().findViewById(R.id.homeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseManager.loadNotification(role, new FirebaseCallBack.AllNotificationCallBack() {
            @Override
            public void onCallback(List<Notification> notifications) {
                adapter = new HomeAdapter(getContext(), notifications);
                adapter.setOnItemListenerListener(new HomeAdapter.OnItemListener() {
                    @Override
                    public void OnItemClickListener(View view, int position) {

                    }

                    @Override
                    public void OnItemLongClickListener(View view, int position) {

                    }
                });
                recyclerView.setAdapter(adapter);
                hud.dismiss();
            }
        });
    }
    public User getSavedInfo() {
        User user = new User();
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        user.setName(sharedPref.getString("name", "Contact Handbook"));
        user.setRole(sharedPref.getString("role", "student"));
        return  user;
    }
}