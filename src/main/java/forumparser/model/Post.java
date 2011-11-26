

package forumparser.model;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "forum_post")
public class Post {


  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public int    id;

  @Column
  public String username;

  @Column
  @Lob
  public String content;

  @Column
  @Temporal(TemporalType.TIMESTAMP)
  public Date   timestamp;

  @Column
  public String referencedUsername;


  @Override
  public String toString() {
    return String.format("Post [username=%s;content=%s;timestamp=%s;referencedUsername=%s]", username, content, timestamp, referencedUsername);
  }
}
