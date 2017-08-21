package com.example.brandonpaw.receipttracker;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Brandon Paw on 6/14/2017.
 */

public class Receipt {

    // Fields to write to the firebase database
    public Long rid;
    public String receipt;
    public double tip;
    public double tax;
    public double total;
    public String date;
    public ArrayList<String> folders;
    public String photoPath;

    public Receipt() {}

    public Receipt(String receipt, double tip, double tax, double total, ArrayList<String> Xfolders, String path) {
        this.receipt = receipt;
        this.tip = tip;
        this.tax = tax;
        this.total = total;
        this.folders = new ArrayList<>();
        for (String folder : Xfolders) {

            folders.add(folder);
        }
        this.photoPath = path;
    }

    public static Receipt JSONtoReceipt(JSONObject json) throws JSONException {
        Receipt convertedRec = new Receipt();
        convertedRec.receipt = json.getString("receipt");
        convertedRec.date = json.getString("date");
        convertedRec.total = json.getDouble("total");
        convertedRec.tip = json.getDouble("tip");
        convertedRec.tax = json.getDouble("tax");
        convertedRec.rid = json.getLong("rid");
        return convertedRec;
    }

    @Override
    public String toString() {
        return receipt + " : " + rid;
    }
}
