package ch.hes.foreignlanguageschool.DAO;

import java.io.Serializable;

/**
 * Created by patrickclivaz on 11.04.17.
 */

public class Assignment implements Serializable {

    private int id;
    private String title;
    private String description;
    private String date;
    private Teacher teacher;
    private String imageName;
    private boolean addedToCalendar;

    public Assignment(int id, String title, String description, String date, Teacher teacher) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.teacher = teacher;
    }

    public Assignment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String toString() {
        return title;
    }


    public boolean isAddedToCalendar() {
        return addedToCalendar;
    }

    public void setAddedToCalendar(boolean addedToCalendar) {
        this.addedToCalendar = addedToCalendar;
    }
}
