package ch.hes.foreignlanguageschool.DB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ch.hes.foreignlanguageschool.Student;

/**
 * Created by patrickclivaz on 11.04.17.
 */

public class DBStudent {

    private DatabaseHelper db;

    public DBStudent(DatabaseHelper db) {
        this.db = db;
    }

    /**
     * Insert all values for teacher
     *
     * @param firstName
     * @param lastName
     * @param address
     * @param country
     * @param mail
     * @param startDate
     * @param endDate
     */
    public void insertValues(String firstName, String lastName, String address, String country, String mail, String startDate, String endDate) {

        SQLiteDatabase sql = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(db.getSTUDENT_FIRSTNAME(), firstName);
        values.put(db.getSTUDENT_LASTNAME(), lastName);
        values.put(db.getSTUDENT_ADDRESS(), address);
        values.put(db.getSTUDENT_COUNTRY(), country);
        values.put(db.getSTUDENT_MAIL(), mail);
        values.put(db.getSTUDENT_STARTDATE(), startDate);
        values.put(db.getSTUDENT_ENDDATE(), endDate);
        values.put(db.getIMAGE_NAME(), "student_icon");

        sql.insert(db.getTableStudent(), null, values);
        sql.close();
    }

    /**
     * get a student by id
     *
     * @param idStudent
     * @return
     */
    public Student getStudentById(int idStudent) {
        SQLiteDatabase sql = db.getWritableDatabase();

        Student student = new Student();
        String selectQuery = "SELECT * FROM " + db.getTableStudent() + " WHERE " + db.getKeyId() + " = " + idStudent;

        Cursor cursor = sql.rawQuery(selectQuery, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        student.setId(Integer.parseInt(cursor.getString(0)));
        student.setFirstName(cursor.getString(1));
        student.setLastName(cursor.getString(2));
        student.setAddress(cursor.getString(3));
        student.setCountry(cursor.getString(4));
        student.setMail(cursor.getString(5));
        student.setStartDate(cursor.getString(6));
        student.setEndDate(cursor.getString(7));
        student.setImageName(cursor.getString(8));

        sql.close();


        // return teacher
        return student;
    }

    /**
     * get all students for a lecture
     *
     * @param idLecture
     * @return
     */
    public ArrayList<Student> getStudentsListByLecture(int idLecture) {
        SQLiteDatabase sql = db.getWritableDatabase();

        ArrayList<Student> studentsList = new ArrayList<Student>();

        String selectQuery = "SELECT " + db.getLECTURESTUDENT_FKSTUDENT() + " FROM " + db.getTableLecturestudent() + " WHERE " + db.getLECTURESTUDENT_FKLECTURE() + " = " + idLecture;

        Cursor cursor = sql.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Student student = getStudentById(Integer.parseInt(cursor.getString(0)));

                // Adding student to list
                studentsList.add(student);
            } while (cursor.moveToNext());
        }

        sql.close();


        // return teacher
        return studentsList;

    }

    /**
     * get all students
     *
     * @return
     */
    public ArrayList<Student> getAllStudents() {

        SQLiteDatabase sql = db.getWritableDatabase();

        ArrayList<Student> studentsList = new ArrayList<Student>();
        String selectQuery = "SELECT * FROM " + db.getTableStudent() + " ORDER BY " + db.getSTUDENT_FIRSTNAME() + ", " + db.getSTUDENT_LASTNAME();

        Cursor cursor = sql.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Student student = new Student();
                student.setId(Integer.parseInt(cursor.getString(0)));
                student.setFirstName(cursor.getString(1));
                student.setLastName(cursor.getString(2));
                student.setAddress(cursor.getString(3));
                student.setCountry(cursor.getString(4));
                student.setMail(cursor.getString(5));
                student.setStartDate(cursor.getString(6));
                student.setEndDate(cursor.getString(7));
                student.setImageName(cursor.getString(8));

                // Adding student to list
                studentsList.add(student);
            } while (cursor.moveToNext());
        }


        sql.close();

        // return students list
        return studentsList;
    }

    /**
     * delete a student
     *
     * @param idStudent
     */
    public void deleteStudent(int idStudent) {
        deleteStudentById(idStudent);
        deleteStudentFromLectures(idStudent);
    }

    /**
     * delete the student in student table
     *
     * @param idStudent
     */
    private void deleteStudentById(int idStudent) {
        SQLiteDatabase sql = db.getWritableDatabase();

        sql.delete(db.getTableStudent(), db.getKeyId() + " = ?",
                new String[]{String.valueOf(idStudent)});
        sql.close();

    }

    /**
     * delete the student in lecturestudent
     *
     * @param idStudent
     */
    private void deleteStudentFromLectures(int idStudent) {


        SQLiteDatabase sql = db.getWritableDatabase();

        sql.delete(db.getTableLecturestudent(), db.getLECTURESTUDENT_FKSTUDENT() + " = ?",
                new String[]{String.valueOf(idStudent)});
        sql.close();
    }

    /**
     * update a student by id
     *
     * @param idStudent
     * @param firstName
     * @param lastName
     * @param address
     * @param country
     * @param mail
     * @param startDate
     * @param endDate
     * @return
     */
    public int updateStudentById(int idStudent, String firstName, String lastName, String address, String country, String mail, String startDate, String endDate) {

        SQLiteDatabase sql = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(db.getSTUDENT_FIRSTNAME(), firstName);
        values.put(db.getSTUDENT_LASTNAME(), lastName);
        values.put(db.getSTUDENT_ADDRESS(), address);
        values.put(db.getSTUDENT_COUNTRY(), country);
        values.put(db.getSTUDENT_MAIL(), mail);
        values.put(db.getSTUDENT_STARTDATE(), startDate);
        values.put(db.getSTUDENT_ENDDATE(), endDate);


        return sql.update(db.getTableStudent(),
                values,
                db.getKeyId() + " = ?",
                new String[]{String.valueOf(idStudent)});

    }
}
