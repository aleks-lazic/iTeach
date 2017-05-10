package ch.hes.foreignlanguageschool.DB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ch.hes.foreignlanguageschool.DAO.Assignment;

/**
 * Created by patrickclivaz on 11.04.17.
 */

public class DBAssignment {

    private DatabaseHelper db;

    public DBAssignment(DatabaseHelper db) {
        this.db = db;
    }

    /**
     * Insert assignment's values in the DB
     * @param title
     * @param description
     * @param date
     * @param idTeacher
     * @param addedToCalendar
     */
    public void insertValues(String title, String description, String date, int idTeacher, int addedToCalendar) {

        SQLiteDatabase sql = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(db.getASSIGNMENT_TITLE(), title);
        values.put(db.getASSIGNMENT_DESCRIPTION(), description);
        values.put(db.getASSIGNMENT_DATE(), date);
        values.put(db.getIMAGE_NAME(), "assignment_icon");
        values.put(db.getASSIGNMENT_FKTEACHER(), idTeacher);
        values.put(db.getASSIGNMENT_ADDTOCALENDAR(), addedToCalendar);

        sql.insert(db.getTableAssignement(), null, values);
        sql.close();
    }

    /**
     * get all assignments for db
     * @return
     */
    public ArrayList<Assignment> getAllAssignments() {

        SQLiteDatabase sql = db.getWritableDatabase();

        ArrayList<Assignment> assignmentsList = new ArrayList<Assignment>();
        String selectQuery = "SELECT * FROM " + db.getTableAssignement();

        Cursor cursor = sql.rawQuery(selectQuery, null);

        DBTeacher teacher = new DBTeacher(db);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Assignment assignment = new Assignment();
                assignment.setId(Integer.parseInt(cursor.getString(0)));
                assignment.setTitle(cursor.getString(1));
                assignment.setDescription(cursor.getString(2));
                assignment.setDate(cursor.getString(3));
                assignment.setImageName(cursor.getString(4));
                assignment.setTeacher(teacher.getTeacherById(Integer.parseInt(cursor.getString(5))));
                boolean flag = (Integer.parseInt(cursor.getString(6)) > 0);
                assignment.setAddedToCalendar(flag);

                // Adding assignment to list
                assignmentsList.add(assignment);
            } while (cursor.moveToNext());
        }

        sql.close();


        // return assignments list
        return assignmentsList;
    }

    /**
     * get assignment by id
     * @param idAssignment
     * @return
     */
    public Assignment getAssignmentById(int idAssignment) {

        SQLiteDatabase sql = db.getReadableDatabase();

        String selectQuery = "SELECT *"
                + " FROM " + db.getTableAssignement()
                + " WHERE " + db.getKeyId() + " = " + idAssignment;

        Cursor cursor = sql.rawQuery(selectQuery, null);

        // looping through all rows and adding to list

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Assignment assignment = new Assignment();
        assignment.setId(Integer.parseInt(cursor.getString(0)));
        assignment.setTitle(cursor.getString(1));
        assignment.setDescription(cursor.getString(2));
        assignment.setDate(cursor.getString(3));
        assignment.setImageName(cursor.getString(4));
        boolean flag = (Integer.parseInt(cursor.getString(6)) > 0);
        assignment.setAddedToCalendar(flag);


        sql.close();

        return assignment;
    }

    /**
     * delete assignment by id
     * @param idAssignment
     */
    public void deleteAssignmentById(int idAssignment) {

        SQLiteDatabase sql = db.getWritableDatabase();

        sql.delete(db.getTableAssignement(), db.getKeyId() + " = ?",
                new String[]{String.valueOf(idAssignment)});
        sql.close();

    }

    /**
     * update assignment by id
     * @param idAssignment
     * @param title
     * @param description
     * @param date
     * @param addedToCalendar
     * @return
     */
    public int updateAssignmentById(int idAssignment, String title, String description, String date, int addedToCalendar) {
        SQLiteDatabase sql = db.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(db.getASSIGNMENT_TITLE(), title);
        values.put(db.getASSIGNMENT_DESCRIPTION(), description);
        values.put(db.getASSIGNMENT_DATE(), date);
        values.put(db.getASSIGNMENT_ADDTOCALENDAR(), addedToCalendar);


        return sql.update(db.getTableAssignement(), values, db.getKeyId() + " = ?",
                new String[]{String.valueOf(idAssignment)});

    }

    /**
     *
     * @return
     */
    public long getNumberOfRowsInTableAssignment() {

        SQLiteDatabase sql = db.getReadableDatabase();

        String query = "SELECT Count(*) FROM " + db.getTableLecture();


        long nbRows = DatabaseUtils.queryNumEntries(sql, db.getTableLecture());
        return nbRows;
    }
}
