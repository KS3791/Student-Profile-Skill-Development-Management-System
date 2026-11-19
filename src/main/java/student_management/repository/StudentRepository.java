package student_management.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import student_management.model.Student;

@Repository
public class StudentRepository {

    private final JdbcTemplate jdbcTemplate;

    public StudentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Map database row to Student object
    private final RowMapper<Student> rowMapper = (rs, rowNum) -> {
        Student s = new Student();

        s.setStudentId(rs.getInt("student_id"));
        s.setName(rs.getString("name"));
        s.setEmail(rs.getString("email"));
        s.setCourse(rs.getString("course"));
        s.setProgrammingScore(rs.getDouble("programming_score"));
        s.setMathematicsScore(rs.getDouble("mathematics_score"));

        return s;
    };

    // Fetch skills of a student
    private List<String> getSkills(Integer studentId) {
        String sql = "SELECT skill FROM student_skills WHERE student_id = ?";
        return jdbcTemplate.queryForList(sql, String.class, studentId);
    }

    // Attach skills to students
    private void loadSkills(List<Student> students) {
        for (Student s : students) {
            s.setTechnicalSkills(getSkills(s.getStudentId()));
        }
    }

    // Add student
    public int save(Student s) {

        String sql = "INSERT INTO students "
                + "(name, email, course, programming_score, mathematics_score) "
                + "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, s.getName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getCourse());
            ps.setDouble(4, s.getProgrammingScore());
            ps.setDouble(5, s.getMathematicsScore());

            return ps;
        }, keyHolder);

        int id = keyHolder.getKey().intValue();
        s.setStudentId(id);

        saveSkills(id, s.getTechnicalSkills());

        return id;
    }

    // Insert student skills
    private void saveSkills(Integer studentId, List<String> skills) {

        if (skills == null || skills.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO student_skills (student_id, skill) VALUES (?, ?)";

        for (String skill : skills) {
            jdbcTemplate.update(sql, studentId, skill);
        }
    }

    // Get all students
    public List<Student> findAll() {

        String sql = "SELECT * FROM students";

        List<Student> students = jdbcTemplate.query(sql, rowMapper);

        loadSkills(students);

        return students;
    }

    // Find student by ID
    public Optional<Student> findById(int id) {

        String sql = "SELECT * FROM students WHERE student_id = ?";

        List<Student> students = jdbcTemplate.query(sql, rowMapper, id);

        loadSkills(students);

        return students.stream().findFirst();
    }

    // Find student by email
    public Optional<Student> findByEmail(String email) {

        String sql = "SELECT * FROM students WHERE email = ?";

        List<Student> students = jdbcTemplate.query(sql, rowMapper, email);

        loadSkills(students);

        return students.stream().findFirst();
    }

    // Find students by course
    public List<Student> findByCourse(String course) {

        String sql = "SELECT * FROM students WHERE course = ?";

        List<Student> students = jdbcTemplate.query(sql, rowMapper, course);

        loadSkills(students);

        return students;
    }

    // Update scores
    public boolean updateScores(int id, double programming, double mathematics) {

        String sql = "UPDATE students SET programming_score = ?, "
                + "mathematics_score = ? WHERE student_id = ?";

        return jdbcTemplate.update(sql, programming, mathematics, id) > 0;
    }

    // Update skills
    public void updateSkills(int id, List<String> skills) {

        String deleteSql = "DELETE FROM student_skills WHERE student_id = ?";

        jdbcTemplate.update(deleteSql, id);

        saveSkills(id, skills);
    }

    // Delete student
    public boolean delete(int id) {

        String sql = "DELETE FROM students WHERE student_id = ?";

        return jdbcTemplate.update(sql, id) > 0;
    }

    // Merit list
    public List<Student> meritList() {

        String sql = "SELECT * FROM students "
                + "ORDER BY (programming_score + mathematics_score) / 2.0 DESC";

        List<Student> students = jdbcTemplate.query(sql, rowMapper);

        loadSkills(students);

        return students;
    }
}