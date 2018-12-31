package wingsoloar.com.xjtlu_rooms.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import wingsoloar.com.xjtlu_rooms.R;

/**
 * Created by wingsolarxu on 2018/3/18.
 */

public class Personal_information_activity extends Activity {

    private RelativeLayout photo_setting;
    private RelativeLayout name_setting;
    private RelativeLayout gender_setting;
    private RelativeLayout major_setting;
    private ImageView back_button;
    private ImageView avator;
    private TextView name;
    private TextView gender;
    private TextView major;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_information_main);

        initLayout();
    }

    private void initLayout(){
        avator=findViewById(R.id.avator);
        photo_setting=findViewById(R.id.photo_setting);
        name_setting=findViewById(R.id.name_setting);
        gender_setting=findViewById(R.id.gender_setting);
        major_setting=findViewById(R.id.major_setting);
        back_button=findViewById(R.id.back_button);
        name=findViewById(R.id.name);
        gender=findViewById(R.id.gender);
        major=findViewById(R.id.major);

        refresh();

        photo_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(Personal_information_activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Personal_information_activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
                }else{

                    openAlbum();
                }
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        name_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),Name_setting_activity.class);
                startActivity(intent);
            }
        });

        gender_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),Gender_setting_activity.class);
                startActivity(intent);
            }
        });

        major_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),Major_setting_activity.class);
                startActivity(intent);
            }
        });
    }

    //refresh personal information
    private void refresh(){
        preferences=getSharedPreferences("user_info",MODE_PRIVATE);

        String imagePath=preferences.getString("avator_path","");

        name.setText(preferences.getString("name",""));
        major.setText(preferences.getString("major",""));

        if(preferences.getBoolean("is_male",false)){
            gender.setText("Male");
        }else{
            gender.setText("Female");
        }

        //if avator_path is not ""
        if(imagePath.length()>1){
            displayImage(imagePath);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refresh();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissons,int[] grantResult){
        switch(requestCode){
            case 10:if(grantResult.length>0 && grantResult[0]==PackageManager.PERMISSION_GRANTED){
                openAlbum();
            }else{
                Toast.makeText(Personal_information_activity.this,"permission denied",Toast.LENGTH_SHORT).show();
            }break;
            default:;
        }
    }

    public void openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case 2:if(resultCode==RESULT_OK){
                if(Build.VERSION.SDK_INT>=19){
                    handleImageOnKitKat(data);
                }else{
                    handleImageBeforeKitKat(data);
                }
            }break;
            default:;
        }
    }

    //save the path of new picture for avator and display the new image
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection= MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath=getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath=uri.getPath();
        }
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString("avator_path",imagePath);
        editor.commit();
        displayImage(imagePath);
    }

    //save the path of new picture for avator and display the new image
    private void handleImageBeforeKitKat(Intent data){
        String imagePath=null;
        Uri uri=data.getData();
        imagePath=getImagePath(uri,null);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString("avator_path",imagePath);
        editor.commit();
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri,String selection){
        String Path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                Path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return Path;
    }

    //refresh avator
    private void displayImage(String Path){
        Bitmap bm= BitmapFactory.decodeFile(Path);
        avator.setImageBitmap(imageCrop(bm));
    }

    //resize the picture to get a square
    public static Bitmap imageCrop(Bitmap bitmap) {

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int width = w > h ? h : w;

        int retX = (w - width) / 2;
        int retY = (h - width) / 2;

        return Bitmap.createBitmap(bitmap, retX, retY, width, width, null, false);
    }

}
