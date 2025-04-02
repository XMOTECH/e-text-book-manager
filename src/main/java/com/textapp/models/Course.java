package com.textapp.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;

public class Course {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty description;
    private final SimpleStringProperty courseCode;
    private final SimpleIntegerProperty teacherId;
    private final SimpleStringProperty teacher;
    private final SimpleStringProperty schedule;
    private final SimpleStringProperty date;
    private final SimpleStringProperty content;
    private final SimpleBooleanProperty valid;

    public Course() {
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.courseCode = new SimpleStringProperty();
        this.teacherId = new SimpleIntegerProperty();
        this.teacher = new SimpleStringProperty();
        this.schedule = new SimpleStringProperty();
        this.date = new SimpleStringProperty();
        this.content = new SimpleStringProperty();
        this.valid = new SimpleBooleanProperty();
    }

    public Course(int id, String name, String description, String courseCode, int teacherId, String teacher, String schedule, String date, String content, boolean valid) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.courseCode = new SimpleStringProperty(courseCode);
        this.teacherId = new SimpleIntegerProperty(teacherId);
        this.teacher = new SimpleStringProperty(teacher);
        this.schedule = new SimpleStringProperty(schedule);
        this.date = new SimpleStringProperty(date);
        this.content = new SimpleStringProperty(content);
        this.valid = new SimpleBooleanProperty(valid);
    }

    public Course(int id, String name, String description, String courseCode, int teacherId, String teacher, String schedule) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.courseCode = new SimpleStringProperty(courseCode);
        this.teacherId = new SimpleIntegerProperty(teacherId);
        this.teacher = new SimpleStringProperty(teacher);
        this.schedule = new SimpleStringProperty(schedule);
        this.date = new SimpleStringProperty();
        this.content = new SimpleStringProperty();
        this.valid = new SimpleBooleanProperty();
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getCourseCode() {
        return courseCode.get();
    }

    public void setCourseCode(String courseCode) {
        this.courseCode.set(courseCode);
    }

    public int getTeacherId() {
        return teacherId.get();
    }

    public void setTeacherId(int teacherId) {
        this.teacherId.set(teacherId);
    }

    public String getTeacher() {
        return teacher.get();
    }

    public void setTeacher(String teacher) {
        this.teacher.set(teacher);
    }

    public String getSchedule() {
        return schedule.get();
    }

    public void setSchedule(String schedule) {
        this.schedule.set(schedule);
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getContent() {
        return content.get();
    }

    public void setContent(String content) {
        this.content.set(content);
    }

    public boolean isValid() {
        return valid.get();
    }

    public void setValid(boolean valid) {
        this.valid.set(valid);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty teacherProperty() {
        return teacher;
    }

    public StringProperty scheduleProperty() {
        return schedule;
    }

    public StringProperty dateProperty() {
        return date;
    }

    public StringProperty contentProperty() {
        return content;
    }

    public BooleanProperty validProperty() {
        return valid;
    }

    @Override
    public String toString() {
        return name.get() + " (" + courseCode.get() + ")";
    }
}