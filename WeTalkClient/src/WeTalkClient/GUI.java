package WeTalkClient;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.*;

public class GUI extends JFrame implements MouseListener{
	JPanel user=new JPanel();
	JPanel pass=new JPanel();
	JLabel usern=new JLabel("Username: ");
	JLabel passw=new JLabel("Password: ");
	JTextField u1=new JTextField();
	JPasswordField p1=new JPasswordField();
    JLabel text = new JLabel();
    JButton buttonLog = new JButton("LOG IN");
    JButton buttonSign = new JButton("SIGN UP");
   
    public GUI() throws IOException
    {   	
    	this.setTitle("WeTalk 1.1");
    	this.setLayout(null);
		this.setSize(970, 640);
		user.setBounds(200,100,80,50);
		user.add(usern);
		add(user);
		u1.setBounds(280,105,80,20);
		add(u1);
		pass.setBounds(200,150,80,50);
		pass.add(passw);
		add(pass);
		p1.setBounds(280,155,80,20);
		add(p1);
		buttonLog.setBounds(200,400,100,100);
		add(buttonLog);
	    buttonLog.addMouseListener(this);
		buttonSign.setBounds(600,400,100,100);
		add(buttonSign);
		buttonSign.addMouseListener(this);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
    }
	
    public static void main(String[] args) {
	try {
		GUI j=new GUI();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource()==buttonSign)
		{   
			this.dispose();
			try {
				SignGui k=new SignGui();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if(e.getSource()==buttonLog)
		{
			try {String p=new String(p1.getPassword());
			 EchoClient ec1=new EchoClient();
				ec1.talk("userlog"+"*"+u1.getText()+"*"+p);
				if(ec1.getChaoshi()==1)
				{
					JOptionPane.showMessageDialog(null, "Oh! Server error! Please try again!", "ERRO", JOptionPane.INFORMATION_MESSAGE);
				}
	  if(ec1.getJudge()==1)
		{JOptionPane.showMessageDialog(null, "Done!", "ok", JOptionPane.INFORMATION_MESSAGE);
       	this.dispose();
       	GuiChat l=new GuiChat();

	  } 
				//else
				//{
			//		JOptionPane.showMessageDialog(null, "Password or Password error!", "ERRO",  JOptionPane.ERROR_MESSAGE);
				//}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	  public static int receiveStr(int str){  
	      return   str;
	  } 
}
