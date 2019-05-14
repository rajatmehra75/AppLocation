package com.rajat.applocation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rajat.applocation.data.History;
import com.rajat.applocation.db.DB;
import com.rajat.applocation.service.LocationService;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class HistoryListActivity extends AppCompatActivity {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    List<History> historyList = new ArrayList<>();
    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_history_list);

        View view = findViewById(R.id.list);
        historyList = DB.getInstance(getApplicationContext()).getHistory();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyHistoryRecyclerViewAdapter(historyList, mListener));
        }
    }

    private OnListFragmentInteractionListener mListener= new OnListFragmentInteractionListener() {
        @Override
        public void onListFragmentInteraction(History item,String type) {
            if("open".equalsIgnoreCase(type)){
                openMapForDirections(item);
            } else if("delete".equalsIgnoreCase(type)){
                historyList.remove(item);
                DB.getInstance(getApplicationContext()).deleteHistory(item.getId());
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    };

    private void openMapForDirections(History item) {
        if(item!=null && item.getLatitude()!=0 && item.getLongitude()!=0 && LocationService.canGetLocation() && LocationService.getmCurrentLocation()!=null){
//                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", item.getLatitude(), item.getLongitude());
            String uri = "http://maps.google.com/maps?daddr=" + item.getLatitude() + "," + item.getLongitude() + "&saddr=" + LocationService.getmCurrentLocation().getLatitude() + "," + LocationService.getmCurrentLocation().getLongitude();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(History item,String type);
    }
}
