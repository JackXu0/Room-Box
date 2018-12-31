package wingsoloar.com.xjtlu_rooms.Objects;

/**
 * Created by wingsolarxu on 2018/3/9.
 */

public class Thought {

    private int id;
    private String username;
    private String building;
    private String room;
    private String date;
    private String day_of_week;
    private String time;
    private String content;
    private int issecret;
    private int isClock;

    // 0 stands for false and 1 stands for true

    public Thought(){

    }

    public Thought(String username, String building, String room, String date,String day_of_week, String time,String content, int issecret, int isClock){
        this.username=username;
        this.building=building;
        this.room=room;
        this.date=date;
        this.day_of_week=day_of_week;
        this.time=time;
        this.content=content;
        this.issecret=issecret;
        this.isClock=isClock;
    }


    public Thought(int id,String username, String building, String room, String date,String day_of_week, String time,String content, int issecret,int isClock){
        this.id=id;
        this.username=username;
        this.building=building;
        this.room=room;
        this.date=date;
        this.day_of_week=day_of_week;
        this.time=time;
        this.content=content;
        this.issecret=issecret;
        this.isClock=isClock;
    }

    public int getId(){
        return id;
    }

    public String getUsername(){
        return username;
    }

    public String getBuilding(){
        return building;
    }

    public String getRoom(){
        return room;
    }

    public String getDate(){
        return date;
    }

    public String getDay_of_week(){
        return day_of_week;
    }

    public String getTime_24(){
        return time;
    }

    public String getTime_12(){
        if (Integer.parseInt(time)<13)
            return time;
        else
            return (Integer.parseInt(time)-12)+"";
    }

    public String get_am_pm(){
        if (Integer.parseInt(time)<13)
            return "AM";
        else
            return "PM";
    }

    public String getContent(){
        return content;
    }

    public int IsSecret(){
        return issecret;
    }

    public int IsClock(){
        return isClock;
    }

}
