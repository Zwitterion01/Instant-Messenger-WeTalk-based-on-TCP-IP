package WeTalkClient;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//UDP协议制作的GUI聊天室,欢迎各位大侠们来指点
public class GuiChat {
 private Frame frame = new Frame("WeTalk1.1");  //需要的成员变量
 private Panel centerPanel = new Panel();
 private Panel southPanel = new Panel();
 private TextArea centerTextArea = new TextArea();
 private TextArea southTextArea = new TextArea(5, 1);  //5行1列,但会铺满边界
 private TextField textField = new TextField(20);
 private Button sendButton = new Button("发送"); 
 private Button clearButton = new Button("清空");
 private Button logButton = new Button("聊天记录");
 private Button shakeButton = new Button("震屏");
 ArrayList<User> userlist;
 private DatagramSocket socket;
 private Lock lock = new ReentrantLock();
 private Writer writer;
 
 public GuiChat() throws IOException{     //事件方法
  inti();
  generateGUI();
  handleEvent();
  new newThread().start();
  try {
	Thread.sleep(1000);
} catch (InterruptedException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
  DatagramSocket socket1 = new DatagramSocket(); 
  String req=new String("request");
   byte[]  bytes=req.getBytes(); 
   DatagramPacket packetout = new DatagramPacket(bytes, bytes.length, InetAddress.getByName("10.205.18.108"), 8000);
   socket1.send(packetout);
   socket1.close();
 }

 private void inti() {             //抛异常对象
   try {
   userlist=new ArrayList<User>(); 
   socket = new DatagramSocket();
   writer = new BufferedWriter(new FileWriter("log.txt", true));
  } catch (IOException e) {
   
   throw new RuntimeException();
  }
  
 }

 private void handleEvent() {  
  //窗口监听器
  frame.addWindowListener(new WindowAdapter(){

   @Override
   public void windowClosing(WindowEvent e) {
    try {
    	DatagramSocket socket1 = new DatagramSocket(); 
    	  String req=new String("close");
    	   byte[]  bytes=req.getBytes(); 
    	   DatagramPacket packetout = new DatagramPacket(bytes, bytes.length, InetAddress.getByName("10.205.18.108"), 8000);
    	   socket1.send(packetout);
    	   socket1.close();
     writer.close();
     socket.close();
     
     System.exit(0);
    } catch (IOException e1) {
     // TODO Auto-generated catch block
     e1.printStackTrace();
    }
   }
   
  });
  //发送按钮监听器
  sendButton.addMouseListener(new MouseAdapter(){

   @Override
   public void mousePressed(MouseEvent e) {
    send();
   }
   
  });
  
  //南边区域监听器
  /*southTextArea.addKeyListener(new KeyAdapter(){

   @Override
   public void keyPressed(KeyEvent e) {
    if(e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown())
     send();
    e.consume(); // 把指针显示在第一行
   }
   
  });
  */
  //清空监听器
  clearButton.addMouseListener(new MouseAdapter(){

   @Override
   public void mousePressed(MouseEvent e) {
    centerTextArea.setText("");
   }
   
  });
  
  //日志监听器
  logButton.addMouseListener(new MouseAdapter(){

   @Override
   public void mousePressed(MouseEvent e) {
    showLog();
   }
   
  });
  
  //震动监听器
  shakeButton.addMouseListener(new MouseAdapter(){

   @Override
   public void mousePressed(MouseEvent e) {
    sendShake();
   }
   
  });
 }
 
 //设计震屏,但震屏不能出现在发送的内容中,这就选一个特殊的数字代替
 private void sendShake() {
	String ip = textField.getText();
	 for(int x=1;x<=userlist.size();x++)
	  {
		   if(userlist.get(x-1).getName().equals(ip))
		   {
			   ip=userlist.get(x-1).getIp().toString().substring(1);                                  //show the message of user you send
			   break;
		   }
	  }
  sendDate(ip, new byte[]{-1});
  
 }
 
 //聊天记录方法
 private void showLog(){
  
   try {
    writer.flush();
    FileInputStream fis = new FileInputStream("log.txt");
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int len;
    while ((len = fis.read(buffer)) != -1) {
     baos.write(buffer, 0, len);
    }
    fis.close();
    baos.close();
    
    String content = new String(baos.toByteArray());
    centerTextArea.setText(content);
   } catch (Exception e) {
    // TODO: handle exception
   }
  
  
 }
 
 //发送消息方法
 private void send() {
  String message = southTextArea.getText();
  String ip = textField.getText();
  
  
  for(int x=1;x<=userlist.size();x++)
  {
	   if(userlist.get(x-1).getName().equals(ip))
	   {
		   ip=userlist.get(x-1).getIp().toString().substring(1);                                  //show the message of user you send
		   break;
	   }
  }
  
  ip = "".equals(ip) ? "255.255.255.255" : ip;
  
  if(message.length() == 0)
   return;
  
  String regexPart = "((\\d)|(\\d{2})|([01]\\d{2})|(2[0-4]\\d)|(25[0-5]))";
  String isRegex = regexPart + "\\." + regexPart + "\\." + regexPart + "\\." +regexPart;
  if(!ip.matches(isRegex))
   return;
  
  lock.lock();
  try {
   sendDate(ip, message.getBytes()); //定义一个发送数据的方法
   //这里有可能会出错误,当数据还没有打印出来,而接收到的数据就显示出来了,就要使用同步
   for(int x=1;x<=userlist.size();x++)
   {
	   if(userlist.get(x-1).getIp().toString().substring(1).equals(ip))
	   {
		   ip=userlist.get(x-1).getName();                                  //show the message of user you send
		   break;
	   }
   }
   String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
   String content = time + " 我对 " + ip + " 说: " + "\r\n" + message + "\r\n\r\n";
   centerTextArea.append(content);
   writer.write(content);
   southTextArea.setText("");
  } catch (Exception e) {
   throw new RuntimeException();
  }finally{
  lock.unlock();
  }
 }
 
 //发送消息数据方法
 private void sendDate(String ip, byte[] bytes){
  
  try {
   DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(ip), 6000);
   socket.send(packet);
  } catch (IOException e) {
   
   throw new RuntimeException(e);
  }
  
 }
 
