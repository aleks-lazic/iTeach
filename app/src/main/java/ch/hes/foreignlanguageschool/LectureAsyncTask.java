package ch.hes.foreignlanguageschool;


import android.os.AsyncTask;
import android.util.Log;


import com.example.patrickclivaz.myapplication.backend.lectureApi.LectureApi;
import com.example.patrickclivaz.myapplication.backend.lectureApi.model.Lecture;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LectureAsyncTask extends AsyncTask<Void, Void, List<Lecture>> {
    private static LectureApi lectureApi = null;
    private static final String TAG = LectureAsyncTask.class.getName();
    private Lecture lecture;

    public LectureAsyncTask(){

    }

    public LectureAsyncTask(Lecture lecture) {
        this.lecture = lecture;
    }

    @Override
    protected List<Lecture> doInBackground(Void... params) {

        if (lectureApi == null) {
            // Only do this once
            LectureApi.Builder builder = new LectureApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    // if you deploy on the cloud backend, use your app name
                    // such as https://<your-app-id>.appspot.com
                    .setRootUrl("https://iteach-167221.appspot.com/_ah/api")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            lectureApi = builder.build();
        }

        try {
            // Call here the wished methods on the Endpoints
            // For instance insert
            if (lecture != null) {
                lectureApi.insert(lecture).execute();
                Log.i(TAG, "insert lecture");
            }
            // and for instance return the list of all employees
            return lectureApi.list().execute().getItems();

        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return new ArrayList<Lecture>();
        }
    }

    //This method gets executed on the UI thread - The UI can be manipulated directly inside
    //of this method
    @Override
    protected void onPostExecute(List<Lecture> result) {

        if (result != null) {
            for (Lecture l : result) {
                Log.i(TAG, "Title : " + l.getName());

                Log.i(TAG, "Teacher name : " + l.getTeacher().getFirstName());

                for (com.example.patrickclivaz.myapplication.backend.lectureApi.model.Student s : l.getStudentsList()
                        ) {
                    Log.i(TAG, "Student name : " + s.getFirstName());
                }
            }
        }
    }
}