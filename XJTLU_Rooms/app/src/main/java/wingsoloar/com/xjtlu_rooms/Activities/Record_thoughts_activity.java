package wingsoloar.com.xjtlu_rooms.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import wingsoloar.com.xjtlu_rooms.R;
import wingsoloar.com.xjtlu_rooms.Objects.Thought;
import wingsoloar.com.xjtlu_rooms.Database.mDBThoughts;


/**
 * Created by wingsolarxu on 2017/9/22.
 */

public class Record_thoughts_activity extends Activity {

    private TextView save_button;
    private EditText editText;
    private String building_name;
    private String room_name;
    private ImageView back_button;
    private String username="Admin"; //TODO need to be deleted

    private mDBThoughts mDBThoughts;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_thoughts_main);

        save_button=findViewById(R.id.done_button);
        editText=findViewById(R.id.text);
        back_button=findViewById(R.id.back_button);

        building_name=getIntent().getStringExtra("building_name");
        room_name=getIntent().getStringExtra("room_name");

        mDBThoughts=new mDBThoughts(this);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                final int month = c.get(Calendar.MONTH)+1;
                int day = c.get(Calendar.DAY_OF_MONTH);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int day_of_week=c.get(Calendar.DAY_OF_WEEK);
                String day_of_week_string="";
                String month_string="";

                // change day in week from integer to String
                switch(day_of_week){
                    case 0:
                        day_of_week_string="SUN";
                        break;
                    case 1:
                        day_of_week_string="MON";
                        break;
                    case 2:
                        day_of_week_string="TUE";
                        break;
                    case 3:
                        day_of_week_string="WEN";
                        break;
                    case 4:
                        day_of_week_string="THU";
                        break;
                    case 5:
                        day_of_week_string="FRI";
                        break;
                    case 6:
                        day_of_week_string="SAT";
                        break;
                }

                //change month from integer to String
                switch (month){
                    case 1:
                        month_string="JAN";
                        break;
                    case 2:
                        month_string="FEB";
                        break;
                    case 3:
                        month_string="MAR";
                        break;
                    case 4:
                        month_string="APR";
                        break;
                    case 5:
                        month_string="MAY";
                        break;
                    case 6:
                        month_string="JUN";
                        break;
                    case 7:
                        month_string="JUL";
                        break;
                    case 8:
                        month_string="AUG";
                        break;
                    case 9:
                        month_string="SEP";
                        break;
                    case 10:
                        month_string="OCT";
                        break;
                    case 11:
                        month_string="NOV";
                        break;
                    case 12:
                        month_string="DEC";
                        break;
                }

                String text=editText.getText().toString();

                //alert if user want to save an empty content
                if(text.trim().length()==0){
                    Toast.makeText(getBaseContext(),"Please Input something before saving",Toast.LENGTH_SHORT).show();
                    return;
                }
                String date=""+year+";";
                if(month<10)
                    date=date+"0"+month;
                else
                    date=date+month;
                if(day<10)
                    date=date+";0"+day;
                else
                    date=date+";"+day;

                date=month_string+" "+day+", "+year;
                String time=""+hour;

                //add to database
                Thought thought=new Thought("'"+username+"'","'"+building_name+"'","'"+room_name+"'","'"+date+"'","'"+day_of_week_string+"'","'"+time+"'","'"+text+"'",1,0);
                mDBThoughts.addThought(thought);

                Toast.makeText(getBaseContext(),"Save Succeed",Toast.LENGTH_SHORT).show();

                finish();

            }
        });

    }

}
