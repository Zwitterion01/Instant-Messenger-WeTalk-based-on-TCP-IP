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

//UDPЭ��������GUI������,��ӭ��λ��������ָ��
public class GuiChat {
 private Frame frame = new Frame("WeTalk1.1");  //��Ҫ�ĳ�Ա����
 private Panel centerPanel = new Panel();
 private Panel southPanel = new Panel();
 private TextArea centerTextArea = new TextArea();
 private TextArea southTextArea = new TextArea(5, 1);  //5��1��,���������߽�
 private TextField textField = new TextField(20);
 private Button sendButton = new Button("����"); 
 private Button clearButton = new Button("���");
 private Button logButton = new Button("�����¼");
 private Button shakeButton = new Button("����");
 ArrayList<User> userlist;
 private DatagramSocket socket;
 private Lock lock = new ReentrantLock();
 private Writer writer;
 
 public GuiChat() throws IOException{     //�¼�����
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

 private void inti() {             //���쳣����
   try {
   userlist=new ArrayList<User>(); 
   socket = new DatagramSocket();
   writer = new BufferedWriter(new FileWriter("log.txt", true));
  } catch (IOException e) {
   
   throw new RuntimeException();
  }
  
 }

 private void handleEvent() {  
  //���ڼ�����
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
  //���Ͱ�ť������
  sendButton.addMouseListener(new MouseAdapter(){

   @Override
   public void mousePressed(MouseEvent e) {
    send();
   }
   
  });
  
  //�ϱ����������
  /*southTextArea.addKeyListener(new KeyAdapter(){

   @Override
   public void keyPressed(KeyEvent e) {
    if(e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown())
     send();
    e.consume(); // ��ָ����ʾ�ڵ�һ��
   }
   
  });
  */
  //��ռ�����
  clearButton.addMouseListener(new MouseAdapter(){

   @Override
   public void mousePressed(MouseEvent e) {
    centerTextArea.setText("");
   }
   
  });
  
  //��־������
  logButton.addMouseListener(new MouseAdapter(){

   @Override
   public void mousePressed(MouseEvent e) {
    showLog();
   }
   
  });
  
  //�𶯼�����
  shakeButton.addMouseListener(new MouseAdapter(){

   @Override
   public void mousePressed(MouseEvent e) {
    sendShake();
   }
   
  });
 }
 
 //�������,���������ܳ����ڷ��͵�������,���ѡһ����������ִ���
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
 
 //�����¼����
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
 
 //������Ϣ����
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
   sendDate(ip, message.getBytes()); //����һ���������ݵķ���
   //�����п��ܻ������,�����ݻ�û�д�ӡ����,�����յ������ݾ���ʾ������,��Ҫʹ��ͬ��
   for(int x=1;x<=userlist.size();x++)
   {
	   if(userlist.get(x-1).getIp().toString().substring(1).equals(ip))
	   {
		   ip=userlist.get(x-1).getName();                                  //show the message of user you send
		   break;
	   }
   }
   String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
   String content = time + " �Ҷ� " + ip + " ˵: " + "\r\n" + message + "\r\n\r\n";
   centerTextArea.append(content);
   writer.write(content);
   southTextArea.setText("");
  } catch (Exception e) {
   throw new RuntimeException();
  }finally{
  lock.unlock();
  }
 }
 
 //������Ϣ���ݷ���
 private void sendDate(String ip, byte[] bytes){
  
  try {
   DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(ip), 6000);
   socket.send(packet);
  } catch (IOException e) {
   
   throw new RuntimeException(e);
  }
  
 }
 
 //�����µ��߳�����������Ϣ
 class newThread extends Thread{

  @Override
  public void run() {
   try {
	   
	   
	   System.out.println("thread up");
    // ʹ��UDPЭ���������
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
   content = time + " "+  ip + " ����˵: " + "\r\n" + message + "\r\n\r\n";*/
    	 for(int i=1;i<=userlist.size();i++)
        {  
    		
    	   if(packet.getAddress().equals(userlist.get(i-1).getIp())) 
    	   {   
    		   content = time + " "+  userlist.get(i-1).getName() + " ����˵: " + "\r\n" + message + "\r\n\r\n";
    		   break;
    	   }
        }
    	
    	 }
     lock.lock();
     try{
    	
     centerTextArea.append(content);  //��ʾ��centerTextArea��
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
 
 //��������
 public void doShake() {
  int x = frame.getLocation().x;
  int y = frame.getLocation().y;
  // frame.toFront();   // �п�����Ч
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
 
 //ͼ�ν��淽��
 private void generateGUI() {
  //����frame
  frame.setSize(970, 640);
  frame.setResizable(false);
  frame.setLocationRelativeTo(null);
  //����м��Panel
  frame.add(centerPanel, BorderLayout.CENTER);
  centerPanel.setLayout(new BorderLayout());
  centerPanel.add(centerTextArea,BorderLayout.CENTER);
  centerPanel.add(southTextArea, BorderLayout.SOUTH);
  
  //southTextArea.setEditable(true);
  centerTextArea.setEditable(false);
  centerTextArea.setBackground(Color.WHITE);
  
  //����ϱߵ�Panel
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