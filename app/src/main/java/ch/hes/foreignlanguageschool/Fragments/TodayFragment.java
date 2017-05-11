package ch.hes.foreignlanguageschool.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ch.hes.foreignlanguageschool.Activities.AssignmentActivity;
import ch.hes.foreignlanguageschool.Activities.LectureActivity;
import ch.hes.foreignlanguageschool.Adapters.CustomAdapterAssignment;
import ch.hes.foreignlanguageschool.Adapters.CustomAdapterLecture;
import ch.hes.foreignlanguageschool.Assignment;
import ch.hes.foreignlanguageschool.Lecture;
import ch.hes.foreignlanguageschool.DB.DBAssignment;
import ch.hes.foreignlanguageschool.DB.DBLecture;
import ch.hes.foreignlanguageschool.DB.DBStudent;
import ch.hes.foreignlanguageschool.DB.DatabaseHelper;
import ch.hes.foreignlanguageschool.R;

public class TodayFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ListView listViewLectures;
    private ListView listViewAssignments;
    private TextView txtSummaryDay;

    //the home will display all the lectures and assignments OF THE DAY
    private ArrayList<Lecture> lectures;
    private ArrayList<Assignment> assignments;

    //classes needed for the database
    private DatabaseHelper db;
    private DBLecture dbLecture;
    private DBAssignment dbAssignment;

    //create adapters
    private CustomAdapterLecture adapterLecture;
    private CustomAdapterAssignment adapterAssignment;
    private SimpleDateFormat simpleDateFormat;
    private String currentDate;

    private Date todayDate;
    private Date assignmentDate;

    public TodayFragment() {
        // Required empty public constructor
    }

    public static TodayFragment newInstance(String param1, String param2) {
        TodayFragment fragment = new TodayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        //get listViews
        listViewLectures = (ListView) view.findViewById(R.id.home_lectures);
        listViewAssignments = (ListView) view.findViewById(R.id.home_assignments);
        txtSummaryDay = (TextView) view.findViewById(R.id.today_summary);

        //create everything linked to the current date
        simpleDateFormat = new SimpleDateFormat(("dd.MM.yyyy"));
        currentDate = simpleDateFormat.format(new Date());

        txtSummaryDay.setText(getResources().getString(R.string.daySummary) + " " + currentDate);


        //Everything related to the database
        db = DatabaseHelper.getInstance(getActivity().getApplicationContext());
        dbLecture = new DBLecture(db);
        dbAssignment = new DBAssignment(db);

        //Set the listener to switch to the activity when an item is selected
        listViewLectures.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(view.getContext(), LectureActivity.class);
                DBStudent dbStudent = new DBStudent(db);

                Lecture lecture = (Lecture) parent.getItemAtPosition(position);
                lecture.setStudentsList(dbStudent.getStudentsListByLecture(lecture.getId()));
                myIntent.putExtra("lecture", lecture);

                startActivity(myIntent);
            }
        });

        //Set the listener to switch to the activity when an item is selected
        listViewAssignments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(view.getContext(), AssignmentActivity.class);

                Assignment assignment = (Assignment) parent.getItemAtPosition(position);
                myIntent.putExtra("assignment", assignment);

                startActivity(myIntent);
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

    @Override
    public void onResume() {
        super.onResume();
        updateDisplay();
    }

    /**
     * Update the display when the user comes back
     */
    public void updateDisplay() {

        try {
            todayDate = simpleDateFormat.parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        lectures = dbLecture.getLecturesForCurrentDateInHome(currentDate);
        adapterLecture = new CustomAdapterLecture(getActivity(),
                lectures);
        listViewLectures.setAdapter(adapterLecture);
        listViewLectures.setFocusable(false);
        setDynamicHeight(listViewLectures);


        assignments = getAssignmentsForGoodDate();
        adapterAssignment = new CustomAdapterAssignment(getActivity(),
                assignments, todayDate);
        listViewAssignments.setAdapter(adapterAssignment);
        listViewAssignments.setFocusable(false);
        setDynamicHeight(listViewAssignments);

    }

    /**
     * Display the curent day assignments
     * @return
     */
    public ArrayList<Assignment> getAssignmentsForGoodDate() {

        ArrayList<Assignment> allAssignments = dbAssignment.getAllAssignments();
        ArrayList<Assignment> res = new ArrayList<Assignment>();
        try {
            todayDate = simpleDateFormat.parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        for (Assignment a : allAssignments
                ) {

            try {
                assignmentDate = simpleDateFormat.parse(a.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (todayDate.equals(assignmentDate) || assignmentDate.before(todayDate)) {
                res.add(a);
            }
        }
        return res;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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
}
