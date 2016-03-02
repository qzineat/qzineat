package com.codepath.qzineat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Shyam Rokde on 3/2/16.
 *
 * All APIs for QZinEat App
 */

public class QzinEatClient {

  private static final String API_BASE_URL = "https://raw.githubusercontent.com/qzineat/qzineat/";

  private AsyncHttpClient client;

  public QzinEatClient(){
    client = new AsyncHttpClient();
  }

  private String getApiUrl(String relativeUrl) {
    return API_BASE_URL + relativeUrl;
  }

  /**
   * Get Events
   * TODO: We can change this later for getting events based on "query"
   */
  public void getEvents(AsyncHttpResponseHandler handler){
    RequestParams params = new RequestParams();

    client.get(getApiUrl("master/eventlist.json"), params, handler);
  }
}
