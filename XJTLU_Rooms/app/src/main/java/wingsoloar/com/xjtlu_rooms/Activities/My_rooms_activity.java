package wingsoloar.com.xjtlu_rooms.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wingsoloar.com.xjtlu_rooms.Objects.Room;
import wingsoloar.com.xjtlu_rooms.Database.mDBLikelist;
import wingsoloar.com.xjtlu_rooms.Database.mDBRoom;
import wingsoloar.com.xjtlu_rooms.R;
import wingsoloar.com.xjtlu_rooms.Threads.CheckInternetThread;
import wingsoloar.com.xjtlu_rooms.Threads.GetPeopleInfoThread;

/**
 * Created by wingsolarxu on 2018/3/12.
 */

public class My_rooms_activity extends Activity {

    private mDBRoom databaseRoom;
    private mDBLikelist databaseLikelist;
    private List<Room> rooms_raw;
    private List<Room> actual_rooms;
    private List<String> extra;
    private mListViewAdapter adapter;
    private ListView rooms_view;
    private ImageView back_icon;

    private SharedPreferences preference_lastTime;
    private CircularProgressView progressView;

    private String lastTime="";
    private Thread thread;

    private int current_hour;
    private HashMap<String,String> hasPeople=new HashMap<>();
    private List<String> room_names;

