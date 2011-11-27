

package forumparser.persistence;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;


public class GenericDAO {


  private static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("forum_unit");


  protected static EntityManager entityManager() {
    return entityManagerFactory.createEntityManager();
  }


  public static void persist(Object object) {
    EntityManager entityManager = entityManager();

    EntityTransaction entityTransaction = entityManager.getTransaction();
    entityTransaction.begin();
    entityManager.persist(object);
    entityTransaction.commit();
  }
}
