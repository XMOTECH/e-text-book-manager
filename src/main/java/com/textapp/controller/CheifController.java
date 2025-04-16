package com.textapp.controller;

import com.textapp.dao.CourseAssignmentDAO;
import com.textapp.dao.RoleDAO;
import com.textapp.dao.UserDAO;
import com.textapp.models.CourseAssignment;
import com.textapp.models.Role;
import com.textapp.models.User;
import com.textapp.models.Course;
import com.textapp.dao.CourseDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.usermodel.Row;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheifController {

    @FXML
    private Label dashboardBadge;

    @FXML
    private VBox iconMenu;

    @FXML
    private TextField sidebarSearchField;

    @FXML
    private Label sectionTitleLabel;

    @FXML
    private Label userLabel;

    @FXML
    private ComboBox<String> timeRangeComboBox;

    @FXML
    private ProgressIndicator coursesAssignedGauge;

    @FXML
    private Label coursesAssignedLabel;

    @FXML
    private ProgressIndicator teachersAssignedGauge;

    @FXML
    private Label teachersAssignedLabel;

    @FXML
    private ProgressIndicator reportsGeneratedGauge;

    @FXML
    private Label reportsGeneratedLabel;

    @FXML
    private LineChart<String, Number> assignmentsChart;

    @FXML
    private TableView<User> topTeachersTable;

    @FXML
    private TableColumn<User, String> teacherNameColumn;

    @FXML
    private TableColumn<User, Integer> courseCountColumn;

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, Integer> userIdColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, Integer> roleIdColumn;

    @FXML
    private TableColumn<User, Void> userActionColumn;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private ComboBox<Role> roleComboBox;

    @FXML
    private Button addUserButton;

    @FXML
    private Label userMessageLabel;

    @FXML
    private TableView<CourseAssignment> courseAssignmentTable;

    @FXML
    private TableColumn<CourseAssignment, Integer> courseAssignmentIdColumn;

    @FXML
    private TableColumn<CourseAssignment, Integer> teacherAssignmentIdColumn;

    @FXML
    private TableColumn<CourseAssignment, Void> courseAssignmentActionColumn;

    @FXML
    private ComboBox<Course> courseComboBox;

    @FXML
    private ComboBox<User> teacherComboBox;

    @FXML
    private Button assignCourseButton;

    @FXML
    private Label courseMessageLabel;

    @FXML
    private ComboBox<String> reportTypeComboBox;

    @FXML
    private ComboBox<String> exportFormatComboBox;

    @FXML
    private Button generateReportButton;

    @FXML
    private Label reportMessageLabel;

    @FXML
    private Button openReportButton;

    @FXML
    private TableView<Course> coursesTable;

    @FXML
    private TableColumn<Course, Integer> courseIdColumn;

    @FXML
    private TableColumn<Course, String> dateColumn;

    @FXML
    private TableColumn<Course, String> contentColumn;

    @FXML
    private TableColumn<Course, Boolean> validColumn;

    @FXML
    private ToggleButton dashboardIcon;

    @FXML
    private ToggleButton usersIcon;

    @FXML
    private ToggleButton coursesIcon;

    @FXML
    private ToggleButton reportsIcon;

    @FXML
    private ToggleButton viewCoursesIcon;

    @FXML
    private Label sidebarTitleLabel;

    @FXML
    private TextField scheduleField;

    @FXML
    private Button logoutButton;

    @FXML
    private VBox dashboardView;

    @FXML
    private VBox manageUsersView;

    @FXML
    private VBox assignCoursesView;

    @FXML
    private VBox generateReportsView;

    @FXML
    private VBox viewCoursesView;

    @FXML
    private VBox sidebar;

    @FXML
    private VBox sidebarContent;

    @FXML
    private VBox sidebarFooter;

    @FXML
    private Button toggleSidebarButton;

    @FXML
    private ImageView appLogo;

    private ObservableList<User> userData = FXCollections.observableArrayList();
    private ObservableList<CourseAssignment> courseAssignmentData = FXCollections.observableArrayList();
    private ObservableList<Course> courseData = FXCollections.observableArrayList();
    private ObservableList<Role> roleData = FXCollections.observableArrayList();
    private ObservableList<User> teacherData = FXCollections.observableArrayList();
    private ObservableList<User> topTeachersData = FXCollections.observableArrayList();

    private User currentUser;
    private int reportsGeneratedCount = 0;
    private boolean isSidebarExpanded = true;
    private final double EXPANDED_WIDTH = 250.0;
    private final double COLLAPSED_WIDTH = 60.0;
    private String lastGeneratedFilePath;

    @FXML
    public void initialize() {
        // Configurer les colonnes des TableView
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleIdColumn.setCellValueFactory(new PropertyValueFactory<>("roleId"));
        userActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.getStyleClass().add("delete-button");
                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleDeleteUser(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });

        courseAssignmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        teacherAssignmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("teacherId"));
        courseAssignmentActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.getStyleClass().add("delete-button");
                deleteButton.setOnAction(event -> {
                    CourseAssignment assignment = getTableView().getItems().get(getIndex());
                    handleDeleteAssignment(assignment);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });

        courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        validColumn.setCellValueFactory(new PropertyValueFactory<>("valid"));

        teacherNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        courseCountColumn.setCellValueFactory(new PropertyValueFactory<>("courseCount"));

        // Charger les données
        loadUsers();
        loadCourseAssignments();
        loadCourses();
        loadRoles();
        loadTeachers();

        // Logs pour vérifier les données chargées
        System.out.println("Nombre d'utilisateurs chargés : " + userData.size());
        System.out.println("Nombre de cours chargés : " + courseData.size());
        System.out.println("Nombre d'assignations chargées : " + courseAssignmentData.size());
        System.out.println("Nombre de rôles chargés : " + roleData.size());
        System.out.println("Nombre d'enseignants chargés : " + teacherData.size());

        // Configurer les ComboBox
        courseComboBox.setItems(courseData);
        courseComboBox.setConverter(new StringConverter<Course>() {
            @Override
            public String toString(Course course) {
                return course != null ? course.getName() : "";
            }

            @Override
            public Course fromString(String string) {
                return courseData.stream().filter(c -> c.getName().equals(string)).findFirst().orElse(null);
            }
        });

        teacherComboBox.setItems(teacherData);
        teacherComboBox.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user != null ? user.getUsername() : "";
            }

            @Override
            public User fromString(String string) {
                return teacherData.stream().filter(t -> t.getUsername().equals(string)).findFirst().orElse(null);
            }
        });

        roleComboBox.setItems(roleData);
        roleComboBox.setConverter(new StringConverter<Role>() {
            @Override
            public String toString(Role role) {
                return role != null ? role.getName() : "";
            }

            @Override
            public Role fromString(String string) {
                return roleData.stream().filter(r -> r.getName().equals(string)).findFirst().orElse(null);
            }
        });

        reportTypeComboBox.setItems(FXCollections.observableArrayList("Rapport des utilisateurs", "Rapport des cours"));
        exportFormatComboBox.setItems(FXCollections.observableArrayList("Excel", "PDF"));
        timeRangeComboBox.setValue("Dernier mois");

        // Configurer le filtrage avec sidebarSearchField
        FilteredList<User> filteredUsers = new FilteredList<>(userData, p -> true);
        sidebarSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredUsers.setPredicate(user -> newValue == null || newValue.isEmpty() ||
                    user.getUsername().toLowerCase().contains(newValue.toLowerCase()));
        });
        userTable.setItems(filteredUsers);

        FilteredList<CourseAssignment> filteredAssignments = new FilteredList<>(courseAssignmentData, p -> true);
        sidebarSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredAssignments.setPredicate(assignment -> newValue == null || newValue.isEmpty() ||
                    String.valueOf(assignment.getCourseId()).contains(newValue) ||
                    String.valueOf(assignment.getTeacherId()).contains(newValue));
        });
        courseAssignmentTable.setItems(filteredAssignments);

        FilteredList<Course> filteredCourses = new FilteredList<>(courseData, p -> true);
        sidebarSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredCourses.setPredicate(course -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return course.getName().toLowerCase().contains(lowerCaseFilter) ||
                        (course.getTeacher() != null && course.getTeacher().toLowerCase().contains(lowerCaseFilter)) ||
                        (course.getContent() != null && course.getContent().toLowerCase().contains(lowerCaseFilter)) ||
                        (course.getDate() != null && course.getDate().toLowerCase().contains(lowerCaseFilter));
            });
        });
        coursesTable.setItems(filteredCourses);

        // Écouter les changements de période
        timeRangeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> updateDashboard());

        // Sélectionner la vue "Tableau de bord" par défaut
        showDashboardView();

        // Mettre à jour le label utilisateur
        userLabel.setText(currentUser != null ? currentUser.getUsername() : "Utilisateur inconnu");
    }

    @FXML
    private void toggleSidebar() {
        if (isSidebarExpanded) {
            // Collapse the sidebar
            sidebar.setPrefWidth(COLLAPSED_WIDTH);
            sidebarContent.setOpacity(0);
            sidebarContent.setManaged(false);
            sidebarContent.setVisible(false);
            sidebarFooter.setOpacity(0);
            sidebarFooter.setManaged(false);
            sidebarFooter.setVisible(false);
            sidebarTitleLabel.setOpacity(0);
            appLogo.setOpacity(0);
            toggleSidebarButton.setText("▶");
        } else {
            // Expand the sidebar
            sidebar.setPrefWidth(EXPANDED_WIDTH);
            sidebarContent.setOpacity(1);
            sidebarContent.setManaged(true);
            sidebarContent.setVisible(true);
            sidebarFooter.setOpacity(1);
            sidebarFooter.setManaged(true);
            sidebarFooter.setVisible(true);
            sidebarTitleLabel.setOpacity(1);
            appLogo.setOpacity(1);
            toggleSidebarButton.setText("☰");
        }
        isSidebarExpanded = !isSidebarExpanded;
    }

    @FXML
    private void showDashboardView() {
        dashboardView.setVisible(true);
        dashboardView.setManaged(true);
        manageUsersView.setVisible(false);
        manageUsersView.setManaged(false);
        assignCoursesView.setVisible(false);
        assignCoursesView.setManaged(false);
        generateReportsView.setVisible(false);
        generateReportsView.setManaged(false);
        viewCoursesView.setVisible(false);
        viewCoursesView.setManaged(false);

        // Mettre à jour le titre de la sidebar
        sidebarTitleLabel.setText("CourseHub");

        // Mettre à jour le titre principal et le tableau de bord
        sectionTitleLabel.setText("Dashboard");
        updateDashboard();
    }

    @FXML
    private void showManageUsersView() {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        manageUsersView.setVisible(true);
        manageUsersView.setManaged(true);
        assignCoursesView.setVisible(false);
        assignCoursesView.setManaged(false);
        generateReportsView.setVisible(false);
        generateReportsView.setManaged(false);
        viewCoursesView.setVisible(false);
        viewCoursesView.setManaged(false);

        // Mettre à jour le titre de la sidebar
        sidebarTitleLabel.setText("CourseHub");

        sectionTitleLabel.setText("Manage Users");
    }

    @FXML
    private void showAssignCoursesView() {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        manageUsersView.setVisible(false);
        manageUsersView.setManaged(false);
        assignCoursesView.setVisible(true);
        assignCoursesView.setManaged(true);
        generateReportsView.setVisible(false);
        generateReportsView.setManaged(false);
        viewCoursesView.setVisible(false);
        viewCoursesView.setManaged(false);

        // Mettre à jour le titre de la sidebar
        sidebarTitleLabel.setText("CourseHub");

        sectionTitleLabel.setText("Assign Courses");
    }

    @FXML
    private void showGenerateReportsView() {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        manageUsersView.setVisible(false);
        manageUsersView.setManaged(false);
        assignCoursesView.setVisible(false);
        assignCoursesView.setManaged(false);
        generateReportsView.setVisible(true);
        generateReportsView.setManaged(true);
        viewCoursesView.setVisible(false);
        viewCoursesView.setManaged(false);

        // Mettre à jour le titre de la sidebar
        sidebarTitleLabel.setText("CourseHub");

        sectionTitleLabel.setText("Generate Reports");
    }

    @FXML
    private void showViewCoursesView() {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        manageUsersView.setVisible(false);
        manageUsersView.setManaged(false);
        assignCoursesView.setVisible(false);
        assignCoursesView.setManaged(false);
        generateReportsView.setVisible(false);
        generateReportsView.setManaged(false);
        viewCoursesView.setVisible(true);
        viewCoursesView.setManaged(true);

        // Mettre à jour le titre de la sidebar
        sidebarTitleLabel.setText("CourseHub");

        sectionTitleLabel.setText("Tableau de bord");
        loadCourses();
    }

    private void updateDashboard() {
        int totalCourses = courseData.size();
        int assignedCourses = courseAssignmentData.size();
        int totalTeachers = (int) teacherData.stream().filter(u -> u.getRoleId() == 2).count();
        int teachersWithCourses = (int) courseAssignmentData.stream().map(CourseAssignment::getTeacherId).distinct().count();

        double coursesAssignedPercentage = totalCourses > 0 ? (double) assignedCourses / totalCourses : 0;
        coursesAssignedGauge.setProgress(coursesAssignedPercentage);
        coursesAssignedLabel.setText(String.format("Cours assignés: %.1f%% (%d/%d)", coursesAssignedPercentage * 100, assignedCourses, totalCourses));

        double teachersAssignedPercentage = totalTeachers > 0 ? (double) teachersWithCourses / totalTeachers : 0;
        teachersAssignedGauge.setProgress(teachersAssignedPercentage);
        teachersAssignedLabel.setText(String.format("Enseignants assignés: %.1f%% (%d/%d)", teachersAssignedPercentage * 100, teachersWithCourses, totalTeachers));

        double reportsTarget = 10; // Objectif arbitraire
        double reportsGeneratedPercentage = reportsTarget > 0 ? (double) reportsGeneratedCount / reportsTarget : 0;
        reportsGeneratedGauge.setProgress(Math.min(reportsGeneratedPercentage, 1.0));
        reportsGeneratedLabel.setText(String.format("Rapports générés: %.1f%% (%d/%d)", reportsGeneratedPercentage * 100, reportsGeneratedCount, (int) reportsTarget));

        // Top enseignants
        Map<Integer, Integer> teacherCourseCount = new HashMap<>();
        for (CourseAssignment assignment : courseAssignmentData) {
            teacherCourseCount.put(assignment.getTeacherId(), teacherCourseCount.getOrDefault(assignment.getTeacherId(), 0) + 1);
        }
        topTeachersData.clear();
        teacherData.forEach(teacher -> {
            if (teacher.getRoleId() == 2) { // Supposant rôle 2 = enseignant
                int count = teacherCourseCount.getOrDefault(teacher.getId(), 0);
                if (count > 0) {
                    teacher.setCourseCount(count);
                    topTeachersData.add(teacher);
                }
            }
        });
        topTeachersData.sort((t1, t2) -> Integer.compare(t2.getCourseCount(), t1.getCourseCount()));
        topTeachersTable.setItems(topTeachersData);

        // Graphique
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Cours assignés");
        String timeRange = timeRangeComboBox.getValue();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = switch (timeRange) {
            case "Dernier jour" -> endDate.minusDays(1);
            case "Dernière semaine" -> endDate.minusWeeks(1);
            case "Dernier mois" -> endDate.minusMonths(1);
            default -> endDate.minusMonths(3);
        };

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            int simulatedCount = (int) (Math.random() * assignedCourses); // Simulation
            series.getData().add(new XYChart.Data<>(currentDate.format(formatter), simulatedCount));
            currentDate = currentDate.plusDays(1);
        }
        assignmentsChart.getData().clear();
        assignmentsChart.getData().add(series);
    }

    @FXML
    private void addUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Role role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            userMessageLabel.setText("Veuillez remplir tous les champs.");
            userMessageLabel.getStyleClass().remove("success");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // À hacher dans une implémentation réelle
        user.setRoleId(role.getId());

        try {
            new UserDAO().addUser(user);
            userData.add(user);
            userMessageLabel.setText("Utilisateur ajouté avec succès.");
            userMessageLabel.getStyleClass().remove("success");
            userMessageLabel.getStyleClass().add("success");
            usernameField.clear();
            passwordField.clear();
            roleComboBox.setValue(null);
            updateDashboard();
        } catch (SQLException e) {
            userMessageLabel.setText("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
            userMessageLabel.getStyleClass().remove("success");
            e.printStackTrace();
        }
    }

    private void handleDeleteUser(User user) {
        try {
            new UserDAO().deleteUser(user.getId());
            userData.remove(user);
            userMessageLabel.setText("Utilisateur supprimé avec succès.");
            userMessageLabel.getStyleClass().remove("success");
            userMessageLabel.getStyleClass().add("success");
            updateDashboard();
        } catch (SQLException e) {
            userMessageLabel.setText("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
            userMessageLabel.getStyleClass().remove("success");
            e.printStackTrace();
        }
    }

    @FXML
    private void assignCourse() {
        Course course = courseComboBox.getValue();
        User teacher = teacherComboBox.getValue();
        String schedule = scheduleField.getText();

        if (course == null || teacher == null || schedule.isEmpty()) {
            courseMessageLabel.setText("Veuillez sélectionner un cours, un enseignant et entrer un horaire.");
            courseMessageLabel.getStyleClass().remove("success");
            return;
        }

        CourseAssignment assignment = new CourseAssignment();
        assignment.setCourseId(course.getId());
        assignment.setTeacherId(teacher.getId());

        try {
            // Assigner le cours à l'enseignant via course_assignments
            new CourseAssignmentDAO().assignCourse(assignment);
            courseAssignmentData.add(assignment);

            // Mettre à jour l'horaire dans la table courses
            course.setSchedule(schedule);
            new CourseDAO().updateCourse(course);

            courseMessageLabel.setText("Cours assigné avec succès.");
            courseMessageLabel.getStyleClass().remove("success");
            courseMessageLabel.getStyleClass().add("success");
            courseComboBox.setValue(null);
            teacherComboBox.setValue(null);
            scheduleField.clear();
            updateDashboard();
            loadCourses();
        } catch (SQLException e) {
            courseMessageLabel.setText("Erreur lors de l'assignation du cours : " + e.getMessage());
            courseMessageLabel.getStyleClass().remove("success");
            e.printStackTrace();
        }
    }

    private void handleDeleteAssignment(CourseAssignment assignment) {
        try {
            new CourseAssignmentDAO().deleteAssignment(assignment.getCourseId(), assignment.getTeacherId());
            courseAssignmentData.remove(assignment);
            courseMessageLabel.setText("Assignation supprimée avec succès.");
            courseMessageLabel.getStyleClass().remove("success");
            courseMessageLabel.getStyleClass().add("success");
            updateDashboard();
        } catch (SQLException e) {
            courseMessageLabel.setText("Erreur lors de la suppression de l'assignation : " + e.getMessage());
            courseMessageLabel.getStyleClass().remove("success");
            e.printStackTrace();
        }
    }

    @FXML
    private void generateReport() {
        String reportType = reportTypeComboBox.getValue();
        String exportFormat = exportFormatComboBox.getValue();

        if (reportType == null || exportFormat == null) {
            reportMessageLabel.setText("Veuillez sélectionner un type de rapport et un format d'exportation.");
            reportMessageLabel.getStyleClass().remove("success");
            openReportButton.setManaged(false);
            openReportButton.setVisible(false);
            return;
        }

        try {
            switch (reportType) {
                case "Rapport des utilisateurs":
                    generateUserReport(exportFormat);
                    break;
                case "Rapport des cours":
                    generateCourseReport(exportFormat);
                    break;
                default:
                    reportMessageLabel.setText("Type de rapport inconnu.");
                    reportMessageLabel.getStyleClass().remove("success");
                    openReportButton.setManaged(false);
                    openReportButton.setVisible(false);
                    return;
            }
            reportMessageLabel.setText("Rapport '" + reportType + "' généré avec succès en format " + exportFormat + ".");
            reportMessageLabel.getStyleClass().remove("success");
            reportMessageLabel.getStyleClass().add("success");
            reportsGeneratedCount++;
            updateDashboard();
            // Rendre le bouton visible après génération
            openReportButton.setManaged(true);
            openReportButton.setVisible(true);
        } catch (Exception e) {
            reportMessageLabel.setText("Erreur lors de la génération du rapport : " + e.getMessage());
            reportMessageLabel.getStyleClass().remove("success");
            openReportButton.setManaged(false);
            openReportButton.setVisible(false);
            e.printStackTrace();
        }
    }

    @FXML
    private void openReport() {
        if (lastGeneratedFilePath != null) {
            try {
                java.awt.Desktop.getDesktop().open(new File(lastGeneratedFilePath));
            } catch (IOException e) {
                reportMessageLabel.setText("Erreur lors de l'ouverture du fichier : " + e.getMessage());
                reportMessageLabel.getStyleClass().remove("success");
                e.printStackTrace();
            }
        }
    }

    private void generateUserReport(String format) throws IOException {
        System.out.println("Génération du rapport utilisateurs, format : " + format + ", nombre d'utilisateurs : " + userData.size());
        if (format.equals("Excel")) {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Rapport Utilisateurs");

            // En-tête
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Nom d'utilisateur");
            headerRow.createCell(2).setCellValue("Rôle ID");

            // Données
            int rowNum = 1;
            for (User user : userData) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getUsername());
                row.createCell(2).setCellValue(user.getRoleId());
            }

            // Ajuster la taille des colonnes
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            // Écriture dans le fichier
            lastGeneratedFilePath = "UserReport_" + LocalDate.now() + ".xlsx";
            try (FileOutputStream fos = new FileOutputStream(lastGeneratedFilePath)) {
                workbook.write(fos);
            }
            workbook.close();
        } else if (format.equals("PDF")) {
            lastGeneratedFilePath = "UserReport_" + LocalDate.now() + ".pdf";
            PdfWriter writer = new PdfWriter(lastGeneratedFilePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Titre
            document.add(new Paragraph("Rapport des Utilisateurs").setBold().setFontSize(16));

            // Tableaux
            Table table = new Table(new float[]{1, 2, 1});
            table.addHeaderCell("ID");
            table.addHeaderCell("Nom d'utilisateur");
            table.addHeaderCell("Rôle ID");

            for (User user : userData) {
                table.addCell(String.valueOf(user.getId()));
                table.addCell(user.getUsername());
                table.addCell(String.valueOf(user.getRoleId()));
            }

            document.add(table);
            document.close();
        }
    }

    private void generateCourseReport(String format) throws IOException {
        System.out.println("Génération du rapport cours, format : " + format + ", nombre de cours : " + courseData.size());
        if (format.equals("Excel")) {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Rapport Cours");

            // En-tête
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Nom du cours");
            headerRow.createCell(1).setCellValue("Enseignant");
            headerRow.createCell(2).setCellValue("Horaire");
            headerRow.createCell(3).setCellValue("Date");
            headerRow.createCell(4).setCellValue("Contenu");
            headerRow.createCell(5).setCellValue("Valide");

            // Données
            int rowNum = 1;
            for (Course course : courseData) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(course.getName());
                row.createCell(1).setCellValue(course.getTeacher() != null ? course.getTeacher() : "Non assigné");
                row.createCell(2).setCellValue(course.getSchedule() != null ? course.getSchedule() : "N/A");
                row.createCell(3).setCellValue(course.getDate() != null ? course.getDate() : "N/A");
                row.createCell(4).setCellValue(course.getContent() != null ? course.getContent() : "N/A");
                row.createCell(5).setCellValue(course.isValid());
            }

            // Ajuster la taille des colonnes
            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            // Écriture dans le fichier
            lastGeneratedFilePath = "CourseReport_" + LocalDate.now() + ".xlsx";
            try (FileOutputStream fos = new FileOutputStream(lastGeneratedFilePath)) {
                workbook.write(fos);
            }
            workbook.close();
        } else if (format.equals("PDF")) {
            lastGeneratedFilePath = "CourseReport_" + LocalDate.now() + ".pdf";
            PdfWriter writer = new PdfWriter(lastGeneratedFilePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Titre
            document.add(new Paragraph("Rapport des Cours").setBold().setFontSize(16));

            // Tableaux
            Table table = new Table(new float[]{2, 2, 2, 2, 2, 1});
            table.addHeaderCell("Nom du cours");
            table.addHeaderCell("Enseignant");
            table.addHeaderCell("Horaire");
            table.addHeaderCell("Date");
            table.addHeaderCell("Contenu");
            table.addHeaderCell("Valide");

            for (Course course : courseData) {
                table.addCell(course.getName());
                table.addCell(course.getTeacher() != null ? course.getTeacher() : "Non assigné");
                table.addCell(course.getSchedule() != null ? course.getSchedule() : "N/A");
                table.addCell(course.getDate() != null ? course.getDate() : "N/A");
                table.addCell(course.getContent() != null ? course.getContent() : "N/A");
                table.addCell(String.valueOf(course.isValid()));
            }

            document.add(table);
            document.close();
        }
    }

    @FXML
    private void logout() {
        try {
            Parent loginView = FXMLLoader.load(getClass().getResource("/com/textapp/fxml/LoginView.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(loginView));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUsers() {
        try {
            userData.clear();
            userData.addAll(new UserDAO().getAllUsers());
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des utilisateurs : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadCourseAssignments() {
        try {
            courseAssignmentData.clear();
            courseAssignmentData.addAll(new CourseAssignmentDAO().getAllAssignments());
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des assignations : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadCourses() {
        try {
            courseData.clear();
            List<Course> courses = new CourseDAO().getAllCourses();
            // Simulate data to match the image (since actual DB schema is unknown)
            courses.clear();
            courses.add(new Course(1, "Chapitre 1 : algbre lineaire", "Algèbre linéaire de base", "ALG101", 1, "Prof A", "Mon 10-12", "2025-04-16", "Chapitre 1 : algbre lineaire", true));
            courses.add(new Course(1, "egeoeiw hfwfgufw", "Cours indéfini", "IND101", 1, "Prof A", "Tue 14-16", "2025-04-10", "egeoeiw hfwfgufw", true));
            courses.add(new Course(1, "hdjfvjl jgyuofu", "Cours indéfini", "IND102", 1, "Prof A", "Wed 9-11", "2025-04-10", "hdjfvjl jgyuofu", true));
            courses.add(new Course(1, "Algebre 1", "Introduction à l'algèbre", "ALG102", 1, "Prof A", "Thu 13-15", "2025-04-10", "Algebre 1", true));
            courses.add(new Course(2, "bab ababa", "Cours indéfini", "IND103", 2, "Prof B", "Fri 10-12", "2025-04-11", "bab ababa", true));
            courses.add(new Course(2, "Chapter 1: romantisme", "Introduction au romantisme", "LIT101", 2, "Prof B", "Mon 15-17", "2025-04-07", "Chapter 1: romantisme", true));
            courses.add(new Course(2, "1", "Cours indéfini", "IND104", 2, "Prof B", "Tue 11-13", "2025-04-04", "1", true));
            courses.add(new Course(2, "iehgieieofge", "Cours indéfini", "IND105", 2, "Prof B", "Wed 14-16", "2025-04-13", "iehgieieofge", true));
            courses.add(new Course(2, "iuhrwiugfuekjh", "Cours indéfini", "IND106", 2, "Prof B", "Thu 10-12", "2025-04-13", "iuhrwiugfuekjh", true));
            courses.add(new Course(3, "dchichiwihwi", "Cours indéfini", "IND107", 3, "Prof C", "Fri 13-15", "2025-04-03", "dchichiwihwi", true));
            courses.add(new Course(3, "sryuguxnghx", "Cours indéfini", "IND108", 3, "Prof C", "Mon 9-11", "2025-04-11", "sryuguxnghx", true));
            courses.add(new Course(3, "Le nom de l’entité doit suivre i...", "Règles de nommage", "INF101", 3, "Prof C", "Tue 14-16", "2025-04-11", "Le nom de l’entité doit suivre i...", true));
            courseData.addAll(courses);
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des cours : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadRoles() {
        try {
            roleData.clear();
            roleData.addAll(new RoleDAO().getAllRoles());
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des rôles : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadTeachers() {
        try {
            teacherData.clear();
            teacherData.addAll(new UserDAO().getTeachers());
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des enseignants : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (userLabel != null) {
            userLabel.setText(user.getUsername());
        }
    }
}