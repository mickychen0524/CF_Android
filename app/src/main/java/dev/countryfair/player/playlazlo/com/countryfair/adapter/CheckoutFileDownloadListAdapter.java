package dev.countryfair.player.playlazlo.com.countryfair.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;


import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dev.countryfair.player.playlazlo.com.countryfair.MainActivity;
import dev.countryfair.player.playlazlo.com.countryfair.R;
import dev.countryfair.player.playlazlo.com.countryfair.TicketListActivity;
import dev.countryfair.player.playlazlo.com.countryfair.helper.AppHelper;
import dev.countryfair.player.playlazlo.com.countryfair.helper.CircleDisplay;
import dev.countryfair.player.playlazlo.com.countryfair.helper.Constants;
import dev.countryfair.player.playlazlo.com.countryfair.service.ServiceResultReceiver;
import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by mymac on 3/18/17.
 */

public class CheckoutFileDownloadListAdapter extends ArrayAdapter<JSONObject> {
    private static final String TAG = CheckoutFileDownloadListAdapter.class.getSimpleName();
    private final Activity mContext;

    private List<JSONObject> shoppingCartList = new ArrayList<JSONObject>();
    private List<JSONObject> downloadFileList = new ArrayList<JSONObject>();
    private List<JSONObject> ticketSuccessList = new ArrayList<JSONObject>();
    private List<JSONObject> ticketErrorList = new ArrayList<JSONObject>();

    private Intent mServiceIntent;
    private ServiceResultReceiver mReceiverForDownload;

    public CheckoutFileDownloadListAdapter(Activity mContext, List<JSONObject> shoppingCartList, List<JSONObject> downloadFileList, List<JSONObject> ticketSuccessList, List<JSONObject> ticketErrorList) {
        super(mContext, R.layout.checkout_file_download_row, downloadFileList);
        this.mContext = mContext;
        this.shoppingCartList = shoppingCartList;
        this.downloadFileList = downloadFileList;
        this.ticketSuccessList = ticketSuccessList;
        this.ticketErrorList = ticketErrorList;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        setupServiceReceiver();
        final JSONObject downloadFileItem = downloadFileList.get(position);
        LayoutInflater inflater = mContext.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.checkout_file_download_row, null, true);

        final CircleDisplay cd = (CircleDisplay) rowView.findViewById(R.id.checkout_download_list_circle_progress);
        cd.setValueWidthPercent(15f);
        cd.setTextSize(16f);
        cd.setColor(Color.GREEN);
        cd.setDrawText(true);
        cd.setDrawInnerCircle(true);
        cd.setFormatDigits(1);
        cd.setUnit("%");
        cd.setStepSize(0.5f);

        // cd.setCustomText(...); // sets a custom array of text
        cd.showValue(0f, 100f, false);

        final TextView txtProgress = (TextView) rowView.findViewById(R.id.checkout_download_list_status_txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.checkout_download_list_tile_image);

