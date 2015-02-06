package com.hutchgrant.app;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;

import com.hutchgrant.Elements.Contact;
import com.hutchgrant.Elements.Group;
import com.hutchgrant.Elements.LifeGroup;
import com.hutchgrant.coconut.Endpoints;
import com.hutchgrant.provider.tunadatamodel;

public class GroupAccess {

	public static GroupAccess instance;
	static Context ctx;
	ContentResolver cr;
	Cursor cur;
	public int LAST_GROUPID = 0;

	public GroupAccess(Context context){
		ctx = context;
		cr = ctx.getContentResolver();
	}

	public static void initAccess(Context context){
		if(instance == null){
			instance = new GroupAccess(context);
		}
	}

	public static GroupAccess getInstance(){
		return instance;
	}

	//// GROUP QUERIES======
	/*
	 *   Get group name and details
	 *   boolean ID - search by id
	 *   boolean ID false - search by name
	 */
	public Group getGroup(String selGroupID, boolean searchID) {
		Group group = new Group(ctx);
		System.out.println(" Reading a group id : "+selGroupID);
		cr = ctx.getContentResolver();
		cur = cr.query(tunadatamodel.TunaGroup.CONTENT_URI, null, null, null, null);

		int GrpID_Index = 0, GrpNAME_Index = 0, GrpSIZE_Index = 0, GrpSYNCD_Index = 0, GrpSYNCT_Index = 0;

		GrpID_Index = cur
				.getColumnIndex(tunadatamodel.TunaGroup.Cols.GRPID);
		GrpNAME_Index = cur
				.getColumnIndex(tunadatamodel.TunaGroup.Cols.GRPNAME);
		GrpSIZE_Index = cur
				.getColumnIndex(tunadatamodel.TunaGroup.Cols.GRPSIZE);
		GrpSYNCD_Index = cur
				.getColumnIndex(tunadatamodel.TunaGroup.Cols.GRPSYNCDATE);
		GrpSYNCT_Index = cur
				.getColumnIndex(tunadatamodel.TunaGroup.Cols.GRPSYNCTIME);


		
		if(cur.moveToFirst()){
			do{

				System.out.println("group name example "+cur.getString(GrpNAME_Index));
				if(cur.getString(GrpID_Index).equals(selGroupID)){
					group.set(cur.getString(GrpID_Index), cur.getString(GrpNAME_Index), cur.getString(GrpSYNCD_Index), cur.getString(GrpSYNCT_Index));
					group.display();
				}
				if(cur.getString(GrpNAME_Index).equals(selGroupID)){
					group.set(cur.getString(GrpID_Index), cur.getString(GrpNAME_Index), cur.getString(GrpSYNCD_Index), cur.getString(GrpSYNCT_Index));
					group.display();
				}
			}while(cur.moveToNext());
		}
		cur.close();
		return group;
	}

	/*
	 *   Get rest of group contacts filled, then return group
	 */
	public Group getGroupContacts(String grpID) {
		cr = ctx.getContentResolver();
		cur = cr.query(tunadatamodel.TunaContact.CONTENT_URI, null, tunadatamodel.TunaContact.Cols.CNTGRPID+" = ?", 
				new String[]{grpID},
				null);

		int cntID_Index = 0, cntGRPID_Index = 0, cntNAME_Index = 0,cntPHONE_Index = 0, cntEMAIL_Index = 0, cntGoogID_index= 0;
		int cntImg_Index = 0;
		cntID_Index = cur
				.getColumnIndex(tunadatamodel.TunaContact.Cols.CNTID);
		cntGRPID_Index = cur
				.getColumnIndex(tunadatamodel.TunaContact.Cols.CNTGRPID);
		cntNAME_Index = cur
				.getColumnIndex(tunadatamodel.TunaContact.Cols.CNTNAME);
		cntPHONE_Index = cur
				.getColumnIndex(tunadatamodel.TunaContact.Cols.CNTPHONE);
		cntEMAIL_Index = cur
				.getColumnIndex(tunadatamodel.TunaContact.Cols.CNTEMAIL);
		cntGoogID_index = cur.getColumnIndex(tunadatamodel.TunaContact.Cols.CNTGOOGID);
		cntImg_Index = cur.getColumnIndex(tunadatamodel.TunaContact.Cols.CNTIMG);
		
		Group group = new Group(ctx);
		if(cur.moveToFirst()){
			do{
				if(cur.getString(cntGRPID_Index).equals(grpID)){ 
					Contact contact = new Contact();
					contact.setContact(cur.getString(cntID_Index),cur.getString(cntGRPID_Index), cur.getString(cntGoogID_index), cur.getString(cntNAME_Index), cur.getString(cntEMAIL_Index), cur.getString(cntPHONE_Index), cur.getString(cntImg_Index), true);
					group.addContact(contact);
				}
			}while(cur.moveToNext());
		}
		cur.close();
		return group;
	}

