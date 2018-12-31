package wingsoloar.com.xjtlu_rooms.Activities;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wingsoloar.com.xjtlu_rooms.Network.GetRoomData;
import wingsoloar.com.xjtlu_rooms.R;
import wingsoloar.com.xjtlu_rooms.Objects.Room;
import wingsoloar.com.xjtlu_rooms.Database.mDBRoom;
import wingsoloar.com.xjtlu_rooms.Threads.CheckInternetThread;
import wingsoloar.com.xjtlu_rooms.Threads.GetPeopleInfoThread;

/**
 * Created by wingsolarxu on 2017/9/21.
 */

public class All_rooms_activity extends Activity {

    private ListView rooms_view;
    private mListViewAdapter adapter;

    private FrameLayout frameLayout;

    private int current_hour;
    private mDBRoom database;
    private List<Room> rooms_raw;
    private static List<Room> actual_rooms;
    private List<String> extra;
    private ImageView back_icon;
    private ImageView fliter_button;
    private EditText search_box;
    private boolean is_lecture_theatre=false;
    private boolean is_class_room=false;
    private boolean is_computer_room=false;
    private boolean is_wifi_signal_strong=false;
    private boolean has_printer_nearby=false;
    private boolean is__no_couple_signed=false;
    private boolean is_no_people_signed=false;

    private SharedPreferences preference_lastTime;
    private CircularProgressView progressView;

    private final static HashMap<String,String> hasPeople=new HashMap<>();

    private String lastTime="";

