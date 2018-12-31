package wingsoloar.com.xjtlu_rooms.Network;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
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
import wingsoloar.com.xjtlu_rooms.Database.mDBRoom;

/**
 * Created by wingsolarxu on 2018/5/7.
 */

public class GetRoomData {

    private static int total=25;
    private static int count=0;
    private static mDBRoom database;
    private static ArrayList<String> rooms=new ArrayList<>();

    public static void crawl(Activity activity,Context context,Thread thread){

        database= new mDBRoom(context);
        database.clear();
        rooms.clear();
        count=0;

        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        final int m = c.get(Calendar.MONTH)+1;
        int d = c.get(Calendar.DAY_OF_MONTH);
        String[] areaClass = {"24","9","3","54","40","60","39","58","81","36","26"};
        String[] areaComputer ={"80","37","53","68","17","67","23"};
        String[] areaLecture ={"35","63","61","1","59","79","27"};

        for(int i=0; i<areaClass.length; i++){
            String url = "https://mrbs.xjtlu.edu.cn/day.php?year="+y+"&month="+m+"&day="+d+"&area="+areaClass[i];
            System.out.println(url);
            System.out.println("area:" + areaClass[i]);
            url = url + " C";
            try {
                captureData(url,activity,thread,areaClass[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for(int i=0; i<areaComputer.length; i++){
            String url = "https://mrbs.xjtlu.edu.cn/day.php?year="+y+"&month="+m+"&day="+d+"&area="+areaComputer[i];
            System.out.println(url);
            System.out.println("area:" + areaComputer[i]);
            url = url + " S";
            try {
                captureData(url,activity,thread,areaComputer[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for(int i=0; i<areaLecture.length; i++){
            String url = "https://mrbs.xjtlu.edu.cn/day.php?year="+y+"&month="+m+"&day="+d+"&area="+areaLecture[i];
            System.out.println(url);
            System.out.println("area:" + areaLecture[i]);
            url = url + " L";
            try {
                captureData(url,activity,thread,areaLecture[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public static void captureData(final String urlA, final Activity activity, final Thread thread, final String area) throws Exception {

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
        //http://47.100.222.190:8080/WebApplication4/RoomsServlet?time=2018/5/5
        Log.e("URL",urlA.substring(0,urlA.length()-2));
        Request request1 = builder.url(urlA.substring(0,urlA.length()-2)).get().build();

        Call call=okHttpClient.newCall(request1);

        //execute contact_list_child
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ddd1","1");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {

                    String res_string= response.body().string();

                    Document doc = Jsoup.parse(res_string);
                    Elements tableElements = doc.getElementsByClass("dwm_main").select("tbody").select("tr");
                    Elements elements1 = doc.getElementsByClass("dwm_main").select("th");
                    int [][] occupation = new int[tableElements.size()][elements1.size()];

                    for (int i=0;i<tableElements.size();i++){
                        for (int j=0;j<elements1.size();j++){
                            occupation[i][j]=0;
                        }
                    }

                    String[][] matrix = new String[tableElements.size() + 1][elements1.size()];

                    for (int i = 1; i < tableElements.size() +1 ; i++) {
                        for (int j = 0; j < elements1.size() ; j++) {
                            matrix[i][j] = "0";
                        }
                    }

                    for (int i = 0; i < elements1.size(); i++) {
                        matrix[0][i] = String.valueOf(elements1.get(i).text());
                    }

                    for (int m = 0; m < tableElements.size() ; m++) {
                        int column=0;
                        Elements tds = tableElements.get(m).select("td");
                        for (int n = 1; n < tds.size(); n++) {
                            if(column>=elements1.size()){
                                break;
                            }
                            String code = String.valueOf(tds.get(n));
                            //System.out.println("tds size "+tds.size());
                            while(occupation[m][column]==1){
                                column++;
                            }
                            //System.out.print("column "+column+"; ");
                            if(code.contains("rowspan")){
                                //System.out.print(1);
                                String code2 =code.substring(code.indexOf("rowspan=\"")+9,code.indexOf("rowspan=\"")+13);
                                String code3 = code2.replaceAll("[^0-9]","");
                                int rowSpan = Integer.parseInt(code3);
                                System.out.println("column="+column+"; n="+n+";rowspan="+rowSpan);
                                for(int i=0;i<rowSpan;i++){
                                    occupation[m+i][column]=1;
                                }
                            }

                            if ((code.contains("D")||code.contains("I"))&&code.contains("rowspan")){

                                String code2 =code.substring(code.indexOf("rowspan=\"")+9,code.indexOf("rowspan=\"")+13);
                                String code3 = code2.replaceAll("[^0-9]","");
                                int rowSpan = Integer.parseInt(code3);

                                for(int q =0;q<rowSpan;q++) {
                                    matrix[m+q+1][column] = "1";
                                }
                            }

                            column++;
                        }
                    }

                    LinkedHashMap<String, String> sequence = new LinkedHashMap<String, String>();

                    for (int j=1;j<elements1.size();j++) {
                        String repre= "";
                        for (int i=1;i<tableElements.size()+1;i++){
                            repre = repre + matrix[i][j-1];
                        }
                        repre = repre + "0";
                        String seq = "";
                        char[] array = repre.toCharArray();
                        for(int a = 0;a<array.length-2;a=a+2){
                            if(array[a]=='0'&&array[a+1]=='0'){
                                seq = seq +"0";
                            }else{
                                seq = seq + "1";
                            }
                        }
                        String roomArea = matrix[0][j].replaceAll("[^a-z^A-Z]", "");
                        String room = roomArea + " "+ matrix[0][j].replaceAll("[a-zA-Z]", "");
                        String regex = ".*[a-zA-z].*";
                        if (!matrix[0][j].matches(regex)){
                            room = "FB "+matrix[0][j];
                        }
                        Log.e("timetable",room+" ; "+seq);
                        sequence.put(room,seq);
                    }

                    Iterator<Map.Entry<String, String>> it = sequence.entrySet().iterator();

                    while (it.hasNext()) {
                        String consequence = "";
                        Map.Entry<String, String> entry = it.next();
                        String name=entry.getKey();
                        if(name.contains("(")){
                            name=name.substring(0,name.indexOf("("));
                        }

                        if(name.contains("C -")){
                            name=name.replace("C -","CB ");
                        }

                        if(name.trim().length()==3&&name.charAt(1)==' '){
                            name="FB "+name.trim().charAt(2)+name.trim().charAt(0);
                        }
                        if (urlA.endsWith("C")) {
                            consequence = name + ";Class Room;" + entry.getKey().replaceAll("[^a-z^A-Z]", "") + ";" + entry.getValue()+";"+area;
                        }
                        if(urlA.endsWith("S")){
                            consequence = name + ";Computer Room;" + entry.getKey().replaceAll("[^a-z^A-Z]", "") + ";" + entry.getValue()+";"+area;
                        }
                        if(urlA.endsWith("L")){
                            consequence = name + ";Lecture Theatre;" + entry.getKey().replaceAll("[^a-z^A-Z]", "") + ";" + entry.getValue()+";"+area;
                        }
                        Log.e("sequnence",consequence);
                        rooms.add(consequence);
                        Log.e("room size2:", rooms.size()+"");
                    }

                    count++;
                    Log.e("count",count+"");
                    if(count==total){
                        database.addAll(rooms);
                        try {
                            thread.interrupt();
                        }catch(Exception e){
                            Log.e("ad","ddsdsd");
                        }
                    }

                }else{
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity,"Server Error",Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.e("server error","1");

                    activity.finish();
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






