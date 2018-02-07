package com.maty.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.maty.snmpclient.SNMPClient;
/*
 * 该程序用来发现交换机的arp信息
 */
public class ARP
{
	public  static Map<String,String> getARP(String ip,String community) throws Exception
	{
		Map<String,String> map = new HashMap<>();
		String ipNetToMediaIfIndex = ".1.3.6.1.2.1.4.22.1.1";
		String ipNetToMediaPhysAddress = ".1.3.6.1.2.1.4.22.1.2";
		String ipNetToMediaNetAddress = "1.3.6.1.2.1.4.22.1.3";
		SNMPClient client = new SNMPClient(ip,161,community ,2,1500, 2);
		//先计算出ARP表单号码
		String index = client.sendGetNext(ipNetToMediaIfIndex);
		//在计算出该ARP表表单中存在哪些IP
		String pre_ipNetToMediaNetAddress =  ipNetToMediaNetAddress + "." + index;
		String key_ip = null;
		while(true)
		{
			key_ip = client.sendGetNext(pre_ipNetToMediaNetAddress);
			if(!key_ip.contains("."))
				break;
			//System.out.println(key_ip);
			pre_ipNetToMediaNetAddress = ipNetToMediaNetAddress + "." + index;
			pre_ipNetToMediaNetAddress = pre_ipNetToMediaNetAddress +"."+key_ip;
			String value_mac = client.sendGet(ipNetToMediaPhysAddress+"."+index+"."+key_ip);
			//System.out.println(value_mac);
			//System.out.println("------------------------------------------");
			map.put(key_ip, value_mac);
		}
		return map;
	}
	
	public static void main(String[] args) throws Exception
	{
		ARP arp = new ARP();
		Map<String, String> map = arp.getARP("192.168.99.253", "public");  //园区核心：10.3.70.130 zjuwlan
		for(String key : map.keySet())
			System.out.println(key+"<--->"+map.get(key));
	}
}
