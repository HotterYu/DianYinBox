package com.znt.lib.bean;

public class StaticIpInfor 
{

	private String ip = "";
	private String gateway = "";
	private String dns1 = "";
	private String dns2 = "";
	
	public void setIp(String ip)
	{
		this.ip = ip;
	}
	
	public String getIp()
	{
		return ip;
	}
	
	public void setGateway(String gateway)
	{
		this.gateway = gateway;
	}
	
	public String getGateway()
	{
		return gateway;
	}
	
	public void setDns1(String dns1)
	{
		this.dns1 = dns1;
	}
	
	public String getDns1()
	{
		return dns1;
	}
	
	public void setDns2(String dns2)
	{
		this.dns2 = dns2;
	}
	
	public String getDns2()
	{
		return dns2;
	}
	
}
