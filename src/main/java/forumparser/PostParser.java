

package forumparser;


import forumparser.model.Post;


public interface PostParser {


  public Post parsePost(String data) throws Exception;
}
