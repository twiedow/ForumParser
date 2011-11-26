

package forumparser;


import forumparser.model.Topic;


public interface TopicParser {


  public Topic parseTopic(String data) throws Exception;
}
