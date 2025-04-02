package com.textapp.dao;

import com.textapp.db.DatabaseUtil;
import com.textapp.models.CourseAssignment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseAssignmentDAO {

    public void assignCourse(CourseAssignment assignment) throws SQLException {
        String query = "INSERT INTO course_assignments (course_id, teacher_id) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, assignment.getCourseId());
            stmt.setInt(2, assignment.getTeacherId());
            stmt.executeUpdate();
        }
    }

    public void deleteAssignment(int courseId, int teacherId) throws SQLException {
        String query = "DELETE FROM course_assignments WHERE course_id = ? AND teacher_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, teacherId);
            stmt.executeUpdate();
        }
    }

    public List<CourseAssignment> getAllAssignments() throws SQLException {
        String query = "SELECT * FROM course_assignments";
        List<CourseAssignment> assignments = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                CourseAssignment assignment = new CourseAssignment(
                        rs.getInt("course_id"),
                        rs.getInt("teacher_id")
                );
                assignments.add(assignment);
            }
        }
        return assignments;
    }


}