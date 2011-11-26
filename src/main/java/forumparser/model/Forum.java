

package forumparser.model;


import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "forum_forum")
public class Forum {


  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public int            id;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "forum_id")
  public List<SubForum> subForumList;
}
