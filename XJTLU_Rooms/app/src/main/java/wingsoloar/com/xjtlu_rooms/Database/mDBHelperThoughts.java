package wingsoloar.com.xjtlu_rooms.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wingsolarxu on 2018/3/9.
 */

public class mDBHelperThoughts extends SQLiteOpenHelper{

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "thoughts.db";
    public static final String TABLE_NAME = "Thoughts";

    public mDBHelperThoughts(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String sql = "create table if not exists Thoughts (Id int primary key, Username text, Building text, Room text, Date text, DayOfWeek text, Time text, Content text, IsSecret int, IsClock int)";
        sqLiteDatabase.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

}
