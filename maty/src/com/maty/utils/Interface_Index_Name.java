package com.maty.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.maty.snmpclient.SNMPClient;

//获取接口索引和名字的对应关系
public class Interface_Index_Name
{
	public static Map<String,String> getInterface_Index_Name(String ip,String community) throws Exception
	{
		SNMPClient client = new SNMPClient(ip, 161, community, 2, 1500, 2);
		String ifIndex = ".1.3.6.1.2.1.2.2.1.1";
		String ifDescr = ".1.3.6.1.2.1.2.2.1.2";
		String ifIndexTmp = ifIndex;
		String interface_index = null;
		String interface_name = null;
		int count = Integer.valueOf(Interface_Index_Name.getInterface_Number(ip, community));
		Map<String,String> map = new HashMap();
		for(int i = 0;i < count;i++)
		{
			interface_index = client.sendGetNext(ifIndexTmp);
			ifIndexTmp = ifIndex+"."+interface_index;
			interface_name = client.sendGet(ifDescr+"."+interface_index);
		//	System.out.println(interface_index+"======="+interface_name);
			map.put(interface_index, interface_name);
		}
		return map;
	}

	//获取接口数量
	public static String getInterface_Number(String ip,String community) throws Exception
	{
		SNMPClient client = new SNMPClient(ip, 161, community, 2, 1500, 2);
		String number = client.sendGet("1.3.6.1.2.1.2.1.0");
		return number;
	}
	
	public static void main(String[] args) throws Exception
	{
		Interface_Index_Name.getInterface_Index_Name("192.168.99.253", "public");
	}
}
