package com.example.contacthandbook.fragment.classes;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.contacthandbook.LoginActivity;
import com.example.contacthandbook.R;
import com.example.contacthandbook.firebaseManager.FirebaseCallBack;
import com.example.contacthandbook.firebaseManager.FirebaseManager;
import com.example.contacthandbook.fragment.students.StudentFragment;
import com.example.contacthandbook.model.Student;
import com.example.contacthandbook.model.Teacher;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClassFragment extends Fragment {
    ClassFragment.CommonRecyclerAdapter adapter;

    FirebaseManager firebaseManager = new FirebaseManager(getContext());
    public ClassFragment() {

    }


    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
        FloatingActionButton fab = getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog("", true);
            }
        });

        loadList();

    }


    public void loadList() {
        RecyclerView recyclerView = getView().findViewById(R.id.studentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseManager.loadClasses(new FirebaseCallBack.AllClassName() {
            @Override
            public void onCallback(List<String> classes) {
                adapter = new ClassFragment.CommonRecyclerAdapter(getContext(), classes);
                adapter.setOnItemListenerListener(new StudentFragment.CommonRecyclerAdapter.OnItemListener() {
                    @Override
                    public void OnItemClickListener(View view, int position) {

                    }

                    @Override
                    public void OnItemLongClickListener(View view, int position) {
                        String className = classes.get(position);
                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext())
                                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                .setTitle(className)
                                .setMessage("")
                                .addButton("Edit", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                    dialog.dismiss();
                                    showDialog(className, false);
                                })
                                .addButton("Delete", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                    dialog.dismiss();
                                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_LONG).show();
                                })
                                .addButton("CANCEL", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                    dialog.dismiss();
                                });
                        builder.show();
                    }
                });
                recyclerView.setAdapter(adapter);
//                hud.dismiss();
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void showDialog(String className, boolean isAdd) {
        firebaseManager.getAllTeacher(new FirebaseCallBack.AllTeacherCallBack() {
            @Override
            public void onCallback(List<Teacher> teachers) {
                View dialogLayout = LayoutInflater.from(getContext()).inflate(R.layout.class_dialog, null);
                TextInputEditText name = dialogLayout.findViewById(R.id.name_editText);
                name.setText(className);
                Spinner teacherSpinner = dialogLayout.findViewById(R.id.spinner_teacher);
                List<String> teacherList = new ArrayList<>();
                for (int i = 0; i< teachers.size(); i++) {
                    teacherList.add(teachers.get(i).getName());
                }
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                        (getContext(), android.R.layout.simple_spinner_dropdown_item, teacherList);
                teacherSpinner.setAdapter(spinnerArrayAdapter);
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext())
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                        .setHeaderView(dialogLayout)
                        .addButton("OK", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                            String teacherName = teacherSpinner.getSelectedItem().toString();
                            int index = teacherList.indexOf(teacherName);
                            firebaseManager.addTeacherToClass(name.getText().toString(), teachers.get(index));
                        });

                builder.show();
            }
        });


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_class, container, false);
    }

    public static class CommonRecyclerAdapter extends RecyclerView.Adapter<ClassFragment.CommonRecyclerAdapter.ViewHolder> {

        private List<String> mData;
        private LayoutInflater mInflater;
        private StudentFragment.CommonRecyclerAdapter.OnItemListener onItemListener;

        // create arraylist
        private ArrayList<String> arraymData;

        // data is passed into the constructor
        public CommonRecyclerAdapter(Context context, List<String> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;

            this.arraymData = new ArrayList<String>();
            this.arraymData.addAll(mData);
        }

        // class filter
        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            mData.clear();
            if (charText.length() == 0) {
                mData.addAll(arraymData);
            } else {
            }
            notifyDataSetChanged();
        }

        // inflates the row layout from xml when needed
        @Override
        public ClassFragment.CommonRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.common_row, parent, false);
            view.setOnClickListener(new ClassFragment.CommonRecyclerAdapter.RV_ItemListener());
            view.setOnLongClickListener(new ClassFragment.CommonRecyclerAdapter.RV_ItemListener());
            return new ClassFragment.CommonRecyclerAdapter.ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(ClassFragment.CommonRecyclerAdapter.ViewHolder holder, int position) {
            //Common object = mData.get(position);
            holder.myTextView.setText(mData.get(position));
            holder.itemView.setId(position);
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
        }


        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder  {
            TextView myTextView;

            ViewHolder(View itemView) {
                super(itemView);
                myTextView = itemView.findViewById(R.id.title);
            }

        }

        public  interface OnItemListener{
            void OnItemClickListener(View view, int position);
            void OnItemLongClickListener(View view, int position);
        }

        class RV_ItemListener implements View.OnClickListener, View.OnLongClickListener{

            @Override
            public void onClick(View view) {
                if (onItemListener != null){
                    onItemListener.OnItemClickListener(view, view.getId());
                }
            }
            @Override
            public boolean onLongClick(View view) {
                if (onItemListener != null){
                    onItemListener.OnItemLongClickListener(view,view.getId());
                }
                return true;
            }
        }

        public void setOnItemListenerListener(StudentFragment.CommonRecyclerAdapter.OnItemListener listener){
            this.onItemListener = listener;
        }

    }
}