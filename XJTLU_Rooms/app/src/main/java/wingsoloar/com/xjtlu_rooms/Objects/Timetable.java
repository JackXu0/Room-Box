package wingsoloar.com.xjtlu_rooms.Objects;

/**
 * Created by wingsolarxu on 2018/3/12.
 */

public class Timetable {

    private String room_name;
    private int hour_12;
    private int hour_24;
    private String am_pm;
    private String bar_color;

    public Timetable(){}

    public Timetable(String room_name, int hour_12, String am_pm, String bar_color){
        this.room_name=room_name;
        this.hour_12=hour_12;
        this.am_pm=am_pm;
        this.bar_color=bar_color;
    }

    public String getRoom_name(){
        return room_name;
    }

    public int getHour_12(){
        return hour_12;
    }

    public String getAM_PM(){
        return am_pm;
    }

    public String get_bar_color(){
        return bar_color;
    }
}
