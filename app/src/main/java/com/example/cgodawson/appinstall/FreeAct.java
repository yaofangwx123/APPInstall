package com.example.cgodawson.appinstall;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class FreeAct extends Activity {
   private List<String> data;
   private ArrayAdapter<String> adapter;
   private ListView listView;
   private String hostIP;
   private AlertDialog loadingDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.freefilelayout);
        listView = findViewById(R.id.freelist);
        data = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this,R.layout.freeitem,data);
        listView.setAdapter(adapter);
        Intent intent = getIntent();
        hostIP = intent.getStringExtra("hostIP");
        String free_files = intent.getStringExtra("free_files");
        String[] files = free_files.split("#",-1);


        loadingDialog = new AlertDialog.Builder(FreeAct.this).setTitle("下载提示").setMessage("").setCancelable(false).create();
        loadingDialog.setCanceledOnTouchOutside(false);
        for(String s:files)
        {
            if(!s.isEmpty())
            {
                data.add(s);
            }
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String name = data.get(i);

                AlertDialog dialog = new AlertDialog.Builder(FreeAct.this).setTitle("下载提示").setMessage("是否下载文件\n"+name)
                        .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showLoading();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            DatagramSocket socket = new DatagramSocket(12597);
                                            byte[] data = name.getBytes();
                                            DatagramPacket packet = new DatagramPacket(data, data.length,InetAddress.getByName(hostIP),12597);
                                            socket.send(packet);
                                            socket.close();
                                            ServerSocket serverSocket = new ServerSocket(12599);
                                            Socket recSocket = serverSocket.accept();
                                            InputStream inputStream = recSocket.getInputStream();
                                            final File file = new File("/sdcard/install_"+name);
                                            if(file.exists())
                                            {
                                                file.delete();
                                            }
                                            FileOutputStream fileOutputStream = new FileOutputStream("/sdcard/install_"+name);
                                            byte[] buff = new byte[4096];
                                            int len = 0;
                                            long size = 0;
                                            while((len=inputStream.read(buff))>0)
                                            {
                                                fileOutputStream.write(buff,0,len);
                                                fileOutputStream.flush();
                                                size+=len;

                                                updateLoading("正在接收 :"+name+"\n   "+(size/1024/1024)+"M");

                                            }

                                            fileOutputStream.close();
                                            recSocket.close();
                                            serverSocket.close();
                                            closeLoading();
                                            toast("下载 "+name+" 成功！");
                                        }catch (Exception e)
                                        {
                                            e.printStackTrace();
                                            toast("下载 "+name+" 失败...");
                                            closeLoading();
                                        }
                                    }
                                }).start();
                            }
                        }).setNegativeButton("取消",null).setCancelable(false).create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
    }
  private void updateLoading(final String msg)
  {
      runOnUiThread(new Runnable() {
          @Override
          public void run() {
              loadingDialog.setMessage(msg);
          }
      });
  }
  private void closeLoading()
  {
      runOnUiThread(new Runnable() {
          @Override
          public void run() {
              loadingDialog.dismiss();
          }
      });
  }
    private void showLoading()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog.show();
            }
        });
    }
    private void toast(final String msg)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FreeAct.this,msg,Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