    Typeface fontFace ;
    private Thread thread;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.building_rooms_root);

        actual_rooms = new ArrayList<Room>();
        rooms_raw=new ArrayList<Room>();
        extra=new ArrayList<String>();
        database = new mDBRoom(this);
        rooms_raw=database.queryAll();

        Log.e("oncreate","1");

        Log.e("room raw size",rooms_raw.size()+"");
        adapter=new mListViewAdapter(actual_rooms,hasPeople);

        initTest();
    }

    @Override
    protected void onResume() {

        Log.e("wswsws","dsaff");
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
        //!current_time.equals(lastTime)
        if(!current_time.equals(lastTime)){

            new CheckInternetThread(myHandler).start();

        }else{

            Log.e("dsfsdfsdfsd","1");
            initialLayout();

            new GetPeopleInfoThread(myHandler,0).start();
        }
    }

    private void initialLayout(){

        rooms_view=findViewById(R.id.rooms_per_building_listView);
        frameLayout=findViewById(R.id.framelayout);
        back_icon=findViewById(R.id.back_button);
        fliter_button=findViewById(R.id.fliter_button);
        search_box=findViewById(R.id.search_box);

        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fliter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_box.setText("");
                search_box.clearFocus();
                hideKeyboard(search_box);
                Fliter_popupwindow1 popupwindow=new Fliter_popupwindow1(getBaseContext(),frameLayout);
                if (!popupwindow.isShowing()) {
                    popupwindow.showAsDropDown(fliter_button, 0, 25);
                    showAnimator().start();
                } else {
                    popupwindow.dismiss();
                    dismissAnimator().start();
                }


                popupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        dismissAnimator().start();
                    }
                });
            }
        });

        search_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
               with_search_box(search_box.getText().toString());
            }
        });


        frameLayout.getForeground().setAlpha( 0);

        rooms_view.setAdapter(adapter);

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
                    Log.e("sdsdsdsds",code+"");
                    if(code ==0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"Please check interner access",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

                        return;
                    }else if(code==2){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

                    }else{
                        initialLayout();


                        new GetPeopleInfoThread(myHandler,0).start();
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

                    rooms_raw=database.queryAll();

                    Log.e("room raw size",rooms_raw.size()+"");

                    //for rooms that are currently available, tag them "Available" in extra
                    for (int i = 0; i < rooms_raw.size(); i++) {
                        if (rooms_raw.get(i).isAvailable(hour)) {
                            //Log.e("room1", "success");
                            actual_rooms.add(rooms_raw.get(i));
                            extra.add("Available");
                        }
                    }

                    //for rooms that are occupied currently, tag them the latest available time in extra
                    for (int i = hour; i < 22; i++) {
                        for (int j = 0; j < rooms_raw.size(); j++)
                            if (rooms_raw.get(j).isAvailable(i)) {
                                //Log.e("room1", "success");
                                if (!ifContain(rooms_raw.get(j).getName())) {
                                    actual_rooms.add(rooms_raw.get(j));
                                    extra.add(i + " : 00");
                                }
                            }
                    }

                    //update listview
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });


                    //getAllRooms();

                    break;
            }
        }
    };


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
        //!lastTime.equals(current_time)
        if(!lastTime.equals(current_time)) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressView=findViewById(R.id.progress_view);
                    progressView.setVisibility(View.VISIBLE);
                }
            });


            new Thread(new Runnable() {
                @Override
                public void run() {

                    Log.e("sss","4");
                    // 子线程执行完毕的地方，利用主线程的handler发送消息
                    Message msg = new Message();
                    msg.what = 3;
                    GetRoomData.crawl(All_rooms_activity.this,getApplicationContext(),Thread.currentThread());
                    try {
                        Thread.sleep(Long.MAX_VALUE);
                    } catch (InterruptedException e) {
                    }
                    myHandler.sendMessage(msg);

                }
            }).start();


            return;
        }


        //get all rooms info from local database
        rooms_raw=database.queryAll();

        Log.e("room raw size",rooms_raw.size()+"");

        //for rooms that are currently available, tag them "Available" in extra
        for (int i = 0; i < rooms_raw.size(); i++) {
            if (rooms_raw.get(i).isAvailable(hour)) {
                //Log.e("room1", "success");
                actual_rooms.add(rooms_raw.get(i));
                extra.add("Available");
            }
        }

        //for rooms that are occupied currently, tag them the latest available time in extra
        for (int i = hour; i < 22; i++) {
            for (int j = 0; j < rooms_raw.size(); j++)
                if (rooms_raw.get(j).isAvailable(i)) {
                    //Log.e("room1", "success");
                    if (!ifContain(rooms_raw.get(j).getName())) {
                        actual_rooms.add(rooms_raw.get(j));
                        extra.add(i + " : 00");
                    }
                }
        }

        //update listview

        //adapter.notifyDataSetChanged();
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

    private void with_search_box(String keyword){
        actual_rooms.clear();
        extra.clear();

        //make a copy of all rooms information
        List<Room> temp = new ArrayList<>();

        for(int i=0;i<rooms_raw.size();i++){

            //keyword is mathed after replacing white space and lowercasing
            if(rooms_raw.get(i).getName().toLowerCase().replace(" ","").contains(keyword.toLowerCase().replace(" ",""))){
                temp.add(rooms_raw.get(i));
            }

        }

        //for rooms that are currently available, tag them "Available" in extra
        for(int i=0;i<temp.size();i++){
            if (temp.get(i).isAvailable(current_hour)) {
                //Log.e("room1","success");
                actual_rooms.add(temp.get(i));
                extra.add("Available");
            }
        }

        //for rooms that are occupied currently, tag them the latest available time in extra
        for (int i = current_hour; i < 22; i++) {
            for(int j=0;j<temp.size();j++)
                if (temp.get(j).isAvailable(i)) {
                    //Log.e("room1","success");
                    if(!ifContain(temp.get(j).getName())){
                        actual_rooms.add(temp.get(j));
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
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void with_filter(String room_type, String wifi, String printer,boolean is_no_couple, boolean is_no_people){
        actual_rooms.clear();
        extra.clear();
        rooms_raw.clear();
        Calendar c = Calendar.getInstance();
        current_hour=c.get(Calendar.HOUR_OF_DAY);

        //get all rooms info after filtering room type, wifi signal, and is has printer
        rooms_raw= database.filter(room_type,wifi,printer);


        //if "is no people" button was chosen, remove records that have people signed in
        if(is_no_people){
            List<Room> temp=new ArrayList<>();
            for(int i =0;i<rooms_raw.size();i++){
                if(!hasPeople.containsKey(rooms_raw.get(i).getName())){
                    temp.add(rooms_raw.get(i));
                }
            }

            rooms_raw.clear();
            rooms_raw.addAll(temp);
        }


        //if "is no couple" button was chosen, remove records that have couple signed in
        if(is_no_couple){
            List<Room> temp=new ArrayList<>();
            for(int i =0;i<rooms_raw.size();i++){
                if(!(hasPeople.containsKey(rooms_raw.get(i).getName())&&hasPeople.get(rooms_raw.get(i).getName()).equals("1"))){
                    temp.add(rooms_raw.get(i));
                }
            }

            rooms_raw.clear();
            rooms_raw.addAll(temp);
        }

        //for rooms after filtering that are currently available, tag them "Available" in extra
        for(int i=0;i<rooms_raw.size();i++){

            if (rooms_raw.get(i).isAvailable(current_hour)) {
                //Log.e("room1","success");
                actual_rooms.add(rooms_raw.get(i));
                extra.add("Available");
            }
        }

        //for rooms after filtering that are occupied currently, tag them the latest available time in extra
        for (int i = current_hour; i < 22; i++) {
            for(int j=0;j<rooms_raw.size();j++)
                if (rooms_raw.get(j).isAvailable(i)) {
                    //Log.e("room1","success");
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


    //hide keyboard when user clicks white space and keyboard has expanded
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            View v = getCurrentFocus();

            if (isShouldHideInput(search_box, ev)) {
                search_box.clearFocus();
                hideKeyboard(search_box);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {

                return false;
            } else {
                return true;
            }
        }

        return false;
    }

    public static void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    //used for dismissing filter popup window
    private ValueAnimator dismissAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(0.7f, 1.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                frameLayout.getForeground().setAlpha( 0);
            }
        });


        return animator;
    }

    //used to show filter popup window
    private ValueAnimator showAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.7f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                frameLayout.getForeground().setAlpha( 120);
            }
        });
        animator.setDuration(1);
        return animator;
    }




    //popup filtering window
    public class Fliter_popupwindow1 extends PopupWindow {

        private View conentView;
        private Context context;
        private FrameLayout frameLayout;

        private LinearLayout lecture_theatre_gray;
        private LinearLayout lecture_theatre_red;
        private LinearLayout class_room_gray;
        private LinearLayout class_room_red;
        private LinearLayout computer_room_gray;
        private LinearLayout computer_room_red;
        private LinearLayout wifi_signal_strong_gray;
        private LinearLayout wifi_signal_strong_red;
        private LinearLayout printer_nearby_gray;
        private LinearLayout printer_nearby_red;
        private LinearLayout no_couple_signed_gray;
        private LinearLayout no_couple_signed_red;
        private LinearLayout no_people_signed_gray;
        private LinearLayout no_people_signed_red;
        private TextView cancel;
        private TextView fliter;

        public Fliter_popupwindow1(final Context context, FrameLayout frameLayout) {
            super(context);
            this.context = context;
            this.frameLayout=frameLayout;
            this.initPopupWindow();

        }

        private void initPopupWindow() {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            conentView = inflater.inflate(R.layout.fliter_popup, null);
            this.setContentView(conentView);
            this.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
            this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            this.setFocusable(true);
            this.setTouchable(true);
            this.setOutsideTouchable(true);
            this.update();
            ColorDrawable dw = new ColorDrawable(0000000000);
            this.setBackgroundDrawable(dw);
            this.setAnimationStyle(R.style.AnimationPreview);

            lecture_theatre_gray=conentView.findViewById(R.id.lecture_theatre_gray);
            lecture_theatre_red=conentView.findViewById(R.id.lecture_theatre_red);
            class_room_gray=conentView.findViewById(R.id.class_room_gray);
            class_room_red=conentView.findViewById(R.id.class_room_red);
            computer_room_gray=conentView.findViewById(R.id.computer_room_gray);
            computer_room_red=conentView.findViewById(R.id.computer_room_red);
            wifi_signal_strong_gray=conentView.findViewById(R.id.wifi_signal_strong_gray);
            wifi_signal_strong_red=conentView.findViewById(R.id.wifi_signal_strong_red);
            printer_nearby_gray=conentView.findViewById(R.id.printer_nearby_gray);
            printer_nearby_red=conentView.findViewById(R.id.printer_nearby_red);
            no_couple_signed_gray=conentView.findViewById(R.id.no_couple_signed_gray);
            no_couple_signed_red=conentView.findViewById(R.id.no_couple_signed_red);
            no_people_signed_gray=conentView.findViewById(R.id.no_people_signed_gray);
            no_people_signed_red=conentView.findViewById(R.id.no_people_signed_red);

            cancel=conentView.findViewById(R.id.cancel);
            fliter=conentView.findViewById(R.id.fliter);

            if(is_lecture_theatre){
                lecture_theatre_gray.setVisibility(View.GONE);
                lecture_theatre_red.setVisibility(View.VISIBLE);
            }

            if(is_class_room){
                class_room_gray.setVisibility(View.GONE);
                class_room_red.setVisibility(View.VISIBLE);
            }

            if(is_computer_room){
                computer_room_gray.setVisibility(View.GONE);
                computer_room_red.setVisibility(View.VISIBLE);
            }

            if(is_wifi_signal_strong){
                wifi_signal_strong_gray.setVisibility(View.GONE);
                wifi_signal_strong_red.setVisibility(View.VISIBLE);
            }

            if(has_printer_nearby){
                printer_nearby_gray.setVisibility(View.GONE);
                printer_nearby_red.setVisibility(View.VISIBLE);
            }

            if(is__no_couple_signed){
                no_couple_signed_gray.setVisibility(View.GONE);
                no_couple_signed_red.setVisibility(View.VISIBLE);
            }

            if(is_no_people_signed){
                no_people_signed_gray.setVisibility(View.GONE);
                no_people_signed_red.setVisibility(View.VISIBLE);
            }

            lecture_theatre_gray.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lecture_theatre_gray.setVisibility(View.GONE);
                    lecture_theatre_red.setVisibility(View.VISIBLE);
                    is_lecture_theatre=true;

                }
            });

            lecture_theatre_red.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lecture_theatre_gray.setVisibility(View.VISIBLE);
                    lecture_theatre_red.setVisibility(View.GONE);
                    is_lecture_theatre=false;
                }
            });

            class_room_gray.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    class_room_gray.setVisibility(View.GONE);
                    class_room_red.setVisibility(View.VISIBLE);
                    is_class_room=true;
                }
            });

            class_room_red.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    class_room_red.setVisibility(View.GONE);
                    class_room_gray.setVisibility(View.VISIBLE);
                    is_class_room=false;
                }
            });

            computer_room_gray.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    computer_room_gray.setVisibility(View.GONE);
                    computer_room_red.setVisibility(View.VISIBLE);
                    is_computer_room=true;
                }
            });

            computer_room_red.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    computer_room_red.setVisibility(View.GONE);
                    computer_room_gray.setVisibility(View.VISIBLE);
                    is_computer_room=false;
                }
            });

            wifi_signal_strong_gray.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    wifi_signal_strong_gray.setVisibility(View.GONE);
                    wifi_signal_strong_red.setVisibility(View.VISIBLE);
                    is_wifi_signal_strong=true;
                }
            });

            wifi_signal_strong_red.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    wifi_signal_strong_red.setVisibility(View.GONE);
                    wifi_signal_strong_gray.setVisibility(View.VISIBLE);
                    is_wifi_signal_strong=false;
                }
            });

            printer_nearby_gray.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    printer_nearby_gray.setVisibility(View.GONE);
                    printer_nearby_red.setVisibility(View.VISIBLE);
                    has_printer_nearby=true;
                }
            });

            printer_nearby_red.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    printer_nearby_red.setVisibility(View.GONE);
                    printer_nearby_gray.setVisibility(View.VISIBLE);
                    has_printer_nearby=false;
                }
            });

            no_couple_signed_gray.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    no_couple_signed_gray.setVisibility(View.GONE);
                    no_couple_signed_red.setVisibility(View.VISIBLE);
                    is__no_couple_signed=true;
                }
            });

            no_couple_signed_red.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    no_couple_signed_red.setVisibility(View.GONE);
                    no_couple_signed_gray.setVisibility(View.VISIBLE);
                    is__no_couple_signed=false;
                }
            });

            no_people_signed_gray.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    no_people_signed_gray.setVisibility(View.GONE);
                    no_people_signed_red.setVisibility(View.VISIBLE);
                    is_no_people_signed=true;
                }
            });

            no_people_signed_red.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    no_people_signed_red.setVisibility(View.GONE);
                    no_people_signed_gray.setVisibility(View.VISIBLE);
                    is_no_people_signed=false;
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });

            fliter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String temp="";
                    if(is_lecture_theatre)
                        temp=temp+"'Lecture Theatre',";
                    if(is_class_room)
                        temp=temp+"'Class Room',";
                    if(is_computer_room)
                        temp=temp+"'Computer Room',";

                    if (temp.contains(",")){
                        temp=temp.substring(0,temp.length()-1);
                    }

                    Log.e("ccd",temp);

                    String wifi;
                    String printer;
                    if(is_wifi_signal_strong)
                        wifi="strong";
                    else
                        wifi="weak";

                    if(has_printer_nearby)
                        printer="yes";
                    else
                        printer="no";
                    with_filter(temp,wifi,printer,is__no_couple_signed,is_no_people_signed);
                    dismiss();
                }
            });

        }
    }


    //adapter for listview that displays information of searching results
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
                LayoutInflater inflater = All_rooms_activity.this.getLayoutInflater();
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

            //extra.get(position).length()<8 means this room is now unavailable, thus no need to display if someone in info
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

            //if a particular room record is clicked, go to Room_info_activity
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(All_rooms_activity.this,Room_info_activity.class);
                    intent.putExtra("room_name",rooms.get(position).getName());
                    intent.putExtra("building_name",rooms.get(position).getBuilding().toString());
                    startActivity(intent);

                }
            });

            return view;
        }
    }

}


