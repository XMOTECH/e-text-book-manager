package com.textapp.dao;

import com.textapp.models.Session;
import com.textapp.db.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionDAO {

    public void addSession(Session session) throws SQLException {
        String query = "INSERT INTO sessions (course_id, date, content, validated) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, session.getCourseId());
            stmt.setDate(2, Date.valueOf(session.getDate()));
            stmt.setString(3, session.getContent());
            stmt.setBoolean(4, session.isValidated());
            stmt.executeUpdate();
        }
    }

    public List<Session> getUnvalidatedSessions() throws SQLException {
        String query = "SELECT * FROM sessions WHERE validated = false";
        List<Session> sessions = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Session session = new Session(
                        rs.getInt("course_id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getString("content")
                );
                session.setId(rs.getInt("id"));
                session.setValidated(rs.getBoolean("validated"));
                sessions.add(session);
            }
            System.out.println("Nombre de séances non validées trouvées : " + sessions.size());
        }
        return sessions;
    }
    public void validateSession(int sessionId) throws SQLException {
        String query = "UPDATE sessions SET validated = true WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, sessionId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Seances ID: " + sessionId + "validee avec sucess");
            } else {
                throw new SQLException("Aucune seances trouves avec l'ID" + sessionId);
            }
        }

    }

    public List<Session> getSessionsByTeacher(int teacherId) throws SQLException {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT s.id, s.course_id, s.date, s.content, s.validated " +
                "FROM sessions s " +
                "JOIN courses c ON s.course_id = c.id " +
                "WHERE c.teacher_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, teacherId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Session session = new Session(
                        rs.getInt("id"),
                        rs.getInt("course_id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getString("content"),
                        rs.getBoolean("validated")
                );
                sessions.add(session);
            }
        }
        return sessions;
    }
    public List<Session> getSessionsByCourse(int courseId) throws SQLException {
        String query = "SELECT * FROM sessions WHERE course_id = ?";
        List<Session> sessions = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Session session = new Session(
                            rs.getInt("course_id"),
                            rs.getDate("date").toLocalDate(),
                            rs.getString("content")
                    );
                    session.setId(rs.getInt("id"));
                    session.setValidated(rs.getBoolean("validated"));
                    sessions.add(session);
                }
            }
        }
        return sessions;
    }
}