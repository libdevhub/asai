package com.univ.haifa.asai.model;

import java.util.ArrayList;
import java.util.Locale;


public class Sogazikaf extends GenericField {
	 public String type;
	 public int order;
	 public String fieldCode;
	 public ArrayList<Entry> entries;
	 
	 public Sogazikaf() {
		super("690", new char[] {'c', 'd'}, new String[] {"|", "|"}, ' ', ' ');
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
