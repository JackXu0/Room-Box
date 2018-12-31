package wingsoloar.com.xjtlu_rooms.Threads;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wingsolarxu on 2018/5/18.
 */

public class CheckInternetThread extends  Thread{

    private Handler handler;

    public CheckInternetThread(Handler handler){
        this.handler=handler;
    }

    @Override
    public void run() {
        Log.e("dcddcdcdc","2");
        final Message msg=new Message();
        msg.what=1;
        final int[] result=new int[1];
        final boolean s;
        OkHttpClient.Builder http_builder = new OkHttpClient.Builder();
        http_builder.connectTimeout(1000, TimeUnit.SECONDS);
        http_builder.sslSocketFactory(createSSLSocketFactory());
        http_builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        OkHttpClient okHttpClient = http_builder.build();

        Request.Builder builder = new Request.Builder();
        final Request request = builder.url("https://mrbs.xjtlu.edu.cn/day.php?area=81&day=18&month=5&year=2018").get().build();
        Call call=okHttpClient.newCall(request);

        result[0]=0;
        //execute contact_list_child
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("dcddcdcdc","3");
                Bundle b =new Bundle();
                b.putInt("response_code",0);
                msg.setData(b);
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if(response.isSuccessful()){
                    Log.e("dcddcdcdc","4");
                    Bundle b =new Bundle();
                    b.putInt("response_code",1);
                    msg.setData(b);
                    handler.sendMessage(msg);
                }else{
                    Log.e("dcddcdcdc","3");
                    Bundle b =new Bundle();
                    b.putInt("response_code",2);
                    msg.setData(b);
                    handler.sendMessage(msg);
                }

            }
        });

    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }

    static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {}

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
    }

}
