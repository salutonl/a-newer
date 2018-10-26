package xiaoche;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Time;

import javax.net.ssl.SSLException;

import java.net.InetAddress;

public class xiaoche {
	
	public static void main (String[] args) throws InterruptedException {
		
		try {
            //1.创建一个服务器端Socket，即ServerSocket，指定绑定的端口，并监听此端口
            ServerSocket serverSocket=new ServerSocket(3333);
			ServerSocket serverSocket2=new ServerSocket(2222);
            Socket socket=null;
			final Socket socket2=serverSocket2.accept();
            System.out.println("***服务器即将启动，等待客户端的连接***");
            //循环监听等待客户端的连接
            while(true){
				
                //调用accept()方法开始监听，等待客户端的连接
                socket=serverSocket.accept();
                BufferedReader BIS = new BufferedReader(new InputStreamReader(socket.getInputStream()));			
				
                String[] BIS_String= {""};
					String content;
					while ((content=BIS.readLine())!=null)
					{
						BIS_String[0] += content;
					}
					System.out.println(BIS_String[0]);
				if(BIS_String!=null)
				{
					new Thread(new Runnable() {
			            @Override
			            public synchronized  void run() {
			                try {
			                	BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(socket2.getOutputStream()));
			                	BufferedReader BIS2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
			                	BW.write(BIS_String[0]);
								BW.flush();
								System.out.println(BIS2.readLine());
								
			                } catch (UnknownHostException e) {
			                    e.printStackTrace();
			                    System.out.println("IOException e");
			                } catch (IOException e) {
			                    e.printStackTrace();
			                    System.out.println("IOException e");
			                }
			               

			            }
			        }).start();
					
					
				}
				socket.close();	
                
            }
		}
         catch (IOException e) {
            e.printStackTrace();
        }
	
				  
}
}