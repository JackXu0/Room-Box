package wingsoloar.com.xjtlu_rooms.Activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import wingsoloar.com.xjtlu_rooms.Objects.Footprint;
import wingsoloar.com.xjtlu_rooms.Database.mDBFootprints;
import wingsoloar.com.xjtlu_rooms.R;

/**
 * Created by wingsolarxu on 2018/3/10.
 */

public class Footprints_activity extends Activity {

    private mDBFootprints databaseFootprints;

    private ImageView back_button;
    private TextView date_tv;

    private LinearLayout day1;
    private LinearLayout day2;
    private LinearLayout day3;
    private ViewPager vp;
    private RelativeLayout rl_progress;
    private VPAdapter vpa;
    private TextView view;
    private int width=130;
    private List<ListView> list;
    private static List<Footprint> footprints=new ArrayList<>();

    private static Footprint[] time_stamps1=new Footprint[16];
    private static Footprint[] time_stamps2=new Footprint[16];
    private static Footprint[] time_stamps3=new Footprint[16];


    private mAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.footprint_main);

        databaseFootprints=new mDBFootprints(this);

        initLayout();

    }

    private void initLayout(){
        back_button=findViewById(R.id.back_button);
        date_tv=findViewById(R.id.date_tv);
        day1=findViewById(R.id.day1);
        day2=findViewById(R.id.day2);
        day3=findViewById(R.id.day3);
        rl_progress=findViewById(R.id.rl_progress);
        vp = (ViewPager) findViewById(R.id.viewpager);

        //show current date on a textview
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        fill_time(year, month, day);

        list=new ArrayList<ListView>();

        list.add(new ListView(getBaseContext()));
        list.add(new ListView(getBaseContext()));
        list.add(new ListView(getBaseContext()));

        vpa=new VPAdapter((ArrayList<ListView>) list);
        vp.setAdapter(vpa);
        vp.setOffscreenPageLimit(0);
        //this view is designed to emulate a white bar
        view=new TextView(this);
        view.setWidth(width);
        view.setHeight(10);
        view.setBackgroundColor(Color.parseColor("#ffffff"));

        rl_progress.addView(view);
        //vp.setCurrentItem(0);
        vp.getChildAt(0).setSelected(true);

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                final int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                ListView iv = list.get(arg0);
                iv.setAdapter(new mAdapter(fill_in_listview(arg0)));
                Log.e("position:" , arg0+"");
                fill_time(year,month,day-arg0);
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                int left =(width+20)*arg0 + (int) ((width)*arg1);
                RelativeLayout.LayoutParams rllp = (RelativeLayout.LayoutParams) view.getLayoutParams();
                rllp.leftMargin = left;
                view.setLayoutParams(rllp);

            }
            @Override
            public void onPageScrollStateChanged(int arg0) {

            }

            @Override
            protected void finalize() throws Throwable {
                super.finalize();
            }
        });


        day1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vp.setCurrentItem(0,false);
            }
        });

        day2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vp.setCurrentItem(1,false);
            }
        });

        day3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vp.setCurrentItem(2,false);
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    //using the current date to search for relavent information in database and refresh listview
    private Footprint[] fill_in_listview(int offset) {

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH)-offset;

        footprints.clear();
        footprints.addAll(databaseFootprints.getFootprints(year+";"+month+";"+day));
        Log.e("ccc_size",footprints.size()+"");

        for(int i=0;i<time_stamps3.length;i++){
            if(i<5){
                time_stamps3[i] = new Footprint("", i+8 , "AM", "#ffffff");
            }else{
                time_stamps3[i] = new Footprint("", i-4 , "PM", "#ffffff");
            }

        }

        for(int i=0;i<footprints.size();i++){
            String temp1=footprints.get(i).getRoom_name();
            int temp2=footprints.get(i).getHour();
            int temp3=footprints.get(i).getDuration();
            String temp4=footprints.get(i).get_bar_color();
            for (int j=0;j<temp3;j++){
                if(temp2>7&&(temp2+temp3)<24) {
                    if (temp2 + j < 13) {
                        time_stamps3[temp2 + j - 8] = new Footprint(temp1, temp2 + j, "AM", temp4);
                        Log.e("timestamp", time_stamps3[temp2 + j - 8] + "");
                    }
                    else {
                        time_stamps3[temp2 + j - 8] = new Footprint(temp1, temp2 + j - 12, "PM", temp4);
                        Log.e("timestamp", time_stamps3[temp2 + j - 8] + "");
                    }
                }
            }
        }


        return time_stamps3;



    }

    //used for setting current date
    private void fill_time(int year, int month, int day) {
        switch (month){
            case 0:
                date_tv.setText("January "+day+", "+year);
                break;
            case 1:
                date_tv.setText("February "+day+", "+year);
                break;
            case 2:
                date_tv.setText("March "+day+", "+year);
                break;
            case 3:
                date_tv.setText("April "+day+", "+year);
                break;
            case 4:
                date_tv.setText("May "+day+", "+year);
                break;
            case 5:
                date_tv.setText("June "+day+", "+year);
                break;
            case 6:
                date_tv.setText("July "+day+", "+year);
                break;
            case 7:
                date_tv.setText("August "+day+", "+year);
                break;
            case 8:
                date_tv.setText("September "+day+", "+year);
                break;
            case 9:
                date_tv.setText("October "+day+", "+year);
                break;
            case 10:
                date_tv.setText("November "+day+", "+year);
                break;
            case 11:
                date_tv.setText("December "+day+", "+year);
                break;

        }
    }

    class mAdapter extends BaseAdapter {

        private Footprint[] footprints;

        public mAdapter(Footprint[] footprints) {
            // TODO Auto-generated constructor stub
            this.footprints=footprints;
        }

        public int getCount() {
            return 16;
        }

        public Footprint getItem(int position) {
            return footprints[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;

            if(convertView==null){
                LayoutInflater inflater = Footprints_activity.this.getLayoutInflater();
                view = inflater.inflate(R.layout.footprint_child, null);
            }else{
                view = convertView;
            }

            TextView hour_tv=view.findViewById(R.id.hour);
            TextView am_pm_tv=view.findViewById(R.id.am_pm);
            TextView room_name_tv=view.findViewById(R.id.room_name);
            LinearLayout color_bar=view.findViewById(R.id.color_bar);

            hour_tv.setText(footprints[position].getHour()+"");
            am_pm_tv.setText(footprints[position].getAM_PM());
            room_name_tv.setText(footprints[position].getRoom_name());
            color_bar.setBackgroundColor(Color.parseColor(footprints[position].get_bar_color()));

            return view;
        }
    }



    class VPAdapter extends PagerAdapter {
        private ArrayList<ListView> lists;

        public VPAdapter(ArrayList<ListView> lists) {
            super();
            this.lists = lists;
        }

        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0==arg1;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ListView iv = list.get(position);
            if(position==0){
                mAdapter adapter=new mAdapter(fill_in_listview(position));
                iv.setAdapter(adapter);
            }

            container.addView(iv);
            Log.e("pos",position+"");
            return iv;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(lists.get(position));
        }
    }
}
