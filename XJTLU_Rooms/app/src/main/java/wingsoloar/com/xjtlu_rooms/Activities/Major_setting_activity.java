package wingsoloar.com.xjtlu_rooms.Activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import wingsoloar.com.xjtlu_rooms.R;

/**
 * Created by wingsolarxu on 2018/3/18.
 */

public class Major_setting_activity extends Activity {
    private TextView cancel;
    private TextView done;
    private EditText editText;
    private ImageView clear_button;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.major_settig_main);

        cancel=findViewById(R.id.cancel_button);
        done=findViewById(R.id.done_button);
        editText=findViewById(R.id.edittext);
        clear_button=findViewById(R.id.clear_button);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                preferences=getSharedPreferences("user_info",MODE_PRIVATE);;
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putString("major",editText.getText().toString());
                editor.commit();
                finish();
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i== EditorInfo.IME_ACTION_DONE){
                    preferences=getSharedPreferences("user_info",MODE_PRIVATE);;
                    final SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("name",editText.getText().toString());
                    editor.commit();
                    finish();
                }

                return true;
            }
        });

    }

}
