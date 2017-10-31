package dev.countryfair.player.playlazlo.com.countryfair;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;

import dev.countryfair.player.playlazlo.com.countryfair.database.ReceiptOCR;
import dev.countryfair.player.playlazlo.com.countryfair.model.HistoryItem;

/**
 * Created by admin on 10/23/17.
 */

public class HistoryActivity extends FragmentActivity {

    private static final String TAG = HistoryActivity.class.getSimpleName();
    private RecyclerView rvHistory;
    private HistoryListAdapter mHistoryListAdapter;
    private JSONObject receivedObj = new JSONObject();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("devoloTest", "HistoryActivity");
        setContentView(R.layout.activity_history);
        init();
    }

    private void init(){

        mHistoryListAdapter = new HistoryActivity.HistoryListAdapter(new ReceiptOCR(this).getHistoryItem());
        rvHistory = (RecyclerView) findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setHasFixedSize(true);
        rvHistory.setAdapter(mHistoryListAdapter);


    }


    public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ItemHolder>{

        List<HistoryItem> historyItem;

        public class ItemHolder extends RecyclerView.ViewHolder {
            public TextView tvData;

            public ItemHolder(View itemView) {
                super(itemView);
                tvData = (TextView) itemView.findViewById(R.id.tvData);
            }
        }


        public HistoryListAdapter(List<HistoryItem> historyItem) {
            this.historyItem = historyItem;
        }

        @Override
        public HistoryListAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int type) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_history, parent, false);
            return new ItemHolder(v);
        }

        @Override
        public void onBindViewHolder(HistoryListAdapter.ItemHolder holder, final int position) {
//            final JSONObject loyalty = loyaltyList.get(position);
//            try {
//
//                LayerDrawable layerDrawable = (LayerDrawable) holder.rbPendingPoints.getProgressDrawable();
//                DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(0)), Color.WHITE);   // Empty star
//                DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(1)), ContextCompat.getColor(LoyaltyActivity.this,R.color.colorAccent)); // Partial star
//                DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(2)), ContextCompat.getColor(LoyaltyActivity.this,R.color.colorAccent));
//
            holder.tvData.setText(historyItem.get(position).toString());
//                holder.rbPendingPoints.setRating((float)loyalty.getDouble("pendingCount")/2);
//                holder.tvLifetimePoints.setText(String.valueOf(loyalty.getDouble("lifetimeCount")));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public int getItemCount() {
            return historyItem.size();
        }
    }
}
