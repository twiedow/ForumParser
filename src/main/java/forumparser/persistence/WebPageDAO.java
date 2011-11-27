

package forumparser.persistence;


import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import forumparser.model.WebPage;


public class WebPageDAO extends GenericDAO {


  public static WebPage findByUrl(String url) {
    WebPage webPage = null;

    try {
      EntityManager entityManager = entityManager();

      TypedQuery<WebPage> query = entityManager.createQuery("select page from WebPage page where url=:url", WebPage.class);
      query.setParameter("url", url);

      webPage = query.getSingleResult();
      return webPage;
    }
    catch (Exception e) {
    }

    return webPage;
  }
}
