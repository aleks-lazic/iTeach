package ch.hes.foreignlanguageschool.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ch.hes.foreignlanguageschool.DAO.Teacher;
import ch.hes.foreignlanguageschool.R;

/**
 * Created by Aleksandar on 25.04.2017.
 */

public class CustomAdapterTeacher extends ArrayAdapter<Teacher> {

    private final Activity activity;
    private ArrayList<Teacher> teachers;

    public CustomAdapterTeacher(Activity activity, ArrayList<Teacher> teachers) {
        super(activity, R.layout.listview_image_text, teachers);

        this.activity = activity;
        this.teachers = teachers;

    }


    /**
     * Create the custom adapter
     *
     * @param position
     * @param view
     * @param parent
     * @return
     */
    public View getView(int position, View view, ViewGroup parent) {

        View v = view;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.listview_image_text, null);
        }

        Teacher teacher = (Teacher) getItem(position);

        if (teacher != null) {
            TextView txtTitle = (TextView) v.findViewById(R.id.calendar_lectures_name);
            ImageView imgLecture = (ImageView) v.findViewById(R.id.calendar_lectures_image);

            txtTitle.setText(teachers.get(position).toString());
            int id = activity.getResources().getIdentifier("ch.hes.foreignlanguageschool:drawable/" + teachers.get(position).getImageName(), null, null);
            imgLecture.setImageResource(id);

        }

        return v;

    }


}
