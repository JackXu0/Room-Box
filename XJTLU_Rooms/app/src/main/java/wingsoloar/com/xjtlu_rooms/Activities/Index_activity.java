package wingsoloar.com.xjtlu_rooms.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Calendar;

import wingsoloar.com.xjtlu_rooms.R;

/**
 * Created by wingsolarxu on 2018/3/10.
 */

public class Index_activity extends Activity {

    private RelativeLayout room_selection_button;
    private RelativeLayout foot_mark_button;
    private RelativeLayout my_thoughts_button;
    private ImageView like_icon;
    private ImageView user_icon;

    @Override
    protected void onCreate( final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index_main);
        Calendar c = Calendar.getInstance();
        long init=c.getTimeInMillis();


        room_selection_button=findViewById(R.id.room_selection_button);
        foot_mark_button=findViewById(R.id.foot_mark_button);
        my_thoughts_button=findViewById(R.id.my_thoughts_button);
        like_icon=findViewById(R.id.like_icon);
        user_icon=findViewById(R.id.user_icon);

        room_selection_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getBaseContext(),All_rooms_activity.class);
                startActivity(intent);
            }
        });

        foot_mark_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getBaseContext(), Footprints_activity.class);
                startActivity(intent);
            }
        });

        my_thoughts_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getBaseContext(), MyThoughts_activity.class);
                startActivity(intent);
            }
        });

        like_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getBaseContext(),My_rooms_activity.class);
                startActivity(intent);
            }
        });

        user_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getBaseContext(),Personal_information_activity.class);
                startActivity(intent);
            }
        });


        Log.e("time="+(c.getTimeInMillis()-init)+"","initial index page");


    }
}

