package dev.countryfair.player.playlazlo.com.countryfair.model;

import android.database.Cursor;

import com.google.gson.Gson;

/**
 * Created by skilz on 10/24/17.
 */

public class HistoryItem {

    //{"receiptRefId":"", "occurredOn":"", "receiptId":"", "lineItemsCount":12}

    private String receiptRefId;
    private String occurredOn;
    private String receiptId;
    private String lineItemsCount;

    public HistoryItem(String receiptRefId, String occurredOn, String receiptId, String lineItemsCount) {
        this.receiptRefId = receiptRefId;
        this.occurredOn = occurredOn;
        this.receiptId = receiptId;
        this.lineItemsCount = lineItemsCount;
    }

    public HistoryItem(Cursor cursor) {
        receiptRefId = cursor.getString(1);
        receiptId = cursor.getString(2);
        lineItemsCount = cursor.getString(3);
        occurredOn = cursor.getString(4);
    }


    public String getReceiptRefId() {
        return receiptRefId;
    }

    public void setReceiptRefId(String receiptRefId) {
        this.receiptRefId = receiptRefId;
    }

    public String getOccurredOn() {
        return occurredOn;
    }

    public void setOccurredOn(String occurredOn) {
        this.occurredOn = occurredOn;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getLineItemsCount() {
        return lineItemsCount;
    }

    public void setLineItemsCount(String lineItemsCount) {
        this.lineItemsCount = lineItemsCount;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
