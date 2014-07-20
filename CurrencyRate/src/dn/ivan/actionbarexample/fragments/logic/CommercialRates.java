package dn.ivan.actionbarexample.fragments.logic;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CommercialRates implements Serializable{
	
	public String date = "";
	public String bankName = "";
	public String sourceUrl = "";
	public String codeNumeric = "";
	public String codeAlpha = "";
	public String rateBuy = "";
	public String rateBuyDelta = "";
	public String rateSale = "";
	public String rateSaleDelta = "";
	
	@Override
	public String toString() {
		return "CommercialRates [date=" + date + ", bankName=" + bankName
				+ ", sourceUrl=" + sourceUrl + ", codeNumeric=" + codeNumeric
				+ ", codeAlpha=" + codeAlpha + ", rateBuy=" + rateBuy
				+ ", rateBuyDelta=" + rateBuyDelta + ", rateSale=" + rateSale
				+ ", rateSaleDelta=" + rateSaleDelta + "]";
	}
}