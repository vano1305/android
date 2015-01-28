package dn.ivan.actionbarexample.logic;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BlackMarketItem implements Serializable {
	
	public String date = "";
	public String opCode = "";
	public String cityCode = "";
	public String currencyCode = "";
	public String rate = "";
	public String rate_delta = "";
	
	@Override
	public String toString() {
		return "BlackMarketItem [date=" + date + ", opCode=" + opCode
				+ ", cityCode=" + cityCode + ", currencyCode=" + currencyCode
				+ ", rate=" + rate + ", rate_delta=" + rate_delta + "]";
	}
}