package ch.hes.foreignlanguageschool.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.security.Principal;
import java.util.Locale;

import ch.hes.foreignlanguageschool.Activities.NavigationActivity;
import ch.hes.foreignlanguageschool.DB.DBAssignment;
import ch.hes.foreignlanguageschool.DB.DBDay;
import ch.hes.foreignlanguageschool.DB.DBLecture;
import ch.hes.foreignlanguageschool.DB.DBStudent;
import ch.hes.foreignlanguageschool.DB.DBTeacher;
import ch.hes.foreignlanguageschool.DB.DatabaseHelper;
import ch.hes.foreignlanguageschool.R;

public class SettingsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private Button btnLanguage;
    private Button btnSync;

    private DatabaseHelper db;
    private DBAssignment dbAssignment;
    private DBDay dbDay;
    private DBStudent dbStudent;
    private DBTeacher dbTeacher;
    private DBLecture dbLecture;


    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Set a button and its listener
        btnLanguage = (Button) view.findViewById(R.id.btnLanguage);
        btnSync = (Button) view.findViewById(R.id.btnSync);

        //create database objects
        db = DatabaseHelper.getInstance(getActivity().getApplicationContext());
        dbAssignment = new DBAssignment(db);
        dbStudent = new DBStudent(db);
        dbTeacher = new DBTeacher(db);
        dbDay = new DBDay(db);
        dbLecture = new DBLecture(db);

        btnLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Show a dialog to choose a language
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle(getResources().getString(R.string.chooseLanguage));
                String[] languages = getResources().getStringArray(R.array.languages);
                alertDialog.setItems(languages, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Aleks", "" + which);
                        changeLanguage(view, which);
                    }
                });
                alertDialog.show();
            }
        });

        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //send data to google cloud
                dbAssignment.syncAssignmentsToCloud();
                dbStudent.syncStudentsToCloud();
                dbTeacher.syncTeachersToCloud();
                dbDay.syncDaysToCloud();
                dbLecture.syncLecturesToCloud();

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

    /**
     * Choose the desired language depending the selection of the user
     *
     * @param view
     * @param position
     */
    public void changeLanguage(View view, int position) {

        switch (position) {
            case 0:
                changeToEN(view);
                return;
            case 1:
                changeToFR(view);
                return;
            case 2:
                changeToGE(view);
                return;
        }
    }

    /**
     * Switch to French language
     *
     * @param v
     */
    public void changeToFR(View v) {
        String languageToLoad = "fr";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, v.getResources().getDisplayMetrics());


        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit().putString("LANGUAGE", "fr").commit();

        getActivity().finish();
        Intent myIntent = new Intent(v.getContext(), NavigationActivity.class);
        myIntent.putExtra("tag", "Settings");
        startActivity(myIntent);
    }

    /**
     * Switch to German Language
     *
     * @param v
     */
    public void changeToGE(View v) {
        String languageToLoad = "de";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, v.getResources().getDisplayMetrics());

        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit().putString("LANGUAGE", "de").commit();

        getActivity().finish();
        Intent myIntent = new Intent(v.getContext(), NavigationActivity.class);
        myIntent.putExtra("tag", "Settings");
        startActivity(myIntent);
    }

    /**
     * Switch to English language
     *
     * @param v
     */
    public void changeToEN(View v) {
        String languageToLoad = "en";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, v.getResources().getDisplayMetrics());

        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit().putString("LANGUAGE", "en").commit();

        getActivity().finish();
        Intent myIntent = new Intent(v.getContext(), NavigationActivity.class);
        myIntent.putExtra("tag", "Settings");
        startActivity(myIntent);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
