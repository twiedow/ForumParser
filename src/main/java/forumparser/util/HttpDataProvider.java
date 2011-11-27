

package forumparser.util;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import forumparser.model.WebPage;
import forumparser.persistence.WebPageDAO;


public class HttpDataProvider {


  private static class Proxy {


    public String url  = null;
    public int    port = -1;
  }

  private static List<Proxy> proxyList = new ArrayList<Proxy>();
  private static Random      random    = new Random();

  static {
    try {
      InputStream inputStream = HttpDataProvider.class.getClassLoader().getResourceAsStream("proxies.csv");
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

      String line = bufferedReader.readLine();

      while (line != null) {
        String[] values = line.trim().split(",");

        Proxy proxy = new Proxy();
        proxy.url = values[0];
        proxy.port = Integer.parseInt(values[1]);

        proxyList.add(proxy);
        line = bufferedReader.readLine();
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private boolean            useProxy  = false;


  public HttpDataProvider() {
    this(false);
  }


  public HttpDataProvider(boolean useProxy) {
    this.useProxy = useProxy;
  }


  public String downloadData(String url) throws Exception {
    WebPage webPage = WebPageDAO.findByUrl(url);

    if (webPage != null) {
      System.out.println(String.format("Returning content for url %s from cache.", url));
      return webPage.data;
    }

    HttpClient httpClient = null;
    String response = null;

    try {
      httpClient = new DefaultHttpClient();

      if (useProxy) {
        Proxy proxy = proxyList.get(random.nextInt(proxyList.size()));
        HttpHost proxyHost = new HttpHost(proxy.url, proxy.port);
        httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);
      }

      HttpGet httpGet = new HttpGet(url);

      ResponseHandler<String> responseHandler = new BasicResponseHandler();

      response = httpClient.execute(httpGet, responseHandler);

    }
    finally {
      if (httpClient != null)
        httpClient.getConnectionManager().shutdown();
    }

    System.out.println(String.format("Returning downloaded content for url %s.", url));

    webPage = new WebPage();
    webPage.url = url;
    webPage.data = response;
    WebPageDAO.persist(webPage);

    return response;
  }
}
