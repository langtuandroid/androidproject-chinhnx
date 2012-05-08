package com.krazevina.euro2012;

import java.util.Vector;

import com.krazevina.euro2012.News.ViewHolder;
import com.krazevina.objects.Global;
import com.krazevina.objects.Match;
import com.krazevina.objects.sqlite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class Main extends Activity implements OnClickListener {
	
    Button btnSchedule,btnNews,btnTeams,btnSetting;
    LinearLayout llbtnsched,llbtnnews,llbtnteams,llbtnsetting;
    Vector<Match> match;
    LinearLayout llgroupa,llgroupb,llgroupc,llgroupd,llquarter,llsemi,llfinal;
    int i ;
    sqlite sql;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Global.getLang(this);
        llbtnsched = (LinearLayout)findViewById(R.id.llbtnsched);
        llbtnnews = (LinearLayout)findViewById(R.id.llbtnnews);
        llbtnteams = (LinearLayout)findViewById(R.id.llbtnteams);
        llbtnsetting = (LinearLayout)findViewById(R.id.llbtnsetting);
        
        llgroupa = (LinearLayout)findViewById(R.id.llgroupa);
        llgroupb = (LinearLayout)findViewById(R.id.llgroupb);
        llgroupc = (LinearLayout)findViewById(R.id.llgroupc);
        llgroupd = (LinearLayout)findViewById(R.id.llgroupd);
        llquarter = (LinearLayout)findViewById(R.id.llquarter);
        llsemi = (LinearLayout)findViewById(R.id.llsemi);
        llfinal = (LinearLayout)findViewById(R.id.llfinal);
                
        llbtnnews.setOnTouchListener(touch);
        llbtnsched.setOnTouchListener(touch);
        llbtnteams.setOnTouchListener(touch);
        llbtnsetting.setOnTouchListener(touch);
        
        btnSchedule = (Button)findViewById(R.id.btnSchedule);
        btnNews = (Button)findViewById(R.id.btnNews);
        btnTeams = (Button)findViewById(R.id.btnTeams);
        btnSetting = (Button)findViewById(R.id.btnSetting);
        
        btnNews.setOnClickListener(this);
        btnTeams.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        sql = new sqlite(this);
        
        match = sql.getAllMatches();
        
		LinearLayout ll;
		LayoutInflater mInflater = LayoutInflater.from(this);
		TextView txtsday,txtstime,txtsteam1,txtsteam2,txtsratio;
		ImageView imgsflag1,imgsflag2;
		
		int j = 0;
		for(i = 0;i<match.size();i++){
			ll = (LinearLayout) mInflater.inflate(R.layout.itemschedule, null);
			ll.setOrientation(LinearLayout.HORIZONTAL);

			txtsday = (TextView)ll.findViewById(R.id.txtsday);
			txtstime = (TextView)ll.findViewById(R.id.txtstime);
			txtsteam1 = (TextView)ll.findViewById(R.id.txtsteam1);
			txtsteam2 = (TextView)ll.findViewById(R.id.txtsteam2);
			txtsratio = (TextView)ll.findViewById(R.id.txtsratio);
			imgsflag1 = (ImageView)ll.findViewById(R.id.imgsflag1);
			imgsflag2 = (ImageView)ll.findViewById(R.id.imgsflag2);
			String timefull = match.elementAt(i).start;
			String time[] = timefull.split(" ");
			txtsday.setText(time[0].split("-")[2]+"/"+time[0].split("-")[1]);
			txtstime.setText(time[1].split(":")[0]+":"+time[1].split(":")[1]);
			int teamID1 = match.elementAt(i).team1;
			int teamID2 = match.elementAt(i).team2;
			imgsflag1.setImageResource(searchFlag(teamID1));
			imgsflag2.setImageResource(searchFlag(teamID2));
			txtsteam1.setText(sql.searchNameTeam(teamID1));
			txtsteam2.setText(sql.searchNameTeam(teamID2));
			String ratio = match.elementAt(i).finalScore;
			if(ratio!=null){
				txtsratio.setText(ratio);
			}else{
				txtsratio.setText("?-?");
			}
			txtsteam1.setOnClickListener(new OnClickListener() {
				int ind = i;
				@Override
				public void onClick(View v) {
					Intent i  = new Intent(Main.this,TeamDetails.class);
					i.putExtra("idTeam", match.elementAt(ind).team1);
					startActivity(i);
				}
			});
			txtsteam2.setOnClickListener(new OnClickListener() {
				int ind = i;
				@Override
				public void onClick(View v) {
					Intent i  = new Intent(Main.this,TeamDetails.class);
					i.putExtra("idTeam", match.elementAt(ind).team2);
					startActivity(i);
				}
			});
			j++;
			if(j%2==0){
				ll.setBackgroundColor(Color.parseColor("#ffffff"));
			}else{
				ll.setBackgroundColor(Color.parseColor("#e9efe9"));
			}
			
			if(match.elementAt(i).groupID == 1){
			   llgroupa.addView(ll);
			}else if(match.elementAt(i).groupID ==2){
				llgroupb.addView(ll);
			}else if(match.elementAt(i).groupID ==3){
				llgroupc.addView(ll);
			}else if(match.elementAt(i).groupID ==4){
				llgroupd.addView(ll);
			}else if(match.elementAt(i).groupID ==5){
				llquarter.addView(ll);
			}else if(match.elementAt(i).groupID ==6){
				llsemi.addView(ll);
			}else if(match.elementAt(i).groupID ==7){
				llfinal.addView(ll);
			}
		}
    }
    
    OnTouchListener touch = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			try{
				LinearLayout l = (LinearLayout)v;
				((Button)l.getChildAt(0)).onTouchEvent(event);
			}catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
	};
    
    
	@Override
	public void onClick(View v) {
		if(v.getId() == btnNews.getId()){
			startActivity(new Intent(this,News.class));
			finish();
		}
		
		if(v.getId() == btnTeams.getId()){
			startActivity(new Intent(this,Teams.class));
			finish();
		}
		
		if(v.getId() == btnSetting.getId()){
			startActivity(new Intent(this,Setting.class));
			finish();
		}
		
	}
	
	
	public int searchFlag(int teamID){
		switch (teamID) {
		case 157:
			return R.drawable.pol;
		case 155:
			return R.drawable.ger;
		case 171:
			return R.drawable.ita;
		case 159:
			return R.drawable.eng;
		case 10144:
			return R.drawable.gre;
		case 10145:
			return R.drawable.rus;
		case 175:
			return R.drawable.cze;
		case 166:
			return R.drawable.ned;
		case 10141:
			return R.drawable.den;
		case 168:
			return R.drawable.por;
		case 185:
			return R.drawable.esp;
		case 10148:
			return R.drawable.irl;
		case 179:
			return R.drawable.cro;
		case 182:
			return R.drawable.fra;
		case 186:
			return R.drawable.ukr;
		case 162:
			return R.drawable.swe;
		default:
			break;
		}
		
		return 0;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		sql.recycle();
	}
	
}