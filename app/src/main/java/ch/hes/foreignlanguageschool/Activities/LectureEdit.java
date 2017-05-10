package ch.hes.foreignlanguageschool.Activities;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ch.hes.foreignlanguageschool.DAO.Day;
import ch.hes.foreignlanguageschool.DAO.Lecture;
import ch.hes.foreignlanguageschool.DAO.Student;
import ch.hes.foreignlanguageschool.DB.DBDay;
import ch.hes.foreignlanguageschool.DB.DBLecture;
import ch.hes.foreignlanguageschool.DB.DBStudent;
import ch.hes.foreignlanguageschool.DB.DatabaseHelper;
import ch.hes.foreignlanguageschool.R;

import static ch.hes.foreignlanguageschool.R.id.spinnerDay;


public class LectureEdit extends AppCompatActivity {

    private EditText txtTitle;
    private EditText txtDescription;
    private TextView txtTeacherName;
    private Spinner spinnerDays;
    private ListView listViewStudents;
    private EditText editTxtTimePickerFrom;
    private EditText editTxtTimePickerTo;

    private ArrayList<Student> allStudents;
    private ArrayList<Student> students;
    private ArrayAdapter<Student> adapterStudent;
    private Student student;

    private String[] daysOfWeek;
    private ArrayList<Day> days;
    private ArrayAdapter<String> adapterDay;
    private Day day;

    private DatabaseHelper db;
    private DBStudent dbStudent;
    private DBLecture dbLecture;
    private DBDay dbDay;

    private ArrayList<Lecture> lectures;
    private Lecture lecture;


