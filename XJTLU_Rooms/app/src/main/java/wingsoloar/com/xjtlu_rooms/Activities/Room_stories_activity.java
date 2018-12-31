package wingsoloar.com.xjtlu_rooms.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.OkHttpClient;
import wingsoloar.com.xjtlu_rooms.Objects.Story;
import wingsoloar.com.xjtlu_rooms.R;
import wingsoloar.com.xjtlu_rooms.Threads.GetRoomStoriesThread;
import wingsoloar.com.xjtlu_rooms.Threads.PublishStoryThread;

/**
 * Created by wingsolarxu on 2018/3/16.
 */

public class Room_stories_activity extends Activity {

    private final ArrayList<Story> stories=new ArrayList<>();
    private mAdapter adapter;
    private ListView listView;
    private RelativeLayout back_button;
    private CircularProgressView progressView;
    private EditText comment_box;
    private SharedPreferences preferences;
    private String username;
    private String avator_path;
    private OkHttpClient okHttpClient=new OkHttpClient();
    private Thread thread;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_moments_activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        listView=findViewById(R.id.room_stories_listView);
        back_button=findViewById(R.id.back_button);
        comment_box=findViewById(R.id.commet_box);

        preferences=getSharedPreferences("user_info",MODE_PRIVATE);

        username=preferences.getString("name","User");
        avator_path=preferences.getString("avator_path","");

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        adapter=new mAdapter(stories);

        listView.setAdapter(adapter);

        comment_box.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i== EditorInfo.IME_ACTION_SEND){
                    final String content=comment_box.getText().toString();
                    if(content.trim().length()==0){
                        Toast.makeText(getApplicationContext(),"Please Input Something First",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    Calendar c = Calendar.getInstance();
                    final int year = c.get(Calendar.YEAR);
                    final int month = c.get(Calendar.MONTH)+1;
                    final int day = c.get(Calendar.DAY_OF_MONTH);
                    final int hour = c.get(Calendar.HOUR_OF_DAY);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream


                    BitmapFactory.Options newOpts =  new  BitmapFactory.Options();
                    newOpts.inJustDecodeBounds =  true ;
                    newOpts.inJustDecodeBounds =  false ;
                    int  w = newOpts.outWidth;
                    int  h = newOpts.outHeight;
                    newOpts.inSampleSize = 2;
                    //Bitmap newBm = BitmapFactory.decodeFile(avator_path, newOpts);
                    final Bitmap bm;
                    if(avator_path!=""){
                        Log.e("avator path",avator_path);
                        bm= BitmapFactory.decodeFile(avator_path,newOpts);
                    }else{
                        bm= BitmapFactory.decodeResource(getResources(),R.drawable.default_avator,newOpts);
                    }

                    bm.compress(Bitmap.CompressFormat.PNG,1, baos);
                    byte[] appicon = baos.toByteArray();// 转为byte数组
                    Log.e("length2",appicon.length+"");
                    final String pic_string= Base64.encodeToString(appicon, Base64.DEFAULT);
                    Log.e("string",pic_string);

                    new PublishStoryThread(myHandler,bm,username,getIntent().getStringExtra("room_name"),content,year+";"+month+";"+day,pic_string).start();
                }

                return true;
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressView=findViewById(R.id.progress_view);
                progressView.setVisibility(View.VISIBLE);
            }
        });

        SharedPreferences preferences=getSharedPreferences("user_info",MODE_PRIVATE);
        String avator_path=preferences.getString("avator_path","");

        new GetRoomStoriesThread(getIntent().getStringExtra("room_name"),myHandler,avator_path).start();
    }

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(final Message msg) {
            Bundle b;
            int code;
            switch (msg.what){
                case 1:
                    b=msg.getData();
                    Calendar c = Calendar.getInstance();
                    final int month = c.get(Calendar.MONTH)+1;
                    final int day = c.get(Calendar.DAY_OF_MONTH);

                    code=b.getInt("response_code");

                    switch(code){
                        case 0:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"Please Check Internet Access!",Toast.LENGTH_SHORT).show();
                                }
                            });

                            break;
                        case 1:
                            final String content= b.getString("content");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("sese","1");
                                    stories.add(stories.size(),new Story((Bitmap) msg.obj,username,month+"/"+day,content));
                                    adapter.notifyDataSetChanged();

                                    hideKeyboard(comment_box);
                                    comment_box.setText("");
                                }
                            });
                            break;
                        case 2:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                    }



                    break;

                case 2:
                    b=msg.getData();
                    code=b.getInt("response_code");

                    switch(code){
                        case 0:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"Please Check Internet Access!",Toast.LENGTH_SHORT).show();
                                }
                            });

                            break;
                        case 1:
                            stories.clear();
                            stories.addAll((ArrayList<Story>) msg.obj);
                            break;
                        case 2:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressView.stopAnimation();
                            progressView.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    break;

            }
        }
    };

    //hide keyboard when user clicks white space and keyboard has been expanded
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();

            if (isShouldHideInput(comment_box, ev)) {
                comment_box.clearFocus();
                hideKeyboard(comment_box);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    //mainly used to get default avator which is placed in drawable folder
    public int getImageResourceId(String name) {
        R.drawable drawables=new R.drawable();
        int resId=0x7f02000b;
        try {
            java.lang.reflect.Field field=R.drawable.class.getField(name);
            resId=(Integer)field.get(drawables);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resId;
    }

    //resize picture to get a square
    public static Bitmap imageCrop(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int width = w > h ? h : w;

        int retX = (w - width) / 2;
        int retY = (h - width) / 2;

        return Bitmap.createBitmap(bitmap, retX, retY, width, width, null, false);
    }


    class mAdapter extends BaseAdapter {

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        private List<Story> stories;

        public mAdapter(ArrayList<Story> stories) {
            // TODO Auto-generated constructor stub
            this.stories=stories;
        }

        public int getCount() {
            return stories.size();
        }

        public Story getItem(int position) {
            return stories.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final Holder holder;

            if(convertView==null){
                LayoutInflater inflater = Room_stories_activity.this.getLayoutInflater();
                convertView = inflater.inflate(R.layout.room_moments_activity_child, null);
                holder = new Holder();
                holder.user_photo=convertView.findViewById(R.id.user_photo);
                holder.username=convertView.findViewById(R.id.username);
                holder.content=convertView.findViewById(R.id.content);
                holder.date=convertView.findViewById(R.id.date);
                convertView.setTag(holder);

            }else{
                holder= (Holder) convertView.getTag();
            }

            try{
                holder.user_photo.setImageBitmap(imageCrop(stories.get(getCount()-1-position).getPhoto()));

            }catch(Exception e){
                e.printStackTrace();
                holder.user_photo.setImageResource(getImageResourceId("default_avator"));
            }

            if(Integer.parseInt(stories.get(getCount()-1-position).getDate().split("/")[0])==month
                    &&Integer.parseInt(stories.get(getCount()-1-position).getDate().split("/")[1])==day){
                holder.date.setText("Today");
            }else{
                Log.e(month+"/"+day,"weweq");
                holder.date.setText(stories.get(getCount()-1-position).getDate());
            }

            holder.username.setText(stories.get(getCount()-1-position).getUsername());

            holder.content.setText(stories.get(getCount()-1-position).getContent());

            return convertView;
        }
    }

    static class Holder {
        protected ImageView user_photo;
        protected TextView username;
        protected TextView content;
        protected TextView date;
    }





}
