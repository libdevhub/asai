package com.univ.haifa.asai.model;

import java.util.ArrayList;
import java.util.Locale;

public class Circumstances extends GenericField {
	 public String type;
	 public int order;
	 public String fieldCode;
	 public ArrayList<Entry> entries;
	 
	 public Circumstances() {
		super("556", new char[] {'a'}, new String[] {null}, ' ', ' ');
	 }
	 
	 public ArrayList<String> getSubFieldValue() {
		 ArrayList<String> values = new ArrayList<String>();
		 for (Entry e : entries) {
			 values.add(e.lines.get(0).textValue);
		 }
		 return values;
	 }
	 
	 public String getSubFieldLangValue() {
		 String langCode = entries.get(0).lines.get(0).langCode;
		 try {
			 Locale locale = new Locale(langCode);
			 langCode = locale.getISO3Language();
		 }catch (Exception e) {
			System.out.println("Could not convert iso3language");
		 }
		 return langCode;
		 //return entries.get(0).lines.get(0).langCode;
	 }

	@Override
	public ArrayList<String> getRelatedFieldValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getSubFieldValueAsRelated() {
		// TODO Auto-generated method stub
		return null;
	}

}
