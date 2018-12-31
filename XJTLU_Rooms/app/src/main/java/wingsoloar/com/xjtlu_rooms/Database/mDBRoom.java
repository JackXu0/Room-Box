package wingsoloar.com.xjtlu_rooms.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import wingsoloar.com.xjtlu_rooms.Objects.Room;

import static android.content.ContentValues.TAG;

/**
 * Created by wingsolarxu on 2017/9/22.
 */

public class mDBRoom {
    private mDBHelperRoom helper;
    private Context context;
    private final String[] ORDER_COLUMNS = new String[] {"Name", "Type","Building","Timetable"};

    public mDBRoom(Context context){
        this.context=context;
        helper=new mDBHelperRoom(context);
    }

    public List<Room> filter(String room_type, String wifi, String printer){
        SQLiteDatabase db = null;
        db = helper.getReadableDatabase();
        Cursor cursor;
        switch (wifi+";"+printer){
            case "strong;yes":
                if (room_type.length()>1) {
                    cursor = db.rawQuery("select * from Rooms where Type in (" + room_type + ") and Wifi = '" + wifi + "' and Printer = '" + printer + "'", null);
                    Log.e("ccd","select * from Rooms where Type in (" + room_type + ") and Wifi = '" + wifi + "' and Printer = '" + printer + "'");
                }else{
                    cursor=db.rawQuery("select * from Rooms where Wifi = '" + wifi + "' and Printer = '" + printer + "'", null);
                }
                break;
            case "strong;no":
                if (room_type.length()>1) {
                    cursor = db.rawQuery("select * from Rooms where Type in (" + room_type + ") and Wifi = '" + wifi + "'", null);
                    Log.e("ccd","select * from Rooms where Type in (" + room_type + ") and Wifi = '" + wifi + "'");
                }else{
                    cursor=db.rawQuery("select * from Rooms where Wifi = '" + wifi + "'", null);
                }
                break;
            case "weak;yes":
                if (room_type.length()>1) {
                    cursor = db.rawQuery("select * from Rooms where Type in (" + room_type + ") and Printer = '" + printer + "'", null);
                    Log.e("ccd","select * from Rooms where Type in (" + room_type + ") and Printer = '" + printer + "'");
                }else{
                    cursor=db.rawQuery("select * from Rooms where Printer = '" + printer + "'", null);
                    Log.e("ccd","select * from Rooms where Printer = '" + printer + "'");
                }
                break;
            case "weak;no":
                if (room_type.length()>1) {
                    cursor = db.rawQuery("select * from Rooms where Type in (" + room_type + ") ", null);
                    Log.e("ccd","select * from Rooms where Type in (" + room_type + ") ");
                }else{
                    cursor=db.rawQuery("select * from Rooms", null);
                }
                break;
            default:
                if (room_type.length()>1) {
                    cursor = db.rawQuery("select * from Rooms where Type in (" + room_type + ") ", null);
                    Log.e("ccd","select * from Rooms where Type in (" + room_type + ") ");
                }else{
                    cursor=db.rawQuery("select * from Rooms", null);
                }
                break;
        }

        List<Room> rooms=new ArrayList<>();

        if (cursor != null && cursor.moveToFirst())
            rooms = new ArrayList<Room>(cursor.getCount());
        for (int i=0;i<cursor.getCount();i++){
            String name=cursor.getString(0);
            String type=cursor.getString(1);
            String building=cursor.getString(2);
            String timetable=cursor.getString(3);
            String wifi_String=cursor.getString(4);
            String printer_string=cursor.getString(5);

            Room room = new Room(name,type,building,timetable,wifi_String,printer_string);
            rooms.add(room);
            cursor.moveToNext();
        }

        return rooms;
    }

    public List<Room> queryAll(){
        SQLiteDatabase db = null;
        db = helper.getReadableDatabase();
        Cursor  cursor = db.rawQuery("select * from Rooms ",null);
        List<Room> rooms=new ArrayList<>();

        if (cursor != null && cursor.moveToFirst())
            rooms = new ArrayList<Room>(cursor.getCount());
        for (int i=0;i<cursor.getCount();i++){
            String name=cursor.getString(0);
            String type=cursor.getString(1);
            String building=cursor.getString(2);
            String timetable=cursor.getString(3);
            String wifi=cursor.getString(4);
            String printer=cursor.getString(5);

            Room room = new Room(name,type,building,timetable,wifi,printer);
            rooms.add(room);
            cursor.moveToNext();
        }

        return rooms;
    }

    public void addAll(List<String> rooms){

        SQLiteDatabase db = null;
        for(int i=0;i<rooms.size();i++){
            try {
                db = helper.getWritableDatabase();
                db.beginTransaction();
                //int area=Integer.parseInt(rooms.get(i).split(";")[4]);
                Log.e("info",rooms.get(i));
                String wifi;
                String has_printer;
                String[] area_wifi_strong= new String[]{"3","54","40","60","39","58","81","36","37","53","68","63","61","1","59","79"};
                String[] area_has_printer=new String[]{"24","9","3","54","40","60","39","58","81","36","26","80","37","53","68","17","67","63","61","1","59","79","27"};
                if(Arrays.asList(area_wifi_strong).contains(rooms.get(i).split(";")[4])){
                    wifi="strong";
                }else{
                    wifi="weak";
                }

                if(Arrays.asList(area_has_printer).contains(rooms.get(i).split(";")[4])){
                    has_printer="yes";
                }else{
                    has_printer="no";
                }
                db.execSQL("insert into Rooms (Name,Type,Building,Timetable,Wifi, Printer) values('"
                        +rooms.get(i).split(";")[0].trim()+"','"
                        +rooms.get(i).split(";")[1].trim()+"','"
                        +rooms.get(i).split(";")[2].trim()+"','"
                        +rooms.get(i).split(";")[3].trim()+"','"
                        +wifi+"','"
                        +has_printer+"')");

                db.setTransactionSuccessful();
            }catch (Exception e){
                Log.e(TAG, "", e);
            }finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
            }
        }

    }

    public void clear(){
        SQLiteDatabase db = null;

        try {
            db = helper.getWritableDatabase();
            db.beginTransaction();
            db.execSQL("delete from Rooms");
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

    public Room getRoom(String room_name){
        SQLiteDatabase db = null;
        db = helper.getReadableDatabase();
        Cursor  cursor = db.rawQuery("select * from Rooms where Name = '"+room_name+"'",null);

        if(cursor.getCount()==0)
            return null;
        else{
            cursor.moveToFirst();
            String name=cursor.getString(0);
            String type=cursor.getString(1);
            String building=cursor.getString(2);
            String timetable=cursor.getString(3);
            String wifi=cursor.getString(4);
            String printer=cursor.getString(5);

            Room room = new Room(name,type,building,timetable,wifi,printer);

            return room;
        }
    }


}
