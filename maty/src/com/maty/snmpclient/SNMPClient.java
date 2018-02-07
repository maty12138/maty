package com.maty.snmpclient;

import java.io.IOException;
import java.util.ListIterator;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 * This Programe come from YouTuBe
 * @author maty
 *
 */

public class SNMPClient
{
	//SNMP类具备发送和接收SNMP PDU
	private Snmp msnmpClient = null;
	
	//transport mapping variable
	private TransportMapping mTransportMappings = null;
	
	//community target consist of all snmp information
	private CommunityTarget communityTarget = null;
	
	//init and set community target that save all snmp information
	public SNMPClient(String strHost,int iPort,String strCommunity,int iVersion,int iTimeout,int iRetry) throws IOException
	{
		//使用UDP协议来发送SNMP PDU
		this.mTransportMappings = new DefaultUdpTransportMapping();
		
		//开启端口侦听
		this.mTransportMappings.listen();
		
		//创建SNMP实类
		msnmpClient = new Snmp(this.mTransportMappings);
		
		//构建目标类
		communityTarget = new CommunityTarget();
		communityTarget.setAddress(new UdpAddress(strHost+"/"+iPort));
		communityTarget.setCommunity(new OctetString(strCommunity));
		communityTarget.setRetries(iRetry);
		communityTarget.setTimeout(iTimeout);
		communityTarget.setVersion(iVersion - 1);
	}
	
	//编写get方法的代码
	public String sendGet(String strOID) throws IOException
	{
		String strResult = null;
		//创建PDU实体类
		PDU pdu = new PDU();
		//将OID添加到PDU实体类中
		pdu.add(new VariableBinding(new OID(strOID)));
		//设置该PDU的类型
		pdu.setType(PDU.GET);
		//发送该PDU
		ResponseEvent response = msnmpClient.send(pdu, communityTarget);
		if(response != null)
		{
			PDU pduResult = response.getResponse();

			
			if(pduResult != null && pduResult.getErrorStatus() == PDU.noError)
			{
				//System.out.println("successfully");
				Variable variable = pduResult.get(0).getVariable();  //如果出现问题，请检查这里！！！！！！！
				if(variable != null)
				{
					strResult = variable.toString();
				}
			}else
			{
				//System.out.println("error");
				strResult = "-1";
			}
		}
		//清除该PDU的绑定数据和request_id
		pdu.clear(); 
		return strResult;
	}
	
	
	//编写getnext方法
	public  String sendGetNext(String strOID)
	{
		String strResult = null;
		PDU pdu = new PDU();
		try
		{
			pdu.add(new VariableBinding(new OID(strOID)));
			pdu.setType(PDU.GETNEXT);
			ResponseEvent response = this.msnmpClient.send(pdu, this.communityTarget);
			if(response != null)
			{
				PDU pduResult = response.getResponse();
				if(pduResult.getErrorStatus() == PDU.noError)
				{
					VariableBinding variable = (VariableBinding)pduResult.getVariableBindings().get(0);
					if(variable != null)
					{
						strResult = variable.toValueString();
					}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		pdu.clear();
		return strResult;
	}
	
	//编写getbulk方法
	public String sendGetBulk(String strOID) throws Exception
	{
		String strResult = null;
		PDU pdu = new PDU();
		pdu.setType(PDU.GETBULK);
		pdu.setMaxRepetitions(100);
		pdu.setNonRepeaters(100);
		
		for(int i = 0;i < 100;i ++)
		{
			pdu.addOID(new VariableBinding(new OID(strOID)));
			ResponseEvent responseEvent = msnmpClient.send(pdu, communityTarget);
				if(responseEvent != null)
				{
					PDU response = responseEvent.getResponse();
					strResult = response.getVariableBindings().get(0).toString();
					System.out.println("响应包含的数据数量:"+response.size());
					System.out.println("st1="+strResult);
				}
		}
		
		
		
			
			
			
			
		return strResult;
	}
}

