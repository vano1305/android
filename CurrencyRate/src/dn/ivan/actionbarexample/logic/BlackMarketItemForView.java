package dn.ivan.actionbarexample.logic;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BlackMarketItemForView implements Serializable {
	
	public String date = "";
	public String cityCode = "";
	public String currencyCode = "";
	public String rate_buy = "";
	public String rate_buy_delta = "";
	public String rate_sale = "";
	public String rate_sale_delta = "";
}