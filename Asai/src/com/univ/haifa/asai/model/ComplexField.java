package com.univ.haifa.asai.model;

import java.util.ArrayList;
import java.util.HashMap;

public class ComplexField {
	public final static int TYPE_FIELD = 0;
	public final static int VALUE_FIELD = 1;
	
	 public String type;
	 public int order;
	 public String fieldCode;
	 public ArrayList<Entry> entries;
	 public int dataTypeField;
	 
	 public ComplexField(int dataTypeField) {
		 this.dataTypeField = dataTypeField;
	 }
	 
	 public HashMap<String, ComplexFieldData> getCodeAndTextValues() {
		 HashMap<String, ComplexFieldData> values = new HashMap<String, ComplexFieldData>();
		 for (Entry e : entries) {
			 values.put(e.code, new ComplexFieldData(e.lines.get(0).textValue, dataTypeField));
		 }
		 return values;
	 }
	 
	 public int getFieldType() {
		return dataTypeField;
	 }
	 
	 public class ComplexFieldData {
		 String value;
		 int type;
		 
		 public ComplexFieldData(String value, int type) {
			this.value = value;
			this.type = type;
		}
	 }

}
