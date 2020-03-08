package service;

import domain.*;
import repository.*;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class Service {
    private StudentXMLRepository studentXmlRepo;
    private AssignmentXMLRepository temaXmlRepo;
    private GradeXMLRepository notaXmlRepo;

    public Service(StudentXMLRepository studentXmlRepo, AssignmentXMLRepository temaXmlRepo, GradeXMLRepository notaXmlRepo) {
        this.studentXmlRepo = studentXmlRepo;
        this.temaXmlRepo = temaXmlRepo;
        this.notaXmlRepo = notaXmlRepo;
    }

    public Iterable<Student> findAllStudents() { return studentXmlRepo.findAll(); }

    public Iterable<Assignment> findAllTeme() { return temaXmlRepo.findAll(); }

    public Iterable<Grade> findAllNote() { return notaXmlRepo.findAll(); }

    public int saveStudent(String id, String nume, int grupa) {
        Student student = new Student(id, nume, grupa);
        Student result = studentXmlRepo.save(student);

        if (result == null) {
            return 1;
        }
        return 0;
    }

    public int saveTema(String id, String descriere, int deadline, int startline) {
        Assignment assignment = new Assignment(id, descriere, deadline, startline);
        Assignment result = temaXmlRepo.save(assignment);

        if (result == null) {
            return 1;
        }
        return 0;
    }

    public int saveNota(String idStudent, String idTema, double valNota, int predata, String feedback) {
        if (studentXmlRepo.findOne(idStudent) == null || temaXmlRepo.findOne(idTema) == null) {
            return -1;
        }
        else {
            int deadline = temaXmlRepo.findOne(idTema).getDeadline();

            if (predata - deadline > 2) {
                valNota =  1;
            } else {
                valNota =  valNota - 2.5 * (predata - deadline);
            }
            Grade grade = new Grade(new Pair(idStudent, idTema), valNota, predata, feedback);
            Grade result = notaXmlRepo.save(grade);

            if (result == null) {
                return 1;
            }
            return 0;
        }
    }

    public int deleteStudent(String id) {
        Student result = studentXmlRepo.delete(id);

        if (result == null) {
            return 0;
        }
        return 1;
    }

    public int deleteTema(String id) {
        Assignment result = temaXmlRepo.delete(id);

        if (result == null) {
            return 0;
        }
        return 1;
    }

    public int updateStudent(String id, String numeNou, int grupaNoua) {
        Student studentNou = new Student(id, numeNou, grupaNoua);
        Student result = studentXmlRepo.update(studentNou);

        if (result == null) {
            return 0;
        }
        return 1;
    }

    public int updateTema(String id, String descriereNoua, int deadlineNou, int startlineNou) {
        Assignment assignmentNoua = new Assignment(id, descriereNoua, deadlineNou, startlineNou);
        Assignment result = temaXmlRepo.update(assignmentNoua);

        if (result == null) {
            return 0;
        }
        return 1;
    }

    public int extendDeadline(String id, int noWeeks) {
        Assignment assignment = temaXmlRepo.findOne(id);

        if (assignment != null) {
            LocalDate date = LocalDate.now();
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int currentWeek = date.get(weekFields.weekOfWeekBasedYear());

            if (currentWeek >= 39) {
                currentWeek = currentWeek - 39;
            } else {
                currentWeek = currentWeek + 12;
            }

            if (currentWeek <= assignment.getDeadline()) {
                int deadlineNou = assignment.getDeadline() + noWeeks;
                return updateTema(assignment.getID(), assignment.getDescriere(), deadlineNou, assignment.getStartline());
            }
        }
        return 0;
    }

    public void createStudentFile(String idStudent, String idTema) {
        Grade grade = notaXmlRepo.findOne(new Pair(idStudent, idTema));

        notaXmlRepo.createFile(grade);
    }
}
