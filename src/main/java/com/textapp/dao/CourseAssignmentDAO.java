package com.textapp.dao;

import com.textapp.db.DatabaseUtil;
import com.textapp.models.CourseAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CourseAssignmentDAO {
    private static final Logger logger = LoggerFactory.getLogger(CourseAssignmentDAO.class);
    private static CourseAssignmentDAO instance;

    public CourseAssignmentDAO() {}

    public static CourseAssignmentDAO getInstance() {
        if (instance == null) {
            synchronized (CourseAssignmentDAO.class) {
                if (instance == null) {
                    instance = new CourseAssignmentDAO();
                }
            }
        }
        return instance;
    }

    public void assignCourse(CourseAssignment assignment) throws SQLException {
        String query = "INSERT INTO course_assignments (course_id, teacher_id, assignment_date) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, assignment.getCourseId());
            stmt.setInt(2, assignment.getTeacherId());
            stmt.setDate(3, assignment.getAssignmentDate());
            stmt.executeUpdate();
            logger.info("Assignation ajoutée : cours ID {}, enseignant ID {}.",
                    assignment.getCourseId(), assignment.getTeacherId());
        } catch (SQLException e) {
            String message = e.getSQLState() != null && e.getSQLState().equals("23000") ?
                    "Assignation existante pour cours ID " + assignment.getCourseId() + " et enseignant ID " + assignment.getTeacherId() :
                    "Erreur lors de l'assignation : " + e.getMessage();
            logger.error(message, e);
            throw new SQLException(message, e);
        }
    }

    public void deleteAssignment(int courseId, int teacherId) throws SQLException {
        String query = "DELETE FROM course_assignments WHERE course_id = ? AND teacher_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, teacherId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                logger.info("Assignation supprimée : cours ID {}, enseignant ID {}.", courseId, teacherId);
            } else {
                logger.warn("Aucune assignation trouvée pour cours ID {} et enseignant ID {}.", courseId, teacherId);
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression de l'assignation cours ID {}, enseignant ID {}",
                    courseId, teacherId, e);
            throw new SQLException("Erreur lors de la suppression : " + e.getMessage(), e);
        }
    }

    public List<CourseAssignment> getAllAssignments() throws SQLException {
        List<CourseAssignment> assignments = new ArrayList<>();
        String query = "SELECT course_id, teacher_id, assignment_date FROM course_assignments";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                CourseAssignment assignment = new CourseAssignment();
                assignment.setCourseId(rs.getInt("course_id"));
                assignment.setTeacherId(rs.getInt("teacher_id"));
                assignment.setAssignmentDate(rs.getDate("assignment_date"));
                assignments.add(assignment);
            }
            logger.info("Assignations chargées : {} trouvées.", assignments.size());
            return assignments;
        } catch (SQLException e) {
            logger.error("Erreur lors du chargement des assignations", e);
            throw new SQLException("Erreur lors du chargement des assignations : " + e.getMessage(), e);
        }
    }

    public List<CourseAssignment> getAssignmentsByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<CourseAssignment> assignments = new ArrayList<>();
        String query = "SELECT course_id, teacher_id, assignment_date FROM course_assignments " +
                "WHERE assignment_date BETWEEN ? AND ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CourseAssignment assignment = new CourseAssignment();
                    assignment.setCourseId(rs.getInt("course_id"));
                    assignment.setTeacherId(rs.getInt("teacher_id"));
                    assignment.setAssignmentDate(rs.getDate("assignment_date"));
                    assignments.add(assignment);
                }
            }
            logger.info("Assignations chargées pour la plage {} à {} : {} trouvées.", startDate, endDate, assignments.size());
            return assignments;
        } catch (SQLException e) {
            logger.error("Erreur lors du chargement des assignations pour la plage {} à {}", startDate, endDate, e);
            throw new SQLException("Erreur lors du chargement des assignations : " + e.getMessage(), e);
        }
    }
}