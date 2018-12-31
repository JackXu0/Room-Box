package wingsoloar.com.xjtlu_rooms.Activities;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import wingsoloar.com.xjtlu_rooms.Objects.Thought;
import wingsoloar.com.xjtlu_rooms.Database.mDBThoughts;
import wingsoloar.com.xjtlu_rooms.R;

/**
 * Created by wingsolarxu on 2018/3/18.
 */

public class MyThoughts_activity extends Activity {

    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;

    private ImageView back_button;

    private ArrayList<String> group;
    private ArrayList<ArrayList<Thought>> child;
    private ArrayList<Thought> thoughts;
    private mDBThoughts dbThoughts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_thought_main_scroll);

        Calendar c = Calendar.getInstance();
        long init=c.getTimeInMillis();
        init();
        Log.e("time="+(c.getTimeInMillis()-init)+"","initial my thoughts");

    }

    private void init(){
        dbThoughts=new mDBThoughts(getBaseContext());

        group = new ArrayList<String>();
        child = new ArrayList<ArrayList<Thought>>();
        thoughts=new ArrayList<>();

        thoughts=(ArrayList<Thought>) dbThoughts.queryAll();

        if(thoughts.size()==0){
            finish();
            Toast.makeText(getBaseContext(),"No Thoughts Yet",Toast.LENGTH_SHORT).show();
            return;
        }

        Log.e("ssd",group.size()+"dfafds");

        //fillful group and thought array
        int count=-1;
        for (int i =0;i<thoughts.size();i++){

            if(!group.contains(thoughts.get(i).getDay_of_week()+", "+thoughts.get(i).getDate())){
                group.add(0,thoughts.get(i).getDay_of_week()+", "+thoughts.get(i).getDate());
                count=count+1;
                child.add(0,new ArrayList<Thought>());
                child.get(0).add(0,thoughts.get(i));
            }else{
                child.get(0).add(0,thoughts.get(i));
            }
        }

        expandableListView = findViewById(R.id.expandableListView);
        expandableListAdapter = new ExpandableListAdapter(group, child);
        expandableListView.setAdapter(expandableListAdapter);
        back_button = findViewById(R.id.back_button);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(getBaseContext(),Index_activity.class);
                startActivity(intent);
            }
        });

        for (int i = 0; i < group.size(); i++) {
            expandableListView.expandGroup(i);
        }

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;
            }
        });
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private final LayoutInflater inf;
        private ArrayList<String> groups;
        private ArrayList<ArrayList<Thought>> thoughts;

        public ExpandableListAdapter(ArrayList<String> groups, ArrayList<ArrayList<Thought>> children) {
            this.groups = groups;
            thoughts = children;
            inf = LayoutInflater.from(getBaseContext());

        }

        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            Log.e("ssd",""+groupPosition+" groupPosition");
            Log.e("ssd",""+thoughts.get(0).size()+" users.size");
            return thoughts.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return thoughts.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            final ChildViewHolder holder;
            if (convertView == null) {
                convertView = inf.inflate(R.layout.my_thoughts_child, parent, false);
                holder = new ChildViewHolder();
                holder.hour_tv = convertView.findViewById(R.id.hour_tv);
                holder.am_pm_tv = convertView.findViewById(R.id.am_pm_tv);
                holder.building_color = convertView.findViewById(R.id.building_color);
                //holder.building_color.setImageResource(R.drawable.avator_male);
                holder.room_name = (TextView) convertView.findViewById(R.id.room_name);
                holder.content = (TextView) convertView.findViewById(R.id.content);
                convertView.setTag(holder);
            } else {
                holder = (ChildViewHolder) convertView.getTag();
            }

            Thought thought = (Thought) getChild(groupPosition, childPosition);

            holder.hour_tv.setText(thought.getTime_12());
            holder.am_pm_tv.setText(thought.get_am_pm());
            holder.room_name.setText(thought.getRoom());
            holder.content.setText(thought.getContent());

            switch (thought.getBuilding()) {
                case "FB":
                    holder.building_color.setImageResource(R.drawable.building_color_fb);
                    break;
                case "S":
                    holder.building_color.setImageResource(R.drawable.building_color_s);
                    break;
                case "P":
                    holder.building_color.setImageResource(R.drawable.building_color_p);
                    break;
                case "B":
                    holder.building_color.setImageResource(R.drawable.building_color_b);
                    break;
                case "EE":
                    holder.building_color.setImageResource(R.drawable.building_color_ee_eb);
                    break;
                case "EB":
                    holder.building_color.setImageResource(R.drawable.building_color_ee_eb);
                    break;
                case "HS":
                    holder.building_color.setImageResource(R.drawable.building_color_hs);
                    break;
                case "DB":
                    holder.building_color.setImageResource(R.drawable.building_color_db);
                    break;
                case "IBSS":
                    holder.building_color.setImageResource(R.drawable.building_color_ibss);
                    break;
                case "ES":
                    holder.building_color.setImageResource(R.drawable.building_color_es);
                    break;
            }

            return convertView;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final GroupViewHolder holder;

            if (convertView == null) {
                convertView = inf.inflate(R.layout.my_thoughts_group, parent, false);

                holder = new GroupViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.thoughts_date);

                convertView.setTag(holder);
            } else {
                holder = (GroupViewHolder) convertView.getTag();
            }

            holder.text.setText(getGroup(groupPosition).toString());

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            super.registerDataSetObserver(observer);
        }


    }

    static class ChildViewHolder {
        protected TextView hour_tv;
        protected TextView am_pm_tv;
        protected ImageView building_color;
        protected TextView room_name;
        protected TextView content;
    }

    static class GroupViewHolder {
        protected TextView text;
    }

}
