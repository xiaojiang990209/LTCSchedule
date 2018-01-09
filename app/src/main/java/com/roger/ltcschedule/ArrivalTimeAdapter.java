package com.roger.ltcschedule;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/10/15.
 */

public class ArrivalTimeAdapter extends RecyclerView.Adapter<ArrivalTimeAdapter.ViewHolder> {

    private List<RouteStopModel> mDataSet;
    private static String TAG = "ArrivalTimeAdapter";

    // Provides a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Each data item is a route_stop_item.xml
        public TextView routeNumberTextView;
        public TextView routeDestinationTextView;
        public TextView stopNameTextView;
        public TextView[] arrivalTimeTextViews;

        public ViewHolder(View itemView) {
            super(itemView);
            // Match the individual layout items with the
            // corresponding java variables for further
            // manipulation
            routeNumberTextView = (TextView) itemView.findViewById(R.id.route_number);
            routeDestinationTextView = (TextView) itemView.findViewById(R.id.route_destination);
            stopNameTextView = (TextView) itemView.findViewById(R.id.stop_name);
            arrivalTimeTextViews = new TextView[3];
            arrivalTimeTextViews[0] = (TextView) itemView.findViewById(R.id.arrival_time_1);
            arrivalTimeTextViews[1] = (TextView) itemView.findViewById(R.id.arrival_time_2);
            arrivalTimeTextViews[2] = (TextView) itemView.findViewById(R.id.arrival_time_3);
        }
    }

    public ArrivalTimeAdapter(List<RouteStopModel> dataSet) {
        mDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ArrivalTimeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate custom layout
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.route_stop_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    // Replace the content of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RouteStopModel routeStopModel = mDataSet.get(position);
        holder.routeNumberTextView.setText(routeStopModel.getRouteNumber());
        holder.routeDestinationTextView.setText(routeStopModel.getDestination());
        holder.stopNameTextView.setText(routeStopModel.getStopName());
        System.out.println(TAG + ", " + routeStopModel.getRouteNumber() + ", "
                + routeStopModel.getDestination() + ", size = " + routeStopModel.getArrivalTime().size());
        for(int i = 0; i < routeStopModel.getArrivalTime().size(); i++) {
            System.out.println(TAG + ", " + routeStopModel.getArrivalTime().get(i));
            holder.arrivalTimeTextViews[i].
                    setText(routeStopModel.getArrivalTime().get(i));
            // Fix occasional IndexOutOfBounds Exception
            if (i >= 3) break;
        }
    }

    // Return the size of the dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
