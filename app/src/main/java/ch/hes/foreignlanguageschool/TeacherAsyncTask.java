package ch.hes.foreignlanguageschool;


import android.os.AsyncTask;
import android.util.Log;

import com.example.patrickclivaz.myapplication.backend.teacherApi.TeacherApi;
import com.example.patrickclivaz.myapplication.backend.teacherApi.model.Teacher;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.hes.foreignlanguageschool.Activities.NavigationActivity;
import ch.hes.foreignlanguageschool.Activities.SyncActivity;
import ch.hes.foreignlanguageschool.DB.DBAssignment;
import ch.hes.foreignlanguageschool.DB.DBTeacher;


public class TeacherAsyncTask extends AsyncTask<Void, Void, List<Teacher>> {
    private static TeacherApi teacherApi = null;
    private static final String TAG = TeacherAsyncTask.class.getName();
    private Teacher teacher;

    public TeacherAsyncTask() {

    }

    public TeacherAsyncTask(Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    protected List<Teacher> doInBackground(Void... params) {

        if (teacherApi == null) {
            // Only do this once
            TeacherApi.Builder builder = new TeacherApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    // if you deploy on the cloud backend, use your app name
                    // such as https://<your-app-id>.appspot.com
                    .setRootUrl("https://iteach-167221.appspot.com/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            teacherApi = builder.build();
        }

        try {
            // Call here the wished methods on the Endpoints
            // For instance insert
            if (teacher != null) {
                teacherApi.insert(teacher).execute();
                SyncActivity.teacherTask = true;
                Log.i(TAG, "insert teacher");
            }
            // and for instance return the list of all employees
            return teacherApi.list().execute().getItems();

        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return new ArrayList<Teacher>();
        }
    }

    //This method gets executed on the UI thread - The UI can be manipulated directly inside
    //of this method
    @Override
    protected void onPostExecute(List<Teacher> result) {

        if (SyncActivity.lastTeacherResult.equals(result)) {
            return;
        }

        SyncActivity.lastTeacherResult = result;

        if (teacher != null) {
            return;
        }


        if (result != null) {

            DBTeacher dbTeacher = new DBTeacher(SyncActivity.databaseHelper);
            dbTeacher.retrieveTeacher(result);

            for (Teacher t : result) {
                Log.e(TAG, "Teacher name : " + t.getFirstName() + "Teacher id : " + t.getId());
            }

            SyncActivity.teacherTask = true;
        }

    }
}