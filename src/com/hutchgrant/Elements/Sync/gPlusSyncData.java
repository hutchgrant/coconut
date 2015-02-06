package com.hutchgrant.Elements.Sync;

import java.util.ArrayList;

import android.content.Context;

import com.hutchgrant.Elements.Contact;
import com.hutchgrant.Elements.Group;

public class gPlusSyncData {

	public ArrayList<GContact> items = new ArrayList<GContact>();

	public Group convertToGroup(Context ctx) {
		Group googCirc = new Group(ctx);
		Contact gUser = new Contact();
		for(int i=0; i< items.size(); i++){
			gUser = new Contact();
			gUser.setContact(items.get(i).id, "", items.get(i).id,  items.get(i).displayName, "n/a", "n/a", items.get(i).image.url,false);
			googCirc.addContact(gUser);
		}
		
		return googCirc;
	}
}
