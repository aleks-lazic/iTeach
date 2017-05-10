package ch.hes.foreignlanguageschool.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import ch.hes.foreignlanguageschool.Activities.NavigationActivity;
import ch.hes.foreignlanguageschool.DB.DBTeacher;
import ch.hes.foreignlanguageschool.DB.DatabaseHelper;
import ch.hes.foreignlanguageschool.R;

public class ProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private EditText txtFirstName;
    private EditText txtLastName;
    private EditText txtMail;

    private DatabaseHelper db;
    private DBTeacher dbTeacher;

    private FloatingActionButton fab;


    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtFirstName = (EditText) view.findViewById(R.id.teacher_firstname);
        txtFirstName.setText(NavigationActivity.currentTeacher.getFirstName());

        txtLastName = (EditText) view.findViewById(R.id.teacher_lastname);
        txtLastName.setText(NavigationActivity.currentTeacher.getLastName());

        txtMail = (EditText) view.findViewById(R.id.teacher_mail);
        txtMail.setText(NavigationActivity.currentTeacher.getMail());

        setEditable(false);

        // reate database objects
        db = DatabaseHelper.getInstance(getActivity().getApplicationContext());
        dbTeacher = new DBTeacher(db);

        fab = (FloatingActionButton) view.findViewById(R.id.fragment_fab_profile);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditable(true);
                showKeyboard(getActivity().getApplicationContext());
                txtFirstName.requestFocus();
                cursorAtEnd(txtFirstName);
                setHasOptionsMenu(true);
                fab.setVisibility(View.INVISIBLE);

            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save) {

            hideKeyboard();

            if (!checkEverythingOnSaveClick()) {

                return super.onOptionsItemSelected(item);
            }

            // Save the modifications for the current teacher in the Database and update the informations
            String fistname = txtFirstName.getText().toString();
            String lastname = txtLastName.getText().toString();
            String mail = txtMail.getText().toString();

            dbTeacher.updateTeacherById(NavigationActivity.currentTeacher.getId(), fistname, lastname, mail);
            NavigationActivity.currentTeacher = dbTeacher.getTeacherById(1);
            NavigationActivity.setNavigationView();

            setEditable(false);
            setHasOptionsMenu(false);

            fab.setVisibility(View.VISIBLE);

        }

        return super.onOptionsItemSelected(item);
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
     * Hide the Keyboard for all the txtEdit
     */
    public void hideKeyboard() {
        closeKeyboard(getActivity(), txtFirstName.getWindowToken());
        closeKeyboard(getActivity(), txtLastName.getWindowToken());
        closeKeyboard(getActivity(), txtMail.getWindowToken());
    }

    /**
     * Define if the editText are editable or not
     * @param bool
     */
    public void setEditable(boolean bool) {
        txtFirstName.setCursorVisible(bool);
        txtFirstName.setClickable(bool);
        txtFirstName.setFocusable(bool);
        txtFirstName.setFocusableInTouchMode(bool);

        txtLastName.setCursorVisible(bool);
        txtLastName.setClickable(bool);
        txtLastName.setFocusable(bool);
        txtLastName.setFocusableInTouchMode(bool);

        txtMail.setCursorVisible(bool);
        txtMail.setClickable(bool);
        txtMail.setFocusable(bool);
        txtMail.setFocusableInTouchMode(bool);
    }

    public static void showKeyboard(Context context) {
        ((InputMethodManager) (context).getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * Set the cursot at the end of the editText
     * @param editText
     */
    public void cursorAtEnd(EditText editText) {
        editText.setSelection(editText.getText().length());
    }

    /**
     * Check if every parameters are correctly written before saving
     * @return
     */
    public boolean checkEverythingOnSaveClick() {
        //check if the firsname is filled
        if (txtFirstName.getText().toString().trim().equals("")) {
            txtFirstName.setError(getResources().getString(R.string.TitleAlert));
            return false;
        }

        //check if the lastname is filled
        else if (txtLastName.getText().toString().trim().equals("")) {
            txtLastName.setError(getResources().getString(R.string.TitleAlert));
            return false;
        }

        //check if the mail is filled
        else if (txtMail.getText().toString().trim().equals("")) {
            txtMail.setError(getResources().getString(R.string.TitleAlert));
            return false;
        }

        return true;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
