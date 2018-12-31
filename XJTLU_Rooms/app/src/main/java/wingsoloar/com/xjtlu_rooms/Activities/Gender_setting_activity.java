package wingsoloar.com.xjtlu_rooms.Activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import wingsoloar.com.xjtlu_rooms.R;

/**
 * Created by wingsolarxu on 2018/3/18.
 */

public class Gender_setting_activity extends Activity {

    private TextView cancel;
    private TextView done;
    private RelativeLayout male;
    private RelativeLayout female;
    private ImageView check_male;
    private ImageView check_female;
    private boolean is_male;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gender_setting_main);

        cancel=findViewById(R.id.cancel_button);
        done=findViewById(R.id.done_button);
        male=findViewById(R.id.male);
        female=findViewById(R.id.female);
        check_male=findViewById(R.id.check_male);
        check_female=findViewById(R.id.check_female);

        preferences=getSharedPreferences("user_info",MODE_PRIVATE);
        is_male=preferences.getBoolean("is_male",false);

        if(is_male){
            check_female.setVisibility(View.INVISIBLE);
            check_male.setVisibility(View.VISIBLE);
        }else{
            check_male.setVisibility(View.INVISIBLE);
            check_female.setVisibility(View.VISIBLE);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_male=true;
                check_male.setVisibility(View.VISIBLE);
                check_female.setVisibility(View.INVISIBLE);
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_male=false;
                check_female.setVisibility(View.VISIBLE);
                check_male.setVisibility(View.INVISIBLE);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SharedPreferences.Editor editor = preferences.edit();
                Log.e(""+is_male,"bb");
                editor.putBoolean("is_male",is_male);
                editor.commit();
                finish();
            }
        });

    }
}
