package ch.hes.foreignlanguageschool.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ch.hes.foreignlanguageschool.Adapters.CustomAdapterStudent;
import ch.hes.foreignlanguageschool.DAO.Day;
import ch.hes.foreignlanguageschool.DAO.Lecture;
import ch.hes.foreignlanguageschool.DAO.Student;
import ch.hes.foreignlanguageschool.DB.DBDay;
import ch.hes.foreignlanguageschool.DB.DBLecture;
import ch.hes.foreignlanguageschool.DB.DBStudent;
import ch.hes.foreignlanguageschool.DB.DatabaseHelper;
import ch.hes.foreignlanguageschool.R;

public class LectureActivity extends AppCompatActivity {

    private ListView listView_students;
    private Lecture lecture;

    private TextView description;
    private TextView teacher;
    private TextView startTime;
    private TextView endTime;
    private TextView txtDay;

    private DatabaseHelper db;
    private DBLecture dbLecture;
    private DBDay dbDay;
    private DBStudent dbStudent;

    private Day day;
    private String[] daysOfWeek;
    private ArrayList<Student> students;
    private CustomAdapterStudent studentsAdapter;

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    /**
     * Method for Setting the Height of the ListView dynamically.
     * Hack to fix the issue of not showing all the items of the ListView
     * when placed inside a ScrollView
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, CollapsingToolbarLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        lecture = (Lecture) intent.getSerializableExtra("lecture");
        description = (TextView) findViewById(R.id.activity_lecture_description);
        teacher = (TextView) findViewById(R.id.activity_lecture_teacher);
        startTime = (TextView) findViewById(R.id.activity_lecture_starttime);
        endTime = (TextView) findViewById(R.id.activity_lecture_endtime);
        txtDay = (TextView) findViewById(R.id.activity_lecture_day);
        listView_students = (ListView) findViewById(R.id.activity_lecture_students);
        //database
        db = DatabaseHelper.getInstance(this);
        dbLecture = new DBLecture(db);
        dbDay = new DBDay(db);
        dbStudent = new DBStudent(db);

        //set textviews
        setTitle(lecture.toString());
        description.setText(lecture.getDescription());
        teacher.setText(lecture.getTeacher().getFirstName() + " " + lecture.getTeacher().getLastName());
        startTime.setText(lecture.getStartTime());
        endTime.setText(lecture.getEndTime());

        daysOfWeek = getResources().getStringArray(R.array.DaysOfWeekUntilSaturday);
        day = dbDay.getDayById(lecture.getIdDay());
        txtDay.setText(daysOfWeek[(day.getId() - 1)]);

        //set the students from the lecture
        students = lecture.getStudentsList();
        studentsAdapter = new CustomAdapterStudent(this, students);
        listView_students.setAdapter(studentsAdapter);
        setListViewHeightBasedOnChildren(listView_students);
        listView_students.setFocusable(false);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_lecture_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LectureActivity.this, LectureEdit.class);
                intent.putExtra("lecture", lecture);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_delete) {
            dbLecture.deleteLecture(lecture.getId());
            finish();
            Toast toast = Toast.makeText(this, lecture.toString() + " " + getResources().getString(R.string.Lecture) + " " + getResources().getString(R.string.DeletedSuccess), Toast.LENGTH_SHORT);
            toast.show();
        } else {
            finish();
            return true;
        }

        return true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_menu, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDisplay();
    }

    /**
     * Update display when the user comes back to the activity
     */
    public void updateDisplay() {
        lecture = dbLecture.getLectureByIdForUpdate(lecture.getId());
        lecture.setStudentsList(dbStudent.getStudentsListByLecture(lecture.getId()));

        description.setText(lecture.getDescription());
        startTime.setText(lecture.getStartTime());
        endTime.setText(lecture.getEndTime());
        daysOfWeek = getResources().getStringArray(R.array.DaysOfWeekUntilSaturday);
        day = dbDay.getDayById(lecture.getIdDay());
        txtDay.setText(daysOfWeek[(day.getId() - 1)]);
        students = lecture.getStudentsList();
        studentsAdapter = new CustomAdapterStudent(this, students);
        listView_students.setAdapter(studentsAdapter);
        setListViewHeightBasedOnChildren(listView_students);
        listView_students.setFocusable(false);


        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(lecture.toString());


    }


}
