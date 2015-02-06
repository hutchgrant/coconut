package com.hutchgrant.app;

import java.util.ArrayList;

import com.hutchgrant.Elements.Invite;
import com.hutchgrant.Elements.LifeInvite;
import com.hutchgrant.Elements.Sync.SyncObj;
import com.hutchgrant.provider.tunadatamodel;
import com.hutchgrant.tasks.GroupTask;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class InviteAccess {

	public static InviteAccess instance;
	static Context ctx;
	ContentResolver cr;
	Cursor cur;
	
	public InviteAccess(Context context){
		
		ctx = context;
		cr = ctx.getContentResolver();
	}
	public static void initAccess(Context context){
		if(instance == null){
			instance = new InviteAccess(context);
		}
	}
	
	public static InviteAccess getInstance(){
		return instance;
	}
	
	/*
	 * save Invite
	 */
	public void saveInvite(Invite inv, boolean update){
		cr = ctx.getContentResolver();
		ContentValues val = new ContentValues();
		if(update){
			val.put(tunadatamodel.SQL_INS_REP, true);  
		}
		val.put(tunadatamodel.TunaInvite.Cols.INVTOKEN, inv.InviteID);
		val.put(tunadatamodel.TunaInvite.Cols.INVSENDID, inv.InviteUserGID);
		val.put(tunadatamodel.TunaInvite.Cols.INVSENDNAME, inv.InviteUserName);
		val.put(tunadatamodel.TunaInvite.Cols.INVRECNAME, inv.RecipientName);
		val.put(tunadatamodel.TunaInvite.Cols.INVRECID, inv.RecipientUserGID);
		val.put(tunadatamodel.TunaInvite.Cols.INVRECEMAIL, inv.RecipientEmail);
		val.put(tunadatamodel.TunaInvite.Cols.INVRECPHONE, inv.RecipientPhone);
		val.put(tunadatamodel.TunaInvite.Cols.INVDATE, inv.InviteDate);
		val.put(tunadatamodel.TunaInvite.Cols.INVTIME,  inv.InviteTime);
		val.put(tunadatamodel.TunaInvite.Cols.INVSTATUS, inv.InviteStatus);
		cr.insert(tunadatamodel.TunaInvite.CONTENT_URI, val);	
	}
	
	/*
	 *  save all invites
	 */
	public void saveAllInvite(ArrayList<Invite> life, boolean update){
		boolean pendingFriend = false;
		LifeInvite lifeInv = new LifeInvite();
		lifeInv = getInvites(true, "all","");
		
		Invite inv = new Invite();
		Invite localInv = new Invite();

		for(int i=0; i<life.size(); i++){
			inv = new Invite();
			localInv = new Invite();
			inv = life.get(i);
			localInv = lifeInv.getInviteByID(inv.InviteID);
		
			if(!localInv.InviteID.equals("")){        /// invite exists locally
				if(inv.InviteStatus.equals("confirmed")){
					 removeInvite(inv); 
					 pendingFriend = true;
				}else{
					saveInvite(inv, update);
				}
				
				if(inv.InviteDate.equals("removed") && inv.InviteStatus.equals("removed")){
					 removeInvite(inv);
				}
			}else{      /// invite doesn't exist create it
				saveInvite(inv, false);
			}
			
		}
		
		if(pendingFriend){
			SyncObj localSync = new SyncObj();
			SyncAllAccess.initAccess(ctx);
			localSync = SyncAllAccess.getInstance().getLocalTally();
			GroupTask gt = new GroupTask(ctx);
			gt.setSyncType(0);
			gt.execute(localSync);
		}
	}
	 
	private void removeInvite(Invite inv) {
		cr = ctx.getContentResolver();
		cr.delete(tunadatamodel.TunaInvite.CONTENT_URI, tunadatamodel.TunaInvite.Cols.INVTOKEN+"=?", new String [] { inv.InviteID } );
	}
	/*
	 * get Invites
	 */
	public LifeInvite getInvites(boolean clean, String status, String profileID){
		cr = ctx.getContentResolver();
		LifeInvite inv = new LifeInvite();
		cur = cr.query(tunadatamodel.TunaInvite.CONTENT_URI, null, null, null,
			    null);
		
		int invID_index = 0,invToken_Index = 0, invSendID_Index = 0, invSendName_Index = 0, invRecID_Index = 0, invRecEmail_Index = 0, 
				invRecPhone_Index = 0, invDate_Index = 0, invTime_Index = 0, invStatus_Index = 0, invRecName_Index = 0;
		String invID = "", invToken = "", invSenderID = "", invRecName = "", invRecID = "", invRecEmail, invRecPhone = "", invDate = "", invTime = "", invStatus = "";
		int counter = 0;
		String invSenderName = "";
		
		System.out.println("DATABASE READ INVITE PRofile="+profileID);
		if(cur.moveToFirst()){
				do{
					invToken_Index = cur.getColumnIndex(tunadatamodel.TunaInvite.Cols.INVTOKEN);
				    invSendID_Index = cur.getColumnIndex(tunadatamodel.TunaInvite.Cols.INVSENDID);
				    invSendName_Index = cur.getColumnIndex(tunadatamodel.TunaInvite.Cols.INVSENDNAME);
				    invRecName_Index = cur.getColumnIndex(tunadatamodel.TunaInvite.Cols.INVRECNAME);
				    invRecID_Index = cur.getColumnIndex(tunadatamodel.TunaInvite.Cols.INVRECID);
				    invRecEmail_Index = cur.getColumnIndex(tunadatamodel.TunaInvite.Cols.INVRECEMAIL);
				    invRecPhone_Index = cur.getColumnIndex(tunadatamodel.TunaInvite.Cols.INVRECPHONE);
				    invDate_Index = cur.getColumnIndex(tunadatamodel.TunaInvite.Cols.INVDATE);
				    invTime_Index = cur.getColumnIndex(tunadatamodel.TunaInvite.Cols.INVTIME);
					invStatus_Index = cur.getColumnIndex(tunadatamodel.TunaInvite.Cols.INVSTATUS);
				    
						invToken = cur.getString(invToken_Index);
						invSenderID = cur.getString(invSendID_Index);
						invSenderName = cur.getString(invSendName_Index);
						invRecName = cur.getString(invRecName_Index);
						invRecID = cur.getString(invRecID_Index);
						invRecEmail = cur.getString(invRecEmail_Index);
						invRecPhone = cur.getString(invRecPhone_Index);
						invDate = cur.getString(invDate_Index);
						invTime = cur.getString(invTime_Index);
						invStatus = cur.getString(invStatus_Index);
						
						/// reminder to check for status "sent/received" so this method returns both types of invites
						
						if(clean){
							if(status.equals("sent")){
								if(invSenderID.equals(profileID)){    /// client sent this message
									if(invStatus.equals("na") && !invDate.equals("removed")){
										inv.fill(invToken, invSenderID, invSenderName, invRecName, invRecID, invRecEmail, invRecPhone, invDate, invTime, invStatus);
									}
								}
							}else if(status.equals("received")){
								if(invRecID.equals(profileID)){   /// client received this message
									if(invStatus.equals("na")  && !invDate.equals("removed")){
										inv.fill(invToken, invSenderID, invSenderName, invRecName, invRecID, invRecEmail, invRecPhone, invDate, invTime, invStatus);
									}
								}
							}else{  /// get all client messages (clean)
								if(invStatus.equals("na") && !invDate.equals("removed")){
									inv.fill(invToken, invSenderID, invSenderName, invRecName, invRecID, invRecEmail, invRecPhone, invDate, invTime, invStatus);
								}
							}
						}else{  ///  Retreive all pending invities to be sent. (Dirty)
							if(status.equals("all")){
								inv.fill(invToken, invSenderID, invSenderName, invRecName, invRecID, invRecEmail, invRecPhone, invDate, invTime, invStatus);
							}else{
								if((invStatus.equals("na") && invDate.equals("0")) || (invStatus.equals("confirmed"))){
									inv.fill(invToken, invSenderID, invSenderName, invRecName, invRecID, invRecEmail, invRecPhone, invDate, invTime, invStatus);
								}
							}
						}
						counter++;
						
			}while(cur.moveToNext());
		}
		cur.close();
		return inv;		
	}
}