    Typeface fontFace ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_room_main);

        actual_rooms = new ArrayList<Room>();
        rooms_raw=new ArrayList<Room>();
        extra=new ArrayList<String>();
        databaseRoom = new mDBRoom(this);
        databaseLikelist=new mDBLikelist(this);
        adapter=new mListViewAdapter(actual_rooms,hasPeople);

        rooms_view=findViewById(R.id.rooms_per_building_listView);
        progressView=findViewById(R.id.progress_view);
        back_icon=findViewById(R.id.back_button);

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rooms_view.setAdapter(adapter);

        room_names=databaseLikelist.get_likelist();

        if (room_names.size()==0){
            finish();
            Toast.makeText(getBaseContext(),"No Liked Rooms",Toast.LENGTH_SHORT).show();
            return;
        }

        initTest();

        new GetPeopleInfoThread(myHandler,0).start();

    }

    @Override
    protected void onResume() {

        new GetPeopleInfoThread(myHandler,1).start();
        super.onResume();
    }

    //finish the activity if cannot connect to XJTLU campus wifi and have not crawled data earlier this day
    private void initTest(){

        preference_lastTime=getSharedPreferences("lastTime",MODE_PRIVATE);
        lastTime=preference_lastTime.getString("lastTime","");

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        current_hour=hour;

        String current_time= year+"/"+month+"/"+day;


        //if have not crawled data earlier this day
        if(!current_time.equals(lastTime)){
            new CheckInternetThread(myHandler).start();

        }
    }

    private void getAllRooms(){
        actual_rooms.clear();
        extra.clear();
        Log.e("dafdfaf size",hasPeople.size()+"");

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        current_hour=hour;

        String current_time= year+"/"+month+"/"+day;
        Log.e("eeeeee",current_time);

        preference_lastTime=getSharedPreferences("lastTime",MODE_PRIVATE);
        lastTime=preference_lastTime.getString("lastTime","");
        Log.e("eeeeee",lastTime);

        //if this is the first attemp to get all rooms data, crawl from Room Booking Website of XJTLU
        if(!lastTime.equals(current_time)) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressView=findViewById(R.id.progress_view);
                    progressView.setVisibility(View.VISIBLE);
                }
            });

            thread=Thread.currentThread();

            //get all rooms information from internet and store them in local database
           // GetRoomData.crawl(My_rooms_activity.this,getApplicationContext(),thread);

            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressView.stopAnimation();
                    progressView.setVisibility(View.GONE);
                }
            });

            //stored the latest date that crawled data in local storage
            final SharedPreferences.Editor editor = preference_lastTime.edit();
            editor.putString("lastTime",current_time);
            editor.commit();
        }

        for (int i=0;i<room_names.size();i++){
            rooms_raw.add(databaseRoom.getRoom(room_names.get(i)));
        }

        for(int i=0;i<rooms_raw.size();i++){
            if (rooms_raw.get(i).isAvailable(current_hour)) {
                Log.e("room1","success");
                actual_rooms.add(rooms_raw.get(i));
                extra.add("Available");
            }
        }
        for (int i = current_hour+1; i < 20; i++) {
            for(int j=0;j<rooms_raw.size();j++)
                if (rooms_raw.get(j).isAvailable(i)) {
                    Log.e("room1","success");
                    if(!ifContain(rooms_raw.get(j).getName())){
                        actual_rooms.add(rooms_raw.get(j));
                        extra.add(i + " : 00");
                    }
                }

        }

        //update listview
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try{
                    adapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean ifContain(String s){
        for(int i=0;i<actual_rooms.size();i++){
            if(actual_rooms.get(i).getName().equals(s))
                return true;
        }
        return false;
    }

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.e("sss","2");
            Bundle b;
            int code;

            switch (msg.what){
                case 1:
                    b=msg.getData();
                    code=b.getInt("response_code");
                    if(code ==0){
                        Toast.makeText(getApplicationContext(),"Please check interner access",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;

                case 2:
                    Log.e("result3",msg.getData().getInt("index")+"");

                    Log.e("index",msg.getData().getInt("index")+"");

                    hasPeople.clear();
                    hasPeople.putAll((Map<String,String>) msg.obj);

                    if(msg.getData().getInt("index")==0){
                        getAllRooms();
                    }else{

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }
                    break;

                case 3:

                    progressView.stopAnimation();
                    progressView.setVisibility(View.GONE);

                    actual_rooms.clear();
                    extra.clear();

                    Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    final int month = c.get(Calendar.MONTH)+1;
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    current_hour=hour;

                    String current_time= year+"/"+month+"/"+day;

                    final SharedPreferences.Editor editor = preference_lastTime.edit();
                    editor.putString("lastTime",current_time);
                    editor.commit();

                    rooms_raw=databaseRoom.queryAll();

                    Log.e("room raw size",rooms_raw.size()+"");

                    for (int i=0;i<room_names.size();i++){
                        rooms_raw.add(databaseRoom.getRoom(room_names.get(i)));
                    }

                    for(int i=0;i<rooms_raw.size();i++){
                        if (rooms_raw.get(i).isAvailable(current_hour)) {
                            Log.e("room1","success");
                            actual_rooms.add(rooms_raw.get(i));
                            extra.add("Available");
                        }
                    }
                    for (int i = current_hour+1; i < 20; i++) {
                        for(int j=0;j<rooms_raw.size();j++)
                            if (rooms_raw.get(j).isAvailable(i)) {
                                Log.e("room1","success");
                                if(!ifContain(rooms_raw.get(j).getName())){
                                    actual_rooms.add(rooms_raw.get(j));
                                    extra.add(i + " : 00");
                                }
                            }

                    }

                    //update listview
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try{
                                adapter.notifyDataSetChanged();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
            }
        }
    };

    class mListViewAdapter extends BaseAdapter {
        private List<Room> rooms;
        private HashMap<String,String> hasPeople;

        public mListViewAdapter(List<Room> rooms,HashMap<String,String> hasPeople) {
            // TODO Auto-generated constructor stub
            this.rooms=rooms;
            this.hasPeople=hasPeople;
        }

        public int getCount() {
            return rooms.size();
        }

        public String getItem(int position) {
            return rooms.get(position).getName();
        }

        @Override
        public long getItemId(int i) {
            return i;
        }


        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;

            if(convertView==null){
                LayoutInflater inflater = My_rooms_activity.this.getLayoutInflater();
                view = inflater.inflate(R.layout.building_rooms_child, null);
            }else{
                view = convertView;
            }

            TextView name_tv = (TextView) view.findViewById(R.id.room_name);
            name_tv.setText(rooms.get(position).getName());
            TextView available_time_tv=view.findViewById(R.id.available_time);
            ImageView wifi_strong=view.findViewById(R.id.wifi_strong);
            ImageView wifi_weak=view.findViewById(R.id.wifi_weak);
            ImageView printer_icon=view.findViewById(R.id.printer_icon);
            ImageView couple=view.findViewById(R.id.couple_icon);
            ImageView single=view.findViewById(R.id.single_icon);

            if(rooms.get(position).getWifi().equals("strong")) {
                wifi_strong.setVisibility(View.VISIBLE);
                wifi_weak.setVisibility(View.GONE);
            }else{
                wifi_weak.setVisibility(View.VISIBLE);
                wifi_strong.setVisibility(View.GONE);
            }

            if(rooms.get(position).getPrinter().equals("yes")) {
                printer_icon.setVisibility(View.VISIBLE);
            }else{
                printer_icon.setVisibility(View.INVISIBLE);
            }


            available_time_tv.setText(extra.get(position));
            available_time_tv.setVisibility(View.VISIBLE);

            if (extra.get(position).length()<8){
                couple.setVisibility(View.GONE);
                single.setVisibility(View.GONE);
                fontFace = Typeface.createFromAsset(getAssets(),
                        "fonts/led_font.ttf");
                available_time_tv.setTypeface(fontFace);
            }else{
                couple.setVisibility(View.GONE);
                single.setVisibility(View.GONE);
                if(hasPeople.containsKey(rooms.get(position).getName().trim())){
                    if(hasPeople.get(rooms.get(position).getName().trim()).equals("0")){

                        single.setVisibility(View.VISIBLE);
                    }

                    if(hasPeople.get(rooms.get(position).getName().trim()).equals("1")){
                        couple.setVisibility(View.VISIBLE);
                    }
                }
                available_time_tv.setTypeface(Typeface.DEFAULT);
            }



            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(My_rooms_activity.this,Room_info_activity.class);
                    intent.putExtra("room_name",rooms.get(position).getName());
                    intent.putExtra("building_name",rooms.get(position).getBuilding().toString());
                    startActivity(intent);

                }
            });

            return view;
        }
    }

}
