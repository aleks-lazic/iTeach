package ch.hes.foreignlanguageschool;


import android.os.AsyncTask;
import android.util.Log;

import com.example.patrickclivaz.myapplication.backend.dayApi.DayApi;
import com.example.patrickclivaz.myapplication.backend.dayApi.model.Day;
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
import ch.hes.foreignlanguageschool.DB.DBDay;


public class DayAsyncTask extends AsyncTask<Void, Void, List<Day>> {
    private static DayApi dayApi = null;
    private static final String TAG = DayAsyncTask.class.getName();
    private Day day;

    public DayAsyncTask() {

    }

    public DayAsyncTask(Day day) {
        this.day = day;
    }

    @Override
    protected List<Day> doInBackground(Void... params) {

        if (dayApi == null) {
            // Only do this once
            DayApi.Builder builder = new DayApi.Builder(AndroidHttp.newCompatibleTransport(),
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
            dayApi = builder.build();
        }

        try {
            // Call here the wished methods on the Endpoints
            // For instance insert
            if (day != null) {
                dayApi.insert(day).execute();
                SyncActivity.dayTask = true;
                Log.i(TAG, "insert day");
            }
            // and for instance return the list of all employees
            return dayApi.list().execute().getItems();

        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return new ArrayList<Day>();
        }
    }

    //This method gets executed on the UI thread - The UI can be manipulated directly inside
    //of this method
    @Override
    protected void onPostExecute(List<Day> result) {


        if (result != null) {

            if (SyncActivity.lastDayResult.equals(result)) {
                return;
            }

            SyncActivity.lastDayResult = result;

            if (day != null) {
                return;
            }

            DBDay dbDay = new DBDay(SyncActivity.databaseHelper);
            dbDay.retrieveDays(result);

            for (Day day : result) {
                Log.i(TAG, "Title : " + day.getName());


            }

            SyncActivity.dayTask = true;

        }
    }
}