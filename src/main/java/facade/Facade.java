package facade;

import enity.Student;
import enity.Studypoint;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Facade {

    private EntityManagerFactory emf;
    private EntityManager em;

    public static void main(String[] args) {

        Facade fc = new Facade();

        fc.initiateSystem();

        //Find all students 
        fc.findAllStudents();

        //Find all Students in the system with the firstname jan
        fc.findAllWithFirstname("jan");

        //Find all Students in the system with the lastname jan
        fc.findAllWithLastname("olsen");

        //Find the total sum of study points scores, for a student given the student id
        System.out.println("Totalt score: " + fc.findTotalSumStudyPointStudent(1));

        //Find the total sum of studypoint scores, given to all students
        System.out.println(fc.findTotalSumStudyPointsAllStudents());

        //Find the student with most study points 
        System.out.println("User with most study points: " + fc.findStudentWithMostStudyPoints().toString());

        //Find the student with least study points
        System.out.println("User with least study points: " + fc.findStudentWithLeastStudyPoints().toString());

        //Create new student
        fc.createStudent("Ulla", "Terkelsen");

        //Close entityManager
        fc.closeSystem();

    }

    public void initiateSystem() {

        Persistence.generateSchema("pu", null);

        emf = Persistence.createEntityManagerFactory("pu");

        em = emf.createEntityManager();

    }

    public void closeSystem() {

        em.close();

    }

    public void findAllStudents() {

        em.getTransaction().begin();

        List<Student> list = em.createNamedQuery("Student.findAll").getResultList();

        list.forEach((student) -> {

            System.out.println(student.toString());

        });

        em.getTransaction().commit();

    }

    public void findAllWithFirstname(String firstname) {

        List<Student> list = em.createNamedQuery("Student.findByFirstname").setParameter("firstname", firstname).getResultList();
        System.out.println("Found the following with firstname (" + firstname + "): ");
        list.forEach((student) -> {

            System.out.println(student.toString());

        });

    }

    public void findAllWithLastname(String lastname) {

        List<Student> list = em.createNamedQuery("Student.findByLastname").setParameter("lastname", lastname).getResultList();
        System.out.println("Found the following with lastname (" + lastname + "): ");
        list.forEach((student) -> {

            System.out.println(student.toString());

        });

    }

    public int findTotalSumStudyPointStudent(int id) {

        int sum = 0;

        Student student = em.createNamedQuery("Student.findById", Student.class).setParameter("id", id).getSingleResult();

        Collection<Studypoint> studypointCollection = student.getStudypointCollection();

        for (Studypoint sp : studypointCollection) {

            sum += sp.getScore();

        }

        return sum;

    }

    public String findTotalSumStudyPointsAllStudents() {

        int sum = 0;

        List<Student> students = em.createNamedQuery("Student.findAll", Student.class).getResultList();

        for (Student student : students) {

            Collection<Studypoint> col = student.getStudypointCollection();

            for (Studypoint sp : col) {

                sum += sp.getScore();

            }

        }

        return ("Total sum for everyone: " + sum);

    }

    public Student findStudentWithMostStudyPoints() {

        Student student = null;

        int highscore = 0;

        List<Student> students = em.createNamedQuery("Student.findAll", Student.class).getResultList();

        for (Student s : students) {

            int tmp = findTotalSumStudyPointStudent(s.getId());

            if (tmp > highscore) {

                student = s;
                highscore = tmp;

            }

        }

        return student;

    }

    public Student findStudentWithLeastStudyPoints() {

        Student student = null;

        int lowscore = 1000;

        List<Student> students = em.createNamedQuery("Student.findAll", Student.class).getResultList();

        for (Student s : students) {

            int tmp = findTotalSumStudyPointStudent(s.getId());

            if (tmp < lowscore) {

                student = s;
                lowscore = tmp;

            }

        }

        return student;

    }

    public void createStudent(String firstname, String lastname) {

        em.getTransaction().begin();

        em.persist(new Student(firstname, lastname));

        em.getTransaction().commit();

    }

}
