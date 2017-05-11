package ch.hes.foreignlanguageschool.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ch.hes.foreignlanguageschool.Lecture;
import ch.hes.foreignlanguageschool.R;

/**
 * Created by Aleksandar on 25.04.2017.
 */

public class CustomAdapterLecture extends ArrayAdapter<Lecture> {

    private final Activity activity;
    private ArrayList<Lecture> lectures;

    public CustomAdapterLecture(Activity activity, ArrayList<Lecture> lectures) {
        super(activity, R.layout.listview_image_text, lectures);

        this.activity = activity;
        this.lectures = lectures;

    }


    /**
     * Create the custom adapter
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

        Lecture lecture = (Lecture) getItem(position);

        if (lecture != null) {
            TextView txtTitle = (TextView) v.findViewById(R.id.calendar_lectures_name);
            ImageView imgLecture = (ImageView) v.findViewById(R.id.calendar_lectures_image);

            txtTitle.setText(lectures.get(position).toString());
            int id = activity.getResources().getIdentifier("ch.hes.foreignlanguageschool:drawable/" + lectures.get(position).getImageName(), null, null);
            imgLecture.setImageResource(id);

        }

        return v;

    }


}
