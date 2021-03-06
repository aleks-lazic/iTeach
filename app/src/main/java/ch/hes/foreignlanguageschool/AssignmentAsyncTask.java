package ch.hes.foreignlanguageschool;


import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import com.example.patrickclivaz.myapplication.backend.assignmentApi.AssignmentApi;
import com.example.patrickclivaz.myapplication.backend.assignmentApi.model.Assignment;
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


public class AssignmentAsyncTask extends AsyncTask<Void, Void, List<Assignment>> {
    private static AssignmentApi assignmentApi = null;
    private static final String TAG = AssignmentAsyncTask.class.getName();
    private Assignment assignment;
    private boolean delete;
    private long idToDelete;

    public AssignmentAsyncTask(){

    }

    public AssignmentAsyncTask(Assignment assignment, boolean delete, long idToDelete){
        this.assignment = assignment;
        this.delete = delete;
        this.idToDelete = idToDelete;
    }

    public AssignmentAsyncTask(Assignment assignment) {
        this.assignment = assignment;
    }

    @Override
    protected List<Assignment> doInBackground(Void... params) {

        if (assignmentApi == null) {
            // Only do this once
            AssignmentApi.Builder builder = new AssignmentApi.Builder(AndroidHttp.newCompatibleTransport(),
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
            assignmentApi = builder.build();
        }

        try {
            // Call here the wished methods on the Endpoints
            // For instance insert
            if (assignment != null) {
                assignmentApi.insert(assignment).execute();
                SyncActivity.assignmentTask = true;
                Log.i(TAG, "insert assignment");
            }
            // and for instance return the list of all employees
            return assignmentApi.list().execute().getItems();

        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return new ArrayList<Assignment>();
        }
    }

    //This method gets executed on the UI thread - The UI can be manipulated directly inside
    //of this method
    @Override
    protected void onPostExecute(List<Assignment> result) {


        if(delete){
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Log.e("DELETE", "JE SUPPRIME L'ASSIGNMENT MON GARS  + ID : " + idToDelete);
                assignmentApi.remove(idToDelete).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }




        if (result != null) {

            if (SyncActivity.lastAssignmentResult.equals(result)) {
                return;
            }

            SyncActivity.lastAssignmentResult = result;

            if(assignment != null){
                return;
            }

            DBAssignment dbAssignment = new DBAssignment(SyncActivity.databaseHelper);
            dbAssignment.retrieveAssignments(result);

            for (Assignment assignment : result) {
                Log.i(TAG, "Title : " + assignment.getTitle());

                Log.i(TAG, "Teacher name: " + assignment.getTeacher().getFirstName());

            }

            SyncActivity.assignmentTask = true;
        }
    }
}