	//// CONTACT QUERIES

	/*
	 * Get all Contact information(ID, name, phone, email) for those who have
	 * phone numbers
	 */
	public ArrayList<Contact> getContactData() {
		ArrayList<Contact> People = new ArrayList<Contact>();
		cr = ctx.getContentResolver();
		cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
				null);
		int nameIndex = 0, id_Index, hasPhone_index = 0;
		String email = null, phone = null;

		nameIndex = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
		id_Index = cur.getColumnIndex(BaseColumns._ID);

		hasPhone_index = cur
				.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);

		if (cur.moveToFirst()) {
			do {
				String id = null, name = null;
				int columnCount = cur.getColumnCount();
				boolean CorrectContact = false;
				for (int i = 0; i < columnCount; i++) {
					if (i == id_Index) {
						id = cur.getString(i);
					} else if (i == nameIndex) {
						name = cur.getString(i);
					} else if (i == hasPhone_index) {
						if (cur.getInt(i) == 1) {
							CorrectContact = true;
						}
					}
				}
				if (CorrectContact == true) {
					People.add(new Contact(id, "", "", name, email, phone, "na", false));
				}
			} while (cur.moveToNext());
			cur.close();
			for (int i = 0; i < People.size(); i++) {
				phone = null;
				email = null;
				email = getSelectedEmailData(String.valueOf(People.get(i)
						.getID()));
				phone = getSelectedPhoneData(String.valueOf(People.get(i)
						.getID()));
				People.get(i).setPhone(phone);
				People.get(i).setEmail(email);
			}
		}
		return People;
	}

	/*
	 * Get the email for a Contact ID
	 */
	private String getSelectedEmailData(String id) {
		String emailAdd = "";
		int emailIndex = 0;
		cr = ctx.getContentResolver();
		cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
				null, ContactsContract.CommonDataKinds.Email.CONTACT_ID
				+ " = ?", new String[] { id }, null);

		emailIndex = cur
				.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);

		if (cur.moveToFirst()) {
			emailAdd = cur.getString(emailIndex);
		}
		cur.close();
		return emailAdd;
	}

	/*
	 * Get the Phone number for a Contact ID
	 */
	private String getSelectedPhoneData(String id) {
		String phoneNum = "";
		int phoneIndex = 0;
		cr = ctx.getContentResolver();
		cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
				+ " = ?", new String[] { id }, null);

		phoneIndex = cur
				.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

		if (cur.moveToFirst()) {
			phoneNum = cur.getString(phoneIndex);
		}
		cur.close();

		return phoneNum;
	}

	/*
	 *  query databse for group data
	 */
	public ArrayList<Group> getGroupData(){
		ArrayList<Group> groups = new ArrayList<Group>();
		int GrpID_Index = 0, GrpNAME_Index = 0, GrpSIZE_Index = 0, GrpSYNCD_Index = 0, GrpSYNCT_Index = 0;

		cr = ctx.getContentResolver();
		cur = cr.query(tunadatamodel.TunaGroup.CONTENT_URI, null, null, null,
				null);

		GrpID_Index = cur
				.getColumnIndex(tunadatamodel.TunaGroup.Cols.GRPID);
		GrpNAME_Index = cur
				.getColumnIndex(tunadatamodel.TunaGroup.Cols.GRPNAME);
		GrpSIZE_Index = cur
				.getColumnIndex(tunadatamodel.TunaGroup.Cols.GRPSIZE);
		GrpSYNCD_Index = cur
				.getColumnIndex(tunadatamodel.TunaGroup.Cols.GRPSYNCDATE);
		GrpSYNCT_Index = cur
				.getColumnIndex(tunadatamodel.TunaGroup.Cols.GRPSYNCTIME);
		if (cur.moveToFirst()) {
			do{
				Group group = new Group(ctx);
				group.set(cur.getString(GrpID_Index), cur.getString(GrpNAME_Index), 
						cur.getString(GrpSYNCD_Index), cur.getString(GrpSYNCT_Index));
				group.setSize(cur.getInt(GrpSIZE_Index));
				groups.add(group);
				if(cur.isLast()){
					LAST_GROUPID = cur.getInt(GrpID_Index);
				}
			}while(cur.moveToNext());
		}

		cur.close();
		return groups;
	}
	/*
	 * write contacts within a group to database
	 */
	public void saveContact(Contact contact, boolean update){
		ContentValues val = new ContentValues();
		if(update){  // changes our insert to update
			val.put(tunadatamodel.SQL_INS_REP, true);  
		}
		val.put(tunadatamodel.TunaContact.Cols.CNTID, (contact.getID() != null) ? contact.getID() : null);
		val.put(tunadatamodel.TunaContact.Cols.CNTGRPID, (contact.getGroupID() != null) ? contact.getGroupID() : null);  // using grp ID above
		val.put(tunadatamodel.TunaContact.Cols.CNTGOOGID, (contact.getGoogleID() != null) ? contact.getGoogleID() : null);
		val.put(tunadatamodel.TunaContact.Cols.CNTNAME, (contact.getName() != null) ? contact.getName() : null);
		val.put(tunadatamodel.TunaContact.Cols.CNTPHONE, (contact.getPhone() != null) ? contact.getPhone() : null);
		val.put(tunadatamodel.TunaContact.Cols.CNTEMAIL, (contact.getEmail() != null) ? contact.getEmail() : null);
		val.put(tunadatamodel.TunaContact.Cols.CNTIMG, (contact.getImg() != null) ? contact.getImg() : null);
		cr.insert(tunadatamodel.TunaContact.CONTENT_URI, val);
	}
	
	public void saveContactCache(String contactID, int cache){
		ContentValues val = new ContentValues();
			val.put(tunadatamodel.SQL_INS_REP, true);  
		val.put(tunadatamodel.TunaContact.Cols.CNTID, contactID);
	///	val.put(tunadatamodel.TunaContact.Cols.CNTMSGCACHE, tunadatamodel.TunaContact.Cols.ID+cache);
		cr.insert(tunadatamodel.TunaContact.CONTENT_URI, val);
	}
	
	/*
	 * write group to database
	 */
	public void saveGroup(Group cntGroup, boolean update){
		ContentValues value = new ContentValues();
		if(update){  // changes our insert to update
			value.put(tunadatamodel.SQL_INS_REP, true);  
		}
		value.put(tunadatamodel.TunaGroup.Cols.GRPID, (cntGroup.getID() != null) ? cntGroup.getID() : null);
		value.put(tunadatamodel.TunaGroup.Cols.GRPNAME, (cntGroup.getName() != null) ? cntGroup.getName() : null);
		value.put(tunadatamodel.TunaGroup.Cols.GRPSIZE, (cntGroup.getSize() != 0) ? cntGroup.getSize() : 0);
		value.put(tunadatamodel.TunaGroup.Cols.GRPSYNCDATE, (cntGroup.getSyncDate() != null) ? cntGroup.getSyncDate() : null);
		value.put(tunadatamodel.TunaGroup.Cols.GRPSYNCTIME, (cntGroup.getSyncTime() != null) ? cntGroup.getSyncTime() : null);
		cr.insert(tunadatamodel.TunaGroup.CONTENT_URI, value);
		sendMessage(Endpoints.EVENT_GRPWRITE, true);
	}

	public void saveAllGroups(LifeGroup life){
		for(int i=0; i<=life.groups.size(); i++){
			saveGroup(life.groups.get(i), false);
		}
	}

	public void saveAllContacts(Group cntGroup, boolean update){
		/// save contacts in group
		for(int i =0; i< cntGroup.getSize(); i++){
			Contact contact = cntGroup.getContact(i);
			saveContact(contact, update);
		}
	}

	/**
	 *  GROUPS - Sync Control - Insert, Update, Delete
	 */
	public void saveToLife(LifeGroup life) {
		LifeGroup oldlife = new LifeGroup(ctx);

		oldlife.groups = getGroupData();
		for(int i =0; i<oldlife.groups.size(); i++){
			oldlife.groups.get(i).ctx = ctx;
			Group grp = new Group(ctx);
			grp = getGroupContacts(oldlife.groups.get(i).getID());
			oldlife.groups.get(i).setPeople(grp.getAll());
		}

		Group group = new Group(ctx);

		for(int i=0; i< life.groups.size(); i++){
			group = oldlife.getSpecifcGroup(life.groups.get(i).getID());
			if( group != null){ 	/// we have the group ID in the database already
				if(group.getSize() != life.groups.get(i).getSize()){  /// check whether this group's sync date is already the same as the server 
					/// check and update each contact if it exists
					for(int m=0; m<life.groups.get(i).getAll().size(); m++){
						/// check whether we have the contacts already
						System.out.println("CHECKING IF GROUP CONTAINS ID: "+life.groups.get(i).getAll().get(m).getID());
						if(group.ContainsContactID(life.groups.get(i).getAll().get(m).getID()) == true){
							// update contact
							System.out.println("HAD THIS BEEN WORKING THIS WOULD HAVE UPDATED :="+life.groups.get(i).getAll().get(m).getName());
							saveContact(life.groups.get(i).getAll().get(m), true);
						}else{
							/// insert contact
							System.out.println("HAD THIS BEEN WORKING THIS WOULD HAVE INSERTED :="+life.groups.get(i).getAll().get(m).getName());	
							saveContact(life.groups.get(i).getAll().get(m), false);
						}
					}
					/// update group
					System.out.println("HAD THIS BEEN WORKING THIS WOULD HAVE UPDATED :="+life.groups.get(i).getName() );
					saveGroup(life.groups.get(i), true);
				}else{  // do nothing group same on phone and server
					System.out.println("GROUP SYNCED PROPERLY :"+group.getName());
				}
			}else{
				// insert group
				// insert contacts
				System.out.println("GROUP WAS ADDED TO SERVER FROM OTHER DEVICE :"+life.groups.get(i).getName());
				saveGroup(life.groups.get(i), false);
				saveAllContacts(life.groups.get(i), false);
			}

		}
		// removeFromLife(oldlife, life);
	} 

	public void removeFromLife(LifeGroup oldlife){
		for(int i =0; i< oldlife.groups.size(); i++){				
		//	if(life.checkForID(oldlife.groups.get(i).getID()) != true){
				//// Group was removed from server by another device !
		//		System.out.println("GROUP WAS REMOVED FROM SERVER :"+oldlife.groups.get(i).getName());
				/// remove group from phone db
				if(oldlife.groups.get(i).getSyncDate().equals("removed") == true){
					removeLocalGroup(oldlife.groups.get(i).getID());
				}
				
				/// check for other contacts removed from other groups
				for(int x=0; x<oldlife.groups.get(i).getAll().size(); x++){
					/// remove "preremoved" contacts from updated groups in phone db
					if(oldlife.groups.get(i).getAll().get(x).getName().equals("removed") == true){
						removeLocalContact(oldlife.groups.get(i).getAll().get(x).getID());
					}
				}
		//	}
		}
	}	
	/*
	 * remove group - name, id, contacts
	 */
	public void removeLocalGroup(String string){
		ContentResolver cr;
		cr = ctx.getContentResolver();
		cr.delete(tunadatamodel.TunaGroup.CONTENT_URI, tunadatamodel.TunaGroup.Cols.GRPID +"=?", new String [] { string } );
		cr.delete(tunadatamodel.TunaContact.CONTENT_URI, tunadatamodel.TunaContact.Cols.CNTGRPID +"=?", new String [] { string } );
	}
	/*
	 * remove contact
	 */
	public void removeLocalContact(String contactID){
		ContentResolver cr;
		cr = ctx.getContentResolver();
		cr.delete(tunadatamodel.TunaContact.CONTENT_URI, tunadatamodel.TunaContact.Cols.CNTID +"=?", new String [] { contactID } );
	}

	public void writeAddedContacts(ArrayList<Contact> addPPL) {
		for(int i=0; i<addPPL.size(); i++){
			saveContact(addPPL.get(i), false);
		}
		updateGroupSize(addPPL.get(0).getGroupID(), addPPL.size());
	}

	public void updateGroupSize(String string, int groupSize){
		ContentValues value = new ContentValues();
		value.put(tunadatamodel.SQL_INS_REP, true);  
		value.put(tunadatamodel.TunaGroup.Cols.GRPID, (string != null) ? string : null);
		value.put(tunadatamodel.TunaGroup.Cols.GRPSIZE, (groupSize >= 0) ? groupSize : 0);
		value.put(tunadatamodel.TunaGroup.Cols.GRPSYNCDATE, "updated");
		cr.insert(tunadatamodel.TunaGroup.CONTENT_URI, value);
	}

	/*
	 * 
	 * Broadcast messages
	 */
	 private void sendMessage(String event, boolean msg) {
			  Intent intent = new Intent();
			  intent =  Endpoints.checkSuccess(event, msg);
			LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
	 }
	 
	
		
}
