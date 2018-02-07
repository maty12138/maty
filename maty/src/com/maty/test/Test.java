package com.maty.test;

import java.util.Map;

import com.maty.utils.ARP;

public class Test
{
	public static void main(String[] args) throws Exception
	{
		Map<String, String> map = ARP.getARP("192.168.99.253", "public");
		for(String key : map.keySet())
			System.out.println(key+"<--->"+map.get(key));
	}
}
