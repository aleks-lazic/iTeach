package ch.hes.foreignlanguageschool.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import ch.hes.foreignlanguageschool.AssignmentAsyncTask;
import ch.hes.foreignlanguageschool.DB.DBTeacher;
import ch.hes.foreignlanguageschool.DB.DatabaseHelper;
import ch.hes.foreignlanguageschool.DayAsyncTask;
import ch.hes.foreignlanguageschool.Lecture;
import ch.hes.foreignlanguageschool.LectureAsyncTask;
import ch.hes.foreignlanguageschool.R;
import ch.hes.foreignlanguageschool.StudentAsyncTask;
import ch.hes.foreignlanguageschool.TeacherAsyncTask;



public class SyncActivity extends AppCompatActivity {

    public static DatabaseHelper databaseHelper;
    private DBTeacher dbTeacher ;

    private ProgressBar progressBar;
    private int progression = 0;
    private Handler mHandler = new Handler();

    //boolean values for the asyncTask
    public static boolean assignmentFlag = false;
    public static boolean lectureFlag = false;
    public static boolean teacherFlag = false;
    public static boolean studentFlag = false;
    public static boolean dayFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        dbTeacher = new DBTeacher(databaseHelper);

        deleteDatabase(databaseHelper.getDatabaseName());

        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());

        getCloudRetrieve();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(dbTeacher.getNumberOfRowsInTableTeacher() != 1){
                    checkFlags();
                    Log.d("Aleks", " "+dbTeacher.getNumberOfRowsInTableTeacher());

                    //update the progress bar
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progression);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Intent myIntent = new Intent(getApplicationContext(), NavigationActivity.class);
                startActivity(myIntent);

            }
        }).start();






//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent myIntent = new Intent(getApplicationContext(), NavigationActivity.class);
//                startActivity(myIntent);
//            }
//        });


    }

    private void getCloudRetrieve() {
        new TeacherAsyncTask().execute();
        new DayAsyncTask().execute();
        new StudentAsyncTask().execute();
        new AssignmentAsyncTask().execute();
        new LectureAsyncTask().execute();
    }

    private void checkFlags(){

        if(assignmentFlag) {
            progression += 20;
            assignmentFlag = false;
        }

        if(studentFlag) {
            progression += 20;
            studentFlag = false;
        }

        if(dayFlag) {
            progression += 20;
            dayFlag = false;
        }

        if(teacherFlag) {
            progression += 20;
            teacherFlag = false;
        }

        if(lectureFlag) {
            progression += 20;
            lectureFlag = false;
        }

    }
}
