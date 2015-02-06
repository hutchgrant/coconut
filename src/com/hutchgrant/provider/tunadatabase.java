package com.hutchgrant.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class tunadatabase extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "tunadb110.db";
	private static final int DATABASE_VERSION = 1;
	
	public tunadatabase(Context ctx){
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("CREATE TABLE " + tunadatamodel.TunaProfile.NAME+ " ( " +
				tunadatamodel.TunaProfile.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				tunadatamodel.TunaProfile.Cols.USRID 	+ " TEXT , " +
				tunadatamodel.TunaProfile.Cols.USRTOKEN 	+ " TEXT , " +
				tunadatamodel.TunaProfile.Cols.USRNAME 	+ " TEXT , " +
				tunadatamodel.TunaProfile.Cols.USREMAIL	+ " TEXT , " +
				tunadatamodel.TunaProfile.Cols.USRPHONE 	+ " TEXT , " +

				tunadatamodel.TunaProfile.Cols.USRLOCATE 	+ " TEXT , " +
				tunadatamodel.TunaProfile.Cols.USRPROF + " TEXT, " +
				tunadatamodel.TunaProfile.Cols.USRIMGURL + " TEXT, " +
				tunadatamodel.TunaProfile.Cols.USRIMG + " BLOB, " +
				tunadatamodel.TunaProfile.Cols.USRREGDATE 	+ " TEXT , " +
				tunadatamodel.TunaProfile.Cols.USRREGTIME 	+ " TEXT , " +
				tunadatamodel.TunaProfile.Cols.USRLOGINDATE 	+ " TEXT , " +
				tunadatamodel.TunaProfile.Cols.USRLOGINTIME 	+ " TEXT , " +
				tunadatamodel.TunaProfile.Cols.MAXALBUM 	+ " INTEGER , " +
				tunadatamodel.TunaProfile.Cols.MAXIMG 	+ " INTEGER , " +
				tunadatamodel.TunaProfile.Cols.MAXGRP 	+ " INTEGER , " +
				tunadatamodel.TunaProfile.Cols.MAXCONT 	+ " INTEGER , " +

				"UNIQUE (" + 
					tunadatamodel.TunaProfile.Cols.ID + 
				") ON CONFLICT REPLACE)"
			);
		db.execSQL("CREATE TABLE " + tunadatamodel.TunaImage.NAME+ " ( " +
				tunadatamodel.TunaImage.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				tunadatamodel.TunaImage.Cols.IMGID 	+ " TEXT , " +
				tunadatamodel.TunaImage.Cols.IMGALBID 	+ " TEXT , " +
				tunadatamodel.TunaImage.Cols.IMGNAME 	+ " TEXT , " +
				tunadatamodel.TunaImage.Cols.IMGTYPE 	+ " TEXT , " +
				tunadatamodel.TunaImage.Cols.IMGUSR + " TEXT, " +
				tunadatamodel.TunaImage.Cols.IMGUSRID + " TEXT, " +
				tunadatamodel.TunaImage.Cols.IMGTITLE + " TEXT, " +
				tunadatamodel.TunaImage.Cols.IMGDESC 	+ " TEXT , " +
				tunadatamodel.TunaImage.Cols.IMGUPTIME + " TEXT, " +
				tunadatamodel.TunaImage.Cols.IMGUPDATE 	+ " TEXT , " +
				tunadatamodel.TunaImage.Cols.IMGUPTOKEN 	+ " TEXT , " +
				tunadatamodel.TunaImage.Cols.IMGTPLUS + " TEXT, " +
				tunadatamodel.TunaImage.Cols.IMGTMINUS 	+ " TEXT , " +
				tunadatamodel.TunaImage.Cols.IMGTCOMMENTS + " TEXT, " +
				tunadatamodel.TunaImage.Cols.IMGPERMISS 	+ " TEXT , " +
				tunadatamodel.TunaImage.Cols.IMGSYNC + " TEXT, " +
				"UNIQUE (" + 
					tunadatamodel.TunaImage.Cols.ID + 
				") ON CONFLICT REPLACE)"
			);
		db.execSQL("CREATE TABLE " + tunadatamodel.TunaAlbum.NAME+ " ( " +
				tunadatamodel.TunaAlbum.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				tunadatamodel.TunaAlbum.Cols.ALBID 	+ " TEXT , " +
				tunadatamodel.TunaAlbum.Cols.ALBOWNER + " TEXT, " +
				tunadatamodel.TunaAlbum.Cols.ALBNAME 	+ " TEXT , " +
				tunadatamodel.TunaAlbum.Cols.ALBSIZE 	+ " INTEGER , " +
				tunadatamodel.TunaAlbum.Cols.ALBSYNCD + " TEXT, " +
				tunadatamodel.TunaAlbum.Cols.ALBSYNCT + " TEXT, " +
				tunadatamodel.TunaAlbum.Cols.ALBINITD + " TEXT, " +
				"UNIQUE (" + 
					tunadatamodel.TunaAlbum.Cols.ID + 
				") ON CONFLICT REPLACE)"
			);
		db.execSQL("CREATE TABLE " + tunadatamodel.TunaContact.NAME+ " ( " +
				tunadatamodel.TunaContact.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				tunadatamodel.TunaContact.Cols.CNTID 	+ " TEXT , " +
				tunadatamodel.TunaContact.Cols.CNTGRPID 	+ " TEXT , " +
				tunadatamodel.TunaContact.Cols.CNTGOOGID 	+ " TEXT , " +
				tunadatamodel.TunaContact.Cols.CNTNAME 	+ " TEXT , " +
				tunadatamodel.TunaContact.Cols.CNTPHONE 	+ " TEXT , " +
				tunadatamodel.TunaContact.Cols.CNTEMAIL + " TEXT, " +
				tunadatamodel.TunaContact.Cols.CNTIMG + " TEXT, " +
				"UNIQUE (" + 
					tunadatamodel.TunaContact.Cols.ID + 
				") ON CONFLICT REPLACE)"
			);
		db.execSQL("CREATE TABLE " + tunadatamodel.TunaGroup.NAME+ " ( " +
				tunadatamodel.TunaGroup.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				tunadatamodel.TunaGroup.Cols.GRPID 	+ " TEXT , " +
				tunadatamodel.TunaGroup.Cols.GRPNAME + " TEXT, " +
				tunadatamodel.TunaGroup.Cols.GRPSIZE + " INTEGER , " +
				tunadatamodel.TunaGroup.Cols.GRPSYNCDATE + " TEXT, " +
				tunadatamodel.TunaGroup.Cols.GRPSYNCTIME + " TEXT, " +
				"UNIQUE (" + 
					tunadatamodel.TunaGroup.Cols.ID + 
				") ON CONFLICT REPLACE)"
			);
		db.execSQL("CREATE TABLE " + tunadatamodel.TunaSync.NAME+ " ( " +
				tunadatamodel.TunaSync.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				tunadatamodel.TunaSync.Cols.SYNCUSERID 	+ " TEXT , " +
				tunadatamodel.TunaSync.Cols.SYNCTOKEN 	+ " TEXT , " +
				tunadatamodel.TunaSync.Cols.SYNCPROFDATE + " TEXT, " +
				tunadatamodel.TunaSync.Cols.SYNCPROFTIME + " TEXT , " +
				tunadatamodel.TunaSync.Cols.SYNCCONAMT + " INTEGER , " +
				tunadatamodel.TunaSync.Cols.SYNCGRPAMT + " INTEGER , " +
				tunadatamodel.TunaSync.Cols.SYNCGRPDATE + " TEXT, " +
				tunadatamodel.TunaSync.Cols.SYNCGRPTIME + " TEXT, " +
				tunadatamodel.TunaSync.Cols.SYNCALBAMT + " INTEGER , " +
				tunadatamodel.TunaSync.Cols.SYNCIMGAMT + " INTEGER , " +
				tunadatamodel.TunaSync.Cols.SYNCIMGDATE + " TEXT, " +
				tunadatamodel.TunaSync.Cols.SYNCIMGTIME + " TEXT, " +
				tunadatamodel.TunaSync.Cols.SYNCINVTOKEN + " TEXT, " +
				tunadatamodel.TunaSync.Cols.SYNCINVDATE + " TEXT, " +
				tunadatamodel.TunaSync.Cols.SYNCINVTIME + " TEXT, " +
				tunadatamodel.TunaSync.Cols.SYNCINV_RECAMT + " TEXT, " +
				tunadatamodel.TunaSync.Cols.SYNCINV_SNTAMT + " TEXT, " +
				tunadatamodel.TunaSync.Cols.SYNCMSGTOKEN + " TEXT, " +
				tunadatamodel.TunaSync.Cols.SYNCMSGDATE + " TEXT, " +
				tunadatamodel.TunaSync.Cols.SYNCMSGTIME + " TEXT, " +
				tunadatamodel.TunaSync.Cols.SYNCMSG_RECAMT + " TEXT, " +
				tunadatamodel.TunaSync.Cols.SYNCMSG_SNTAMT + " TEXT, " +
				tunadatamodel.TunaSync.Cols.SYNCMSG_CACHE + " TEXT, " +
				"UNIQUE (" + 
					tunadatamodel.TunaSync.Cols.SYNCUSERID + 
				") ON CONFLICT REPLACE)"
			);
		db.execSQL("CREATE TABLE " + tunadatamodel.TunaPref.NAME+ " ( " +
				tunadatamodel.TunaPref.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				tunadatamodel.TunaPref.Cols.PREFSYNCON 	+ " TEXT , " +
				tunadatamodel.TunaPref.Cols.PREFSYNCTIME 	+ " TEXT , " +
				tunadatamodel.TunaPref.Cols.PENDINGDATA 	+ " TEXT , " +
				"UNIQUE (" + 
					tunadatamodel.TunaPref.Cols.ID + 
				") ON CONFLICT REPLACE)"
			);
		db.execSQL("CREATE TABLE " + tunadatamodel.TunaInvite.NAME+ " ( " +
				tunadatamodel.TunaInvite.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				tunadatamodel.TunaInvite.Cols.INVTOKEN 	+ " TEXT , " +
				tunadatamodel.TunaInvite.Cols.INVSENDID 	+ " TEXT , " +
				tunadatamodel.TunaInvite.Cols.INVSENDNAME 	+ " TEXT , " +
				tunadatamodel.TunaInvite.Cols.INVRECNAME	+ " TEXT , " +
				tunadatamodel.TunaInvite.Cols.INVRECID	+ " TEXT , " +
				tunadatamodel.TunaInvite.Cols.INVRECEMAIL	+ " TEXT , " +
				tunadatamodel.TunaInvite.Cols.INVRECPHONE	+ " TEXT , " +
				tunadatamodel.TunaInvite.Cols.INVDATE	+ " TEXT , " +
				tunadatamodel.TunaInvite.Cols.INVTIME	+ " TEXT , " +
				tunadatamodel.TunaInvite.Cols.INVSTATUS	+ " TEXT , " +
				"UNIQUE (" + 
					tunadatamodel.TunaInvite.Cols.ID + 
				") ON CONFLICT REPLACE)"
			);
		db.execSQL("CREATE TABLE " + tunadatamodel.TunaMessage.NAME+ " ( " +
				tunadatamodel.TunaMessage.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				tunadatamodel.TunaMessage.Cols.MID 	+ " TEXT , " +
				tunadatamodel.TunaMessage.Cols.USERID 	+ " TEXT , " +
				tunadatamodel.TunaMessage.Cols.USERNAME 	+ " TEXT , " +
				tunadatamodel.TunaMessage.Cols.TYPE 	+ " TEXT , " +
				tunadatamodel.TunaMessage.Cols.CONTENT 	+ " TEXT , " +
				tunadatamodel.TunaMessage.Cols.RECEIVER 	+ " TEXT , " +
				tunadatamodel.TunaMessage.Cols.RECEIVEGRP 	+ " TEXT , " +
				tunadatamodel.TunaMessage.Cols.MDATE 	+ " TEXT , " +
				tunadatamodel.TunaMessage.Cols.MTIME 	+ " TEXT , " +
				"UNIQUE (" + 
					tunadatamodel.TunaMessage.Cols.ID + 
				") ON CONFLICT REPLACE)"
			);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		
	}
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		
	}
}
