package wingsoloar.com.xjtlu_rooms.Threads;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wingsolarxu on 2018/5/17.
 */

public class SignUpThread extends  Thread {

    private Handler handler;
    private String room_name;
    private String date;
    private String lastTime;
    private String isCouple;

    public SignUpThread(Handler handler,String room_name,String date,String lastTime, String isCouple){
        this.handler=handler;
        this.room_name=room_name;
        this.date=date;
        this.lastTime=lastTime;
        this.isCouple=isCouple;
    }


    @Override
    public void run() {
        final Message msg=new Message();

        FormBody requestBodyBuilder1=new FormBody.Builder()
                .add("room",room_name)
                .add("time",date)
                .add("lastTime",lastTime)
                .add("isCouple",isCouple).build();

        Request.Builder builder = new Request.Builder();

        Request request = builder.url("http://47.100.222.190:8080/WebApplication4/SignInServlet").post(requestBodyBuilder1).build();

        OkHttpClient okHttpClient=new OkHttpClient();
        Call call=okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                msg.what = 1;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                msg.what = 1;
                handler.sendMessage(msg);

            }
        });
    }
}
