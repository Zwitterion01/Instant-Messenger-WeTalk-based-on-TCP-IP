package WeTalkClient;

import java.net.*;
import java.io.*;
import java.nio.channels.*;
import java.util.ArrayList;

public class EchoClient{
  private String remoteHost="localhost";
  private int remotePort=8000;
  private DatagramSocket socket;
  String pa=new String("pass");
  String si=new String("sign");
  String ack=new String("ACK");
  private int judge=0;
  private int chaoshi=0;
  public int signj=0;
  ArrayList<String> rece;
  public EchoClient()throws IOException{
	  rece=new ArrayList<String>();
	  String h=new String("init");
	  rece.add(h);
   DatagramChannel datach=DatagramChannel.open();
	 socket=datach.socket(); //尚未与UDP端口绑定
	 //socket=new DatagramSocket();
     socket.setReuseAddress(true);
     socket.setSoTimeout(4000);
	 //socket.bind(new InetSocketAddress(6000));
  }
  
  public void talk(String msg)throws IOException {
    try{
    	byte[] bs = new byte[] { (byte)10, (byte)205, (byte) 18, (byte)108};
     // InetAddress remoteIP=InetAddress.getByName(remoteHost);
    	InetAddress remoteIP=InetAddress.getByAddress(bs);
        byte[] outputData=msg.getBytes();
        DatagramPacket outputPacket=new DatagramPacket(outputData,
                                    outputData.length,remoteIP,remotePort);
        socket.send(outputPacket);  //给EchoServer发送数据报
        
      DatagramPacket inputPacket=new DatagramPacket(new byte[512],512);
   
       try
       {socket.receive(inputPacket);}
       catch(java.net.SocketTimeoutException e)
       {
    	   chaoshi=1;
       }
        String mass=new String(inputPacket.getData(),0,inputPacket.getLength());System.out.println(mass);
        if(mass.equals(pa))
        {
        	judge=1;
        	 System.out.println("Log successfully.");  
        }
        if(mass.equals(si))
        {
        	signj=1;
        	 System.out.println("Sign successfully.");  
        }
        if(mass.equals(ack))
        {
        	
        	 System.out.println("ACK");  
        }
        //System.out.println(mass);  
        //if(msg.equals("Log successfully!")) 	
      
      
    }catch(IOException e){
       e.printStackTrace();
    }
    
    
    
  }
  
  public void talk1(String msg)throws IOException {
	    try{
	    	byte[] bs = new byte[] { (byte) 10, (byte)205, (byte) 18, (byte) 108 };
	      //InetAddress remoteIP=InetAddress.getByName(remoteHost);
	    	InetAddress remoteIP=InetAddress.getByAddress(bs);
	        byte[] outputData=msg.getBytes();
	        DatagramPacket outputPacket=new DatagramPacket(outputData,
	                                    outputData.length,remoteIP,remotePort);
	        socket.send(outputPacket);  //给EchoServer发送数据报
	        
	    }catch(IOException e){
	       e.printStackTrace();
	    }
	    
	    
	    
	  }
	  
  /*public void run()
  {   System.out.println("Thread up!");
	  while(true)
	  {   int i=0;
		  DatagramPacket inputPacket=new DatagramPacket(new byte[512],512);
      try {
		socket.receive(inputPacket);
		 String mass=new String(inputPacket.getData(),0,inputPacket.getLength());
		 if(mass.equals(pa)||mass.equals(si)||mass.equals(ack))
	      {if(mass.equals(pa))
	      {
	      	judge=1;
	      
	      	 System.out.println("Log successfully.");   
	      }
	      if(mass.equals(si))
	      {
	      	signj=1;
	      	 System.out.println("Sign successfully.");  
	      }
	      if(mass.equals(ack))
	      {
	      	
	      	 System.out.println("ACK");  
	      }}
		 else
			 rece.add(mass);
	      
	      
	      
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
     
	  }
	  
	  
	}
	*/
 public int getJudge()
 {
	 return judge;
 }
 public int getChaoshi()
 {
	 return chaoshi;
 }
}  