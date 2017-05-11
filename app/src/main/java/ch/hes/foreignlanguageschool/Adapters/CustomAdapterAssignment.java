package ch.hes.foreignlanguageschool.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ch.hes.foreignlanguageschool.Assignment;
import ch.hes.foreignlanguageschool.R;

/**
 * Created by Aleksandar on 25.04.2017.
 */

public class CustomAdapterAssignment extends ArrayAdapter<Assignment> {

    private final Activity activity;
    private ArrayList<Assignment> assignments;
    private TextView txtTitle;
    private ImageView imgLecture;
    private Date assignmentDate;
    private Date currentDate;
    private SimpleDateFormat simpleDateFormat;

    public CustomAdapterAssignment(Activity activity, ArrayList<Assignment> assignments, Date currentDate) {
        super(activity, R.layout.listview_image_text, assignments);

        this.activity = activity;
        this.assignments = assignments;
        this.currentDate = currentDate;

    }


    public CustomAdapterAssignment(Activity activity, ArrayList<Assignment> assignments) {
        super(activity, R.layout.listview_image_text, assignments);

        this.activity = activity;
        this.assignments = assignments;
        currentDate = null;
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

        Assignment assignment = (Assignment) getItem(position);

        if (assignment != null) {
            txtTitle = (TextView) v.findViewById(R.id.calendar_lectures_name);
            imgLecture = (ImageView) v.findViewById(R.id.calendar_lectures_image);

            simpleDateFormat = new SimpleDateFormat(("dd.MM.yyyy"));

            try {
                assignmentDate = simpleDateFormat.parse(assignment.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (currentDate != null) {
                if (assignmentDate.before(currentDate)) {
                    txtTitle.setTextColor(Color.RED);
                }
            }

            txtTitle.setText(assignments.get(position).toString());
            int id = activity.getResources().getIdentifier("ch.hes.foreignlanguageschool:drawable/" + assignments.get(position).getImageName(), null, null);
            imgLecture.setImageResource(id);

        }

        return v;

    }


}