    private SparseBooleanArray checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_edit);

        String title = getResources().getString(R.string.Lecture);

        setTitle(title);

        //fill layout objects
        txtTitle = (EditText) findViewById(R.id.editTxtName);
        txtDescription = (EditText) findViewById(R.id.editTxtDescription);
        txtTeacherName = (TextView) findViewById(R.id.teacherName);
        listViewStudents = (ListView) findViewById(R.id.listViewStudents);
        spinnerDays = (Spinner) findViewById(spinnerDay);
        editTxtTimePickerFrom = (EditText) findViewById(R.id.timePickerFrom);
        editTxtTimePickerTo = (EditText) findViewById(R.id.timePickerTo);

        editTxtTimePickerTo.setText("");
        editTxtTimePickerFrom.setText("");
        txtTeacherName.setText(NavigationActivity.currentTeacher.toString());
        daysOfWeek = getResources().getStringArray(R.array.DaysOfWeekUntilSaturday);

        //create database objects
        db = DatabaseHelper.getInstance(this);
        dbDay = new DBDay(db);
        dbStudent = new DBStudent(db);
        dbLecture = new DBLecture(db);
        allStudents = dbStudent.getAllStudents();

        //get the intent to check if it is an update or a new lecture
        Intent intent = getIntent();


        /**
         * If the intent contains a lecture it means that we want to update the content of an existing lecture
         * If there's no lecture it means that we want to create a lecture
         * Everything is done in the same class because the fields are quite the same
         */
        if (intent.getSerializableExtra("lecture") != null) {

            //fill values for update
            lecture = (Lecture) intent.getSerializableExtra("lecture");

            txtTitle.setText(lecture.getName());
            txtDescription.setText(lecture.getDescription());
            editTxtTimePickerFrom.setText(lecture.getStartTime());
            editTxtTimePickerTo.setText(lecture.getEndTime());

            //create the time picker
            createTimePicker();

            //set the cursor at end
            cursorAtEnd(txtTitle);
            cursorAtEnd(txtDescription);

            //create spinnerDay and set default position
            days = dbDay.getAllDays();
            adapterDay = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, daysOfWeek);
            spinnerDays.setAdapter(adapterDay);
            day = dbDay.getDayById(lecture.getIdDay());
            setDefaultValueSpinner(spinnerDays, day.getId());

            //set listview
            students = dbStudent.getStudentsListByLecture(lecture.getId());

            adapterStudent = new ArrayAdapter<Student>(this, android.R.layout.simple_list_item_multiple_choice, allStudents);
            listViewStudents.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listViewStudents.setAdapter(adapterStudent);
            setListViewHeightBasedOnChildren(listViewStudents);

            //check which will be checked and which not
            for (int i = 0; i < allStudents.size(); i++) {
                if (students.contains(allStudents.get(i))) {
                    listViewStudents.setItemChecked(i, true);
                }
            }

        } else {
            //set days spinner
            days = dbDay.getAllDays();
            adapterDay = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, daysOfWeek);
            spinnerDays.setAdapter(adapterDay);
            setDefaultValueSpinner(spinnerDays, 0);


            hideKeyboard();

            createTimePicker();

            //Fill in the listview with all students
            students = dbStudent.getAllStudents();
            adapterStudent = new ArrayAdapter<Student>(this, android.R.layout.simple_list_item_multiple_choice, students);
            listViewStudents.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listViewStudents.setAdapter(adapterStudent);
            setListViewHeightBasedOnChildren(listViewStudents);

        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.save) {

            hideKeyboard();

            if (!checkEverythingOnSaveClick()) {

                return super.onOptionsItemSelected(item);
            }

            //get students from selected list
            checked = listViewStudents.getCheckedItemPositions();
            students = new ArrayList<Student>();

            for (int i = 0; i < checked.size(); i++) {
                // Item position in adapter
                int position = checked.keyAt(i);
                // Add sport if it is checked i.e.) == TRUE!
                if (checked.valueAt(i)) {
                    student = (Student) adapterStudent.getItem(position);
                    students.add(student);
                }
            }

            //check if minimum one student is selected
            if (!checkSizeSelectedStudents(students.size())) {
                return super.onOptionsItemSelected(item);
            }

            //get the day from spinner
            int position = spinnerDays.getSelectedItemPosition() + 1;
            Log.d("Aleks", ""+position);
            day = (Day) dbDay.getDayById(position);

            //insert everything in DB
            String title = txtTitle.getText().toString();
            String description = txtDescription.getText().toString();
            int idTeacher = NavigationActivity.currentTeacher.getId();
            int idDay = day.getId();
            String timeFrom = editTxtTimePickerFrom.getText().toString();
            String timeTo = editTxtTimePickerTo.getText().toString();

            if (!checkIfLectureAtSameTime(lecture, idDay, timeFrom, timeTo)) {
                return super.onOptionsItemSelected(item);
            }

            if (lecture != null) {
                dbLecture.updateLectureNameAndDescription(lecture.getId(), title, description);
                dbLecture.updateDayTime(lecture.getId(), lecture.getIdDay(), idDay, timeFrom, timeTo);
                dbLecture.deleteLectureFromLectureStudent(lecture.getId());
                dbLecture.addStudentsToLecture(students, lecture.getId());
            } else {
                dbLecture.insertLectureWithTeacherDayAndHoursAndStudents
                        (title, description, idTeacher, idDay, timeFrom, timeTo, students);
            }

            finish();


        }
        finish();
        return super.onOptionsItemSelected(item);

    }


    /**
     * check if endtime is after fromtime
     *
     * @param fromTime
     * @param endTime
     * @return
     */
    public boolean checkEndTimeAfterStartTime(int fromTime, int endTime) {
        if (fromTime > endTime || fromTime == endTime) {
            Toast toast = Toast.makeText(this, " " + getResources().getString(R.string.TimeAlert), Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        return true;
    }

    /**
     * Check if title is filled
     *
     * @param title
     * @return
     */
    public boolean checkTitleFilled(String title) {
        if (title.trim().equals("")) {
            txtTitle.setError(getResources().getString(R.string.TitleAlert));
            return false;
        }
        return true;
    }

    /**
     * Check if endtime and begin time are filled
     *
     * @param fromTime
     * @param endTime
     * @return
     */
    public boolean checkFromTimeAndEndTimeFilled(String fromTime, String endTime) {
        if (fromTime.trim().equals("")) {
            editTxtTimePickerFrom.setError(getResources().getString(R.string.HourAlert));
            return false;
        }

        if (endTime.trim().equals("")) {
            editTxtTimePickerTo.setError(getResources().getString(R.string.HourAlert));
            return false;
        }

        return true;
    }

    /**
     * Check if minimum one student is selected
     *
     * @param size
     * @return
     */
    public boolean checkSizeSelectedStudents(int size) {

        if (size == 0) {
            Toast toast = Toast.makeText(this, " " + getResources().getString(R.string.StudentAlert), Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }

    /**
     * create the time picker
     */
    public void createTimePicker() {
        //open timepicker from on edittextclick
        editTxtTimePickerFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(LectureEdit.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        editTxtTimePickerFrom.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle(getResources().getString(R.string.SelectTime));
                mTimePicker.show();
            }
        });


        //open timepicker to on edittextclick
        editTxtTimePickerTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(LectureEdit.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        editTxtTimePickerTo.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle(getResources().getString(R.string.SelectTime));
                mTimePicker.show();
            }
        });
    }

    /**
     * This method will do all the check we need
     * - Check if the title is fille
     * - Check if end time is after begin time
     * - Check if already one student is selected
     *
     * @return
     */
    public boolean checkEverythingOnSaveClick() {

        if (!checkTitleFilled(txtTitle.getText().toString())) {
            return false;
        }

        if (!checkFromTimeAndEndTimeFilled(editTxtTimePickerFrom.getText().toString(), editTxtTimePickerTo.getText().toString())) {

            return false;

        }


        //check if from time is before "To" time
        int fromTime = Integer.parseInt(editTxtTimePickerFrom.getText().toString().replaceAll(":", ""));
        int toTime = Integer.parseInt(editTxtTimePickerTo.getText().toString().replaceAll(":", ""));

        if (!checkEndTimeAfterStartTime(fromTime, toTime)) {
            return false;
        }

        return true;

    }


    /**
     * Set the default value that will be shown to the user when the user opens the activity
     *
     * @param spinner
     * @param id
     */
    public void setDefaultValueSpinner(final Spinner spinner, final int id) {
        spinner.post(new Runnable() {
            public void run() {
                spinner.setSelection(id - 1);
            }
        });
    }

    /**
     * This method will check if a lecture is already available at the same time
     *
     * @param lecture
     * @param idDay
     * @param beginTime
     * @param endTime
     * @return
     */
    public boolean checkIfLectureAtSameTime(Lecture lecture, int idDay, String beginTime, String endTime) {

        if (lecture != null) {
            lectures = dbLecture.getAllLecturesExceptById(lecture.getId());
        } else {
            lectures = dbLecture.getAllLectures();
        }

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date beginTimeA = null;
        Date endTimeA = null;

        try {
            beginTimeA = dateFormat.parse(beginTime);
            endTimeA = dateFormat.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (Lecture l : lectures
                ) {

            if (l.getId() != idDay) {
                return true;
            }
            Date beginTimeB = null;
            Date endTimeB = null;

            try {
                beginTimeB = dateFormat.parse(l.getStartTime());
                endTimeB = dateFormat.parse(l.getEndTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if ((beginTimeA.before(endTimeB) || beginTimeA.equals(endTimeB)) && (beginTimeB.before(endTimeA) || beginTimeB.equals(endTimeA))
                    && (beginTimeA.before(endTimeB) || beginTimeA.equals(endTimeB)) && (beginTimeB.before(endTimeB) || beginTimeB.equals(endTimeB))) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.conflict), Toast.LENGTH_SHORT).show();
                return false;
            }

        }

        return true;
    }

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

    /**
     * Set the cursot at the end of the editText
     *
     * @param editText
     */
    public void cursorAtEnd(EditText editText) {
        editText.setSelection(editText.getText().length());
    }

    /**
     * Hide the keyboard
     */
    public void hideKeyboard() {
        closeKeyboard(LectureEdit.this, txtTitle.getWindowToken());
        closeKeyboard(LectureEdit.this, txtDescription.getWindowToken());
    }

    /**
     * It will close the keyboard for edit text
     *
     * @param c
     * @param windowToken
     */
    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

}

