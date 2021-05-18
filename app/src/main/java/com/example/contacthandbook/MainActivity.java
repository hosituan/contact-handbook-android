package com.example.contacthandbook;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.contacthandbook.fragment.classes.ClassFragment;
import com.example.contacthandbook.fragment.home.HomeFragment;
import com.example.contacthandbook.fragment.notification.NotificationFragment;
import com.example.contacthandbook.fragment.students.StudentFragment;
import com.example.contacthandbook.fragment.teachers.TeacherFragment;
import com.example.contacthandbook.model.User;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;



public class MainActivity  extends AppCompatActivity  {
    private static final String PREFS_NAME = "USER_INFO";
    Toolbar toolbar;
    PrimaryDrawerItem logout = new PrimaryDrawerItem().withName("Logout");
    Drawer drawer;
    MaterialSearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchView = findViewById(R.id.search_view);
        User user = getSavedInfo();
        Fragment homeFrag = new HomeFragment();
        loadFragment(homeFrag);
        getSupportActionBar().setTitle("Home");
        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(
                        new ProfileDrawerItem().withName(user.getName()).withEmail(user.getRole())
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                    @Override
                    public boolean onProfileImageClick(View view, IProfile profile, boolean current) {
                        return false;
                    }

                    @Override
                    public boolean onProfileImageLongClick(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .build();


        if (user.getRole().equals("Admin")) {
            adminAction(headerResult);
        }
        if (user.getRole().equals("Student")) {
            studentAction(headerResult);
        }
        if (user.getRole().equals("Parent")) {
            parentAction(headerResult);
        }
        if (user.getRole().equals("Teacher")) {
            teacherAction(headerResult);
        }



    }


    @Override
    public void onBackPressed()
    {
        // code here to show dialog

        super.onBackPressed();  // optional depending on your needs
        Fragment homeFrag = new HomeFragment();
        loadFragment(homeFrag);
        getSupportActionBar().setTitle("Home");
    }

    public User getSavedInfo() {
        User user = new User();
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        user.setName(sharedPref.getString("name", "Contact Handbook"));
        user.setRole(sharedPref.getString("role", "student"));
        return  user;
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }


    public void studentAction(AccountHeader header) {
        PrimaryDrawerItem home = new PrimaryDrawerItem().withName("Home");
        PrimaryDrawerItem notificationList = new PrimaryDrawerItem().withName("Notifications");
        PrimaryDrawerItem classList = new PrimaryDrawerItem().withName("Classes");
        PrimaryDrawerItem feedback = new PrimaryDrawerItem().withName("Feedback");
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(header)
                .addDrawerItems(
                        home,
                        new DividerDrawerItem(),
                        notificationList,
                        classList,
                        new DividerDrawerItem(),
                        feedback,
                        new DividerDrawerItem(),
                        logout
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem == home) {
                            Fragment homeFrag = new HomeFragment();
                            loadFragment(homeFrag);
                            getSupportActionBar().setTitle("Home");
                        }
                        if (drawerItem == logout) {
                            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(MainActivity.this)
                                    .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                    .setTitle("Are you sure?")
                                    .setMessage("Logout and clear your local information?")
                                    .addButton("YES", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                        SharedPreferences settings = (SharedPreferences) getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                        settings.edit().clear().commit();
                                        dialog.dismiss();
                                        finish();
                                    })
                                    .addButton("CANCEL", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                        dialog.dismiss();
                                    });

                            builder.show();
                            return true;
                        }

                        if (drawerItem == notificationList){
                            toolbar.setTitle("Notifications");
                            loadFragment(new NotificationFragment());
                        }


