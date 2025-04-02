package com.textapp.dao;

import com.textapp.db.DatabaseUtil;
import com.textapp.models.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public List<Course> getCoursesByTeacher(int teacherId) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT c.id, c.name, c.description, c.course_code, c.teacher_id, u.username AS teacher, c.schedule " +
                "FROM courses c " +
                "LEFT JOIN users u ON c.teacher_id = u.id " +
                "WHERE c.teacher_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, teacherId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Course course = new Course(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("course_code"),
                        rs.getInt("teacher_id"),
                        rs.getString("teacher"),
                        rs.getString("schedule")
                );
                courses.add(course);
            }
        }
        return courses;
    }

    public List<Course> getAllCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT c.id, c.name, u.username AS teacher, c.schedule " +
                "FROM courses c " +
                "LEFT JOIN course_assignments ca ON c.id = ca.course_id " +
                "LEFT JOIN users u ON ca.teacher_id = u.id";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setName(rs.getString("name"));
                course.setTeacher(rs.getString("teacher"));
                course.setSchedule(rs.getString("schedule"));
                courses.add(course);
            }
        }
        return courses;
    }
    public void updateCourse(Course course) throws SQLException {
        String query = "UPDATE courses SET schedule = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, course.getSchedule());
            stmt.setInt(2, course.getId());
            stmt.executeUpdate();
        }
    }
}