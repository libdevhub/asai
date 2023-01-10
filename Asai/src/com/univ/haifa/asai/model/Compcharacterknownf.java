package com.univ.haifa.asai.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;


public class Compcharacterknownf extends GenericField {
	 public String type;
	 public int order;
	 public String fieldCode;
	 public ArrayList<Entry> entries;
	 
	 public Compcharacterknownf() {
		super("690", new char[] {'g', 'h'}, new String[] {"|", "|"}, ' ', ' ');
	 }
	 
	 public ArrayList<String> getSubFieldValue() {
		 ArrayList<String> values = new ArrayList<String>();
		 for (Entry e : entries) {
			 values.add(e.lines.get(0).textValue);
		 }
		 return values;
	 }
	 
	 public ArrayList<String> getSubFieldValueAsRelated() {
		 ArrayList<String> values = new ArrayList<String>();
		 for (Entry e : entries) {
			 String value = e.lines.get(0).textValue;
			 Set<String> relatedKeys = e.relatedEntries.keySet();
			 Iterator<String> itr = relatedKeys.iterator();
			 if (value.contains("|") && e.relatedEntries.keySet().size() == 2) { //The classic case when complex field has | between the type and value and the related entries have 2 elements of each field  
				 values.add(itr.next() + " | " + itr.next());
			 }
			 else if(!(value.contains("|")) && e.relatedEntries.keySet().size() == 1) {//This happen when type or value in the complex field is missing (usually happen when cataloging is wrong)
					 values.add(itr.next());
			 }
			 else {//Not suppose to happen in complex fields 
				 values.add(e.lines.get(0).textValue);
			 }
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
	 
	 public ArrayList<String> getRelatedFieldValue() {
		ArrayList<String> values = new ArrayList<String>();
		for (Entry e : entries) {
			values.addAll(e.relatedEntries.keySet());
		}
		return values;
	 }

}
