package com.krazevina.objects;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class sqlite 
{
	private static String DB_PATH = "/data/data/com.krazevina.euro2012/databases/";
	private static final String DATABASE_NAME="euro2012.dbo";
	private static final int DATABASE_VERSION=1;
	
	private SQLiteDatabase mSqlDatabase;
	private SQLiteRssHelper sqlitehelper;
	
	public sqlite(Context context) {
		sqlitehelper=new SQLiteRssHelper(context);
		this.mSqlDatabase=sqlitehelper.getWritableDatabase();
		if(mSqlDatabase.getVersion()!=DATABASE_VERSION)
			sqlitehelper.onUpgrade(mSqlDatabase, mSqlDatabase.getVersion(), DATABASE_VERSION);
	}

	private class SQLiteRssHelper extends SQLiteOpenHelper 
	{
	    private Context myContext;
	    public SQLiteRssHelper(Context context) {
	    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
	        this.myContext = context;
	    }
	    
	    @Override
		public synchronized SQLiteDatabase getWritableDatabase() {
			createDataBase();
			return super.getWritableDatabase();
		}
	 
	    public void createDataBase(){
	    	boolean dbExist = checkDataBase();
	    	if(dbExist){
	    	}else{
	        	try {
	    			copyDataBase();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	        	}
	    	}
	    }
	 
	    private boolean checkDataBase(){
	    	SQLiteDatabase checkDB = null;
	    	try{
	    		String myPath = DB_PATH + DATABASE_NAME;
	    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	    	}catch(SQLiteException e){
	    	}
	 
	    	if(checkDB != null){
	    		checkDB.close();
	    	}
	    	return checkDB != null ? true : false;
	    }
	 
	    private void copyDataBase() throws IOException{
	    	InputStream myInput = myContext.getAssets().open(DATABASE_NAME);
	    	String outFileName = DB_PATH + DATABASE_NAME;
	    	File f = new File(outFileName);
	    	f.getParentFile().mkdirs();
	    	f.createNewFile();
	    	OutputStream myOutput = new FileOutputStream(f);
	 
	    	byte[] buffer = new byte[1024];
	    	int length;
	    	while ((length = myInput.read(buffer))>0){
	    		myOutput.write(buffer, 0, length);
	    	}
	    	myOutput.flush();
	    	myOutput.close();
	    	myInput.close();
	    }
		@Override
		public void onCreate(SQLiteDatabase db) {
		}
	 
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
	
	public synchronized void recycle()
	{
		if(mSqlDatabase!=null)mSqlDatabase.close();
		if(sqlitehelper!=null)sqlitehelper.close();
	}
	
	public Vector<Match>getAllMatches(){
		Vector<Match>ret = new Vector<Match>();
		Cursor c = mSqlDatabase.query("Matches", new String[]{
				"ID","GroupID","FirstTeam","SecondTeam","Stadium","Start","End",
				"FinalScore","MainReferee","FirstPickup","SecondPickup","Status"}, null, null, null, null, null);
		if(c==null)return ret;
		Match m;
		c.moveToFirst();
		for(int i=0;i<c.getCount();i++){
			m = new Match();
			m.ID = c.getInt(c.getColumnIndex("ID"));
			m.groupID = c.getInt(c.getColumnIndex("GroupID"));
			m.team1 = c.getInt(c.getColumnIndex("FirstTeam"));
			m.team2 = c.getInt(c.getColumnIndex("SecondTeam"));
			m.stadium = c.getString(c.getColumnIndex("Stadium"));
			m.start = c.getString(c.getColumnIndex("Start"));
			m.end = c.getString(c.getColumnIndex("End"));
			m.finalScore = c.getString(c.getColumnIndex("FinalScore"));
			m.referee = c.getString(c.getColumnIndex("MainReferee"));
			m.firstPick = c.getInt(c.getColumnIndex("FirstPickup"));
			m.secPick = c.getInt(c.getColumnIndex("SecondPickup"));
			m.status = c.getInt(c.getColumnIndex("Status"));
			ret.add(m);
			c.moveToNext();
		}
		c.close();
		return ret;
	}
	
	public Team getTeam(int teamID){
		Team t = null;
		Cursor c = mSqlDatabase.query("Teams", new String[]{
				"ID","BongdasoID","Name","Flag","Uniform1","Uniform2","EstablishedYear",
				"FifaJoinedYear","FifaRanking","Coach","Desc","AttendTimes",
				"status","NameEng","NameKor","descEng","descKor"}, "BongdasoId="+teamID, null, null, null, null);
		if(c==null)return t;
		c.moveToFirst();
		t = new Team();
		t.ID = c.getInt(c.getColumnIndex("BongdasoID"));
		t.name = c.getString(c.getColumnIndex("Name"));
		t.flag = c.getString(c.getColumnIndex("Flag"));
		t.uniform1 = c.getString(c.getColumnIndex("Uniform1"));
		t.uniform2 = c.getString(c.getColumnIndex("Uniform2"));
		t.establish = c.getInt(c.getColumnIndex("EstablishedYear"));
		t.fifaJoin = c.getInt(c.getColumnIndex("FifaJoinedYear"));
		t.fifaRank = c.getInt(c.getColumnIndex("FifaRanking"));
		t.coach = c.getString(c.getColumnIndex("Coach"));
		t.desc = c.getString(c.getColumnIndex("Desc"));
		t.attendTimes = c.getString(c.getColumnIndex("AttendTimes"));
		t.status = c.getInt(c.getColumnIndex("status"));
		t.nameEng = c.getString(c.getColumnIndex("NameEng"));
		t.nameKor = c.getString(c.getColumnIndex("NameKor"));
		t.descEng = c.getString(c.getColumnIndex("descEng"));
		t.descKor = c.getString(c.getColumnIndex("descKor"));
		c.close();
		return t;
	}
	
	public void getTeamGroup(Team t){
		Cursor c = mSqlDatabase.query("TeamsInRound", new String[]{
			"ID","RoundID","TeamID","Point","LooseScore","WinScore","Win",
			"Lose","Draw","Status"}, "TeamID="+t.ID, null, null, null, null);
		c.moveToFirst();
		t.point = c.getInt(3);
		t.goallose = c.getInt(4);
		t.goalscore = c.getInt(5);
		t.win = c.getInt(6);
		t.lose = c.getInt(7);
		t.draw = c.getInt(8);
		t.status = c.getInt(9);
	}
	
	public Vector<Team> getAllTeams(){
		Vector<Team>tt = new Vector<Team>();
		Team t = null;
		Cursor c = mSqlDatabase.query("Teams", new String[]{
				"ID","BongdasoID","Name","Flag","Uniform1","Uniform2","EstablishedYear",
				"FifaJoinedYear","FifaRanking","Coach","Desc","AttendTimes",
				"status","NameEng","NameKor","descEng","descKor"}, null, null, null, null, null);
		if(c==null)return tt;
		c.moveToFirst();
		for(int i=0;i<c.getCount();i++){
			t = new Team();
			t.ID = c.getInt(c.getColumnIndex("BongdasoID"));
			t.name = c.getString(c.getColumnIndex("Name"));
			t.flag = c.getString(c.getColumnIndex("Flag"));
			t.uniform1 = c.getString(c.getColumnIndex("Uniform1"));
			t.uniform2 = c.getString(c.getColumnIndex("Uniform2"));
			t.establish = c.getInt(c.getColumnIndex("EstablishedYear"));
			t.fifaJoin = c.getInt(c.getColumnIndex("FifaJoinedYear"));
			t.fifaRank = c.getInt(c.getColumnIndex("FifaRanking"));
			t.coach = c.getString(c.getColumnIndex("Coach"));
			t.desc = c.getString(c.getColumnIndex("Desc"));
			t.attendTimes = c.getString(c.getColumnIndex("AttendTimes"));
			t.status = c.getInt(c.getColumnIndex("status"));
			t.nameEng = c.getString(c.getColumnIndex("NameEng"));
			t.nameKor = c.getString(c.getColumnIndex("NameKor"));
			t.descEng = c.getString(c.getColumnIndex("descEng"));
			t.descKor = c.getString(c.getColumnIndex("descKor"));
			c.moveToNext();
			tt.add(t);
			getTeamGroup(t);
		}
		c.close();
		return tt;
	}
	
	public Vector<Player> getTeamPlayers(int teamID){
		Vector<Player>t = new Vector<Player>();
		Cursor c = mSqlDatabase.query("Players", new String[]{
				"TeamID","Name","Image","DOB","Height","Weight",
				"Club","Position","Number","Score","Status","PlayerTip"}, "TeamID="+teamID, null, null, null, null);
		if(c==null)return t;
		Player m;
		c.moveToFirst();
		for(int i=0;i<c.getCount();i++){
			m = new Player();
			m.teamID = teamID;
			m.name = c.getString(c.getColumnIndex("Name"));
			m.imageUrl = c.getString(c.getColumnIndex("Image"));
			m.dob = c.getString(c.getColumnIndex("DOB"));
			m.height = c.getString(c.getColumnIndex("Height"));
			m.weight = c.getString(c.getColumnIndex("Weight"));
			m.club = c.getString(c.getColumnIndex("Club"));
			m.pos = c.getString(c.getColumnIndex("Position"));
			m.number = c.getString(c.getColumnIndex("Number"));
			m.score = c.getInt(c.getColumnIndex("Score"));
			m.status = c.getInt(c.getColumnIndex("Status"));
			m.tip = c.getString(c.getColumnIndex("PlayerTip"));
			t.add(m);
			c.moveToNext();
		}
		c.close();
		return t;
	}
	
	public String searchNameTeam(int teamID){
		String nameTeam="";
		try {
			Cursor c = mSqlDatabase.query("Teams", new String[]{
					"BongdasoID","Name"}, "BongdasoId="+teamID, null, null, null, null);
			if(c==null)return null;

			c.moveToFirst();
			nameTeam = c.getString(1);
			c.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nameTeam;
	}
	
	public void getSetting(){
		try {
			Cursor c = mSqlDatabase.query("Setting", new String[]{"notify","vibrate"}, null, null, null, null, null);
			if(c==null) return;
			c.moveToFirst();
			Global.notify = c.getInt(0);
			Global.vibrate = c.getInt(1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setNotify(int i) {
		try {
			mSqlDatabase.execSQL("update setting set notify=" + i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setVibrate(int i) {
		try {
			mSqlDatabase.execSQL("update setting set vibrate=" + i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}