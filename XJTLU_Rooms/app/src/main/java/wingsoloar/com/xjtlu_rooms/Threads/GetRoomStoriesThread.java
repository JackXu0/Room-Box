package wingsoloar.com.xjtlu_rooms.Threads;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import wingsoloar.com.xjtlu_rooms.Objects.Story;

/**
 * Created by wingsolarxu on 2018/5/17.
 */

public class GetRoomStoriesThread extends  Thread {

    private String room_name;
    private Handler handler;
    private String avator_path;

    public GetRoomStoriesThread(String room_name,Handler handler,String avator_path){
        this.room_name=room_name;
        this.handler=handler;
        this.avator_path=avator_path;
    }

    @Override
    public void run() {
        final Message msg = new Message();
        msg.what=2;
        final ArrayList<Story> stories=new ArrayList<>();

        FormBody requestBodyBuilder1=new FormBody.Builder()
                .add("room",room_name).build();

        Request.Builder builder = new Request.Builder();
        Request request = builder.url("http://47.100.222.190:8080/WebApplication4/ReturnStory").post(requestBodyBuilder1).build();

        OkHttpClient okHttpClient=new OkHttpClient();
        Call call=okHttpClient.newCall(request);

        //execute contact_list_child
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
                    String res_string=response.body().string();

                    if(res_string.length()<2){
                        msg.obj=stories;
                        Bundle b =new Bundle();
                        b.putInt("response_code",1);
                        msg.setData(b);
                        handler.sendMessage(msg);
                        return;
                    }
                    String[] temp=res_string.split("!@!@");
                    Log.e("length",temp.length+"");

                    for(int i=0;i<temp.length;i++){
                        Bitmap bm= BitmapFactory.decodeFile(avator_path);;
                        try
                        {
                            byte[] bitmapArray;
                            bitmapArray = Base64.decode(temp[i].split("!@")[3], Base64.DEFAULT);
                            bm = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
                            Log.e("ss",bm+"");
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        Log.e("temp "+i,temp[i].length()+"");
                        stories.add(new Story(bm,temp[i].split("!@")[0],temp[i].split("!@")[2],temp[i].split("!@")[1]));
                    }

                    msg.obj=stories;
                    Bundle b =new Bundle();
                    b.putInt("response_code",1);
                    msg.setData(b);
                    handler.sendMessage(msg);


                }else{
                    Bundle b =new Bundle();
                    b.putInt("response_code",2);
                    msg.setData(b);
                    handler.sendMessage(msg);
                }


            }
        });
    }
}
