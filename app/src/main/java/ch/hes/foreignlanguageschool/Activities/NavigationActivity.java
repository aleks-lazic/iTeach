package ch.hes.foreignlanguageschool.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ch.hes.foreignlanguageschool.AssignmentAsyncTask;
import ch.hes.foreignlanguageschool.DayAsyncTask;
import ch.hes.foreignlanguageschool.LectureAsyncTask;
import ch.hes.foreignlanguageschool.StudentAsyncTask;
import ch.hes.foreignlanguageschool.Teacher;
import ch.hes.foreignlanguageschool.DB.DBDay;
import ch.hes.foreignlanguageschool.DB.DBTeacher;
import ch.hes.foreignlanguageschool.DB.DatabaseHelper;
import ch.hes.foreignlanguageschool.Fragments.AssignmentsFragment;
import ch.hes.foreignlanguageschool.Fragments.CalendarFragment;
import ch.hes.foreignlanguageschool.Fragments.LecturesFragment;
import ch.hes.foreignlanguageschool.Fragments.ProfileFragment;
import ch.hes.foreignlanguageschool.Fragments.SettingsFragment;
import ch.hes.foreignlanguageschool.Fragments.StudentsFragment;
import ch.hes.foreignlanguageschool.Fragments.TodayFragment;
import ch.hes.foreignlanguageschool.R;
import ch.hes.foreignlanguageschool.TeacherAsyncTask;

import static android.os.Build.VERSION_CODES.M;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static Teacher currentTeacher;
    private static NavigationView navigationView;

    //tags used to attach the fragments
    public final String TAG_TODAY = "Today";
    public final String TAG_CALENDAR = "Calendar";
    public final String TAG_ASSIGNMENTS = "Assignments";
    public final String TAG_LECTURES = "Lectures";
    public final String TAG_STUDENTS = "Students";
    public final String TAG_PROFILE = "Profile";
    public final String TAG_SETTINGS = "Settings";
    public String CURRENT_TAG = "";

    //index to identify current nav menu item
    public int navItemIndex = 0;

    //toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private Handler mHandler;

    private SimpleDateFormat simpleDateFormat;
    private String currentDate;

    //Database
    public static DatabaseHelper databaseHelper;
    private DBTeacher dbTeacher;
    private DBDay dbDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLastLanguage();
        setContentView(R.layout.activity_navigation);

        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        getCloudRetrieve();
        checkPermissions();


        //create the current teacher like if he was logged in
        dbTeacher = new DBTeacher(databaseHelper);
        dbDay = new DBDay(databaseHelper);


        if (dbTeacher.getNumberOfRowsInTableTeacher() == 0) {
            Log.d("Aleks", "Je dois rajouter teacher manuellement...");
            dbTeacher.insertValues("Predrag", "Ljubicic", "pedjo.ljubo@mail.srb");
        }

        currentTeacher = dbTeacher.getTeacherById(1);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setNavigationView();


        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.MenuItems);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_TODAY;
            loadHomeFragment();
        }

        Intent intent = getIntent();

        if (intent.getStringExtra("tag") != null) {
            navItemIndex = 6;
            CURRENT_TAG = TAG_SETTINGS;
            loadHomeFragment();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else if (navItemIndex != 0) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_TODAY;
            loadHomeFragment();
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_today) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_TODAY;
        } else if (id == R.id.nav_calendar) {
            navItemIndex = 1;
            CURRENT_TAG = TAG_CALENDAR;
        } else if (id == R.id.nav_assignments) {
            navItemIndex = 2;
            CURRENT_TAG = TAG_ASSIGNMENTS;
        } else if (id == R.id.nav_lectures) {
            navItemIndex = 3;
            CURRENT_TAG = TAG_LECTURES;
        } else if (id == R.id.nav_students) {
            navItemIndex = 4;
            CURRENT_TAG = TAG_STUDENTS;
        } else if (id == R.id.nav_profile) {
            navItemIndex = 5;
            CURRENT_TAG = TAG_PROFILE;
        } else if (id == R.id.nav_settings) {
            CURRENT_TAG = TAG_SETTINGS;
            navItemIndex = 6;
        }
        //Checking if the item in in checked state or not, if not make it checked
        if (menuItem.isChecked()) {
            menuItem.setChecked(false);
        } else {
            menuItem.setChecked(true);
        }

        menuItem.setChecked(true);
        loadHomeFragment();
        return true;
    }

    /**
     * It will load the good fragment depending on the drawer menu
     */
    public void loadHomeFragment() {

        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
//            toggleFab();
            return;
        }

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                //to replace the current fragment with another one
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
//        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();

    }

    /**
     * Get the good fragment depending on the drawer menu
     *
     * @return
     */
    public Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                TodayFragment todayFragment = new TodayFragment();
                return todayFragment;
            case 1:
                // calendar
                CalendarFragment calendarFragment = new CalendarFragment();
                return calendarFragment;
            case 2:
                // assignments
                AssignmentsFragment assignmentsFragment = new AssignmentsFragment();
                return assignmentsFragment;
            case 3:
                // Lectures
                LecturesFragment lecturesFragment = new LecturesFragment();
                return lecturesFragment;

            case 4:
                // students
                StudentsFragment studentsFragment = new StudentsFragment();
                return studentsFragment;
            case 5:
//                // profile
                ProfileFragment profileFragment = new ProfileFragment();
                return profileFragment;
            case 6:
                // settings
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            default:
                return new TodayFragment();
        }
    }

    public void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    /**
     * change the toolbar title depending on the drawer menu
     */
    public void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    /**
     * Check that the user has all permissions
     */
    private void checkPermissions() {
        int PERMISSION_ALL = 1;

        //List of all the permissions needed by the app
        String[] permissions = new String[]{
                Manifest.permission.WRITE_CALENDAR,
        };

        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
        }
    }

    /**
     * When the user closes the app, it will save the last language
     * Then at the next opening of the app, the last language will be loaded
     */
    private void loadLastLanguage() {
        String language = PreferenceManager.getDefaultSharedPreferences(this).getString("LANGUAGE", "en");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    /**
     * Check the permissions for the calendar (when we want to add an assignment to the phone's calendar
     *
     * @param context
     * @param permissions
     * @return
     */
    private static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Modify the navigation view if needed
     * (name and email in the drawer menu)
     */
    public static void setNavigationView() {

        View hView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView) hView.findViewById(R.id.name);
        nav_user.setText(currentTeacher.toString());

        TextView nav_mail = (TextView) hView.findViewById(R.id.mail);
        nav_mail.setText(currentTeacher.getMail());
    }


    private void getCloudRetrieve() {
        new DayAsyncTask().execute();
//        new TeacherAsyncTask().execute();
        new StudentAsyncTask().execute();
        new AssignmentAsyncTask().execute();
        new LectureAsyncTask().execute();
    }


}
