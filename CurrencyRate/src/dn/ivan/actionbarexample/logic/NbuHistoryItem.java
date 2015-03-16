package dn.ivan.actionbarexample.logic;

import java.io.Serializable;

@SuppressWarnings("serial")
public class NbuHistoryItem implements Serializable {
	
	public String currencyCode = "";
	public String date1 = "";
	public String date2 = "";
	public String history = "";
	
	@Override
	public String toString() {
		return "NbuHistoryItem [currencyCode=" + currencyCode + ", date1="
				+ date1 + ", date2=" + date2 + ", history=" + history + "]";
	}
}