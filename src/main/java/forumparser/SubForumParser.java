

package forumparser;


import forumparser.model.SubForum;


public interface SubForumParser {


  public SubForum parseSubForum(String data) throws Exception;
}
