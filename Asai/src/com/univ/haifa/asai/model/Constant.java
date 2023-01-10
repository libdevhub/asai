package com.univ.haifa.asai.model;

import java.util.ArrayList;

public class Constant extends GenericField{
	 
	 public Constant(String tag, char[] codes, String[] valus, char ind1, char ind2) {
		super(tag, codes, valus, ind1, ind2);
	 }

	@Override
	public ArrayList<String> getSubFieldValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSubFieldLangValue() {
		// TODO Auto-generated method stub
		return null;
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
