package com.univ.haifa.asai.model;

import java.util.ArrayList;
import java.util.Locale;


public class Compplant extends GenericField { 
	 public String type;
	 public int order;
	 public String fieldCode;
	 public ArrayList<Entry> entries;
	 
	 public Compplant() {
		//super("690", new char[] {'m', 'n'}, new String[] {"|", "|"}, ' ', ' ');
		super("690", new char[] {'m'}, new String[] {null}, ' ', ' ');
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

}
