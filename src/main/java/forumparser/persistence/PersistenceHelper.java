

package forumparser.persistence;

import javax.persistence.EntityManager;


public class PersistenceHelper {


  public static void persist(EntityManager entityManager,Object object) {
    entityManager.persist(object);
  }
}
