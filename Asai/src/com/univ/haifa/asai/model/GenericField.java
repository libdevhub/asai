package com.univ.haifa.asai.model;

import java.util.ArrayList;

public abstract class GenericField {
	final String TAG;
	final char[] SUB_FIELD_CODE;
	final String[] SUB_FIELD_VALUES;
	final char IND1;
	final char IND2;
	 
	public GenericField(String tag, char[] code, String[] codeValues, char ind1, char ind2) {
		TAG = tag;
		SUB_FIELD_CODE = code;
		SUB_FIELD_VALUES = codeValues;
		IND1 = ind1;
		IND2 = ind2;
	}
	public String getTAG() {
		return TAG;
	}
	public char[] getSUB_FIELD_CODE() {
		return SUB_FIELD_CODE;
	}
	public char getIND1() {
		return IND1;
	}
	public char getIND2() {
		return IND2;
	}
	public abstract ArrayList<String> getSubFieldValue();
	public abstract String getSubFieldLangValue();
	public abstract ArrayList<String> getRelatedFieldValue(); //Related only for complex fields
	public abstract ArrayList<String> getSubFieldValueAsRelated();//Related only for complex fields
	
}
