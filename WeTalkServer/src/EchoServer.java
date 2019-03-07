import java.io.*;
import java.net.*;
import java.util.ArrayList;
public class EchoServer {
  private int port=8000;
  private DatagramSocket socket;
  ArrayList<User> userlist;
  public EchoServer() throws IOException {
	  userlist=new ArrayList<User>();
    //socket=new DatagramSocket(); //与本地的任意一个UDP端口绑定
	
	//socket.setReuseAddress(true);
	socket=new DatagramSocket(port); //与本地的一个固定端口绑定
    //socket.bind(new InetSocketAddress(port));
	System.out.println("服务器启动");
  }

  public String echo(String msg) {
    return "echo:" + msg;
  }

  public void service() {
    while (true) {
      try {
        DatagramPacket packet=new DatagramPacket(new byte[512],512);
        socket.receive(packet);  //接收来自任意一个EchoClient的数据报
        String msg=new String(packet.getData(),0,packet.getLength());         
        System.out.println(packet.getAddress() + ":" +packet.getPort()
                            +">"+msg);      
      writefile("src/res/visitlog.txt",packet.getAddress() + ":" +packet.getPort() +">"+msg+"\r\n\r\n");
        
      if(msg.length()>10&&(msg.substring(0, 8).equals("usersign")||msg.length()>7&&msg.substring(0, 7).equals("userlog"))||msg.equals("request")||msg.equals("close"))
        {
        
    	  String[] strs=msg.split("\\*");
    	  ArrayList<String> ss1= new ArrayList<String>();
			ss1=readFile("src/res/users.txt");
			
			
			
			//sign
			 if(msg.length()>10&&msg.substring(0, 8).equals("usersign")) 
    	  {int jug1=0;
    	  for(int j=0;ss1!=null&&j<ss1.size();j++)
    	  {
    		  String[] s1=ss1.get(j).split(" ");
    		  if(s1[0].equals(strs[1]))
    		  {
    			  jug1=1;
    			  break;
    		  }
    	  }
    	  
    	  if(msg.length()>10&&msg.substring(0, 8).equals("usersign")&&jug1!=1) 
    	   {File f = new File("src/res/users.txt");
   		FileWriter fw;
   		try {
   			fw = new FileWriter(f.getAbsoluteFile(), true);
   			BufferedWriter bw = new BufferedWriter(fw);
   			bw.newLine();
   			bw.write(strs[1]+" "+strs[2]);
   			bw.close();
   		} catch (IOException e) {
   			e.printStackTrace();
   		}
   		
   		String sign=new String("sign");
		  packet.setData(sign.getBytes());  
	      socket.send(packet);
	      System.out.println("User: "+strs[1]+" sign successfully.");
	      writefile("src/res/visitlog.txt","User: "+strs[1]+" sign successfully."+"\r\n\r\n");
    	   }
    	  if(msg.length()>10&&msg.substring(0, 8).equals("usersign")&&jug1==1)
    	   {
    		   String erro=new String("erro");
			      packet.setData(erro.getBytes());  
				  socket.send(packet);
    	   }
    	  }
    	  
    	  //log
    	  if(msg.length()>7&&msg.substring(0, 7).equals("userlog"))
    	  {   
    		  ArrayList<String> ss= new ArrayList<String>();
    			ss=readFile("src/res/users.txt");
    			int li=0; int ju=0;
    		  for(int x=0;x<ss.size();x++)
    		  {  
    			  String[] s=ss.get(x).split(" ");
    			  for(int j=1;j<=userlist.size();j++)
    			  {
    				  if(userlist.get(j-1).getName().equals(strs[1]))
    				  {
    					  ju=1;                                        //prevent over logs
    					  break;
    				  }
    			  }
    			  if(s[0].equals(strs[1])&&s[1].equals(strs[2])&&ju!=1)
    			  {   
    				  User temp=new User(s[0],packet.getAddress(),packet.getPort());
    				  String hh=new String("Users: "+s[0]+" come online! "+ packet.getAddress());
    				  byte[] outputData=hh.getBytes();
    			      userlist.add(temp);
    				  String pass=new String("pass");
    				  packet.setData(pass.getBytes());  
    			      socket.send(packet);
    			    for(int i=1;i<=userlist.size();i++)
    			      {   if(!temp.equals(userlist.get(i-1)))
    			    	  {     
    			      DatagramPacket outputPacket=new DatagramPacket(outputData,
                              outputData.length,userlist.get(i-1).getIp(),6000);
    			      socket.send(outputPacket);}
    			      }
    			      li=1;
    			      break;
    			  }   System.out.println("User: "+s[0]+" log successfully.");  
    			      writefile("src/res/visitlog.txt","User: "+s[0]+" log successfully."+"\r\n\r\n");   
    		  }if(li==0)
                     {String erro=new String("erro");
				      packet.setData(erro.getBytes());  
    				  socket.send(packet);}	  
    	  }
   	
    	  
    	  //request for userlist
    	  
    	  if(msg.equals("request"))
    	  {
    		   for(int i=1;i<=userlist.size();i++)
			      { 
    		String hh=new String("Existed User: "+userlist.get(i-1).getName()+" "+userlist.get(i-1).getIp());
			  byte[] outputData=hh.getBytes();
			      DatagramPacket outputPacket=new DatagramPacket(outputData,
                       outputData.length,packet.getAddress(),6000);
			      socket.send(outputPacket);
			      System.out.println("Request has been handdled!");
			      writefile("src/res/visitlog.txt","Request has been handdled!"+"\r\n\r\n");		     
			      }
    	  }
    	  if(msg.equals("close"))
    	  {
    		  for(int i=1;i<=userlist.size();i++)
		      { 
    			  if(packet.getAddress().equals(userlist.get(i-1).getIp()))
    			  {
    				  System.out.println("User: "+userlist.get(i-1).getName()+" gone!");
    				  writefile("src/res/visitlog.txt","User: "+userlist.get(i-1).getName()+" gone!"+"\r\n\r\n");
    				  userlist.remove(i-1);
    				  break;
    			  }
		      }
    	  }
    	  
        }
        else
        	{ 
        String ack=new String("ACK");
        packet.setData(ack.getBytes());  
        socket.send(packet);
        System.out.println("done");
            String msgs[]=msg.split("\\*");
        	byte[] outputData=msgs[1].getBytes();
        	for(int x=0;x<userlist.size();x++)
        	{
        		if(!userlist.get(x).getName().equals(msgs[0]))
        		{   
        			DatagramPacket outputPacket=new DatagramPacket(outputData,
                                    outputData.length,userlist.get(x).getIp(),userlist.get(x).getPort());
                    socket.send(outputPacket);  
     
        		}
        		
        	}
        
        
        	} //给EchoClient回复一个数据报
      }
      catch (IOException e) {
         e.printStackTrace();
      }
    }
  }
  public ArrayList<String> readFile(String pth) {

		try {
			String encoding = "UTF-8"; // Coding format
			File file = new File(pth);
			ArrayList<String> list = new ArrayList<String>();
			if (file.isFile() && file.exists()) { // Check if the file exists
				InputStreamReader in = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bfr = new BufferedReader(in);
				String tempString = null;

				// Putting values into Arraylist line by line, until null
				while ((tempString = bfr.readLine()) != null) {
					list.add(tempString);
				
				}
				bfr.close();
				return list;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
  public void writefile(String pth,String content)
  {
	  File f = new File(pth);
 		FileWriter fw;
 		try {
 			fw = new FileWriter(f.getAbsoluteFile(), true);
 			BufferedWriter bw = new BufferedWriter(fw);
 			bw.newLine();
 			bw.write(content);
 			bw.close();
 		} catch (IOException e) {
 			e.printStackTrace();
 		}
  }

  public static void main(String args[])throws IOException {
    new EchoServer().service();
  }
}
