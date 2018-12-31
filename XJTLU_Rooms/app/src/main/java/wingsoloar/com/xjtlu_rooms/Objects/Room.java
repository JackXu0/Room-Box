package wingsoloar.com.xjtlu_rooms.Objects;

/**
 * Created by wingsolarxu on 2017/9/22.
 */

public class Room {
    private String name;
    private String type;
    private String building;
    private String timetable;
    String wifi;
    String printer;
    private char[] timetable_char_array;

    public Room(){}
    public Room(String name, String type, String building, String timetable,String wifi, String printer){

        this.name=name;
        this.type=type;
        this.building=building;
        this.timetable=timetable;
        this.wifi=wifi;
        this.printer=printer;
    }

    public String getName(){
        return name;
    }

    public String getRoomType(){
        return type;
    }

    public String getWifi(){
        return wifi;
    }

    public  String getPrinter(){return printer;}

    public String get_bar_color(){
        switch (building){
            case "B":
                return "#A8A8A8";

            case "P":
                return "#6666cc";

            case "EE":
                return "#cc6699";

            case "EB":
                return "#cc6699";

            case "CB":
                return "#FFCC99";

            case "FB":
                return "#ccffcc";

            case "S":
                return "#cc9966";

            case "DB":
                return "#0099cc";

            case "HS":
                return "#993333";

            case "BSG":
                return "#FF6699";

            case "ES":
                return "#e0e0e0";
            default:
                return "#808080";

        }

    }


//    public String getRoomTypeString(){
//        if(type==0)
//            return "All Rooms";
//        else if(type==1)
//            return "Large Lecture Room";
//        else if(type==2)
//            return "Medium Lecture Room";
//        else if(type==3)
//            return "Self-study Room";
//        else
//            return "COmputer Room";
//    }

    public int getBuildingInt(){
        switch (building){
            case "FB":
                return 1;

            case "S":
                return 2;

            case "PB":
                return 3;

            case "EE":
                return 4;

            case "EB":
                return 5;

            case "IBSS(N)":
                return 6;
        }
        return 0;
    }

    public String getBuilding(){
        return building;
    }

    public boolean isAvailable(int b){
        if(b<8||b>21)
            return true;
        int temp=b-8;

//        Log.e("room1",b+"");
//        Log.e("room1",temp+"");
        timetable_char_array=timetable.toCharArray();
        if(timetable_char_array[temp]=='0')
            return true;
        else
            return false;
    }

//    public String get_one_day_timetable(int day_of_week){
//        int start_point=13*(day_of_week-2);
//        int end_point=13*(day_of_week-1);
//
//        return timetable.substring(start_point,end_point);
//
//    }

    public String getTimetable(){
        return timetable;
    }

}
