

package forumparser.parser;


import forumparser.model.Topic;


public interface TopicParser {


  public Topic parseTopic(String data) throws Exception;
}
