package com.example.lab3databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.*;

public class MyDBHandler extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "products";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PRODUCT_NAME = "name";
    private static final String COLUMN_PRODUCT_PRICE = "price";
    private static final String DATABASE_NAME = "product.db";
    private static final int DATABASE_VERSION = 1;

    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_table_cmd = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_PRODUCT_NAME + " TEXT, " + COLUMN_PRODUCT_PRICE + " DOUBLE " + ")";

        db.execSQL(create_table_cmd);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null); // returns "cursor" all products from the table
    }

    public void addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_PRODUCT_NAME, product.getProductName());
        values.put(COLUMN_PRODUCT_PRICE, product.getProductPrice());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // I made 3 functions to interface with
    // for when searching name
    public ArrayList<Product> search(String productName){
        return searchProducts(productName, 0, false);
    }
    // for when searching price
    public ArrayList<Product> search(double productPrice){
        return searchProducts(null, productPrice, true);
    }
    // for when searching name and price
    public ArrayList<Product> search(String productName, double productPrice){
        return searchProducts(productName, productPrice, true);
    }

    // This gets called from the three nice public funciton above
    private ArrayList<Product> searchProducts(String name, double price, boolean hasPrice){
        SQLiteDatabase db = this.getReadableDatabase();
        String search;

        if (hasPrice && name == null){
            search = "SELECT * FROM " + TABLE_NAME +
                    " WHERE " + COLUMN_PRODUCT_PRICE + " = \"" + price + "\"";
        }
        else if (!hasPrice){
            search = "SELECT * FROM " + TABLE_NAME +
                    " WHERE " + COLUMN_PRODUCT_NAME + " LIKE \"" + name + "%\"";
        }
        else{
            search = "SELECT * FROM " + TABLE_NAME +
                    " WHERE " + COLUMN_PRODUCT_NAME + " LIKE \"" + name +
                    "%\" AND " + COLUMN_PRODUCT_PRICE + " = \"" + price + "\"";
        }

        Cursor c = db.rawQuery(search, null);

        ArrayList<Product> results = new ArrayList<>();
        if(c.moveToFirst()){
            do {
                Product product = new Product();
                product.setId(Integer.parseInt(c.getString(0)));
                product.setProductName(c.getString(1));
                product.setProductPrice(Double.parseDouble(c.getString(2)));
                results.add(product);
            }while (c.moveToNext());
        }
        else
            results = null;
        c.close();
        db.close();
        return results;
    }

    public boolean deleteProduct(String productName){
        boolean result = false;
        SQLiteDatabase db = this.getWritableDatabase();

        String search = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PRODUCT_NAME + " = \"" + productName + "\"";

        Cursor c = db.rawQuery(search, null);

        if (c.moveToFirst()){
            String idstr = c.getString(0);
            Log.d("Tag", "huh " + idstr);
            db.delete(TABLE_NAME, COLUMN_ID + " = " + idstr, null);
            result = true;
        }

        db.close();
        c.close();
        return result;
    }
}
