package WeTalkClient;

import java.net.InetAddress;

public class User {
private String username;
private InetAddress userIP;
private int userPort;
public User(String username,InetAddress userIP,int userPort)
{
  this.userIP=userIP;
  this.username=username;
  this.userPort=userPort;
}
public String getName()
{
	return username;
			}
public InetAddress getIp()
{
	return userIP;
			}
public int getPort()
{
	return userPort;
			}

}
