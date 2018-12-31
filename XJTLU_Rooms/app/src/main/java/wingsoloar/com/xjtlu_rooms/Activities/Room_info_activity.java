package wingsoloar.com.xjtlu_rooms.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.weigan.loopview.LoopView;

import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import wingsoloar.com.xjtlu_rooms.Objects.Footprint;
import wingsoloar.com.xjtlu_rooms.Objects.Room;
import wingsoloar.com.xjtlu_rooms.Objects.Timetable;
import wingsoloar.com.xjtlu_rooms.Database.mDBFootprints;
import wingsoloar.com.xjtlu_rooms.Database.mDBLikelist;
import wingsoloar.com.xjtlu_rooms.R;
import wingsoloar.com.xjtlu_rooms.Database.mDBRoom;
import wingsoloar.com.xjtlu_rooms.Threads.SignUpThread;

/**
 * Created by wingsolarxu on 2017/9/21.
 */

public class Room_info_activity extends Activity {

    private LinearLayout back_button;
    private LinearLayout take_notes;
    private LinearLayout moments_icon;
    private TextView room_name_tv;
    private TextView room_type_tv;
    private ImageView wifi_strong;
    private ImageView wifi_weak;
    private ImageView printer_has;
    private ImageView printer_no;
    private ImageView mark_footprint_icon;
    private ImageView like_icon;
    private LinearLayout like_button;
    private LinearLayout foot_mark_button;
    private LinearLayout time_table_label;
    private LinearLayout mark_choice;
    private LinearLayout single_mark_button;
    private LinearLayout couple_mark_button;
    private ListView available_periods;
    private LoopView loopView;
    private FrameLayout frameLayout;

    private String room_name;
    private String room_type;
    private String building_name;
    private String bar_color;
    private String timetable;
    private String wifi;
    private String printer;
    private boolean is_in_like_mode;
    private mDBRoom databaseRoom;
    private mDBFootprints databaseFootprints;
    private mDBLikelist databaseLikelist;

    private Timetable[] time_stamps;
    private mAdapter mAdapter;

