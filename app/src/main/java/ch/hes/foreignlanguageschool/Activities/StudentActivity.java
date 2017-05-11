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

import ch.hes.foreignlanguageschool.Adapters.CustomAdapterLecture;
import ch.hes.foreignlanguageschool.Student;
import ch.hes.foreignlanguageschool.DB.DBStudent;
import ch.hes.foreignlanguageschool.DB.DatabaseHelper;
import ch.hes.foreignlanguageschool.R;


public class StudentActivity extends AppCompatActivity {
    private Student student;
    private DatabaseHelper db;
    private DBStudent dbStudent;

    private TextView address;
    private TextView country;
    private TextView mail;
    private TextView startDate;
    private TextView endDate;
    private ListView listView_lectures;

    private CustomAdapterLecture adapterLecture;

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton fab;

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
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
        setContentView(R.layout.activity_student);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        //database
        db = DatabaseHelper.getInstance(this);
        dbStudent = new DBStudent(db);

        student = (Student) intent.getSerializableExtra("student");
        setTitle(student.getMail());

        endDate = (TextView) findViewById(R.id.student_enddate);
        country = (TextView) findViewById(R.id.student_country);
        mail = (TextView) findViewById(R.id.student_mail);
        address = (TextView) findViewById(R.id.student_address);
        startDate = (TextView) findViewById(R.id.student_startdate);
        listView_lectures = (ListView) findViewById(R.id.student_list_lectures);

        address.setText(student.getAddress());
        country.setText(student.getCountry());
        mail.setText(student.getMail());
        startDate.setText(student.getStartDate());
        endDate.setText(student.getEndDate());

        //get lectures list and set it
        adapterLecture = new CustomAdapterLecture(this, student.getLecturesList());
        listView_lectures.setAdapter(adapterLecture);
        setListViewHeightBasedOnChildren(listView_lectures);
        listView_lectures.setFocusable(false);

        fab = (FloatingActionButton) findViewById(R.id.fab_student);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentActivity.this, StudentEdit.class);
                intent.putExtra("student", student);
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            dbStudent.deleteStudent(student.getId());
            finish();
            Toast toast = Toast.makeText(this, student.toString() + " " + getResources().getString(R.string.Student) + " " + getResources().getString(R.string.DeletedSuccess), Toast.LENGTH_SHORT);
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
    protected void onResume() {
        super.onResume();
        updateDisplay();
    }

    /**
     * Update the display when the user comes back in the activity
     */
    public void updateDisplay() {
        student = dbStudent.getStudentById(student.getId());
        address.setText(student.getAddress());
        country.setText(student.getCountry());
        mail.setText(student.getMail());
        startDate.setText(student.getStartDate());
        endDate.setText(student.getEndDate());

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(student.toString());

    }

}
