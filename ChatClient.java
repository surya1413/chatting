import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
class ChatClient extends JFrame implements ActionListener,KeyListener
{
   TextArea ta,jta;
   java.awt.List lb;
   JButton send;
   Font fo;
   String cuser,toouser,fromouser;Socket soc;
   JPanel lw,cw;
   JLabel jna;
   JTextField jtb;
   JButton jlog,logout;
   boolean pws=false; 
   PrivateChat chat;
   DataInputStream dis;
   LinkedList <String>topuserslist=new LinkedList<String>();
   LinkedList <String>frompuserslist=new LinkedList<String>();
   LinkedList <String>ignorelist=new LinkedList<String>();
   public ChatClient()
   {
	addWindowListener(new ExitWindow());
	this.cuser=cuser;
	fo=new Font("Arial",Font.PLAIN,15);
	setSize(350,550);
	setLayout(null);
	setResizable(false);
	addLoginWindow();
	addChatWindow();
	setVisible(true);
   }
   void addLoginWindow()//will create login window
   {
	lw=new JPanel();
	lw.setBounds(0,0,350,530);
	lw.setLayout(null);
	add(lw);
	jna=new JLabel("Enter nick name:");
	jna.setBounds(30,200,130,30);
	jna.setFont(fo);
	lw.add(jna);
	jtb=new JTextField();
	jtb.addKeyListener(this);
	jtb.setBounds(160,200,150,30);
	jtb.setFont(fo);
	lw.add(jtb);
	jlog=new JButton("Start Chat");
	jlog.setBounds(120,300,100,35);
	lw.add(jlog);
	jlog.addActionListener(this);
   }
   void addChatWindow()//will create chat window
   {
	cw=new JPanel();
	cw.setBounds(0,0,350,530);
	cw.setLayout(null);
	add(cw);
	cw.setVisible(false);
	logout=new JButton("Logout");
	logout.setBounds(250,5,80,27);
	cw.add(logout);
	logout.addActionListener(this);
	ta=new TextArea();
	ta.setFocusable(false);
	ta.setBounds(10,40,230,390);
	cw.add(ta);
	lb=new java.awt.List();
	lb.setBounds(240,40,90,390);
	lb.addActionListener(this);
	cw.add(lb);
	jta=new TextArea();
	jta.setBounds(10,440,230,60);
	cw.add(jta);
	send=new JButton("Send");
	send.setBounds(240,440,90,60);
	cw.add(send);
	ta.setFont(fo);jta.setFont(fo);
	send.addActionListener(this);
	jta.addKeyListener(this);
	send.setEnabled(false);
   }
   void startChat() //will start chat
   {
        cuser=jtb.getText();
        if(cuser.length()==0)
        {
	JOptionPane.showMessageDialog(this,"Enter your name to start chat");
	return;
        }
        try
        {
	  //soc=new Socket("localhost",2001);
	  soc=new Socket("192.168.43.77",2001);
	DataOutputStream dos=new DataOutputStream(soc.getOutputStream());
	dos.writeUTF(cuser);
	DataInputStream diss=new DataInputStream(soc.getInputStream());
	String str=diss.readUTF();
	if(str.equals("Welcome"))
	{
	  lw.setVisible(false);
	  cw.setVisible(true);
	  new UserThread(this).start();
	  setTitle(cuser+"'s Chat Window");
	  ta.append("Congratulations!! "+cuser+"\n");
	}
	else
	  JOptionPane.showMessageDialog(this,str);
        }        
        catch(Exception ex){System.out.println(ex);}
   }
   public void actionPerformed(ActionEvent evt) //will send messages to other users
   {
      if(evt.getSource()==lb) //will start private chat
      {
  	 toouser=lb.getSelectedItem().trim();
	 if(topuserslist.indexOf(toouser)==-1 && frompuserslist.indexOf(toouser)==-1)
	 {
	   topuserslist.add(toouser);
	   if(!toouser.equals(cuser))
	     chat=new PrivateChat(this,toouser,1);
	 }
      }
      if(evt.getSource()==send) //will start public chat
      {
        try
        {
	   DataOutputStream dos=new DataOutputStream(soc.getOutputStream());
  	   dos.writeUTF(cuser+":"+jta.getText());
	   ta.append(cuser+":"+jta.getText()+"\n");
	   jta.setText("");
	  send.setEnabled(false);
        }
        catch(Exception ex){}
      }
      if(evt.getSource()==jlog)//start chat button
      {
	 startChat();
      }
      if(evt.getSource()==logout)
      {
	 quit();
      }
   }
   public void keyReleased(KeyEvent ke)
   {
	if(jta.getText().length()>0)
	  send.setEnabled(true);
	else
	  send.setEnabled(false);
   }
   public void keyTyped(KeyEvent ke){}
   public void keyPressed(KeyEvent ke)
   {
	/*char ch=ke.getKeyChar();
	if(ke.getSource()==jtb && ch=='\n')
	  startChat();*/
   }
   void quit()//logout
   {
         try
         {
	     if(cuser!=null)
	     {
		DataOutputStream dos=new DataOutputStream(soc.getOutputStream());
  		dos.writeUTF(soc.getLocalPort()+"");
	     }
	     System.exit(0);
         }
         catch(Exception ex){System.out.println(ex);}
	 
   }
   class ExitWindow extends WindowAdapter
   {
	public void windowClosing(WindowEvent we)
	{
	      quit();
	}
   } 
   class UserThread extends Thread
   {
       ChatClient mcw;
       public UserThread(ChatClient mcw)
       {
	this.mcw=mcw;
       } 
       public void run()
       {
            	int ft=0;
	try
	{
	    if(ft==0)
	    {
	         ObjectInputStream ois=new ObjectInputStream(soc.getInputStream());
	         ArrayList list=(ArrayList)ois.readObject();
	         for(int i=0;i<list.size();i++)
	         lb.add(String.valueOf(list.get(i)));
	         ft=1;
	     }
	     dis=new DataInputStream(soc.getInputStream());
	     while(true)
	     { 
	         String str=dis.readUTF();
	         String two=str.substring(0,2);
	         if(two.equals("pc"))//to start private chat 
	         {
	             startPrivateChat(str.substring(2));  
	         }
	         else
	         {
	             char ch=str.charAt(0);
	             if(ch=='#')//logged in
	             {
	                 str=str.substring(1);
	                 String dt=str.substring(0,str.length()-9);
	                 lb.add(dt);
	                 ta.append(str+"\n");
	            }
	            else if(ch=='%')//logged out
	            {
	                 str=str.substring(1);
	                 String dt=str.substring(0,str.length()-10);
	                 lb.remove(dt);
	                 ta.append(str+"\n");
	            }
	            else //public chat
	                 ta.append(str+"\n");
	       }
	}
      }
      catch(Exception ex){System.out.println(ex);}
     }
   }
   private void startPrivateChat(String str)
   {
	
	fromouser=str.substring(0,str.indexOf(":")).trim();
	if(ignorelist.indexOf(fromouser)==-1)
	{
	  if(frompuserslist.indexOf(fromouser)==-1 && topuserslist.indexOf(fromouser)==-1)
	  {
	    frompuserslist.add(fromouser);
                    chat=new PrivateChat(this,str,2);
	  }
	  else
	    chat.pta.append(str+"\n");
	}
   }
   public static void main(String ...s)
   {
	JFrame.setDefaultLookAndFeelDecorated(true);
	new ChatClient();
   }
}

