package WeTalkClient;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.*;

public class SignGui extends JFrame implements MouseListener {
	JPanel u=new JPanel();
	JPanel p1=new JPanel();
	JPanel p2=new JPanel();
	JLabel us1 =new JLabel("User name:");
	JLabel pa1 =new JLabel("Password:");
	JLabel pa2 =new JLabel("Password again:");
    JButton buttonSign = new JButton("Sign up");
    JTextField username=new JTextField(20);
    JTextField password1=new JTextField();
    JTextField password2=new JTextField();
  
	public SignGui() throws IOException
    { 
		this.setTitle("WeTalk 1.1");
    	this.setLayout(null);
		this.setSize(970, 640);
		u.setBounds(100,100,80,20);
		username.setBounds(180,105,80,20);
		u.add(us1);
		add(u);
		add(username);
		
		p1.setBounds(100,140,80,20);
		password1.setBounds(180,145,80,20);
		p1.add(pa1);
		add(p1);
		add(password1);
		
		p2.setBounds(80,180,100,20);
		password2.setBounds(180,185,80,20);
		p2.add(pa2);
		add(p2);
		add(password2);
		
		
		buttonSign.setBounds(450,470,100,100);
		add(buttonSign);
		buttonSign.addMouseListener(this);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
    }	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource()==buttonSign)
		{
			try {
				if(password1.getText().equals(password2.getText()))
				{
				EchoClient ec1=new EchoClient();
				ec1.talk("usersign"+"*"+username.getText()+"*"+password1.getText());
				if(ec1.getChaoshi()==1)
				{
					JOptionPane.showMessageDialog(null, "Oh! Server error! Please try again!", "ERRO", JOptionPane.INFORMATION_MESSAGE);
				}
				if(ec1.signj==1)
	  {JOptionPane.showMessageDialog(null, "Done!", "ok", JOptionPane.INFORMATION_MESSAGE); 
	    this.dispose();
	    GUI n=new GUI();
	  
	    }
				}
				/*else
				{
					JOptionPane.showMessageDialog(null, "Password should be the same!", "ERRO",  JOptionPane.ERROR_MESSAGE);
					password1.setText(" ");
					password2.setText(" ");
				}*/
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
	

}
