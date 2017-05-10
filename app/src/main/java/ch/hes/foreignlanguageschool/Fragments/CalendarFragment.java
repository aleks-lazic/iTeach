package ch.hes.foreignlanguageschool.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ch.hes.foreignlanguageschool.Activities.LectureActivity;
import ch.hes.foreignlanguageschool.Adapters.CustomAdapterLecture;
import ch.hes.foreignlanguageschool.DAO.Lecture;
import ch.hes.foreignlanguageschool.DAO.Student;
import ch.hes.foreignlanguageschool.DB.DBLecture;
import ch.hes.foreignlanguageschool.DB.DBStudent;
import ch.hes.foreignlanguageschool.DB.DatabaseHelper;
import ch.hes.foreignlanguageschool.R;

public class CalendarFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<Lecture> lectures;
    private String mParam1;
    private String mParam2;

    // Database
    private DatabaseHelper db;
    private DBLecture dbLecture;
    private ListView listView_lectures;

    private String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

    private CustomAdapterLecture adapterLecture;


    private OnFragmentInteractionListener mListener;

    public CalendarFragment() {
        // Required empty public constructor
    }

    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Method for Setting the Height of the ListView dynamically.
     * Hack to fix the issue of not showing all the items of the ListView
     * when placed inside a ScrollView
     * @param mListView
     */
    public static void setDynamicHeight(ListView mListView) {
        ListAdapter mListAdapter = mListView.getAdapter();
        if (mListAdapter == null) {
            // when adapter is null
            return;
        }
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < mListAdapter.getCount(); i++) {
            View listItem = mListAdapter.getView(i, null, mListView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
        mListView.setLayoutParams(params);
        mListView.requestLayout();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Set the list of Lectures
        listView_lectures = (ListView) view.findViewById(R.id.calendar_listview);

        // Everything linked to the DB
        db = DatabaseHelper.getInstance(getActivity().getApplicationContext());
        dbLecture = new DBLecture(db);

        lectures = dbLecture.getLecturesForCurrentDateInHome(currentDate);

        // Set a custom adapter
        adapterLecture = new CustomAdapterLecture(getActivity(), lectures);

        listView_lectures.setAdapter(adapterLecture);
        listView_lectures.setFocusable(false);
        setDynamicHeight(listView_lectures);

        // Open an activity when a lecture is selected with all the students related to
        listView_lectures.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent myIntent = new Intent(view.getContext(), LectureActivity.class);
                Lecture lecture = (Lecture) parent.getItemAtPosition(position);

                DBStudent dbStudent = new DBStudent(db);

                ArrayList<Student> students = dbStudent.getStudentsListByLecture(lecture.getId());

                lecture.setStudentsList(students);
                myIntent.putExtra("lecture", lecture);

                startActivity(myIntent);
            }
        });

        // Change the lectures related to the day selected in the calendar
        CalendarView cv = (CalendarView) view.findViewById(R.id.calendar_calendarview);
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(@NonNull CalendarView viewCalendar, int year, int month, int dayOfMonth) {

                lectures = dbLecture.getLecturesForSpecialDate(year, month, dayOfMonth);

                adapterLecture = new CustomAdapterLecture(getActivity(), lectures);

                listView_lectures.setAdapter(adapterLecture);

                listView_lectures.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent myIntent = new Intent(view.getContext(), LectureActivity.class);
                        Lecture lecture = (Lecture) parent.getItemAtPosition(position);

                        DBStudent dbStudent = new DBStudent(db);

                        ArrayList<Student> students = dbStudent.getStudentsListByLecture(lecture.getId());

                        lecture.setStudentsList(students);

                        myIntent.putExtra("lecture", lecture);

                        startActivity(myIntent);
                    }
                });

            }
        });

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