                        return false;
                    }
                })
                .build();
    }

    public void adminAction(AccountHeader header) {
        PrimaryDrawerItem home = new PrimaryDrawerItem().withName("Home");
        PrimaryDrawerItem studentList = new PrimaryDrawerItem().withName("Students");
        PrimaryDrawerItem teacherList = new PrimaryDrawerItem().withName("Teachers");
        PrimaryDrawerItem notificationList = new PrimaryDrawerItem().withName("Notifications");
        PrimaryDrawerItem classList = new PrimaryDrawerItem().withName("Classes");
        PrimaryDrawerItem feedback = new PrimaryDrawerItem().withName("Feedback");
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(header)
                .addDrawerItems(
                        home,
                        studentList,
                        teacherList,
                        new DividerDrawerItem(),
                        notificationList,
                        classList,
                        new DividerDrawerItem(),
                        feedback,
                        new DividerDrawerItem(),
                        logout
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem == home) {
                            Fragment homeFrag = new HomeFragment();
                            loadFragment(homeFrag);
                            getSupportActionBar().setTitle("Home");
                        }
                        if (drawerItem == logout) {
                            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(MainActivity.this)
                                    .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                    .setTitle("Are you sure?")
                                    .setMessage("Logout and clear your local information?")
                                    .addButton("YES", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                        SharedPreferences settings = (SharedPreferences) getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                        settings.edit().clear().commit();
                                        dialog.dismiss();
                                        finish();
                                    })
                                    .addButton("CANCEL", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                        dialog.dismiss();
                                    });

                            builder.show();
                            return true;
                        }
                        if (drawerItem == studentList) {
                            toolbar.setTitle("Student List");
                            loadFragment(new StudentFragment(searchView));
                        }
                        if (drawerItem == teacherList){
                            toolbar.setTitle("Teacher List");
                            loadFragment(new TeacherFragment(searchView));
                        }

                        if (drawerItem == notificationList){
                            toolbar.setTitle("Notifications");
                            loadFragment(new NotificationFragment());
                        }

                        if (drawerItem == classList) {
                            toolbar.setTitle("Class");
                            loadFragment(new ClassFragment());
                        }


                        return false;
                    }
                })
                .build();
    }


    public void parentAction(AccountHeader header) {
        PrimaryDrawerItem home = new PrimaryDrawerItem().withName("Home");
        PrimaryDrawerItem notificationList = new PrimaryDrawerItem().withName("Notifications");
        PrimaryDrawerItem classList = new PrimaryDrawerItem().withName("Classes");
        PrimaryDrawerItem feedback = new PrimaryDrawerItem().withName("Feedback");
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(header)
                .addDrawerItems(
                        home,
                        new DividerDrawerItem(),
                        notificationList,
                        classList,
                        new DividerDrawerItem(),
                        feedback,
                        new DividerDrawerItem(),
                        logout
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem == home) {
                            Fragment homeFrag = new HomeFragment();
                            loadFragment(homeFrag);
                            getSupportActionBar().setTitle("Home");
                        }
                        if (drawerItem == logout) {
                            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(MainActivity.this)
                                    .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                    .setTitle("Are you sure?")
                                    .setMessage("Logout and clear your local information?")
                                    .addButton("YES", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                        SharedPreferences settings = (SharedPreferences) getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                        settings.edit().clear().commit();
                                        dialog.dismiss();
                                        finish();
                                    })
                                    .addButton("CANCEL", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                        dialog.dismiss();
                                    });

                            builder.show();
                            return true;
                        }

                        if (drawerItem == notificationList){
                            toolbar.setTitle("Notifications");
                            loadFragment(new NotificationFragment());
                        }


                        return false;
                    }
                })
                .build();
    }

    public void teacherAction(AccountHeader header) {
        PrimaryDrawerItem home = new PrimaryDrawerItem().withName("Home");
        PrimaryDrawerItem notificationList = new PrimaryDrawerItem().withName("Notifications");
        PrimaryDrawerItem classList = new PrimaryDrawerItem().withName("Classes");
        PrimaryDrawerItem feedback = new PrimaryDrawerItem().withName("Feedback");
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(header)
                .addDrawerItems(
                        home,
                        new DividerDrawerItem(),
                        notificationList,
                        classList,
                        new DividerDrawerItem(),
                        feedback,
                        new DividerDrawerItem(),
                        logout
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem == home) {
                            Fragment homeFrag = new HomeFragment();
                            loadFragment(homeFrag);
                            getSupportActionBar().setTitle("Home");
                        }
                        if (drawerItem == logout) {
                            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(MainActivity.this)
                                    .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                    .setTitle("Are you sure?")
                                    .setMessage("Logout and clear your local information?")
                                    .addButton("YES", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                        SharedPreferences settings = (SharedPreferences) getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                        settings.edit().clear().commit();
                                        dialog.dismiss();
                                        finish();
                                    })
                                    .addButton("CANCEL", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                                        dialog.dismiss();
                                    });

                            builder.show();
                            return true;
                        }

                        if (drawerItem == notificationList){
                            toolbar.setTitle("Notifications");
                            loadFragment(new NotificationFragment());
                        }


                        return false;
                    }
                })
                .build();
    }

}