 //创建新的线程用来接收消息
 class newThread extends Thread{

  @Override
  public void run() {
   try {
	   
	   
	   System.out.println("thread up");
    // 使用UDP协议接收数据
    DatagramSocket socket = new DatagramSocket(6000); 
    DatagramPacket packet = new DatagramPacket(new byte[1024 * (1024)], 1024 * 1024);
    while (true) {
     socket.receive(packet);    
     if(packet.getLength() == 1 && packet.getData()[0] == -1){
      doShake();
      continue;
     }
     String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
     String ip = packet.getAddress().getHostName(); //???
     String message = new String(packet.getData(), 0, packet.getLength());
   System.out.println(message);
     String u=new String("Users:");
     String content=new String();
     if((message.length()>10&&message.substring(0,6).equals(u))||(message.length()>10&&message.substring(0,7).equals("Existed")))
     {
    	 if(message.length()>10&&message.substring(0,6).equals(u))
    	 { String[] str=message.split(" ");
     content= time +" "+message+"\r\n\r\n";
     User temp=new User(str[1],InetAddress.getByName(str[4].substring(1)),6000);
     userlist.add(temp);}
    	 if(message.length()>10&&message.substring(0,7).equals("Existed"))
    	 {
    		 String[] str=message.split(" ");
    	     content= time +" "+message+"\r\n\r\n";
    	     User temp=new User(str[2],InetAddress.getByName(str[3].substring(1)),6000);
    	     userlist.add(temp);
    	 }
     }
     else
     { 
    	 
    	/* for(int i=1;i<=userlist.size();i++)
    	 {
    		 System.out.println(i); System.out.println(userlist.get(i-1).getIp());
    	 }
   content = time + " "+  ip + " 对我说: " + "\r\n" + message + "\r\n\r\n";*/
    	 for(int i=1;i<=userlist.size();i++)
        {  
    		
    	   if(packet.getAddress().equals(userlist.get(i-1).getIp())) 
    	   {   
    		   content = time + " "+  userlist.get(i-1).getName() + " 对我说: " + "\r\n" + message + "\r\n\r\n";
    		   break;
    	   }
        }
    	
    	 }
     lock.lock();
     try{
    	
     centerTextArea.append(content);  //显示在centerTextArea上
     writer.write(content);
     }finally{
      lock.unlock();
     }
    }
   } catch (Exception e) {
    e.getStackTrace();
   }
  
   }
 }
 
 //震屏方法
 public void doShake() {
  int x = frame.getLocation().x;
  int y = frame.getLocation().y;
  // frame.toFront();   // 有可能无效
  // frame.setVisible(true);
    
  try {
   frame.setLocation(x - 20, y - 20);
   Thread.sleep(20);
   frame.setLocation(x + 20, y - 0);
   Thread.sleep(20);
   frame.setLocation(x + 20, y + 20);
   Thread.sleep(20);
   frame.setLocation(x - 20, y + 20);
   frame.setLocation(x - 20, y - 20);
   Thread.sleep(20);
   frame.setLocation(x + 20, y - 0);
   Thread.sleep(20);
   frame.setLocation(x + 20, y + 20);
   Thread.sleep(20);
   frame.setLocation(x - 20, y + 20);
   Thread.sleep(20);
   
   frame.setLocation(x, y);
  } catch (InterruptedException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  
 }
 
 //图形界面方法
 private void generateGUI() {
  //设置frame
  frame.setSize(970, 640);
  frame.setResizable(false);
  frame.setLocationRelativeTo(null);
  //设计中间的Panel
  frame.add(centerPanel, BorderLayout.CENTER);
  centerPanel.setLayout(new BorderLayout());
  centerPanel.add(centerTextArea,BorderLayout.CENTER);
  centerPanel.add(southTextArea, BorderLayout.SOUTH);
  
  //southTextArea.setEditable(true);
  centerTextArea.setEditable(false);
  centerTextArea.setBackground(Color.WHITE);
  
  //设计南边的Panel
  frame.add(southPanel, BorderLayout.SOUTH);
  southPanel.add(textField);
  southPanel.add(sendButton);
  southPanel.add(clearButton);
  southPanel.add(logButton);
  southPanel.add(shakeButton);
  
  textField.setText("");
  
  frame.setVisible(true);
 }


}