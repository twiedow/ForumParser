

package forumparser.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;


@Entity
@Table(name = "forum_webpage")
public class WebPage {


  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public int    id;

  @Column
  public String url;

  @Column
  @Lob
  public String data;


  @Override
  public String toString() {
    return String.format("WebPage [url=%s;data=%s]", url, data);
  }
}
