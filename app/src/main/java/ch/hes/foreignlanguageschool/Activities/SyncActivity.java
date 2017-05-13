package ch.hes.foreignlanguageschool.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import ch.hes.foreignlanguageschool.AssignmentAsyncTask;
import ch.hes.foreignlanguageschool.DB.DBTeacher;
import ch.hes.foreignlanguageschool.DB.DatabaseHelper;
import ch.hes.foreignlanguageschool.DayAsyncTask;
import ch.hes.foreignlanguageschool.LectureAsyncTask;
import ch.hes.foreignlanguageschool.R;
import ch.hes.foreignlanguageschool.StudentAsyncTask;
import ch.hes.foreignlanguageschool.TeacherAsyncTask;

import com.example.patrickclivaz.myapplication.backend.assignmentApi.model.Assignment;
import com.example.patrickclivaz.myapplication.backend.dayApi.model.Day;
import com.example.patrickclivaz.myapplication.backend.lectureApi.model.Lecture;
import com.example.patrickclivaz.myapplication.backend.studentApi.model.Student;
import com.example.patrickclivaz.myapplication.backend.teacherApi.model.Teacher;


public class SyncActivity extends AppCompatActivity {

    public static DatabaseHelper databaseHelper;
    private DBTeacher dbTeacher ;

    private ProgressBar progressBar;
    private int progression = 0;
    private Handler mHandler = new Handler();

    //List for comparing
    public static List<Lecture> lastLectureResult = new ArrayList<Lecture>();
    public static List<Student> lastStudentResult = new ArrayList<Student>();
    public static List<Assignment> lastAssignmentResult = new ArrayList<Assignment>();
    public static List<Day> lastDayResult = new ArrayList<Day>();
    public static List<Teacher> lastTeacherResult = new ArrayList<Teacher>();

    //current teacher
    public static ch.hes.foreignlanguageschool.Teacher teacher;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        //get the progress bar
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //get for DB
        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        dbTeacher = new DBTeacher(databaseHelper);

        //delete current content on DB
        deleteDatabase(databaseHelper.getDatabaseName());

        //retrieve data from the cloud
        getCloudRetrieve();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(dbTeacher.getNumberOfRowsInTableTeacher() != 1){
                    Log.d("Aleks", " "+dbTeacher.getNumberOfRowsInTableTeacher());

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                teacher = dbTeacher.getTeacherById(1);
                Intent myIntent = new Intent(getApplicationContext(), NavigationActivity.class);
                startActivity(myIntent);

            }
        }).start();

    }

    private void getCloudRetrieve() {
        new TeacherAsyncTask().execute();
        new DayAsyncTask().execute();
        new StudentAsyncTask().execute();
        new AssignmentAsyncTask().execute();
        new LectureAsyncTask().execute();
    }



}
