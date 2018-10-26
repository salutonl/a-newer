package com.example.doge.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;



public class SocketUtil {
    private int port = 6666;
    private int SUCCESS= 1;
    private int INTERNET_ERROR=0;
    private int SERVER_ERROR=-1;
    private int CODE_ERROR=-2;
    //private String ip = "47.93.252.130";
    private String ip = "192.168.43.181";
    public SocketUtil() {

    }

    public SocketUtil(int port) {
        this.port = port;
    }

    public SocketUtil(String ip ,int port) {
        this.port = port;
        this.ip = ip;
    }

    //发送数据给服务器，且不返回数据到原活动
    public synchronized void send(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //建立客户端socket连接，指定服务器位置及端口
                    Socket C_socket = new Socket(ip, port);
                    //socket的输入流和输出流
                    java.io.OutputStream os = C_socket.getOutputStream();
                    BufferedWriter C_BW = new BufferedWriter(new OutputStreamWriter(os));
                    InputStream is = C_socket.getInputStream();
                    BufferedReader C_br = new BufferedReader(new InputStreamReader(is));
                    //对socket进行读写操作
                    C_BW.write(message);
                    C_BW.flush();
                    C_br.close();
                    is.close();
                    C_BW.close();
                    os.close();
                    C_socket.close();
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

    //    向服务器查询数据，且将服务器发回的字符串通过handler传回去
//    初始化时需传入发送的消息与用来响应返回数据的handler
    public synchronized void inquire (final String message, final Handler mHandler) {

        new Thread(new Runnable() {
            @Override

            public void run() {



                try {
                    //建立客户端socket连接，指定服务器位置及端口
                    Socket C_socket = new Socket(ip, port);
                    //socket的输入流和输出流
                    OutputStream os = C_socket.getOutputStream();
                    BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                    //对socket进行读写操作
                    String send_message = new String(message.getBytes("UTF-8"));
                    BW.write(send_message);
                    BW.flush();
                    String receive = "";
                    String content;
                    InputStream is = C_socket.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                    while ((content = br.readLine()) != null) {
                        receive += content;
                    }
                    Message msg = new Message();
                    msg.obj= receive;
                    mHandler.sendMessage(msg);
                    //Log.e("信息",msg.toString());
                    br.close();
                    is.close();
                    BW.close();
                    os.close();
                    C_socket.close();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    System.out.println("UnknownHostException e");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("IOException e");
                }


            }
        }).start();


    }


//    //发送数据给服务器，且保存到sharedpreference（这个可有可无，为了方便写的）
//    public synchronized void sendMessage(final String message, final BaseActivity login,final Handler mHandler) {
//
//        new Thread(new Runnable() {
//            @Override
//
//            public void run() {
//
//
//
//                try {
//                    //建立客户端socket连接，指定服务器位置及端口
//                    Socket C_socket = new Socket("123.206.193.239", port);
//                    //socket的输入流和输出流
//                    OutputStream os = C_socket.getOutputStream();
//                    BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
//                    InputStream is = C_socket.getInputStream();
//                    BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
//                    //对socket进行读写操作
//                    String send_message = new String(message.getBytes("UTF-8"));
//                    BW.write(send_message);
//                    BW.flush();
//                    String receive = "";
//                    String content;
//                    while ((content = br.readLine()) != null) {
//                        receive += content+'\n';
//                    }
//                    String [] result =  receive.split("\n");
//                    if (result[0].equals("操作成功")) {
//                        mHandler.sendEmptyMessage(SUCCESS);
//                        if (result.length>1)
//                        {
//                            SharedPreferences sp = login.getSharedPreferences("user", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sp.edit();
//                            editor.putString("name",result[1] );//键值对
//                            editor.commit();
//                        }
//                    } else
//                        mHandler.sendEmptyMessage(CODE_ERROR);
//
//                    br.close();
//                    is.close();
//                    BW.close();
//                    os.close();
//                    C_socket.close();
//                } catch (UnknownHostException e) {
//                    e.printStackTrace();
//                    System.out.println("UnknownHostException e");
//                    mHandler.sendEmptyMessage(SERVER_ERROR);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    System.out.println("IOException e");
//                    mHandler.sendEmptyMessage(INTERNET_ERROR);
//                }
//
//
//            }
//        }).start();
//
//
//    }
//


}