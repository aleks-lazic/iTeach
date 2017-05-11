package ch.hes.foreignlanguageschool.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import ch.hes.foreignlanguageschool.Activities.StudentActivity;
import ch.hes.foreignlanguageschool.Activities.StudentEdit;
import ch.hes.foreignlanguageschool.Adapters.CustomAdapterStudent;
import ch.hes.foreignlanguageschool.Student;
import ch.hes.foreignlanguageschool.DB.DBLecture;
import ch.hes.foreignlanguageschool.DB.DBStudent;
import ch.hes.foreignlanguageschool.DB.DatabaseHelper;
import ch.hes.foreignlanguageschool.R;

public class StudentsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ListView mListView;

    private ArrayList<Student> students;

    private DatabaseHelper db;
    private DBStudent dbStudent;
    private DBLecture dbLecture;

    private CustomAdapterStudent adapterStudent;

    private FloatingActionButton fab;

    public StudentsFragment() {
        // Required empty public constructor
    }

    public static StudentsFragment newInstance(String param1, String param2) {
        StudentsFragment fragment = new StudentsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_students, container, false);

        // Set the list of assignments
        mListView = (ListView) view.findViewById(R.id.students_list);

        // Everything linked to the DB
        db = DatabaseHelper.getInstance(getActivity().getApplicationContext());
        dbStudent = new DBStudent(db);

        students = dbStudent.getAllStudents();

        // Set a custom adapter
        adapterStudent = new CustomAdapterStudent(getActivity(), students);

        mListView.setAdapter(adapterStudent);

        // Set the listener to switch to the activity when an item is selected
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent myIntent = new Intent(view.getContext(), StudentActivity.class);

                Student student = (Student) parent.getItemAtPosition(position);
                dbLecture = new DBLecture(db);
                student.setLecturesList(dbLecture.getLecturesForStudent(student.getId()));

                myIntent.putExtra("student", student);

                startActivity(myIntent);
            }
        });

        // Show a floating action button
        fab = (FloatingActionButton) view.findViewById(R.id.fragment_fab_students);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), StudentEdit.class);
                startActivity(intent);
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
        students = dbStudent.getAllStudents();

        adapterStudent = new CustomAdapterStudent(getActivity(), students);

        mListView.setAdapter(adapterStudent);

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
