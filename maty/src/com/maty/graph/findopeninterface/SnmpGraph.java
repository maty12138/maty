package com.maty.graph.findopeninterface;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.snmp4j.mp.SnmpConstants;

import com.maty.snmpclient.SNMPClient;

public class SnmpGraph extends MouseAdapter
{
	ArrayList list = new ArrayList();
	int tmp ;
	String flagAdmin;
	String flagOper;
	String[] ups = null;
	String sysDescr = ".1.3.6.1.2.1.1.1.0";
	String ifNumber = ".1.3.6.1.2.1.2.1.0";
	String ifIndex = ".1.3.6.1.2.1.2.2.1.1";
	String ifDescr = ".1.3.6.1.2.1.2.2.1.2";
	String ifType = ".1.3.6.1.2.1.2.2.1.3";
	String ifAdminStatus = ".1.3.6.1.2.1.2.2.1.7";
	String ifOperStatus = ".1.3.6.1.2.1.2.2.1.8";
	JTextField ip_field = null;
	JTextField community_field = null;
	JButton sumit_button = null;
	JLabel label_left = null;
	JLabel label_right = null;
	
	public SnmpGraph()
	{
		JFrame f = new JFrame("该程序仅仅使用于SNMP V2C版本!!!!!!");
		f.setLayout(new BorderLayout());
		Container contentPane = f.getContentPane();
		
		//先处理最上层的组件,该组件包含ip、community字符框和确认按钮
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new FlowLayout());
		ip_field = new JTextField("请输入该设备的IP地址");
		ip_field.addMouseListener(this);
		community_field = new JTextField("请输入该的团体字");
		community_field.addMouseListener(this);
		sumit_button = new JButton("确认");
		sumit_button.addActionListener(new ActionListener()
		{	
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(ip_field.getText().equals("") || community_field.getText().equals(""))
				{
					//提示信息，请输入IP和团体字
				}
				else
				{
					//确认之后应该做的事情
					try
					{
						SNMPClient client = new SNMPClient(ip_field.getText(), 161, community_field.getText(), SnmpConstants.version2c, 1000, 2);
						int count = new Integer(client.sendGet(ifNumber));
						String ifIndexTemp =ifIndex;
						String ifDescrs = "";
						String ifOpens = "";
						String temp = null;
						for(int i = 0;i < count;i++)
						{
							//处理interface detail
							tmp = new Integer(client.sendGetNext(ifIndexTemp));  //现将接口的第一个index的结尾弄出来
							ifIndexTemp = ifIndex +"."+ tmp;
							temp = client.sendGet(ifDescr+"."+tmp);
							ifDescrs += temp  + "<br/>";
							
							//处理open interface
							flagOper = client.sendGet(ifOperStatus+"."+tmp);
							if(flagOper.equals("1"))
								ifOpens += temp +"     ("+tmp+")"+"<br/>";
						}
						label_left.setText("<html><body>"+ifDescrs+"一共有"+count+"个接口"+"</body></html>");
						label_right.setText("<html><body>"+ifOpens+"</body></html>");
					} catch (IOException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
			}
		});
		JButton reset_button = new JButton("重置");
		reset_button.addActionListener(new ActionListener()
		{
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ip_field.setText("请输入该设备的IP地址");
				community_field.setText("请输入该设备的团体字");
			}
		});
		inputPanel.add(ip_field);
		inputPanel.add(community_field);
		inputPanel.add(sumit_button);
		inputPanel.add(reset_button);
		contentPane.add(inputPanel, BorderLayout.NORTH);
		
		//处理左边的Interface detail详情
		label_left = new JLabel();
		label_left.setBorder(BorderFactory.createTitledBorder("Interface Detail"));
		//此处可添加接口信息
		//--------------
		
		//处理右边的Open Interfaces信息
		label_right = new JLabel();
		label_right.setBorder(BorderFactory.createTitledBorder("Open Interface"));
		//此处可添加open接口信息
		
		
		JPanel showContentPanel = new JPanel();
		showContentPanel.setLayout(new GridLayout(1, 2));
		ScrollPane sp_left = new ScrollPane();
		sp_left.add(label_left);
		showContentPanel.add(sp_left);
		ScrollPane sp_right = new ScrollPane();
		sp_right.add(label_right);
		showContentPanel.add(sp_right);
		contentPane.add(showContentPanel);
		
		
		
		f.setVisible(true);
		f.setSize(800, 600);
		f.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		if(e.getSource() == ip_field)
			ip_field.setText("");
		else
			community_field.setText("");
	}
	
	
	public static void main(String[] args)
	{
		new SnmpGraph();
	}

	
}
