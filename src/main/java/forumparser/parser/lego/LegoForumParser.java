

package forumparser.parser.lego;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import forumparser.model.Forum;
import forumparser.model.SubForum;
import forumparser.parser.ForumParser;
import forumparser.parser.SubForumParser;
import forumparser.persistence.GenericDAO;
import forumparser.util.HttpDataProvider;


public class LegoForumParser implements ForumParser {


  private String           baseUrl          = null;
  private SubForumParser   subForumParser   = null;
  private HttpDataProvider httpDataProvider = null;


  public LegoForumParser(String baseUrl, HttpDataProvider httpDataProvider, ExecutorService executorService) {
    this.baseUrl = baseUrl;
    this.httpDataProvider = httpDataProvider;
    subForumParser = new LegoSubForumParser(baseUrl, httpDataProvider, executorService);
  }


  public Forum parseForum(String data) throws Exception {
    Document document = Jsoup.parse(data);

    Forum forum = new Forum();
    forum.title = "LEGO Message Board";
    forum.subForumList = parseSubForumList(document);

    return forum;
  }


  private List<SubForum> parseSubForumList(Document document) throws Exception {
    Elements elements = document.select("span.txt2BoldB > a");

    List<SubForum> subForumList = new ArrayList<SubForum>();

    for (Element element : elements) {
      String href = element.attr("href").trim();

      if (href.contains("ShowForum.aspx")) {
        System.out.println("Generate sub forums from " + baseUrl + href);
        String subForumData = httpDataProvider.downloadData(baseUrl + href);
        SubForum subForum = subForumParser.parseSubForum(subForumData);

        subForumList.add(subForum);
      }
    }

    return subForumList;
  }


  public static void main(String[] args) throws Exception {
    ExecutorService executorService = null;

    try {
      executorService = Executors.newFixedThreadPool(8);

      HttpDataProvider httpDataProvider = new HttpDataProvider();
      String data = httpDataProvider.downloadData("http://messageboards.lego.com/");

      LegoForumParser legoForumParser = new LegoForumParser("http://messageboards.lego.com/en-US", httpDataProvider, executorService);
      Forum forum = legoForumParser.parseForum(data);
      System.out.println(forum);

      GenericDAO.persist(forum);

      executorService.shutdown();
    }
    finally {
      if (executorService != null)
        executorService.shutdown();
    }
  }
}
