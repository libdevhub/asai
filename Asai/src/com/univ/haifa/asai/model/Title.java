package com.univ.haifa.asai.model;

import java.util.ArrayList;
import java.util.Locale;


public class Title extends GenericField { 
	 public String type;
	 public int order;
	 public String fieldCode;
	 public ArrayList<Entry> entries;
	 
	 public Title() {
		super("245",new char[] {'a'}, new String[] {null} , '0', '0');
	}
	 
//	 TAG = "245";
//	 public String SUB_FIELD_CODE = "a";
//	 public char IND1 = ' ';
//	 public char IND2 = ' ';
	 
	 public ArrayList<String> getSubFieldValue() {
		 StringBuilder str = new StringBuilder();
		 str.append("аст");
		 str.append("\"");
		 str.append("й");
		 ArrayList<String> values = new ArrayList<String>();
		 for (Entry e : entries) { 
			 values.add(str + " " + e.lines.get(0).textValue);
		 }
		 return values;
	}
	 
	@Override
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
