package wingsoloar.com.xjtlu_rooms.Threads;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import wingsoloar.com.xjtlu_rooms.Activities.Room_stories_activity;

/**
 * Created by wingsolarxu on 2018/5/17.
 */

public class PublishStoryThread extends Thread{

    private Handler handler;
    private Bitmap bm;
    private String username;
    private String room;
    private String content;
    private String date;
    private String pic_string;

    public PublishStoryThread(Handler handler, Bitmap bm, String username, String room, String content, String date, String pic_string){
        this.handler=handler;
        this.bm=bm;
        this.username=username;
        this.room=room;
        this.content=content;
        this.date=date;
        this.pic_string=pic_string;
    }


    @Override
    public void run() {

        Log.e("sss","12");
        final Message msg = new Message();
        msg.what = 1;
        FormBody requestBodyBuilder1=new FormBody.Builder()
                .add("name",username)
                .add("room",room)
                .add("story",content)
                .add("time",date)
                .add("pic",pic_string).build();

        Request.Builder builder = new Request.Builder();
        final Request request = builder.url("http://47.100.222.190:8080/WebApplication4/StoryServlet").post(requestBodyBuilder1).build();

        OkHttpClient okHttpClient=new OkHttpClient();

        Call call=okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Bundle b =new Bundle();
                b.putInt("response_code",0);
                msg.setData(b);
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    msg.obj=bm;
                    Log.e("sssss","3");
                    Bundle b =new Bundle();
                    b.putInt("response_code",1);
                    b.putString("content",content);
                    msg.setData(b);
                    handler.sendMessage(msg);
                }else{
                    Log.e("ccddd","eeeeee");
                    Bundle b =new Bundle();
                    b.putInt("response_code",2);
                    msg.setData(b);
                    handler.sendMessage(msg);
                }


                Log.e("sssss","2");

            }
        });
    }
}
