

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
@Table(name = "forum_subforum")
public class SubForum {


  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public int         id;

  @Column
  public String      title;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "subforum_id")
  public List<Topic> topicList;


  @Override
  public String toString() {
    return String.format("SubForum [topicList=%s;title=%s]", topicList, title);
  }
}
