package wingsoloar.com.xjtlu_rooms.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by wingsolarxu on 2018/3/12.
 */

public class mDBLikelist {
    private mDBHelperLikelist helper;
    private Context context;
    private final String[] ORDER_COLUMNS = new String[] {"Name"};

    public mDBLikelist(Context context){
        this.context=context;
        helper=new mDBHelperLikelist(context);
    }


    public void add_to_likelist(String room_name){

        SQLiteDatabase db = null;

        try {
            db = helper.getWritableDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery("select * from Likelists",null);
            int num;
            if (cursor==null){
                num=0;
            }else{
                num=cursor.getCount();
            }

            // delete from Orders where Id = 7
            db.execSQL("INSERT INTO Likelists (Name) VALUES ('"+room_name+"')");
            db.setTransactionSuccessful();

        } catch (Exception e) {
            //Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }

    }

    public void delete_from_likelist(String room_name){
        SQLiteDatabase db = null;

        try {
            db = helper.getWritableDatabase();
            db.beginTransaction();

            // delete from Orders where Id = 7
            db.delete("Likelists", "Name = ?", new String[]{room_name});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    public boolean is_in_likelist(String room_name){

        SQLiteDatabase db = null;
        db = helper.getReadableDatabase();
        Cursor  cursor;
        cursor= db.rawQuery("SELECT * FROM Likelists WHERE Name= '"+room_name+"'",null);
        Log.e("likelist","SELECT * FROM Likelists WHERE Name= '"+room_name+"'");
        Log.e("likelist",cursor.getCount()+"");

        if (cursor != null&&cursor.getCount()!=0)
            return true;

        return false;
    }

    public List<String> get_likelist(){
        SQLiteDatabase db = null;
        db = helper.getReadableDatabase();
        Cursor  cursor = db.rawQuery("select * from Likelists",null);
        List<String> rooms=new ArrayList<String>();

        if (cursor != null && cursor.moveToFirst())
        for (int i=0;i<cursor.getCount();i++){
            String name=cursor.getString(0);
            rooms.add(name);
            cursor.moveToNext();
        }

        return rooms;
    }

}
