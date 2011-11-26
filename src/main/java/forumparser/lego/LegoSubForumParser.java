

package forumparser.lego;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import forumparser.SubForumParser;
import forumparser.TopicParser;
import forumparser.model.SubForum;
import forumparser.model.Topic;
import forumparser.util.HttpDataProvider;


public class LegoSubForumParser implements SubForumParser {


  private final static String HEAD_TITLE_PREFIX = "LEGO.com  Messageboards Forum : ";

  private String              baseUrl           = null;
  private TopicParser         topicParser       = null;
  private HttpDataProvider    httpDataProvider  = new HttpDataProvider();


  public LegoSubForumParser(String baseUrl) {
    this.baseUrl = baseUrl;
    topicParser = new LegoTopicParser(baseUrl);
  }


  public SubForum parseSubForum(String data) throws Exception {
    Document document = Jsoup.parse(data);

    Element htmlHeadTitleTag = document.select("html > head > title").first();

    String htmlHeadTitleTagText = htmlHeadTitleTag.text();

    String title = null;
    if (htmlHeadTitleTagText.startsWith(HEAD_TITLE_PREFIX)) {
      title = htmlHeadTitleTagText.substring(HEAD_TITLE_PREFIX.length()).trim();
    }

    SubForum subForum = new SubForum();
    subForum.title = title != null ? title : htmlHeadTitleTagText;
    subForum.topicList = parseTopicList(document);

    return subForum;
  }


  private List<Topic> parseTopicList(Document document) throws Exception {
    Elements elements = document.select("span.txt2BoldB > a");

    List<Topic> topicList = new ArrayList<Topic>();

    for (Element element : elements) {
      String href = element.attr("href").trim();

      if (href.contains("ShowPost.aspx")) {
        System.out.println("Generate topics from " + baseUrl + href);
        String topicData = httpDataProvider.downloadData(baseUrl + href);
        Topic topic = topicParser.parseTopic(topicData);

        topicList.add(topic);
      }
    }

    Element tagWithNextLink = document.select("a:contains(next »)").first();

    if (tagWithNextLink != null) {
      String href = tagWithNextLink.attr("href").trim();
      String data = httpDataProvider.downloadData(baseUrl + href);
      Document nextPage = Jsoup.parse(data);

      topicList.addAll(parseTopicList(nextPage));
    }

    return topicList;
  }


  public static void main(String[] args) throws Exception {
    HttpDataProvider httpDataProvider = new HttpDataProvider();
    String data = httpDataProvider.downloadData("http://messageboards.lego.com/en-US/showforum.aspx?ForumID=1542");

    LegoSubForumParser legoSubForumParser = new LegoSubForumParser("http://messageboards.lego.com/en-US");
    SubForum subForum = legoSubForumParser.parseSubForum(data);
    System.out.println(subForum);

    EntityManager entityManager = Persistence.createEntityManagerFactory("forum_unit").createEntityManager();
    EntityTransaction entityTransaction = entityManager.getTransaction();
    entityTransaction.begin();
    entityManager.persist(subForum);
    entityTransaction.commit();
  }
}
