package com.roger.ltcschedule;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by Administrator on 2017/9/16.
 */

public class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "JsoupAsyncTask";
    private static final String urlStart = "http://www.ltconline.ca/WebWatch/Ada.aspx?";
    private String url;
    private RouteStopModel stopModel;

    public JsoupAsyncTask(RouteStopModel stopModel) {
        this.stopModel = stopModel;
        //Concat the exact url needed to request the arrival time online.
        this.url = urlStart + "r=" + stopModel.getRouteNumber()
                              + "&d=" + stopModel.getDirection()
                              + "&s=" + stopModel.getStopId();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Log.d(TAG , stopModel.toString());
            //Get the DOM of the url passed in
            Document document = Jsoup.connect(url).get();
            //Select all table rows under #tblADA
            //i.e. (time) (destination)
            Elements elements = document.select("#tblADA > tbody > tr");
            //Only look at the middle ones.
            //i.e. Do not take into account the first one and the last one.
            for(int i = 1; i < elements.size() - 1; i++) {
                Element element = elements.get(i);
                //Select the arrival time under the tr element
                stopModel.setArrivalTime(element.select("td:nth-child(1) > a").text());
                //Select the destination under the tr element
                stopModel.setDestination(element.select("td:nth-child(2)").text().substring(2));
            }
            System.out.println(TAG + ":" + stopModel.toString());
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: " + e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {

    }
}
