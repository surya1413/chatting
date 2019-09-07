import java.net.*;
import java.io.*;
import java.util.*;
public class ChatServer
{
   ServerSocket server;
   Socket soc;
   ArrayList <Socket>soclist;
   ArrayList <String>namelist;
   String name;
   public ChatServer()
   {
    try
    {
	server=new ServerSocket(2001);
	System.out.println("server started...");
	soclist=new ArrayList<Socket>();
	namelist=new ArrayList<String>();
	while(true)
	{
	  soc=server.accept();
	  if(broadcost())
	    new ClientThread(soc,soclist,namelist,name).start();
	}
    }
    catch(Exception ex){System.out.println("e1"+ex);}
   }
   boolean broadcost()//will inform other user about current user
   {
      boolean bd=true;
      try
      {
	  DataInputStream dis=new DataInputStream(soc.getInputStream());	
	  name=dis.readUTF();
	  if(isUserExist(name))//check the existence of user
	  {
	     DataOutputStream dos=new DataOutputStream(soc.getOutputStream());
	     dos.writeUTF("This name is already in use..");
	     bd=false;
	  }
	  else//inform to all user
	  {
	   soclist.add(soc);
	   DataOutputStream dos=new DataOutputStream(soc.getOutputStream());
	   dos.writeUTF("Welcome");
	   for(int i=0;i<soclist.size();i++)
	   {
  	        Socket sc=(Socket)soclist.get(i);
	        if(sc!=soc)
	        {
	          dos=new DataOutputStream(sc.getOutputStream());
	          dos.writeUTF("#"+name+" logged in");
	        }	
	   }
	   namelist.add(name);
	   ObjectOutputStream oos=new ObjectOutputStream(soc.getOutputStream());
	   oos.writeObject(namelist);
	  }
      } 
      catch(Exception ex){System.out.println("e2"+ex);}
      return bd;
   }
   boolean isUserExist(String name)//will check whether user already exist or not
   {
	if(namelist.indexOf(name)==-1)
	  return false;
	else
	  return true;
   }
   public static void main(String ...s)
   {
	new ChatServer();
   }
}
class ClientThread extends Thread //thread for each client
{
   Socket soc;
   ArrayList soclist,namelist;
   int ft=0;
   String name;
   ClientThread(Socket soc,ArrayList soclist,ArrayList namelist,String name)
   {
	this.soc=soc;
	this.soclist=soclist;
	this.namelist=namelist;
	this.name=name;
	System.out.println(name+"'s socket no:"+soc.getPort());
   }
   public void run()
   {
      boolean st=true;	
      try
      {
	DataInputStream dis=new DataInputStream(soc.getInputStream());
	String data="";	
	BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
	do
	{
	    data=dis.readUTF();
	    String pname="";
	    int ip=data.indexOf(":");
	    if(ip!=-1)
	      pname=data.substring(0,ip).trim();
	    if(data.equals(soc.getPort()+""))
	    {
		  soclist.remove(soc);
		  data="%"+name+" Logged out";
		  namelist.remove(name);
		  st=false;  
	    }
	    if(ip!=-1 && !pname.equals(name))
	    {
	        int i=namelist.indexOf(pname);
	        Socket sc=(Socket)soclist.get(i);
 	        DataOutputStream dos=new DataOutputStream(sc.getOutputStream());
	        dos.writeUTF("pc"+name+":"+data.substring(data.indexOf(":")+1));
	    }
	    else
	    {
	      for(int i=0;i<soclist.size();i++)
	      {
  	        Socket sc=(Socket)soclist.get(i);
	        if(sc!=soc)
	        {
	          DataOutputStream dos=new DataOutputStream(sc.getOutputStream());
	          dos.writeUTF(data);
	        }
	      }
	    }	
	}while(st);
      }  
      catch(Exception ex){ex.printStackTrace();}
   }
}