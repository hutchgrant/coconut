package com.hutchgrant.Elements.Sync;

import java.util.ArrayList;

import com.hutchgrant.Elements.Group;

import com.hutchgrant.Elements.Contact;
public class SpecialGroup {

	ArrayList<SocialContact> ppl;
	SocialGroup social;
	
    public SpecialGroup(){
    	ppl = new ArrayList<SocialContact>();
    	social = new SocialGroup();
    }
    
    public SpecialGroup(Group group){
    	ppl = new ArrayList<SocialContact>();
    	social = new SocialGroup();
    	fillContacts(group.getAll());
    	fillGroup(group.getID(), group.getName(), group.getSize(), group.getSyncDate(), group.getSyncTime());
    }
    
    public void fillGroup(String string, String GRPNAME, int grpSize, String SYNCD, String SYNCT){
    	this.social.fill(string, GRPNAME, grpSize, SYNCD, SYNCT, ppl);
    }
    
    public void fillContacts(ArrayList<Contact> contacts){
    	
    	for(int i=0; i<contacts.size(); i++){
    		Contact ct = new Contact();
    		ct = contacts.get(i);    		
    		add(ct.getID(), ct.getGroupID(), ct.getGoogleID(), ct.getName(), ct.getEmail(), ct.getPhone(), ct.getProfileImg());
    	}
    	
    }
    
    public void add(String id, String groupid, String googleid, String NAME, String EMAIL, String PHONE, String IMAGE){
    	SocialContact contact = new SocialContact();
    	contact.fill(id, groupid, googleid, NAME, EMAIL, PHONE, IMAGE);
    	ppl.add(contact);
    }
    
    public SocialGroup getSocialGroup(){
    	return this.social;
    }
}
