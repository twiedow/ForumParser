

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

import forumparser.PostParser;
import forumparser.TopicParser;
import forumparser.model.Post;
import forumparser.model.Topic;
import forumparser.util.HttpDataProvider;


public class LegoTopicParser implements TopicParser {


  private final static String HEAD_TITLE_PREFIX = "LEGO.com  Messageboards Post : ";

  private String              baseUrl           = null;
  private HttpDataProvider    httpDataProvider  = new HttpDataProvider();
  private PostParser          postParser        = new LegoPostParser();


  public LegoTopicParser(String baseUrl) {
    this.baseUrl = baseUrl;
  }


  public Topic parseTopic(String data) throws Exception {
    Document document = Jsoup.parse(data);

    Element htmlHeadTitleTag = document.select("html > head > title").first();

    String htmlHeadTitleTagText = htmlHeadTitleTag.text();

    String title = null;
    if (htmlHeadTitleTagText.startsWith(HEAD_TITLE_PREFIX)) {
      title = htmlHeadTitleTagText.substring(HEAD_TITLE_PREFIX.length()).trim();
    }

    Topic topic = new Topic();
    topic.title = title != null ? title : htmlHeadTitleTagText;
    topic.postList = parsePostList(document);

    return topic;
  }


  private List<Post> parsePostList(Document document) throws Exception {
    Elements elements = document.select("div[id=lp-container] > table:eq(1) > tbody > tr:gt(0)");

    List<Post> postList = new ArrayList<Post>();

    for (Element element : elements) {
      Post post = postParser.parsePost(element.outerHtml());
      postList.add(post);
    }

    Element tagWithNextLink = document.select("a:contains(next »)").first();

    if (tagWithNextLink != null) {
      String href = tagWithNextLink.attr("href").trim();
      String data = httpDataProvider.downloadData(baseUrl + href);
      Document nextPage = Jsoup.parse(data);

      postList.addAll(parsePostList(nextPage));
    }

    return postList;
  }


  public static void main(String[] args) throws Exception {
    HttpDataProvider httpDataProvider = new HttpDataProvider();
    String data = httpDataProvider.downloadData("http://messageboards.lego.com/en-US/showpost.aspx?PostID=4564674");

    LegoTopicParser legoTopicParser = new LegoTopicParser("http://messageboards.lego.com/en-US");
    Topic topic = legoTopicParser.parseTopic(data);
    System.out.println(topic);

    EntityManager entityManager = Persistence.createEntityManagerFactory("forum_unit").createEntityManager();
    EntityTransaction entityTransaction = entityManager.getTransaction();
    entityTransaction.begin();
    entityManager.persist(topic);
    entityTransaction.commit();
  }
}
