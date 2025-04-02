package com.textapp.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class User {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty username;
    private final SimpleStringProperty password;
    private final SimpleIntegerProperty roleId;
    private final SimpleStringProperty roleName;
    private final SimpleIntegerProperty courseCount;
    private Set<String> permissions;

    public User() {
        this.id = new SimpleIntegerProperty();
        this.username = new SimpleStringProperty();
        this.password = new SimpleStringProperty();
        this.roleId = new SimpleIntegerProperty();
        this.roleName = new SimpleStringProperty();
        this.courseCount = new SimpleIntegerProperty();
        this.permissions = new HashSet<>();
    }

    public User(int id, String username, int roleId, String roleName) {
        this.id = new SimpleIntegerProperty(id);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty();
        this.roleId = new SimpleIntegerProperty(roleId);
        this.roleName = new SimpleStringProperty(roleName);
        this.courseCount = new SimpleIntegerProperty();
        this.permissions = new HashSet<>();
    }

    public boolean hasPermission(String permission) {
        if (permission == null) {
            System.out.println("Permission null");
            return false;
        }
        String upperPermission = permission.toUpperCase();
        System.out.println("Vérification de permission : " + upperPermission + ", Permissions disponibles : " + permissions);
        return permissions.contains(upperPermission);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public int getRoleId() {
        return roleId.get();
    }

    public void setRoleId(int roleId) {
        this.roleId.set(roleId); // Correction : "id" remplacé par "roleId"
    }

    public SimpleIntegerProperty roleIdProperty() {
        return roleId;
    }

    public String getRoleName() {
        return roleName.get();
    }

    public void setRoleName(String roleName) {
        this.roleName.set(roleName);
    }

    public String getRole() {
        return roleName.get();
    }

    public int getCourseCount() {
        return courseCount.get();
    }

    public void setCourseCount(int courseCount) {
        this.courseCount.set(courseCount);
    }

    public SimpleIntegerProperty courseCountProperty() {
        return courseCount;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions.clear();
        if (permissions != null) {
            this.permissions.addAll(permissions.stream()
                    .map(String::toUpperCase)
                    .collect(Collectors.toSet()));
        }
    }

    @Override
    public String toString() {
        return username.get() != null ? username.get() : "Utilisateur inconnu";
    }
}