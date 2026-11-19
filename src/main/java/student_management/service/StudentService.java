package student_management.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import student_management.model.Student;
import student_management.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    // Validate scores
    private void validateScores(double programming, double mathematics) {

        if (programming < 0 || programming > 100
                || mathematics < 0 || mathematics > 100) {
            throw new IllegalArgumentException(
                    "Scores must be between 0 and 100");
        }
    }

    // Add student
    public int addStudent(Student student) {

        validateScores(
                student.getProgrammingScore(),
                student.getMathematicsScore());

        // Prevent duplicate email
        if (repository.findByEmail(student.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        return repository.save(student);
    }

    // Get all students
    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    // Search by ID
    public Optional<Student> getStudentById(int id) {
        return repository.findById(id);
    }

    // Search by email
    public Optional<Student> getStudentByEmail(String email) {
        return repository.findByEmail(email);
    }

    // Search by course
    public List<Student> getStudentsByCourse(String course) {
        return repository.findByCourse(course);
    }

    // Update scores and skills
    public boolean updateStudent(int id, Student student) {

        validateScores(
                student.getProgrammingScore(),
                student.getMathematicsScore());

        if (repository.findById(id).isEmpty()) {
            return false;
        }

        boolean updated = repository.updateScores(
                id,
                student.getProgrammingScore(),
                student.getMathematicsScore());

        if (updated) {
            repository.updateSkills(id, student.getTechnicalSkills());
        }

        return updated;
    }

    // Delete student
    public boolean deleteStudent(int id) {
        return repository.delete(id);
    }

    // Calculate average
    public double calculateAverage(Student student) {
        return student.calculateAverage();
    }

    // Get performance category
    public String getPerformanceCategory(Student student) {
        return student.getPerformanceCategory();
    }

    // Find stronger subject
    public String getStrongerSubject(Student student) {
        return student.getStrongerSubject();
    }

    // Merit list
    public List<Student> getMeritList() {
        return repository.meritList();
    }

    // Students sharing the same technical skill
    public Map<String, List<Student>> getStudentsWithCommonSkills() {

        List<Student> students = repository.findAll();

        Map<String, List<Student>> skillMap = new HashMap<>();

        for (Student student : students) {

            List<String> skills = student.getTechnicalSkills();

            if (skills == null) {
                continue;
            }

            for (String skill : skills) {
                skillMap.computeIfAbsent(skill, k -> new ArrayList<>())
                        .add(student);
            }
        }

        // Remove skills that are possessed by only one student
        skillMap.entrySet().removeIf(entry -> entry.getValue().size() < 2);

        return skillMap;
    }

    // Programming below 50 and Mathematics at least 75
    public List<Student> getStudentsNeedingProgrammingImprovement() {

        List<Student> students = repository.findAll();

        List<Student> result = new ArrayList<>();

        for (Student student : students) {

            if (student.getProgrammingScore() < 50
                    && student.getMathematicsScore() >= 75) {
                result.add(student);
            }
        }

        return result;
    }
}