package com.hutchgrant.Elements.Sync;

import java.util.ArrayList;


public class SocialGroup {

    public String grpID = "";

    public String grpName = "";

    public int grpSize = 0;

    public String grpSyncDate = "";
    
    public String grpSyncTime = "";
    
    public ArrayList<SocialContact> People;
    
    
    public SocialGroup(){
    	People = new ArrayList<SocialContact>();
    }
    
    public void fill(String string, String name, int size, String syncd, String syncT, ArrayList<SocialContact> ppl){
    	this.grpID = string;
    	this.grpName = name;
    	this.grpSize = size;
    	this.grpSyncDate = syncd;
    	this.grpSyncTime = syncT;
    	this.People = ppl;
    	
    }
}
