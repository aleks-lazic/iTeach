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

import ch.hes.foreignlanguageschool.Activities.LectureActivity;
import ch.hes.foreignlanguageschool.Activities.LectureEdit;
import ch.hes.foreignlanguageschool.Adapters.CustomAdapterLecture;
import ch.hes.foreignlanguageschool.DAO.Lecture;
import ch.hes.foreignlanguageschool.DB.DBLecture;
import ch.hes.foreignlanguageschool.DB.DBStudent;
import ch.hes.foreignlanguageschool.DB.DatabaseHelper;
import ch.hes.foreignlanguageschool.R;

public class LecturesFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ListView mListView;

    private CustomAdapterLecture adapterLecture;

    private ArrayList<Lecture> lectures;
    private DatabaseHelper db;
    private DBLecture dbLecture;
    private DBStudent dbStudent;

    private FloatingActionButton fab;

    public LecturesFragment() {
        // Required empty public constructor
    }

    public static LecturesFragment newInstance(String param1, String param2) {
        LecturesFragment fragment = new LecturesFragment();
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
        View view = inflater.inflate(R.layout.fragment_lectures, container, false);

        // Set the list of lectures
        mListView = (ListView) view.findViewById(R.id.lectures_list);

        db = DatabaseHelper.getInstance(getActivity().getApplicationContext());
        dbLecture = new DBLecture(db);
        dbStudent = new DBStudent(db);

        lectures = dbLecture.getAllLectures();

        // Set a cutom adapter
        adapterLecture = new CustomAdapterLecture(getActivity(),
                lectures);

        mListView.setAdapter(adapterLecture);

        // Set the listener to open the right lecture when selected
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent myIntent = new Intent(view.getContext(), LectureActivity.class);

                Lecture lecture = (Lecture) parent.getItemAtPosition(position);
                lecture.setStudentsList(dbStudent.getStudentsListByLecture(lecture.getId()));

                myIntent.putExtra("lecture", lecture);

                startActivity(myIntent);
            }
        });

        // Show a floating action button
        fab = (FloatingActionButton) view.findViewById(R.id.fragment_fab_lecture);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), LectureEdit.class);
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

    public void updateDisplay() {
        lectures = dbLecture.getAllLectures();

        adapterLecture = new CustomAdapterLecture(getActivity(),
                lectures);

        mListView.setAdapter(adapterLecture);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
