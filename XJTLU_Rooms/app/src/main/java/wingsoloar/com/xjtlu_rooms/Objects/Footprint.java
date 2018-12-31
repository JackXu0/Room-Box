package wingsoloar.com.xjtlu_rooms.Objects;

/**
 * Created by wingsolarxu on 2018/3/10.
 */

public class Footprint {

    private String room_name;
    private int hour;
    private String am_pm;
    private String bar_color;
    private String date;
    private String isCouple;
    private int duration;

    public Footprint(){

    }

    public Footprint(String room_name,int hour, String am_pm,String bar_color){
        this.room_name=room_name;
        this.hour=hour;
        this.am_pm=am_pm;
        this.bar_color=bar_color;
    }

    public Footprint(String room_name,String date, int hour, int duration, String bar_color, String isCouple){
        this.room_name=room_name;
        this.date=date;
        this.hour=hour;
        this.duration=duration;
        this.bar_color=bar_color;
        this.isCouple=isCouple;
    }

    public String getRoom_name(){
        return room_name;
    }

    public String getDate(){
        return date;
    }

    public String isCouple(){

        return isCouple;
    }

    public int getHour(){
        return hour;
    }

    public int getDuration(){
        return duration;
    }

    public String getAM_PM(){
        return am_pm;
    }

    public String get_bar_color(){
        return bar_color;
    }
}
