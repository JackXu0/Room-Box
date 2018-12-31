package wingsoloar.com.xjtlu_rooms.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import wingsoloar.com.xjtlu_rooms.Objects.Thought;

import static android.content.ContentValues.TAG;

/**
 * Created by wingsolarxu on 2018/3/9.
 */

public class mDBThoughts {
    private mDBHelperThoughts helper;
    private Context context;
    private final String[] ORDER_COLUMNS = new String[] {"Id", "Username","Building","Room","Date","DayOfWeek","Time","IsSecret"};

    public mDBThoughts(Context context){
        this.context=context;
        helper=new mDBHelperThoughts(context);
    }


    public void addThought(Thought thought){

        SQLiteDatabase db = null;

        try {
            db = helper.getWritableDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery("select * from Thoughts",null);
            int num=0;
            if (cursor!=null){
                num=cursor.getCount();
            }

            // delete from Orders where Id = 7
            db.execSQL("INSERT INTO Thoughts (Id, Username, Building, Room, Date,DayOfWeek, Time, Content, IsSecret, IsClock) VALUES (" +num+","+thought.getUsername()
                    +","+thought.getBuilding()+","+thought.getRoom()+","+thought.getDate()+","+thought.getDay_of_week()+","+thought.getTime_24()+","+thought.getContent()+","+thought.IsSecret()+","+thought.IsClock()+")");
            db.setTransactionSuccessful();
            Log.e("ccc",thought.getUsername());
            Log.e("ccc",thought.getDate());
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }

    }

    public List<Thought> queryAll(){
        SQLiteDatabase db = null;
        db = helper.getReadableDatabase();
        Cursor  cursor = db.rawQuery("select * from Thoughts ",null);
        List<Thought> thoughts= new ArrayList<Thought>();


        //Log.e("ccc",username);

        if (cursor != null && cursor.moveToFirst())
        for (int i=0;i<cursor.getCount();i++){


            int id=cursor.getInt(0);
            String username= cursor.getString(1);
            String building=cursor.getString(2);
            String room=cursor.getString(3);
            String date=cursor.getString(4);
            String day_of_week=cursor.getString(5);
            String time=cursor.getString(6);
            String content=cursor.getString(7);
            int isSecret=cursor.getInt(8);
            int isClock=cursor.getInt(9);


            Thought thought=new Thought(id,username,building,room,date,day_of_week,time,content,isSecret,isClock);
            thoughts.add(thought);
            cursor.moveToNext();
        }

        //Log.e("ccc",thoughts.size()+"");

        return thoughts;
    }
}
