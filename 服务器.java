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
            //1.����һ����������Socket����ServerSocket��ָ���󶨵Ķ˿ڣ��������˶˿�
            ServerSocket serverSocket=new ServerSocket(3333);
			ServerSocket serverSocket2=new ServerSocket(2222);
            Socket socket=null;
			final Socket socket2=serverSocket2.accept();
            System.out.println("***�����������������ȴ��ͻ��˵�����***");
            //ѭ�������ȴ��ͻ��˵�����
            while(true){
				
                //����accept()������ʼ�������ȴ��ͻ��˵�����
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