package wingsoloar.com.xjtlu_rooms.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import wingsoloar.com.xjtlu_rooms.Objects.Footprint;

/**
 * Created by wingsolarxu on 2018/3/11.
 */

public class mDBFootprints {

    private mDBHelperFootprints helper;
    private Context context;
    private final String[] ORDER_COLUMNS = new String[] {"Id", "Room","Date","Time","Duration","Bar_color","IsCouple"};

    public mDBFootprints(Context context){

        this.context=context;
        helper=new mDBHelperFootprints(context);
    }

    public void add_footprint(Footprint footprint){
        SQLiteDatabase db=null;

        try{
            db=helper.getWritableDatabase();
            db.beginTransaction();
            Cursor cursor=db.rawQuery("select * from Footprints",null);
            int num;
            if(cursor==null){
                num=0;
            }else{
                num=cursor.getCount();
            }

            db.execSQL("INSERT into Footprints (Id, Room, Date, Time, Duration, Bar_color, IsCouple) VALUES ("+num+",'"+footprint.getRoom_name()+"','"
            +footprint.getDate()+"',"+footprint.getHour()+","+footprint.getDuration()+",'"+footprint.get_bar_color()+"','"+footprint.isCouple()+"')");
//            Log.e("ccc","INSERT into Footprints (Id, Room, Date, Time, Duration, Bar_color, IsCouple) VALUES ("+num+",'"+footprint.getRoom_name()+"','"
//                    +footprint.getDate()+"',"+footprint.getHour()+","+footprint.getDuration()+",'"+footprint.get_bar_color()+"','"+footprint.isCouple()+"')");
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.e("ccc","aaaa");
        }finally {
            if(db!=null){
                db.endTransaction();
                db.close();
            }
        }
    }

    //Date format: "2018;3;12"
    public List<Footprint> getFootprints(String date){
        SQLiteDatabase db = null;
        db = helper.getReadableDatabase();
        Cursor  cursor = db.rawQuery("select * from Footprints where Date = '"+date+"'",null);
        List<Footprint> footprints=new ArrayList<Footprint>();
        Log.e("count","select * from Footprints where Date = '"+date+"'");

        if (cursor != null && cursor.moveToFirst())
        for (int i=0;i<cursor.getCount();i++){


            int id=cursor.getInt(0);
            String room_name=cursor.getString(1);
            int time=cursor.getInt(3);
            int duration=cursor.getInt(4);
            String bar_color=cursor.getString(5);
            String isCouple=cursor.getString(6);

            Footprint footprint=new Footprint(room_name,date,time,duration,bar_color,isCouple);
            footprints.add(footprint);
            cursor.moveToNext();
        }

        Log.e("raw",footprints.size()+"");

        return footprints;
    }

    public boolean is_marked(String date, String room_name){

        SQLiteDatabase db = null;
        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Footprints WHERE Date='" + date + "' and Room ='" + room_name + "'", null);
//        Log.e("ccc","select * from Footprints where Date = '" + date + "' and Room = '" + room_name + "'");
//        Log.e("ccc",(cursor.getCount())+"");
        if (cursor != null&&cursor.getCount()!=0)
            return true;

        return false;
    }

}
