package dev.countryfair.player.playlazlo.com.countryfair.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dev.countryfair.player.playlazlo.com.countryfair.model.HistoryItem;


public class ReceiptOCR extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "configDB";

    private static final String HISTORY_TABLE = "config";
    private static final int version = 1;

    private static final String KEY_ID = "id";
    private static final String KEY_RECEIPT_ID = "name";
    private static final String KEY_REF_ID = "latitude";
    private static final String KEY_LINE_ITEM = "longitude";
    private static final String KEY_OCCURRED_ON = "alarm_time";

    Context context;

    public ReceiptOCR(Context context) {
        super(context, DATABASE_NAME, null, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + HISTORY_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_RECEIPT_ID + " STRING, "
                + KEY_REF_ID + " STRING, "
                + KEY_LINE_ITEM + " STRING,"
                + KEY_OCCURRED_ON + " STRING) ";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HISTORY_TABLE);
        onCreate(sqLiteDatabase);
    }

    public List<HistoryItem> getHistoryItem() {
        List<HistoryItem> configItems = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + HISTORY_TABLE + " ORDER BY " + KEY_ID + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HistoryItem info = new HistoryItem(cursor);
                configItems.add(info);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Log.i("devolOTEst", "getHistoryItem: " + configItems.size());

        return configItems;
    }

    public void addConfigItem(HistoryItem item) {
        Log.i("devolOTEst", "addConfigItem");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_RECEIPT_ID, item.getReceiptId());
        values.put(KEY_REF_ID, item.getReceiptRefId());
        values.put(KEY_LINE_ITEM, item.getLineItemsCount());
        values.put(KEY_OCCURRED_ON, item.getOccurredOn());
        long i = db.insert(HISTORY_TABLE, null, values);
        db.close();
    }



}