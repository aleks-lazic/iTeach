package ch.hes.foreignlanguageschool.DB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ch.hes.foreignlanguageschool.Teacher;

/**
 * Created by Aleksandar on 06.04.2017.
 */

public class DBTeacher {

    private DatabaseHelper db;

    public DBTeacher(DatabaseHelper db) {
        this.db = db;
    }


    /**
     * insert values to create a new teacher
     * We have only one teacher because we don't have the login
     *
     * @param firstName
     * @param lastName
     * @param mail
     */
    public void insertValues(String firstName, String lastName, String mail) {

        SQLiteDatabase sql = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(db.getTEACHER_FIRSTNAME(), firstName);
        values.put(db.getTEACHER_LASTNAME(), lastName);
        values.put(db.getTEACHER_MAIL(), mail);
        values.put(db.getIMAGE_NAME(), "teacher_icon");

        sql.insert(db.getTableTeacher(), null, values);

        sql.close();
    }

    /**
     * get a teacher by id
     *
     * @param idTeacher
     * @return
     */
    public Teacher getTeacherById(int idTeacher) {
        SQLiteDatabase sql = db.getReadableDatabase();

        Teacher teacher = new Teacher();
        String selectQuery = "SELECT "
                + db.getKeyId() + ", "
                + db.getTEACHER_FIRSTNAME() + ", "
                + db.getTEACHER_LASTNAME() + ", "
                + db.getTEACHER_MAIL() + ", "
                + db.getIMAGE_NAME() +
                " FROM " + db.getTableTeacher() + " WHERE " + db.getKeyId() + " = " + idTeacher;

        Cursor cursor = sql.rawQuery(selectQuery, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        DBLecture dbLecture = new DBLecture(db);


        teacher.setId(Integer.parseInt(cursor.getString(0)));
        teacher.setFirstName(cursor.getString(1));
        teacher.setLastName(cursor.getString(2));
        teacher.setMail(cursor.getString(3));
        teacher.setImageName(cursor.getString(4));

        sql.close();

        teacher.setLecturesList(dbLecture.getLecturesForTeacher(idTeacher));


        // return teacher
        return teacher;
    }

    /**
     * update a teacher by id
     *
     * @param idTeacher
     * @param firstName
     * @param lastName
     * @param mail
     * @return
     */
    public int updateTeacherById(int idTeacher, String firstName, String lastName, String mail) {
        SQLiteDatabase sql = db.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(db.getTEACHER_FIRSTNAME(), firstName);
        values.put(db.getTEACHER_LASTNAME(), lastName);
        values.put(db.getTEACHER_MAIL(), mail);


        return sql.update(db.getTableTeacher(), values, db.getKeyId() + " = ?",
                new String[]{String.valueOf(idTeacher)});

    }

    /**
     * get all teachers from DB
     * this is not used because have only one teacher
     *
     * @return
     */
    public ArrayList<Teacher> getAllTeachers() {

        SQLiteDatabase sql = db.getReadableDatabase();

        ArrayList<Teacher> teachersList = new ArrayList<Teacher>();
        String selectQuery = "SELECT * FROM " + db.getTableTeacher();

        Cursor cursor = sql.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Teacher teacher = new Teacher();
                teacher.setId(Integer.parseInt(cursor.getString(0)));
                teacher.setFirstName(cursor.getString(1));
                teacher.setLastName(cursor.getString(2));
                teacher.setMail(cursor.getString(3));
                teacher.setImageName(cursor.getString(4));

                // Adding teacher to list
                teachersList.add(teacher);
            } while (cursor.moveToNext());
        }

        sql.close();


        // return teachers list
        return teachersList;
    }

    /**
     * Count the number of rows in the teacher table
     * @return
     */
    public long getNumberOfRowsInTableTeacher() {

        SQLiteDatabase sql = db.getReadableDatabase();

        long nbRows = DatabaseUtils.queryNumEntries(sql, db.getTableTeacher());
        return nbRows;
    }

}
