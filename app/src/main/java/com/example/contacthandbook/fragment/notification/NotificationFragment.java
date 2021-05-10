package com.example.contacthandbook.fragment.notification;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.contacthandbook.CommonFunction;
import com.example.contacthandbook.R;
import com.example.contacthandbook.firebaseManager.FirebaseCallBack;
import com.example.contacthandbook.firebaseManager.FirebaseManager;
import com.example.contacthandbook.model.Notification;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.contacthandbook.model.NotifyDestination;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.List;

public class NotificationFragment extends Fragment {

    FirebaseManager firebaseManager = new FirebaseManager(getContext());
    NotificationAdapter adapter;
    public NotificationFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel

        FloatingActionButton fab = getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddMessage();
            }
        });
        loadList();

    }

    void showAddMessage() {
        View dialogLayout = LayoutInflater.from(getContext()).inflate(R.layout.add_notification_dialog, null);
        Spinner spinnerDestination = dialogLayout.findViewById(R.id.spinner_destination);

        final List<String> destinations = NotifyDestination.ALL.getAllCase();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_spinner_dropdown_item, destinations);
        spinnerDestination.setAdapter(spinnerArrayAdapter);

        TextInputEditText titleEditText = dialogLayout.findViewById(R.id.title_editText);
        TextInputEditText messageEditText = dialogLayout.findViewById(R.id.message_editText);

        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext())
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setHeaderView(dialogLayout)
                .addButton("SEND", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                    if (!titleEditText.getText().toString().equals("") && !messageEditText.getText().toString().equals("")) {
                        String destination = spinnerDestination.getSelectedItem().toString();
                        Notification notification = new Notification(NotifyDestination.valueOf(destination), titleEditText.getText().toString(), messageEditText.getText().toString());
                        firebaseManager.addMessage(notification, new FirebaseCallBack.AddMessageCallBack() {
                            @Override
                            public void onCallback(boolean success) {
                                if (success) {
                                    dialog.dismiss();
                                    CommonFunction.showCommonAlert(getContext(), "Message Sent", "OK");
                                    loadList();
                                }
                                else {
                                    CommonFunction.showCommonAlert(getContext(), "Something went wrong", "Let me check");
                                }
                            }
                        });
                    }
                })
                .addButton("CANCEL", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, ((dialog, which) -> {
                    dialog.dismiss();
                }));

        builder.show();
    }

    void loadList() {
        //show progressHUD
        KProgressHUD hud = KProgressHUD.create(getContext())
                .setDetailsLabel("Loading notification")
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        RecyclerView recyclerView = getView().findViewById(R.id.notificationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseManager.loadNotification(new FirebaseCallBack.AllNotificationCallBack() {
            @Override
            public void onCallback(List<Notification> notifications) {
                adapter = new NotificationAdapter(getContext(), notifications);
                adapter.setOnItemListenerListener(new NotificationAdapter.OnItemListener() {
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


}