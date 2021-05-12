package com.example.contacthandbook.fragment.teachers;

import androidx.lifecycle.ViewModelProvider;

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
import com.example.contacthandbook.model.Teacher;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

public class TeacherFragment extends Fragment {

    MaterialSearchView searchView;
    CommonRecyclerAdapter adapter;
    FirebaseManager firebaseManager = new FirebaseManager(getContext());
    public TeacherFragment(MaterialSearchView searchView) {
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
                Log.w("TAG", newText);
                return false;
            }
        });
        return inflater.inflate(R.layout.teacher_fragment, container, false);
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
        loadList();
        FloatingActionButton fab = getView().findViewById(R.id.fabT);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog(false, new Teacher());
            }
        });
    }

    void showAddDialog(boolean isEdit, Teacher teacher) {
        View dialogLayout = LayoutInflater.from(getContext()).inflate(R.layout.teacher_dialog, null);
        TextInputEditText id = dialogLayout.findViewById(R.id.id_editText_T);
        TextInputEditText name = dialogLayout.findViewById(R.id.name_editText_T);
        TextInputEditText classStr = dialogLayout.findViewById(R.id.class_editText_T);
        id.setText(teacher.getId());
        name.setText(teacher.getName());

        String buttonTitle = !isEdit ? "Add" : "Edit";
        String message = !isEdit ? "Added Teacher" : "Edited Teacher";
        classStr.setText(teacher.getClassName());
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext())
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setHeaderView(dialogLayout)
                .addButton(buttonTitle, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {

                    if (!id.equals("") && !name.equals("") && !classStr.equals("")) {
                        teacher.setId(id.getText().toString());
                        teacher.setName(name.getText().toString());
                        teacher.setClassName(classStr.getText().toString());
                        firebaseManager.addTeacher(teacher, new FirebaseCallBack.AddTeacherCallBack() {
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
                .setDetailsLabel("Loading teacher")
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        RecyclerView recyclerView = getView().findViewById(R.id.TeahcerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseManager.getAllTeacher(new FirebaseCallBack.AllTeacherCallBack() {
            @Override
            public void onCallback(List<Teacher> teachers) {
                adapter = new TeacherFragment.CommonRecyclerAdapter(getContext(), teachers);
                adapter.setOnItemListenerListener(new TeacherFragment.CommonRecyclerAdapter.OnItemListener()
                {
                    @Override
                    public void OnItemClickListener (View view,int position){

                }

                    @Override
                    public void OnItemLongClickListener (View view,int position){
                    Teacher teacher_ = teachers.get(position);
                    CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext())
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                            .setTitle(teacher_.getName())
                            .setMessage("")
                            .addButton("Edit", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                dialog.dismiss();
                                showAddDialog(true, teacher_);
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
            }
        });
    }
        public static class CommonRecyclerAdapter extends RecyclerView.Adapter<TeacherFragment.CommonRecyclerAdapter.ViewHolder> {

            private List<Teacher> mData;
            private LayoutInflater mInflater;
            private TeacherFragment.CommonRecyclerAdapter.OnItemListener onItemListener;

            // data is passed into the constructor
            public CommonRecyclerAdapter(Context context, List<Teacher> data) {
                this.mInflater = LayoutInflater.from(context);
                this.mData = data;
            }

            // inflates the row layout from xml when needed
            @Override
            public TeacherFragment.CommonRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = mInflater.inflate(R.layout.common_row, parent, false);
                view.setOnClickListener(new TeacherFragment.CommonRecyclerAdapter.RV_ItemListener());
                view.setOnLongClickListener(new TeacherFragment.CommonRecyclerAdapter.RV_ItemListener());
                return new TeacherFragment.CommonRecyclerAdapter.ViewHolder(view);
            }

            // binds the data to the TextView in each row
            @Override
            public void onBindViewHolder(TeacherFragment.CommonRecyclerAdapter.ViewHolder holder, int position) {
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

            public void setOnItemListenerListener(TeacherFragment.CommonRecyclerAdapter.OnItemListener listener){
                this.onItemListener = listener;
            }

        }

}