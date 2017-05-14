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
import android.widget.TextView;
import android.widget.Toast;

import ch.hes.foreignlanguageschool.Assignment;
import ch.hes.foreignlanguageschool.AssignmentAsyncTask;
import ch.hes.foreignlanguageschool.DB.DBAssignment;
import ch.hes.foreignlanguageschool.DB.DatabaseHelper;
import ch.hes.foreignlanguageschool.R;

public class AssignmentActivity extends AppCompatActivity {

    private Assignment assignment;

    private TextView description;
    private TextView date;
    private TextView teacher;

    private DatabaseHelper db;
    private DBAssignment dbAssignment;

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = DatabaseHelper.getInstance(getApplicationContext());
        dbAssignment = new DBAssignment(db);

        Intent intent = getIntent();

        assignment = (Assignment) intent.getSerializableExtra("assignment");
        setTitle(assignment.getTitle());

        description = (TextView) findViewById(R.id.assignment_description);
        date = (TextView) findViewById(R.id.assignment_date);
        teacher = (TextView) findViewById(R.id.assignment_teacher);

        description.setText(assignment.getDescription());
        date.setText(assignment.getDate());
        teacher.setText(assignment.getTeacher().getFirstName() + " " + assignment.getTeacher().getLastName());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_assignment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AssignmentEdit.class);
                intent.putExtra("assignment", assignment);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_delete) {
            dbAssignment.deleteAssignmentById(assignment.getId());
            dbAssignment.deleteAssignmentInCloud(assignment);
            finish();
            Toast toast = Toast.makeText(this, assignment.toString() + " " + getResources().getString(R.string.Assignment) + " " + getResources().getString(R.string.DeletedSuccess), Toast.LENGTH_SHORT);
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
    public void onRestart() {
        super.onRestart();
    }


    @Override
    public void onResume() {
        super.onResume();
        updateDisplay();
    }

    /**
     * Update display when the user comes back in this activity
     */
    public void updateDisplay() {
        assignment = dbAssignment.getAssignmentById(assignment.getId());
        description.setText(assignment.getDescription());
        date.setText(assignment.getDate());

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(assignment.getTitle());

    }
}
