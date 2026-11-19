package student_management.model;

import java.util.ArrayList;
import java.util.List;

public class Student {

    private Integer studentId;
    private String name;
    private String email;
    private String course;
    private double programmingScore;
    private double mathematicsScore;
    private List<String> technicalSkills;

    // Default Constructor
    public Student() {
        this.technicalSkills = new ArrayList<>();
    }

    // Parameterized Constructor
    public Student(Integer studentId, String name, String email,
                  String course, double programmingScore,
                  double mathematicsScore, List<String> technicalSkills) {

        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.course = course;
        this.programmingScore = programmingScore;
        this.mathematicsScore = mathematicsScore;
        this.technicalSkills = technicalSkills != null
                ? new ArrayList<>(technicalSkills)
                : new ArrayList<>();
    }

    // Getters and Setters

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public double getProgrammingScore() {
        return programmingScore;
    }

    public void setProgrammingScore(double programmingScore) {
        this.programmingScore = programmingScore;
    }

    public double getMathematicsScore() {
        return mathematicsScore;
    }

    public void setMathematicsScore(double mathematicsScore) {
        this.mathematicsScore = mathematicsScore;
    }

    public List<String> getTechnicalSkills() {
        return technicalSkills;
    }

    public void setTechnicalSkills(List<String> technicalSkills) {
        this.technicalSkills = technicalSkills != null
                ? new ArrayList<>(technicalSkills)
                : new ArrayList<>();
    }

    // Calculate Average
    public double calculateAverage() {
        return (programmingScore + mathematicsScore) / 2.0;
    }

    // Performance Category
    public String getPerformanceCategory() {
        double avg = calculateAverage();

        if (avg >= 85)
            return "Excellent";
        else if (avg >= 70)
            return "Good";
        else if (avg >= 50)
            return "Satisfactory";
        else
            return "Needs Improvement";
    }

    // Stronger Subject
    public String getStrongerSubject() {
        if (programmingScore > mathematicsScore)
            return "Programming";
        else if (mathematicsScore > programmingScore)
            return "Mathematics";
        else
            return "Equal";
    }

    // Add Skill
    public void addSkill(String skill) {
        if (skill != null && !skill.isBlank()
                && !technicalSkills.contains(skill)) {
            technicalSkills.add(skill);
        }
    }

    // Display Student Details
    @Override
    public String toString() {
        return "Student ID: " + studentId
                + ", Name: " + name
                + ", Average: " + calculateAverage()
                + ", Category: " + getPerformanceCategory()
                + ", Skills: " + technicalSkills;
    }
}
