package wingsoloar.com.xjtlu_rooms.Threads;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wingsolarxu on 2018/5/18.
 */

public class GetPeopleInfoThread extends  Thread {

    private Handler handler;
    private int i;

    public GetPeopleInfoThread(Handler handler,int i){
        this.handler=handler;
        this.i=i;
    }
    @Override
    public void run() {
        final HashMap<String,String> hasPeople =new HashMap<>();
        final Message msg=new Message();
        msg.what=2;

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);

        Log.e("dcdcdc","1");

        msg.obj=hasPeople;
        Bundle b =new Bundle();
        b.putInt("response_code",1);
        b.putInt("index",i);
        msg.setData(b);
        handler.sendMessage(msg);


//        FormBody requestBodyBuilder1=new FormBody.Builder()
//                .add("time",year+";"+month+";"+day+";"+hour).build();
//
//        Request.Builder builder = new Request.Builder();
//        Log.e("time ",year+";"+month+";"+day+";"+hour);
//        Request request = builder.url("http://47.100.222.190:8080/WebApplication4/HasPeopleServlet").post(requestBodyBuilder1).build();
//
//        OkHttpClient okHttpClient=new OkHttpClient();
//
//        Call call=okHttpClient.newCall(request);
//
//        //execute contact_list_child
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                msg.obj=hasPeople;
//                Bundle b =new Bundle();
//                b.putInt("response_code",0);
//                b.putInt("index",i);
//                msg.setData(b);
//                handler.sendMessage(msg);
//            }
//
//            @Override
//            public void onResponse(Call call, final Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    Log.e("dcdcdc","3");
//                    String res_string=response.body().string();
//                    if(!res_string.contains(";;")){
//                        msg.obj=hasPeople;
//                        Bundle b =new Bundle();
//                        b.putInt("response_code",1);
//                        b.putInt("index",i);
//                        msg.setData(b);
//                        handler.sendMessage(msg);
//                        return;
//                    }
//                    String[] temp=res_string.split(";;");
//                    Log.e("ccfff",temp.length+"");
//                    Log.e("cceee",temp[0]);
//                    for(int i=0;i<temp.length;i++){
//
//                         hasPeople.put(temp[i].split(";")[0],temp[i].split(";")[1]);
//
//                    }
//                    msg.obj=hasPeople;
//                    Bundle b =new Bundle();
//                    b.putInt("response_code",1);
//                    b.putInt("index",i);
//                    msg.setData(b);
//                    handler.sendMessage(msg);
//
//                }else{
//                    msg.obj=hasPeople;
//                    Bundle b =new Bundle();
//                    b.putInt("response_code",2);
//                    b.putInt("index",i);
//                    msg.setData(b);
//                    handler.sendMessage(msg);
//                }
//
//
//            }
//        });
    }
}
