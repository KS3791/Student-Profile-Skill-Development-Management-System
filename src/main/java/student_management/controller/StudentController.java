package student_management.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import student_management.model.Student;
import student_management.service.StudentService;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    // Add student
    @PostMapping
    public ResponseEntity<Integer> addStudent(@RequestBody Student student) {
        int id = service.addStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    // Get all students
    @GetMapping
    public List<Student> getAllStudents() {
        return service.getAllStudents();
    }

    // Get student by ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable int id) {
        Optional<Student> student = service.getStudentById(id);

        return student.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Search by email
    @GetMapping("/email/{email}")
    public ResponseEntity<Student> getStudentByEmail(
            @PathVariable String email) {

        Optional<Student> student = service.getStudentByEmail(email);

        return student.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Search by course
    @GetMapping("/course/{course}")
    public List<Student> getStudentsByCourse(
            @PathVariable String course) {
        return service.getStudentsByCourse(course);
    }

    // Update scores and skills
    @PutMapping("/{id}")
    public ResponseEntity<String> updateStudent(
            @PathVariable int id,
            @RequestBody Student student) {

        boolean updated = service.updateStudent(id, student);

        if (updated) {
            return ResponseEntity.ok("Student updated successfully");
        }

        return ResponseEntity.notFound().build();
    }

    // Delete student
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable int id) {

        boolean deleted = service.deleteStudent(id);

        if (deleted) {
            return ResponseEntity.ok("Student deleted successfully");
        }

        return ResponseEntity.notFound().build();
    }

    // Merit list
    @GetMapping("/merit")
    public List<Student> getMeritList() {
        return service.getMeritList();
    }

    // Students with common skills
    @GetMapping("/common-skills")
    public Map<String, List<Student>> getCommonSkills() {
        return service.getStudentsWithCommonSkills();
    }

    // Programming improvement list
    @GetMapping("/programming-improvement")
    public List<Student> getProgrammingImprovementList() {
        return service.getStudentsNeedingProgrammingImprovement();
    }

    // Student performance details
    @GetMapping("/{id}/performance")
    public ResponseEntity<Map<String, Object>> getPerformance(
            @PathVariable int id) {

        Optional<Student> optional = service.getStudentById(id);

        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Student student = optional.get();

        Map<String, Object> result = Map.of(
                "studentId", student.getStudentId(),
                "average", service.calculateAverage(student),
                "category", service.getPerformanceCategory(student),
                "strongerSubject", service.getStrongerSubject(student)
        );

        return ResponseEntity.ok(result);
    }

    // Handle invalid scores and duplicate emails
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(
            IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
