package com.example.contacthandbook.fragment.students;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.contacthandbook.CommonFunction;
import com.example.contacthandbook.R;
import com.example.contacthandbook.firebaseManager.FirebaseCallBack;
import com.example.contacthandbook.firebaseManager.FirebaseManager;
import com.example.contacthandbook.model.Common;
import com.example.contacthandbook.model.Student;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StudentFragment extends Fragment {

    MaterialSearchView searchView;
    CommonRecyclerAdapter adapter;
    FirebaseManager firebaseManager = new FirebaseManager(getContext());
    public StudentFragment(MaterialSearchView searchView) {
        this.searchView = searchView;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                adapter.filter(newText.trim());
                return false;
            }
        });

        return inflater.inflate(R.layout.student_fragment, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.search_button);
        searchView.setMenuItem(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel


        loadList();
        FloatingActionButton fab = getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog(false, new Student());
            }
        });

    }

    void showAddDialog(boolean isEdit, Student student) {
        View dialogLayout = LayoutInflater.from(getContext()).inflate(R.layout.student_dialog, null);
        TextInputEditText id = dialogLayout.findViewById(R.id.id_editText);
        TextInputEditText name = dialogLayout.findViewById(R.id.name_editText);
        TextInputEditText classStr = dialogLayout.findViewById(R.id.class_editText);
        id.setText(student.getId());
        name.setText(student.getName());

        String buttonTitle = !isEdit ? "Add" : "Edit";
        String message = !isEdit ? "Added Student" : "Edited Student";
        classStr.setText(student.getClassName());
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext())
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setHeaderView(dialogLayout)
                .addButton(buttonTitle, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {

                    if (!id.equals("") && !name.equals("") && !classStr.equals("")) {
                        student.setId(id.getText().toString());
                        student.setName(name.getText().toString());
                        student.setClassName(classStr.getText().toString());
                        firebaseManager.addStudent(student, new FirebaseCallBack.AddStudentCallBack() {
                            @Override
                            public void onCallback(boolean success) {
                                if (success) {
                                    dialog.dismiss();
                                    CommonFunction.showCommonAlert(getContext(), message, "OK");
                                    loadList();
                                }
                                else {
                                    CommonFunction.showCommonAlert(getContext(), "Something went wrong", "Let me check");
                                }
                            }
                        });
                    }

                });
        builder.show();
    }

    void loadList() {
        //show progressHUD
        KProgressHUD hud = KProgressHUD.create(getContext())
                .setDetailsLabel("Loading students")
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        RecyclerView recyclerView = getView().findViewById(R.id.studentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseManager.getAllStudent(new FirebaseCallBack.AllStudentCallBack() {
            @Override
            public void onCallback(List<Student> students) {
                adapter = new CommonRecyclerAdapter(getContext(), students);
                adapter.setOnItemListenerListener(new CommonRecyclerAdapter.OnItemListener() {
                    @Override
                    public void OnItemClickListener(View view, int position) {

                    }

                    @Override
                    public void OnItemLongClickListener(View view, int position) {
                        Student student_ = students.get(position);
                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext())
                                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                .setTitle(student_.getName())
                                .setMessage("")
                                .addButton("Edit", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                    dialog.dismiss();
                                    showAddDialog(true, student_);
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
                hud.dismiss();
                adapter.notifyDataSetChanged();
            }
        });


    }

    public static class CommonRecyclerAdapter extends RecyclerView.Adapter<CommonRecyclerAdapter.ViewHolder> {

        private List<Student> mData;
        private LayoutInflater mInflater;
        private OnItemListener onItemListener;

        // create arraylist
        private ArrayList<Student> arraymData;

        // data is passed into the constructor
        public CommonRecyclerAdapter(Context context, List<Student> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;

            this.arraymData = new ArrayList<Student>();
            this.arraymData.addAll(mData);
        }

        // class filter
        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            mData.clear();
            if (charText.length() == 0) {
                mData.addAll(arraymData);
            } else {
                for (Student student : arraymData) {
                    if (student.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                        mData.add(student);
                    }
                }
            }
            notifyDataSetChanged();
        }

        // inflates the row layout from xml when needed
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.common_row, parent, false);
            view.setOnClickListener(new RV_ItemListener());
            view.setOnLongClickListener(new RV_ItemListener());
            return new ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Common object = mData.get(position);
            holder.myTextView.setText(object.getName());
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

        public void setOnItemListenerListener(OnItemListener listener){
            this.onItemListener = listener;
        }

    }
}