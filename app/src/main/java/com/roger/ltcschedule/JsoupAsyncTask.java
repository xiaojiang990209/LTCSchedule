package com.roger.ltcschedule;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/16.
 */

//public class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {
//
//    private static final String TAG = "JsoupAsyncTask";
//    private static final String urlStart = "http://www.ltconline.ca/WebWatch/Ada.aspx?";
//    private String url;
//    private RouteStopModel stopModel;
//
//    public JsoupAsyncTask(RouteStopModel stopModel) {
//        this.stopModel = stopModel;
//        //Concat the exact url needed to request the arrival time online.
//        this.url = urlStart + "r=" + stopModel.getRouteNumber()
//                              + "&d=" + stopModel.getDirection()
//                              + "&s=" + stopModel.getStopId();
//    }
//
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//    }
//
//    @Override
//    protected Void doInBackground(Void... params) {
//        try {
//            Log.d(TAG , stopModel.toString());
//            //Get the DOM of the url passed in
//            Document document = Jsoup.connect(url).get();
//            //Select all table rows under #tblADA
//            //i.e. (time) (destination)
//            Elements elements = document.select("#tblADA > tbody > tr");
//            //Only look at the middle ones.
//            //i.e. Do not take into account the first one and the last one.
//            for(int i = 1; i < elements.size() - 1; i++) {
//                Element element = elements.get(i);
//                //Select the arrival time under the tr element
//                stopModel.setArrivalTime(element.select("td:nth-child(1) > a").text());
//                //Select the destination under the tr element
//                stopModel.setDestination(element.select("td:nth-child(2)").text().substring(2));
//            }
//            System.out.println(TAG + ":" + stopModel.toString());
//        } catch (IOException e) {
//            Log.e(TAG, "doInBackground: " + e.toString());
//        }
//        return null;
//    }
//
//    @Override
//    protected void onPostExecute(Void result) {
//        super.onPostExecute(result);
//        System.out.println("JSoupAsyncTask finished");
//
//    }
//}

public class JsoupAsyncTask extends AsyncTask<RouteStopModel, Void, List<RouteStopModel>> {

    private static final String TAG = "JsoupAsyncTask";
    private static final String urlStart = "http://www.ltconline.ca/WebWatch/Ada.aspx?";
    private RecyclerView resultRecyclerView;

    public JsoupAsyncTask(RecyclerView recyclerView) {
        resultRecyclerView = recyclerView;
    }

    private RouteStopModel parseInvididualModel(RouteStopModel routeStopModel) {
        try {
            Log.d(TAG , routeStopModel.toString());
            //Concat the exact url needed to request the arrival time online.
            String URL = urlStart + "r=" + routeStopModel.getRouteNumber()
                    + "&d=" + routeStopModel.getDirection()
                    + "&s=" + routeStopModel.getStopId();
            //Get the DOM of the url passed in
            Document document = Jsoup.connect(URL).get();
            //Select all table rows under #tblADA
            //i.e. (time) (destination)
            Elements elements = document.select("#tblADA > tbody > tr");
            //Only look at the middle ones.
            //i.e. Do not take into account the first one and the last one.
            for(int i = 1; i < elements.size() - 1; i++) {
                Element element = elements.get(i);
                //Select the arrival time under the tr element
                routeStopModel.addArrivalTime(element.select("td:nth-child(1) > a").text());
                //Select the destination under the tr element
                routeStopModel.setDestination(element.select("td:nth-child(2)").text().substring(2));
            }
            System.out.println(TAG + ":" + routeStopModel.toString());
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: " + e.toString());
        }
        return routeStopModel;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<RouteStopModel> doInBackground(RouteStopModel... params) {
        int length = params.length;
        List<RouteStopModel> resultList = new LinkedList<>();
        for(int i = 0; i < length; i++) {
            // Parse every routeStopModel that is passed into the AsyncTask
            RouteStopModel routeStopModel = parseInvididualModel(params[i]);
            // If the parsed routeStopModel has an arrival time
            // i.e., there is another incoming bus, which yields
            // an arrival time not equal to null
            // Then we add it to the resultList to be displayed
            if(!routeStopModel.isArrivalTimeEmpty()) {
                resultList.add(routeStopModel);
            }
        }
        return resultList;
    }

    @Override
    protected void onPostExecute(List<RouteStopModel> result) {
        super.onPostExecute(result);
        System.out.println("JSoupAsyncTask finished");
        RecyclerView.Adapter dataAdapter = new ArrivalTimeAdapter(result);
        resultRecyclerView.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
    }
}