    private OkHttpClient okHttpClient=new OkHttpClient();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_info_main);

        room_name=getIntent().getStringExtra("room_name");
        databaseRoom = new mDBRoom(this);
        databaseFootprints=new mDBFootprints(this);
        databaseLikelist=new mDBLikelist(this);

        //get room information using its name
        Room room=databaseRoom.getRoom(room_name);
        building_name=room.getBuilding();
        room_type=room.getRoomType();
        bar_color=room.get_bar_color();
        timetable=room.getTimetable();
        wifi=room.getWifi();
        printer=room.getPrinter();

        initLayout();

    }

    private void initLayout(){
        frameLayout=findViewById(R.id.fragment);
        back_button=findViewById(R.id.back_button);
        take_notes=findViewById(R.id.take_notes_icon);
        moments_icon=findViewById(R.id.moments_icon);
        room_name_tv=findViewById(R.id.room_name);
        room_type_tv=findViewById(R.id.room_type);
        wifi_strong=findViewById(R.id.wifi_strong);
        wifi_weak=findViewById(R.id.wifi_weak);
        printer_has=findViewById(R.id.printer_has);
        printer_no=findViewById(R.id.printer_no);
        mark_footprint_icon=findViewById(R.id.mark_footprint_icon);
        like_icon=findViewById(R.id.like_icon);
        like_button=findViewById(R.id.like_button);
        foot_mark_button=findViewById(R.id.foot_mark_button);
        time_table_label=findViewById(R.id.time_table_label);
        mark_choice=findViewById(R.id.mark_choice);
        single_mark_button=findViewById(R.id.single_mark_button);
        couple_mark_button=findViewById(R.id.couple_mark_button);
        available_periods=findViewById(R.id.available_periods);
        loopView= (LoopView) findViewById(R.id.duration_picker);

        frameLayout.getForeground().setAlpha( 0);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        take_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getBaseContext(),Record_thoughts_activity.class);
                intent.putExtra("building_name",building_name );
                intent.putExtra("room_name",room_name);
                startActivity(intent);
            }
        });

        like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_in_like_mode){
                    like_icon.setImageResource(R.drawable.like_icon_gray);
                    databaseLikelist.delete_from_likelist(room_name);
                    is_in_like_mode=!is_in_like_mode;
                }else {
                    like_icon.setImageResource(R.drawable.like_icon_red);
                    databaseLikelist.add_to_likelist(room_name);
                    is_in_like_mode=!is_in_like_mode;
                }
            }
        });

        foot_mark_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like_button.setVisibility(View.GONE);
                time_table_label.setVisibility(View.GONE);
                foot_mark_button.setVisibility(View.GONE);
                mark_choice.setVisibility(View.VISIBLE);
                loopView.setVisibility(View.VISIBLE);
            }
        });

        single_mark_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO save choice
                like_button.setVisibility(View.VISIBLE);
                time_table_label.setVisibility(View.VISIBLE);
                foot_mark_button.setVisibility(View.VISIBLE);
                mark_choice.setVisibility(View.GONE);
                loopView.setVisibility(View.GONE);

                Calendar c = Calendar.getInstance();
                final int year = c.get(Calendar.YEAR);
                final int month = c.get(Calendar.MONTH)+1;
                final int day = c.get(Calendar.DAY_OF_MONTH);
                final int hour = c.get(Calendar.HOUR_OF_DAY);

                if(hour<8||hour>22){
                    Toast.makeText(getBaseContext(),"Available between 08:00 and 23:00",Toast.LENGTH_SHORT).show();
                }else{

                    new SignUpThread(myHandler,room_name,""+year+";"+month+";"+day+";"+hour,""+(loopView.getSelectedItem()+1),"0").start();

                }
            }
        });

        couple_mark_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO save choice
                like_button.setVisibility(View.VISIBLE);
                time_table_label.setVisibility(View.VISIBLE);
                foot_mark_button.setVisibility(View.VISIBLE);
                mark_choice.setVisibility(View.GONE);
                loopView.setVisibility(View.GONE);

                Calendar c = Calendar.getInstance();
                final int year = c.get(Calendar.YEAR);
                final int month = c.get(Calendar.MONTH)+1;
                final int day = c.get(Calendar.DAY_OF_MONTH);
                final int hour = c.get(Calendar.HOUR_OF_DAY);

                if(hour<8||hour>22){
                    Toast.makeText(getBaseContext(),"Available between 08:00 and 23:00",Toast.LENGTH_SHORT).show();
                }else{

                    new SignUpThread(myHandler,room_name,""+year+";"+month+";"+day+";"+hour,""+(loopView.getSelectedItem()+1),"1").start();

                }


            }
        });

        moments_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getBaseContext(),Room_stories_activity.class);
                intent.putExtra("room_name",room_name);
                startActivity(intent);
            }
        });

        if(wifi.equals("strong")) {
            wifi_strong.setVisibility(View.VISIBLE);
            wifi_weak.setVisibility(View.GONE);
        }else{
            wifi_weak.setVisibility(View.VISIBLE);
            wifi_strong.setVisibility(View.GONE);
        }

        if(printer.equals("yes")) {
            printer_has.setVisibility(View.VISIBLE);
            printer_no.setVisibility(View.GONE);
        }else{
            printer_has.setVisibility(View.GONE);
            printer_no.setVisibility(View.VISIBLE);
        }



        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        //check if has singed in the room today
        if(databaseFootprints.is_marked(year+";"+month+";"+day,room_name)){
            mark_footprint_icon.setImageResource(R.drawable.mark_footprint_blue);
        }

        //check if this room has been liked
        is_in_like_mode=databaseLikelist.is_in_likelist(room_name);
        if(is_in_like_mode){
            like_icon.setImageResource(R.drawable.like_icon_red);
        }

        room_name_tv.setText(room_name);
        room_type_tv.setText(room_type);

        fill_in_listview(available_periods);

        //initial duration picker
        ArrayList<String> list = new ArrayList();
        list.add("1 hour");
        for (int i = 2; i < 6; i++) {
            list.add(i+" hours");
        }

        loopView.setItems(list);
        loopView.setInitPosition(1);
        loopView.setTextSize(20);
        loopView.setNotLoop();
    }

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Calendar c = Calendar.getInstance();
                    final int year = c.get(Calendar.YEAR);
                    final int month = c.get(Calendar.MONTH)+1;
                    final int day = c.get(Calendar.DAY_OF_MONTH);
                    int hour = c.get(Calendar.HOUR_OF_DAY);

                    Footprint footprint=new Footprint(room_name,year+";"+month+";"+day,hour,loopView.getSelectedItem()+1,bar_color,"False");
                    databaseFootprints.add_footprint(footprint);
                    mark_footprint_icon.setImageResource(R.drawable.mark_footprint_blue);
                    Toast.makeText(getBaseContext(),"Footprint Collected",Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        if(databaseFootprints.is_marked(year+";"+month+";"+day,room_name)){
            mark_footprint_icon.setImageResource(R.drawable.mark_footprint_blue);
        }

        if(is_in_like_mode){
            like_icon.setImageResource(R.drawable.like_icon_red);
        }
    }

    private void fill_in_listview(ListView listView) {


        time_stamps=new Timetable[14];
        for (int i =0 ; i<14;i++){
            int j;
            String s;
            if(i+8>12){
                if(timetable.charAt(i)=='0'){
                    time_stamps[i]=new Timetable("",i-4,"PM","#ffffff");
                }else{
                    time_stamps[i]=new Timetable("",i-4,"PM","#FFA07A");
                }
            }else{
                if(timetable.charAt(i)=='0'){
                    time_stamps[i]=new Timetable("",i+8,"AM","#ffffff");
                }else{
                    time_stamps[i]=new Timetable("",i+8,"AM","#FFA07A");
                }
            }

        }

        mAdapter=new mAdapter(time_stamps);

        listView.setAdapter(mAdapter);

    }

    class mAdapter extends BaseAdapter {
        private Timetable[] timetables;

        public mAdapter(Timetable[] timetables) {
            // TODO Auto-generated constructor stub
            this.timetables=timetables;
        }

        public int getCount() {
            return 14;
        }

        public Timetable getItem(int position) {
            return timetables[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;

            if(convertView==null){
                LayoutInflater inflater = Room_info_activity.this.getLayoutInflater();
                view = inflater.inflate(R.layout.room_info_child, null);
            }else{
                view = convertView;
            }

            TextView hour_tv=view.findViewById(R.id.hour);
            TextView am_pm_tv=view.findViewById(R.id.am_pm);
            TextView room_name_tv=view.findViewById(R.id.room_name);
            LinearLayout color_bar=view.findViewById(R.id.color_bar);

            hour_tv.setText(timetables[position].getHour_12()+"");
            am_pm_tv.setText(timetables[position].getAM_PM());
            color_bar.setBackgroundColor(Color.parseColor(timetables[position].get_bar_color()));

            //mark as "OCCUPIED" for periods that have classes or have been reserved
            if(timetable.charAt(position)=='0'){
                room_name_tv.setText("");
            }else{
                room_name_tv.setText("OCCUPIED");
            }

            return view;
        }
    }



}
