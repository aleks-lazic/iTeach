package ch.hes.foreignlanguageschool;


import android.os.AsyncTask;
import android.util.Log;


import com.example.patrickclivaz.myapplication.backend.studentApi.StudentApi;
import com.example.patrickclivaz.myapplication.backend.studentApi.model.Student;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class StudentAsyncTask extends AsyncTask<Void, Void, List<Student>> {
    private static StudentApi studentApi = null;
    private static final String TAG = StudentAsyncTask.class.getName();
    private Student student;

    public StudentAsyncTask(){

    }


    public StudentAsyncTask(Student student) {
        this.student = student;
    }

    @Override
    protected List<Student> doInBackground(Void... params) {

        if (studentApi == null) {
            // Only do this once
            StudentApi.Builder builder = new StudentApi.Builder(AndroidHttp.newCompatibleTransport(),
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
            studentApi = builder.build();
        }

        try {
            // Call here the wished methods on the Endpoints
            // For instance insert
            if (student != null) {
                studentApi.insert(student).execute();
                Log.i(TAG, "insert student");
            }
            // and for instance return the list of all employees
            return studentApi.list().execute().getItems();

        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return new ArrayList<Student>();
        }
    }

    //This method gets executed on the UI thread - The UI can be manipulated directly inside
    //of this method
    @Override
    protected void onPostExecute(List<Student> result) {

        if (result != null) {
            for (Student s : result) {
                Log.i(TAG, "Title : " + s.getFirstName() + "\n result size : " + result.size());
            }
        }
    }
}