        txtProgress.setText("Started...");
        try {
            String localFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/"+Constants.DIR_ROOT+"/";
            String localFilePathForTicket = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/"+Constants.DIR_ROOT+"/"+Constants.DIR_TICKETS+"/";
            String animatedTileName = getTileUrl(downloadFileItem.getString("channelRefId"));

            File animatedImgFile = new File(localFilePath + animatedTileName);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 3;

            if(animatedImgFile.exists()){

                try {
                    GifDrawable animatedTileImage = new GifDrawable(animatedImgFile);
                    imageView.setImageDrawable(animatedTileImage);
                } catch (Exception e) {
                    Log.e("input stream error -->", e.getMessage());
                }
            }

            if (!downloadFileItem.getBoolean("downloaded")) {
                if (downloadFileItem.isNull("sasUri")) {
                    txtProgress.setText("Ticket error");
                    if(mOnTicketDownloadListener != null){
                        cd.showValue(0f, 100f, false);
                        mOnTicketDownloadListener.onDataChanged(position, false);
                    }
                } else {
                    String sassUrl = downloadFileItem.getString("sasUri");
                    String fileName = downloadFileItem.getString("fileName");

                    if (!(new File(localFilePathForTicket).exists())) {
                        new File(localFilePathForTicket).mkdir();
                    }
                    Ion.with(mContext)
                            .load(sassUrl)
                            .progress(new ProgressCallback() {
                                @Override
                                public void onProgress(final long downloaded, final long total) {
                                    mContext.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            cd.showValue(100 * downloaded / total, 100f, false);
                                            Log.d(TAG,"" + downloaded + " / " + total);
                                        }
                                    });
                                }
                            })
                            .write(new File(localFilePathForTicket + fileName))
                            .setCallback(new FutureCallback<File>() {
                                @Override
                                public void onCompleted(final Exception e, File file) {
                                    mContext.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (e == null) {
                                                txtProgress.setText("Download complete");
                                                cd.showValue(100f, 100f, false);
                                                if(mOnTicketDownloadListener != null){
                                                    mOnTicketDownloadListener.onDataChanged(position, true);
                                                }
                                            } else {
                                                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            });
                }
            } else {
                cd.showValue(100f, 100f, false);
                txtProgress.setText("Download complete");
            }

        } catch (Exception ex) {

        }
        return rowView;
    }

    public void setupServiceReceiver() {
        mReceiverForDownload = new ServiceResultReceiver(new Handler());
        // This is where we specify what happens when data is received from the service
        mReceiverForDownload.setReceiver(new ServiceResultReceiver.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == mContext.RESULT_OK) {
                    try{
                        Intent intentMain = new Intent(mContext, MainActivity.class);
                        Intent intentTicket = new Intent(mContext, TicketListActivity.class);

                        switch (resultData.getInt("resultCode")) {
                            case Constants.DOWNLOAD_COMPLETE:
                                Toast.makeText(mContext, "progress : " + String.valueOf(100), Toast.LENGTH_SHORT).show();
//                                cd.showValue(100f, 100f, false);
                                break;
                            case Constants.DOWNLOAD_EXCEPRION:
                                Toast.makeText(mContext, "progress : " + "exception", Toast.LENGTH_SHORT).show();
//                                cd.showValue(0f, 100f, false);
                                break;
                            case Constants.DOWNLOAD_FAILED:
                                Toast.makeText(mContext, "progress : " + "download error", Toast.LENGTH_SHORT).show();
//                                cd.showValue(0f, 100f, false);
                                break;
                            case Constants.DOWNLOAD_PICK:
                                Float percent = resultData.getFloat("resultPercent");
                                Toast.makeText(mContext, "progress : " + String.valueOf(percent), Toast.LENGTH_SHORT).show();
//                                cd.showValue(percent, 100f, false);

                                break;
                        }
                    } catch (Exception e) {
                        Log.e("jsonparsing--->", e.getMessage());
                    }
                }
            }
        });
    }

    private void ticketFileDownload() {

//        mServiceIntent = new Intent(mContext, TicketDownloadService.class);
//        TicketDownloadService.shoppingCartData = shoppingCartList;
//        try {
//            mServiceIntent.putExtra("fileItem", downloadFileItem.toString());
//        } catch (Exception e) {
//            Log.e("jsonparsing--->", e.getMessage());
//        }
//
//        mServiceIntent.putExtra("receiver", mReceiverForDownload);
//        mContext.startService(mServiceIntent);

    }

    private String getTileUrl(String channelRefId) {
        try {
            for (JSONObject channelGrupItem : Constants.channelGroupGlobalArr) {
                List<JSONObject> channelList = AppHelper.parseFromJsonList(channelGrupItem.getJSONArray("channels"));
                for (JSONObject channelItem : channelList) {
                    if (channelItem.getString("channelRefId").equals(channelRefId)) {
                        JSONObject ticketTemplateData = channelItem.getJSONObject("ticketTemplate");
                        return ticketTemplateData.getString("tileAnimatedUrl");
                    }
                }
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    // adapter interface to update activity textview value
    public interface OnTicketDownloadListener{
        public void onDataChanged(int position, boolean ticketStatus);
    }
    OnTicketDownloadListener mOnTicketDownloadListener;
    public void setTicketDownloadListener(OnTicketDownloadListener onTicketDownloadListener){
        mOnTicketDownloadListener = onTicketDownloadListener;
    }
}
