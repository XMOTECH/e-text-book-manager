package com.textapp.models;

import javafx.beans.property.SimpleIntegerProperty;

import java.sql.Date;

public class CourseAssignment {
    private final SimpleIntegerProperty courseId;
    private final SimpleIntegerProperty teacherId;
    private java.sql.Date assignmentDate;
    public CourseAssignment() {
        this.courseId = new SimpleIntegerProperty();
        this.teacherId = new SimpleIntegerProperty();
    }

    public CourseAssignment(int courseId, int teacherId) {
        this.courseId = new SimpleIntegerProperty(courseId);
        this.teacherId = new SimpleIntegerProperty(teacherId);
    }

    public int getCourseId() {
        return courseId.get();
    }

    public void setCourseId(int courseId) {
        this.courseId.set(courseId);
    }

    public int getTeacherId() {
        return teacherId.get();
    }

    public void setTeacherId(int teacherId) {
        this.teacherId.set(teacherId);
    }

    public SimpleIntegerProperty courseIdProperty() {
        return courseId;
    }

    public SimpleIntegerProperty teacherIdProperty() {
        return teacherId;
    }



    // Getters and setters
    public java.sql.Date getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(java.sql.Date assignmentDate) {
        this.assignmentDate = assignmentDate;
    }
}