package com.maty.utils;

import java.io.IOException;

import com.maty.snmpclient.SNMPClient;

public class Interface_Index
{
	public static StringBuffer getIndex(String ip,String community) throws Exception
	{
		String tmp = null;
		String index_oid = "1.3.6.1.2.1.2.2.1.1";
		String index_tmp = index_oid;
		SNMPClient client = new SNMPClient(ip, 161, community, 2, 1500, 2);
		int count = Integer.valueOf(Interface_Index_Name.getInterface_Number(ip, community));
		StringBuffer sb = new StringBuffer();
		for(int i = 0;i < count;i++)
		{
			tmp = client.sendGetNext(index_tmp);
			index_tmp = index_oid +"."+tmp;
			sb.append(tmp+",");
		}
		return sb;
	}
}
