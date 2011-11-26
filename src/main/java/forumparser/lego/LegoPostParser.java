

package forumparser.lego;


import java.text.SimpleDateFormat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import forumparser.PostParser;
import forumparser.model.Post;


public class LegoPostParser implements PostParser {


  private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("'Posted:' dd MMM yyyy - hh:mmaa");


  public Post parsePost(String data) throws Exception {
    Post post = new Post();

    Document doc = Jsoup.parseBodyFragment(data);
    Element body = doc.body();

    Element tagWithUsername = body.select("a").first();
    Element tagWithContent = body.select("div").get(1);
    Element tagWithTimestamp = body.select("table span").first();

    String referencedUsername = null;

    StringBuffer content = new StringBuffer();
    for (Node node : tagWithContent.childNodes()) {
      if (node instanceof TextNode)
        content.append(((TextNode) node).text()).append("\n");
      else if (node instanceof Element) {
        Element element = (Element) node;

        if (!element.tagName().equals("blockquote")) {
          content.append(((Element) node).text()).append("\n");
        }
        else {
          Element tagWithQuotedUser = element.select("div > strong").first();
          int usernameRange = tagWithQuotedUser.text().indexOf("wrote:");
          referencedUsername = tagWithQuotedUser.text().substring(0, usernameRange).trim();
        }
      }
    }

    post.username = tagWithUsername.text();
    post.content = content.toString().trim().replaceAll("\n", "");
    post.timestamp = simpleDateFormat.parse(tagWithTimestamp.text());
    post.referencedUsername = referencedUsername != null ? referencedUsername : post.username;

    return post;
  }
}
