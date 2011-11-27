

package forumparser;


import forumparser.model.Forum;


public interface ForumParser {


  public Forum parseForum(String data) throws Exception;

}
