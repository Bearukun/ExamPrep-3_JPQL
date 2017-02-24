/**
 * Q: Describe how you handled persistence in the last three semesters
 *
 * A: First time I used any sort of persistence was to serialize objects, and save
 * them into a file in the project folder. Later on we were introduced to mySQL and
 * how to persist data onto a database. The data on our 2'nd semester was persisted
 * with the JDBC-driver, and writing the mySQL statements through prepared statements,
 * and then extracting the data from the received resultSet. One of the more recent
 * thing that we have learned is the way that cookies work and how you can save data
 * in them when using web projects.
 *
 * Q: Discuss how we usually have queried a relational database
 * 
 * A: In the 2nd semester project Mapper-classes was used that declared new objects of the Connection and PrepareStatement.
 * Then a String used for a prepare statement, could look something like this:
 * 
 *           String sql = "SELECT * FROM user WHERE email = ? and password = ?;";
 * 
 *           stmt = con.prepareStatement(sql);
 *   
 *           stmt.setString(1, email);
 *           stmt.setString(2, password);
 *           //Execute query, and save the resultset in rs.
 *           rs = stmt.executeQuery();
 * 
 * "rs" would then contain the result set from the query. 
 * 
 * 
 * Q: Explain the Pros & Cons in using an ORM (Object Relational Mapping) framework
 *
 * A:
 *  Pros:
 *      ORM typically reduces the amount of code that needs to be written.
 *      Avoids low level JDBC and SQL code.
 *      Provides database and schema independence.
 *      Allows to use the Object Oriented-paradigm.
 *
 *  Cons:
 *      The high level of abstraction can obscure what is actually happening in the implementation code.
 *      Heavy reliance on ORM software has been cited as a major factor in producing poorly designed databases.
 *      Variety of difficulties that arise when considering how to match an object system to a relational database.
 *      Annotations using JPA is hard to memorize.
 *
 * Q: Elaborate on some of the problems ORM tries to solve
 *
 * A: Answered through the Pros and Cons above.
 *
 *
 * Q: Explain how Inheritance in an OO language can be mapped to tables in a relational database
 * 
 * A: You have to extends the classes with with class you want to inherit from. In this assignment
 * student and employee extends the person class. Then make sure you have deleted the ID from the classes
 * that extends. 
 *
 */
package enity.facade;

import enity.Student;
import enity.Studypoint;
import java.util.Collection;
import java.util.Date;
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
