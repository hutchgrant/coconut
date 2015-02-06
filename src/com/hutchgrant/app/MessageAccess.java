package com.hutchgrant.app;

import java.util.ArrayList;

import com.hutchgrant.Elements.Invite;
import com.hutchgrant.Elements.LifeInvite;
import com.hutchgrant.Elements.LifeMessage;
import com.hutchgrant.Elements.Message;
import com.hutchgrant.Elements.Sync.SyncObj;
import com.hutchgrant.provider.tunadatamodel;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class MessageAccess {

	public static MessageAccess instance;
	static Context ctx;
	ContentResolver cr;
	Cursor cur;
	public MessageAccess(Context context){
		
		ctx = context;
		cr = ctx.getContentResolver();
	}
	public static void initAccess(Context context){
		if(instance == null){
			instance = new MessageAccess(context);
		}
	}
	
	public static MessageAccess getInstance(){
		return instance;
	}
	
	/*
	 * save Invite
	 */
	public void saveMessage(Message msg, boolean update){
		cr = ctx.getContentResolver();
		ContentValues val = new ContentValues();
		if(update){
			val.put(tunadatamodel.SQL_INS_REP, true);  
		}
		val.put(tunadatamodel.TunaMessage.Cols.MID, msg.MID);
		val.put(tunadatamodel.TunaMessage.Cols.USERID, msg.AuthorID);
		val.put(tunadatamodel.TunaMessage.Cols.USERNAME, msg.AuthorName);
		val.put(tunadatamodel.TunaMessage.Cols.TYPE, msg.Type);
		val.put(tunadatamodel.TunaMessage.Cols.CONTENT, msg.Content);
		val.put(tunadatamodel.TunaMessage.Cols.RECEIVER, msg.ReceiverID);
		val.put(tunadatamodel.TunaMessage.Cols.RECEIVEGRP, msg.ReceiverGrpID);
		val.put(tunadatamodel.TunaMessage.Cols.MDATE, msg.mDate);
		val.put(tunadatamodel.TunaMessage.Cols.MTIME, msg.mTime);
		cr.insert(tunadatamodel.TunaMessage.CONTENT_URI, val);	
		System.out.println("message written to"+msg.ReceiverID);
	}
	 
	private void removeMessage(Message msg) {
		cr = ctx.getContentResolver();
		cr.delete(tunadatamodel.TunaInvite.CONTENT_URI, tunadatamodel.TunaInvite.Cols.ID +"=?", new String [] { msg.MID } );
	}
	/*
	 * get Invites
	 */
	public LifeMessage getMessages(boolean clean, String profileID, boolean contactSelected){
		cr = ctx.getContentResolver();
		LifeMessage life = new LifeMessage();
		Message msg = new Message();
		cur = cr.query(tunadatamodel.TunaMessage.CONTENT_URI, null, null,null, tunadatamodel.TunaMessage.Cols.ID+" asc " + " LIMIT 100");
		
		
		String id = "", userID = "", userName = "", type = "", content = "", receiveID = "", receiveGrpID = "", date = "", time = "";
		
		int id_index = 0, userID_index =0, name_index =0, type_index = 0, content_index = 0, receive_index = 0, receiveGrp_index = 0, date_index = 0, time_index =0;
		
		System.out.println("DATABASE READ Message for Profile = "+profileID);
		if(cur.moveToFirst()){
				do{
					id_index = cur.getColumnIndex(tunadatamodel.TunaMessage.Cols.MID);
				    userID_index = cur.getColumnIndex(tunadatamodel.TunaMessage.Cols.USERID);
				    name_index = cur.getColumnIndex(tunadatamodel.TunaMessage.Cols.USERNAME);
					type_index = cur.getColumnIndex(tunadatamodel.TunaMessage.Cols.TYPE);
					content_index = cur.getColumnIndex(tunadatamodel.TunaMessage.Cols.CONTENT);
					receive_index = cur.getColumnIndex(tunadatamodel.TunaMessage.Cols.RECEIVER);
					receiveGrp_index = cur.getColumnIndex(tunadatamodel.TunaMessage.Cols.RECEIVEGRP);
					date_index = cur.getColumnIndex(tunadatamodel.TunaMessage.Cols.MDATE);
					time_index = cur.getColumnIndex(tunadatamodel.TunaMessage.Cols.MTIME);
				    
				    
						id = cur.getString(id_index);
						userID = cur.getString(userID_index);
						userName = cur.getString(name_index);
						type = cur.getString(type_index);
						content = cur.getString(content_index);
						receiveID = cur.getString(receive_index);
						receiveGrpID = cur.getString(receiveGrp_index);
						date = cur.getString(date_index);
						time = cur.getString(time_index);
						
						if(clean){
							if(contactSelected && receiveID.equals(profileID)){
								msg = new Message();
								msg.fill(id, userID, userName, type, content, receiveID, receiveGrpID, date, time);
								life.addMsg(msg);
							}
							if(contactSelected && userID.equals(profileID)){
								msg = new Message();
								msg.fill(id, userID, userName, type, content, receiveID, receiveGrpID, date, time);
								life.addMsg(msg);
							}
							if(!contactSelected && profileID.equals("")){  // get all msgs
								msg = new Message();
								msg.fill(id, userID, userName, type, content, receiveID, receiveGrpID, date, time);
								life.addMsg(msg);
							}
							/*	if(!contactSelected && receiveGrpID.equals(profileID)){
							msg = new Message();
							msg.fill(id, userID, userName, type, content, receiveID, receiveGrpID, date, time);
							life.addMsg(msg);
						} */
						}else{ 
							if(date.equals("na")){
								msg = new Message();
								msg.fill(id, userID, userName, type, content, receiveID, receiveGrpID, date, time);
								life.addMsg(msg);
							}
						}
			}while(cur.moveToNext());
		}
		cur.close();
		return life;		
	}
	public void saveLifeMessage(LifeMessage life) {
		
		LifeMessage old = new LifeMessage();
		old = getMessages(true, "", false);
		
		
		for(int i =0; i< life.messages.size(); i++){
			
			
			if(!old.getMsgByID(life.messages.get(i).MID).MID.equals("")){
				System.out.println("Message SAved:"+life.messages.get(i).Content);
				/// Message exists
				saveMessage(life.messages.get(i), true);
			}else{
				/// message doesn't exist
				saveMessage(life.messages.get(i), false);
			}
			
		}
	}
	public int getCache() {
		// TODO Auto-generated method stub
		return 0;
	}
}
