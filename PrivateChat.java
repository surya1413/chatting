import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;
class PrivateChat extends JFrame implements ActionListener
{
     TextArea pta,ptb;  
     JButton send,ignore;
     Font fo=new Font("Arial",Font.PLAIN,15);
     String ouser;
     ChatClient mw;
     int utype;
     public PrivateChat(ChatClient mw,String ouser,int utype)
     {
	setSize(400,270);
	setLocation(300,100);
	setResizable(false);
	this.utype=utype;
	this.mw=mw;
	setLayout(new FlowLayout(FlowLayout.RIGHT));
	ignore=new JButton("Ignore user");
	add(ignore);
	ignore.addActionListener(this);
 	pta=new TextArea(8,45);
	pta.setFocusable(false);
	ptb=new TextArea(1,37);
	ptb.setText("");
	pta.setFont(fo);ptb.setFont(fo);
	send=new JButton("Send");
	add(pta);add(ptb);add(send);
	send.addActionListener(this);
	addWindowListener(new Pw());
	this.ouser=ouser;
	showMsg();
	setVisible(true);
     }
     void showMsg()
     {
	if(utype==1)
	{
	  setTitle("to "+ouser);
	}
	else
	{
	  pta.append(ouser+"\n");
	  ouser=ouser.substring(0,ouser.indexOf(":"));
	  setTitle("from "+ouser);
	}
     }
     class Pw extends WindowAdapter
     {
	public void windowClosing(WindowEvent we)
	{
	  if(utype==1)
	    mw.topuserslist.remove(ouser);
	  else
	    mw.frompuserslist.remove(ouser);
	}
     } 
     public void actionPerformed(ActionEvent evt)
     {
      try
      {
	if(evt.getSource()==send)
	{
	  DataOutputStream dos=new DataOutputStream(mw.soc.getOutputStream());
	  dos.writeUTF(ouser+":"+ptb.getText());
	  pta.append(mw.cuser+":"+ptb.getText()+"\n");
	  ptb.setText("");
	  if(mw.ignorelist.indexOf(ouser)!=-1)
	   mw.ignorelist.remove(ouser);
	}
	if(evt.getSource()==ignore)
  	{
	  mw.ignorelist.add(ouser);
	}
      }
      catch(Exception ex){}
     }
   }