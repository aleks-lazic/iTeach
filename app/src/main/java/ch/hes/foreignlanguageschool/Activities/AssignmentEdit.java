package ch.hes.foreignlanguageschool.Activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ch.hes.foreignlanguageschool.Assignment;
import ch.hes.foreignlanguageschool.AssignmentAsyncTask;
import ch.hes.foreignlanguageschool.DB.DBAssignment;
import ch.hes.foreignlanguageschool.DB.DatabaseHelper;
import ch.hes.foreignlanguageschool.R;

import static ch.hes.foreignlanguageschool.Activities.SyncActivity.teacher;

public class AssignmentEdit extends AppCompatActivity {

    private EditText txtViewTitle;
    private EditText txtViewDescription;
    private TextView txtViewDueDate;
    private TextView txtViewCurrentTeacher;
    private CheckBox checkBoxCalendar;

    private Assignment assignment;

    private DatabaseHelper db;
    private DBAssignment dbAssignment;

    private SimpleDateFormat simpleDateFormat;
    private Date todayDate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_edit);

        String title = getResources().getString(R.string.Assignment);

        setTitle(title);

        txtViewTitle = (EditText) findViewById(R.id.activity_assignment_edit_title);
        txtViewDescription = (EditText) findViewById(R.id.activity_assignment_edit_description);
        txtViewDueDate = (TextView) findViewById(R.id.datePicker);
        txtViewCurrentTeacher = (TextView) findViewById(R.id.teacherName);
        checkBoxCalendar = (CheckBox) findViewById(R.id.checkBoxCalendar);

        txtViewDueDate.setText("");
        txtViewCurrentTeacher.setText(teacher.toString());


        //create database objects
        db = DatabaseHelper.getInstance(this);
        dbAssignment = new DBAssignment(db);

        Intent intent = getIntent();

        /**if we have something in the intent it means that we want to update an assignment
         if our intent contains nothing it means that we want to create a new one
         this is to prevent us from creating new activities because the fields are quite
         the same
         **/
        if (intent.getSerializableExtra("assignment") != null) {
            assignment = (Assignment) intent.getSerializableExtra("assignment");
            txtViewTitle.setText(assignment.getTitle());
            txtViewDescription.setText(assignment.getDescription());
            txtViewDueDate.setText(assignment.getDate());

            cursorAtEnd(txtViewTitle);
            cursorAtEnd(txtViewDescription);


            if (assignment.isAddedToCalendar()) {
                checkBoxCalendar.setChecked(true);
                checkBoxCalendar.setEnabled(false);
            }

            createDatePicker();

        } else {

            createDatePicker();

            //create database objects
            db = DatabaseHelper.getInstance(this);
            dbAssignment = new DBAssignment(db);

        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.save) {

            hideKeyboard();

            //check if the title is filled
            if (txtViewTitle.getText().toString().trim().equals("")) {
                txtViewTitle.setError(getResources().getString(R.string.TitleAlert));
                return super.onOptionsItemSelected(item);
            }

            if (txtViewDueDate.getText().toString().trim().equals("")) {
                txtViewDueDate.setError(getResources().getString(R.string.TitleAlert));
                return super.onOptionsItemSelected(item);
            }

            //get the teacher from DB
            String title = txtViewTitle.getText().toString();
            String description = txtViewDescription.getText().toString();
            String date = txtViewDueDate.getText().toString();
            long idTeacher = teacher.getId();
            int isChecked = 0;
            if (checkBoxCalendar.isChecked()) {
                addToPhoneCalendar(title, description, date);
                isChecked = 1;
            }

            if (assignment != null) {
                //update the current assignment
                dbAssignment.updateAssignmentById(assignment.getId(), title, description, date, isChecked);
                dbAssignment.syncAssigmentToCloud(dbAssignment.getAssignmentById(assignment.getId()));
            } else {
                //insert everything in DB
                dbAssignment.insertValues(title, description, date, idTeacher, isChecked);
                dbAssignment.syncAssigmentToCloud(dbAssignment.getAssignmentById(dbAssignment.getMaxId()));
            }


            finish();

        }

        finish();
        return super.onOptionsItemSelected(item);

    }

    /**
     *
     * The current assignment will be added to the phone's calendar
     * It can be added only once to our calendar
     *
     * @param title
     * @param description
     * @param date
     */
    public void addToPhoneCalendar(String title, String description, String date) {

        long startTime = 0;

        simpleDateFormat = new SimpleDateFormat(("dd.MM.yyyy"));
        try {
            todayDate = simpleDateFormat.parse(date);
            startTime = todayDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", startTime);
        intent.putExtra("allDay", true);
        intent.putExtra("endTime", startTime);
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        startActivity(intent);

    }

    /**
     * Create the date picker
     * for the date selection
     */
    public void createDatePicker() {
        txtViewDueDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(AssignmentEdit.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                    /*      get date   */
                        selectedmonth = selectedmonth + 1;
                        String day = "" + selectedday;
                        String month = "" + selectedmonth;
                        if (selectedday < 10) {
                            day = "0" + selectedday;
                        }
                        if (selectedmonth < 10) {
                            month = "0" + selectedmonth;
                        }
                        txtViewDueDate.setText(day + "." + month + "." + selectedyear);
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle(getResources().getString(R.string.SelectDate));
                mDatePicker.getDatePicker().setMinDate(new Date().getTime());
                mDatePicker.show();
            }
        });
    }

    /**
     * Set the cursot at the end of the editText
     * @param editText
     */
    public void cursorAtEnd(EditText editText) {
        editText.setSelection(editText.getText().length());
    }

    /**
     * hide the keyboard when the users clicks out of the edit texts
     */
    public void hideKeyboard() {
        closeKeyboard(AssignmentEdit.this, txtViewTitle.getWindowToken());
        closeKeyboard(AssignmentEdit.this, txtViewDescription.getWindowToken());
    }

    /**
     * It will close the keyborard for edit text
     * @param c
     * @param windowToken
     */
    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }


}
