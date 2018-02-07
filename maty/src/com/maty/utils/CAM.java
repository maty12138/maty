package com.maty.utils;
import java.util.HashMap;
import java.util.Map;
import com.maty.snmpclient.SNMPClient;
/*
 * 此类用来获取交换机的CAM表单
 * 思路:1.获取交换机的dot1dTpFdbAddress上的所有mac地址
 * 		   2.然后将mac地址转化成十进制(本来是十六进制)，和1.3.6.1.4.1.2011.5.25.42.2.1.3.1.4拼接成oid，然后获取到该mac的转发接口的index
 * 		   3.通过index来寻找到Interface的名字
 */
public class CAM
{
	public static Map<String,String> getCAM(String ip,String community) throws Exception
	{
		SNMPClient client = new SNMPClient(ip, 161, community, 2, 1500, 2);
		Map<String, String> map = new HashMap<String, String>();  //这个map中的key为mac，value为该接口名称
		String interface_oid = "1.3.6.1.4.1.2011.5.25.42.2.1.3.1.4";  //为了后面拼接oid
		String interface_oid_tmp = interface_oid;
		String all_mac_oid = "1.3.6.1.2.1.17.4.3.1.1";  //获取所有的mac地址，该mac地址为CAM表中所记录的mac地址
		String all_mac_oid_tmp = all_mac_oid;
		Map<String, String> interface_Index_Name = Interface_Index_Name.getInterface_Index_Name(ip, community);  //获取接口索引和名字的对应关系
		String index = "";
		String tmp = null;
		String sum = "";
		while(true)
		{
			tmp = client.sendGetNext(all_mac_oid_tmp);
			if(!tmp.contains(":"))
				break;
			String[] split = tmp.split(":");
			for(int i = 0;i <split.length;i++)
			{
				sum = sum +"." +Hex2Dec.getDec(split[i]); 
			}
			index = client.sendGet(interface_oid+sum+".1.1.48");
			map.put(tmp,interface_Index_Name.get(index));
			System.out.println(interface_Index_Name.get(index)+"<--->"+tmp);
			all_mac_oid_tmp = all_mac_oid + sum;
			sum = "";
		}
		
		return map;
	}
	
	public static void main(String[] args) throws Exception
	{
		Map<String, String> map = CAM.getCAM("192.168.99.253","public");
		System.out.println("------------------------------------");
		for(String value : map.keySet())
			System.out.println(map.get(value));
	}
}
