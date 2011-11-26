

package forumparser.model;


import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "forum_topic")
public class Topic {


  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public int        id;

  @Column
  public String     title;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "topic_id")
  public List<Post> postList;


  @Override
  public String toString() {
    return String.format("Topic [title=%s;postList=%s]", title, postList);
  }
}
