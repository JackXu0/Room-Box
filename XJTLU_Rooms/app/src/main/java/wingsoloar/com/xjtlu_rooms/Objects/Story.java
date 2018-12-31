package wingsoloar.com.xjtlu_rooms.Objects;

import android.graphics.Bitmap;

/**
 * Created by wingsolarxu on 2018/3/16.
 */

public class Story {

    private Bitmap photo;
    private String username;
    private String date;

    public Story(Bitmap bm, String username, String date, String content) {
        this.photo = bm;
        this.username = username;
        this.date = date;
        this.content = content;
    }

    private String content;



    public Bitmap getPhoto() {
        return photo;
    }

    public String getUsername() {
        return username;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }


}
