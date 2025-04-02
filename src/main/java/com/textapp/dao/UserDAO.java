
package com.textapp.dao;

import com.textapp.db.DatabaseUtil;
import com.textapp.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserDAO {
    private static UserDAO instance;

    public User authenticate(String username, String password) throws Exception {
        String query = "SELECT u.id, u.username, u.role_id, r.name AS role_name " +
                "FROM users u " +
                "JOIN roles r ON u.role_id = r.id " +
                "WHERE u.username = ? AND u.password = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getInt("role_id"),
                        rs.getString("role_name")
                );
                System.out.println("Utilisateur authentifié : id=" + user.getId() + ", role_id=" + user.getRoleId() + ", role_name=" + user.getRoleName());
                loadUserPermissions(user);
                return user;
            }
        }
        return null;
    }

    public void addUser(String username, String password, int roleId) throws SQLException {
        String query = "INSERT INTO users (username, password, role_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setInt(3, roleId);
            statement.executeUpdate();
        }
    }

    public void deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }

    public void updateUser(int userId, String username, String password, int roleId) throws SQLException {
        String query = "UPDATE users SET username = ?, password = ?, role_id = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setInt(3, roleId);
            statement.setInt(4, userId);
            statement.executeUpdate();
        }
    }

    public User getUser(int userId) throws SQLException {
        String query = "SELECT u.id, u.username, u.role_id, r.name AS role_name FROM users u JOIN roles r ON u.role_id = r.id WHERE u.id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    String role = resultSet.getString("role_name");
                    return new User(id, username, resultSet.getInt("role_id"), role);
                }
            }
        }
        return null;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT u.id, u.username, u.role_id, r.name AS role_name FROM users u JOIN roles r ON u.role_id = r.id;";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                int roleId = resultSet.getInt("role_id");
                String roleName = resultSet.getString("role_name");
                users.add(new User(id, username, roleId, roleName));
            }
        }
        return users;
    }

    private void loadUserPermissions(User user) throws Exception {
        String query = "SELECT p.name " +
                "FROM role_permissions rp " +
                "JOIN permissions p ON rp.permission_id = p.id " +
                "WHERE rp.role_id = ?";
        System.out.println("Chargement des permissions pour role_id = " + user.getRoleId());
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, user.getRoleId());
            ResultSet rs = stmt.executeQuery();
            Set<String> permissions = new HashSet<>();
            while (rs.next()) {
                String permission = rs.getString("name");
                System.out.println("Permission trouvée : " + permission);
                permissions.add(permission.toUpperCase());
            }
            user.setPermissions(permissions);
            System.out.println("Permissions chargées : " + permissions);
        } catch (SQLException e) {
            System.err.println("Erreur SQL dans loadUserPermissions : " + e.getMessage());
            throw e;
        }
    }

    public List<User> getAllTeachers() throws Exception {
        List<User> teachers = new ArrayList<>();
        String query = "SELECT u.id, u.username, u.role_id, r.name AS role_name " +
                "FROM users u JOIN roles r ON u.role_id = r.id " +
                "WHERE r.name = 'enseignant'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User teacher = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getInt("role_id"),
                        rs.getString("role_name")
                );
                loadUserPermissions(teacher);
                teachers.add(teacher);
            }
        }
        return teachers;
    }

    public List<User> getTeachers() throws SQLException {
        List<User> teachers = new ArrayList<>();
        String query = "SELECT id, username, role_id FROM users WHERE role_id = (SELECT id FROM roles WHERE name = 'Enseignant')"; // Correction : 'Teacher' -> 'Enseignant'
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User teacher = new User();
                teacher.setId(rs.getInt("id"));
                teacher.setUsername(rs.getString("username"));
                teacher.setRoleId(rs.getInt("role_id"));
                teachers.add(teacher);
            }
        }
        return teachers;
    }

    public void addUser(User user) throws SQLException {
        String query = "INSERT INTO users (username, password, role_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setInt(3, user.getRoleId());
            stmt.executeUpdate();
        }
    }
    public static UserDAO getInstance() {
        if (instance == null) {
            synchronized (UserDAO.class) {
                if (instance == null) {
                    instance = new UserDAO();
                }
            }
        }
        return instance;
    }
}
