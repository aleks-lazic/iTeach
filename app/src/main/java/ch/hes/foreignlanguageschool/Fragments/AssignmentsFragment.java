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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ch.hes.foreignlanguageschool.Activities.AssignmentActivity;
import ch.hes.foreignlanguageschool.Activities.AssignmentEdit;
import ch.hes.foreignlanguageschool.Adapters.CustomAdapterAssignment;
import ch.hes.foreignlanguageschool.Assignment;
import ch.hes.foreignlanguageschool.DB.DBAssignment;
import ch.hes.foreignlanguageschool.DB.DatabaseHelper;
import ch.hes.foreignlanguageschool.R;

public class AssignmentsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private DatabaseHelper db;
    private DBAssignment dbAssignment;

    private CustomAdapterAssignment adapterAssignment;

    private OnFragmentInteractionListener mListener;

    private ListView mListView;

    private ArrayList<Assignment> assignments;

    private SimpleDateFormat simpleDateFormat;
    private String currentDate;
    private Date todayDate;

    private FloatingActionButton fab;

    public AssignmentsFragment() {
        // Required empty public constructor
    }

    public static AssignmentsFragment newInstance(String param1, String param2) {
        AssignmentsFragment fragment = new AssignmentsFragment();
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
        View view = inflater.inflate(R.layout.fragment_assignments, container, false);

        // Set the list of assignments
        mListView = (ListView) view.findViewById(R.id.assignments_list);

        // Everything linked to the DB
        db = DatabaseHelper.getInstance(getActivity().getApplicationContext());
        dbAssignment = new DBAssignment(db);

        // Set the listener to switch to the activity when an item is selected
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Assignment assignment = (Assignment) parent.getItemAtPosition(position);

                Intent myIntent = new Intent(view.getContext(), AssignmentActivity.class);

                myIntent.putExtra("assignment", assignment);

                startActivity(myIntent);
            }
        });

        // Show a floating action button
        fab = (FloatingActionButton) view.findViewById(R.id.fragment_fab_assignments);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), AssignmentEdit.class);
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

        //everything linked to the date
        simpleDateFormat = new SimpleDateFormat(("dd.MM.yyyy"));
        currentDate = simpleDateFormat.format(new Date());
        try {
            todayDate = simpleDateFormat.parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assignments = dbAssignment.getAllAssignments();
        adapterAssignment = new CustomAdapterAssignment(getActivity(),
                assignments, todayDate);
        mListView.setAdapter(adapterAssignment);

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
