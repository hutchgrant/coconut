package com.hutchgrant.Elements;

import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hutchgrant.app.TunaApp;
import com.hutchgrant.coconut.Communicate;
import com.hutchgrant.coconut.Endpoints;
import com.hutchgrant.coconut.R;

public class LifeGroup{
	
	public ArrayList<Group> groups;  // all groups

	Context ctx;
	public int groupSize = 0;
	Dialog dialog = null;
	int selectedEditIndex = 0;
	/*
	 * Constructor
	 */
	public LifeGroup(Context context){
		ctx = context;
		groups = new ArrayList<Group>();
	}
	
	/*
	 * Create default group from phone contact data
	 */
	public void init(){
		String name = null;
		Group cntGroup = new Group(ctx);		
		cntGroup.set(UUID.randomUUID().toString(), name, "n/a", "n/a/");
		cntGroup.getContactData();
		addGroup(cntGroup);
	}
	
	/*
	 * create organize entire group - name, id, contacts
	 */
	public void createGroup(ArrayList<Contact> ContactList){
		showDialog(Endpoints.DIALOG_GROUPNAME, ContactList, null, 0);
	}
	
	/* SET AND SAVE GROUP TO DB
	 * add group - name, id, contacts
	 */
	private void addGroup(String name, ArrayList<Contact> ContactList ){
		// create album ID
		String albumID = "";
		albumID = UUID.randomUUID().toString();
		
		/// create group from checked list, add to Array of groups
 		Group cntGroup = new Group(ctx);
		cntGroup = listSelectedGrp(ContactList, albumID);
		cntGroup.set(albumID, name, "n/a", "n/a/");
		addGroup(cntGroup);
		
		/// write to db
		TunaApp app = (TunaApp) ((Activity) ctx).getApplication();
		app.saveGroup(cntGroup, false);
		app.setPendingData(true, true);

	}
	/* SET AND ADD GROUP
	 * add group - Group object
	 */
	public void addGroup(Group cntGroup){
		groups.add(cntGroup);
		groupSize++;
	}


	/*
	 *  remove all groups that are selected
	 */
	public void setRemoveAllGroups(){
		TunaApp app = (TunaApp) ((Activity) ctx).getApplication();

		for(int i=0; i<groups.size(); i++){
			if(groups.get(i).isSelected()){
				groups.get(i).setSyncDate("removed");
				app.updateGroup(groups.get(i));
				groups.remove(i);
				groupSize--;
			}
		}
	}
	/*
	 * List grouped users, set the groupID of each selected contact, return completed group
	 */
	private Group listSelectedGrp(ArrayList<Contact> ContactList, String albumID) {
		
		Group cntGroup = new Group(ctx);
		
		for (int i = 0; i < ContactList.size(); i++) {
			Contact contact = ContactList.get(i);
			if (contact.isSelected()) {
				contact.setGroupID(albumID);
				cntGroup.addContact(contact);
			}
		}
		return cntGroup;
	}
	
	/*
	 * Show all dialog messages and menus
	 */
	void showDialog(int i,final ArrayList<Contact> ContactList, final Group grp, final int selectedIndex) {
		switch(i) {
		case Endpoints.DIALOG_GROUPNAME:
			dialog = new Dialog(ctx);
			dialog.setContentView(R.layout.group_name_dialog);
			dialog.setTitle("Welcome");
			
			TextView diagText = (TextView) dialog.findViewById(R.id.dialog_text);
			diagText.setText("Please enter a name for this group");
			dialog.setCanceledOnTouchOutside(true);
			final TunaApp app = (TunaApp) ((Activity) ctx).getApplication();

			final EditText grpName = (EditText) dialog.findViewById(R.id.input_name);
			if(grp != null){
				grpName.setText(grp.getName());
			}
	         Button buttonEdit = (Button)dialog.findViewById(R.id.btn_exit);
	         buttonEdit.setOnClickListener(new View.OnClickListener() {
	        	 String name = "null";
	             @Override
				public void onClick(View v) {
	            	 
	                 name = grpName.getText().toString();
	             	if(grp != null){
	                 grp.setName(name);
	                 grp.setSyncDate("updated");
	     				app.updateGroup(grp);
	     				System.out.println("Group "+name +" updated name");
	     				groups.set(selectedIndex, grp);
		                 dialog.dismiss();
	             	}else{
	                 addGroup(name, ContactList);
	                 // fill level data
	     			Intent i = new Intent();
	    			i.setClass(ctx, Communicate.class);
	    			i.putExtra("viewNum", 1);
	     			ctx.startActivity(i);
	                 dialog.dismiss();
	             	}
	             }
	         });
			dialog.show();
			break;
		}
	}

	/*  FILL AND SET GROUP DATA FROM DATABASE
	 *  read groups + contacts
	 */
	public void getGroupData(){
		TunaApp app = (TunaApp) ((Activity) ctx).getApplication();
		groups = new ArrayList<Group>();
		groups = app.readGroups();
		for(int i =0; i<groups.size(); i++){
			groups.get(i).ctx = ctx;
			groups.get(i).getDBContactData(groups.get(i).getID());
		}
	}
	
	
	
	
	/* GET AND RETURN ALL GROUPS
	 * update all groups then return them
	 */
	public ArrayList<Group> getAndRetreiveGroupData(){
		getGroupData();
		return this.groups;
	}
	
	/*
	 * check all groups to see if a specific group exists, return true
	 */
	public boolean checkForID(String string){
		for(int i=0; i<groups.size(); i++){
			if(groups.get(i).getID().equals(string)){
				return true;
			}
		}
		return false;
	}
	
	/*
	 * check all groups to see if a specific group exists, return group
	 */
	public Group getSpecifcGroup(String id){
		Group group = new Group(ctx);
		for(int i=0; i<groups.size(); i++){
			if(groups.get(i).getID().equals(id) == true){
				group = groups.get(i);
				return group;
			}
		}
		return null;
	}
	
	/*
	 * check all groups to see if a specific group exists, return group
	 */
	public Group getSpecifcGroupByName(String id){
		Group group = new Group(ctx);
		for(int i=0; i<groups.size(); i++){
			if(groups.get(i).getName().equals(id) == true){
				group = groups.get(i);
				return group;
			}
		}
		return null;
	}

	public void editGroupName() {
		for(int i=0; i<groups.size(); i++){
			if(groups.get(i).isSelected()){
				showDialog(Endpoints.DIALOG_GROUPNAME, null, groups.get(i), i);
			}
		}
	}

	/*
	 * get clean group list without temp removed groups
	 */
	public ArrayList<Group> getCleanGroupData() {
		ArrayList<Group> cleandata = new ArrayList<Group>();
		
		for(int i=0; i<groups.size(); i++){
			if(!groups.get(i).getSyncDate().equals("removed")){
				cleandata.add(groups.get(i));
			}
		}
		
			return cleandata;
	}

	public void addToGroup(ArrayList<Contact> contactList, String selGroupID) {
		ArrayList<Contact> addPPL = new ArrayList<Contact>();
		
		for(int i=0; i<contactList.size(); i++){
			if(contactList.get(i).isSelected()){
				contactList.get(i).setGroupID(selGroupID);
				addPPL.add(contactList.get(i));
			}
		}
		TunaApp app = (TunaApp) ((Activity) ctx).getApplication();
		app.writeAddedContacts(addPPL);
	}
}
