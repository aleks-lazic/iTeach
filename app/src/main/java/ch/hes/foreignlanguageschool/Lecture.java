package ch.hes.foreignlanguageschool;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by patrickclivaz on 11.04.17.
 */

public class Lecture implements Serializable {

    private int id;
    private String name;
    private String description;
    private Teacher teacher;
    private ArrayList<Student> studentsList;
    private String imageName;
    private int idDay;
    private String startTime;
    private String endTime;
    private int idGoogleAppEngine;


    public Lecture(int id, String name, String description, Teacher teacher) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.teacher = teacher;
    }

    public Lecture() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public ArrayList<Student> getStudentsList() {
        return studentsList;
    }

    public void setStudentsList(ArrayList<Student> studentsList) {
        this.studentsList = studentsList;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }


    public int getIdDay() {
        return idDay;
    }

    public void setIdDay(int idDay) {
        this.idDay = idDay;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String toString() {
        return name;
    }

    public int getIdGoogleAppEngine() {
        return idGoogleAppEngine;
    }

    public void setIdGoogleAppEngine(int idGoogleAppEngine) {
        this.idGoogleAppEngine = idGoogleAppEngine;
    }
}
