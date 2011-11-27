

package forumparser.parser.lego;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import forumparser.model.SubForum;
import forumparser.model.Topic;
import forumparser.parser.SubForumParser;
import forumparser.parser.TopicParser;
import forumparser.persistence.GenericDAO;
import forumparser.util.HttpDataProvider;


public class LegoSubForumParser implements SubForumParser {


  private static class TopicFetcherCallable implements Callable<Topic> {


    private String           url;
    private TopicParser      topicParser;
    private HttpDataProvider httpDataProvider;


    public TopicFetcherCallable(String url, TopicParser topicParser, HttpDataProvider httpDataProvider) {
      this.url = url;
      this.topicParser = topicParser;
      this.httpDataProvider = httpDataProvider;
    }


    public Topic call() throws Exception {
      String topicData = httpDataProvider.downloadData(url);
      Topic topic = topicParser.parseTopic(topicData);
      return topic;
    }
  }

  private final static String HEAD_TITLE_PREFIX = "LEGO.com  Messageboards Forum : ";

  private HttpDataProvider    httpDataProvider  = null;
  private ExecutorService     executorService   = null;

  private String              baseUrl           = null;
  private TopicParser         topicParser       = null;


  public LegoSubForumParser(String baseUrl, HttpDataProvider httpDataProvider, ExecutorService executorService) {
    this.baseUrl = baseUrl;
    this.httpDataProvider = httpDataProvider;
    this.executorService = executorService;

    topicParser = new LegoTopicParser(baseUrl, httpDataProvider);
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
    List<Future<Topic>> futureList = new ArrayList<Future<Topic>>();

    for (Element element : elements) {
      String href = element.attr("href").trim();

      if (href.contains("ShowPost.aspx")) {
        // System.out.println("Generate topics from " + baseUrl + href);
        Future<Topic> future = executorService.submit(new TopicFetcherCallable(baseUrl + href, topicParser, httpDataProvider));
        futureList.add(future);
        // String topicData = httpDataProvider.downloadData(baseUrl + href);
        // Topic topic = topicParser.parseTopic(topicData);
        //
        // topicList.add(topic);
      }
    }

    System.out.println("# of submitted callables: " + futureList.size());

    int counter = 0;
    for (Future<Topic> future : futureList) {
      Topic topic = future.get();
      System.out.println(String.format("#%d future returned.", Integer.valueOf(counter)));
      topicList.add(topic);
      counter++;
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
    ExecutorService executorService = null;

    try {
      executorService = Executors.newFixedThreadPool(8);

      HttpDataProvider httpDataProvider = new HttpDataProvider();
      String data = httpDataProvider.downloadData("http://messageboards.lego.com/en-US/showforum.aspx?ForumID=1440");

      LegoSubForumParser legoSubForumParser = new LegoSubForumParser("http://messageboards.lego.com/en-US", httpDataProvider, executorService);
      SubForum subForum = legoSubForumParser.parseSubForum(data);
      System.out.println(subForum);

      GenericDAO.persist(subForum);

    }
    finally {
      if (executorService != null)
        executorService.shutdown();
    }
  }
}
