package com.textapp.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.sql.Date;

public class CourseAssignment {
    private final IntegerProperty courseId;
    private final IntegerProperty teacherId;
    private Date assignmentDate;

    public CourseAssignment() {
        this.courseId = new SimpleIntegerProperty();
        this.teacherId = new SimpleIntegerProperty();
        this.assignmentDate = null;
    }

    public CourseAssignment(int courseId, int teacherId) {
        this.courseId = new SimpleIntegerProperty(courseId);
        this.teacherId = new SimpleIntegerProperty(teacherId);
        this.assignmentDate = null;
    }

    public CourseAssignment(int courseId, int teacherId, Date assignmentDate) {
        this.courseId = new SimpleIntegerProperty(courseId);
        this.teacherId = new SimpleIntegerProperty(teacherId);
        this.assignmentDate = assignmentDate;
    }

    public int getCourseId() { return courseId.get(); }
    public void setCourseId(int courseId) { this.courseId.set(courseId); }
    public int getTeacherId() { return teacherId.get(); }
    public void setTeacherId(int teacherId) { this.teacherId.set(teacherId); }
    public Date getAssignmentDate() { return assignmentDate; }
    public void setAssignmentDate(Date assignmentDate) { this.assignmentDate = assignmentDate; }

    public IntegerProperty courseIdProperty() { return courseId; }
    public IntegerProperty teacherIdProperty() { return teacherId; }
}