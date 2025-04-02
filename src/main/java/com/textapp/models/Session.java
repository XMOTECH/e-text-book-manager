package com.textapp.models;

import java.time.LocalDate;

public class Session {
    private int id;
    private int courseId;
    private LocalDate date;
    private String content;
    private boolean validated;

    public Session(int id, int courseId, LocalDate date, String content, boolean validated) {
        this.id = id;
        this.courseId = courseId;
        this.date = date;
        this.content = content;
        this.validated = validated;
    }

    public Session(int courseId, LocalDate date, String content) {
        this.courseId = courseId;
        this.date = date;
        this.content = content;
        this.validated = false; // Par défaut
    }

    public int getId() {
        return id;
    }

    public int getCourseId() {
        return courseId;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }
}