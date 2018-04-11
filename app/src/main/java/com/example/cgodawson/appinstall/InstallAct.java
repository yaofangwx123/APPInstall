package com.example.cgodawson.appinstall;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cgodawson.touch.TouchAct;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Arrays;

public class InstallAct extends AppCompatActivity {
  private boolean enable = true;
  private TextView textView;
  private boolean enableClear = true;
  private String fileArr;
  private String hostIP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (ActivityCompat.checkSelfPermission(InstallAct.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10088);

        }
        else{
            startRec();
        }
        try {
            ((TextView)findViewById(R.id.version)).setText("版本:"+ getPackageManager().getPackageInfo(this.getPackageName(),0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        textView = (TextView) findViewById(R.id.text);
        Button fab = (Button) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(InstallAct.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    setText("请打开存储权限", Color.RED);
                    return;
                }
                else if(!enable){
                    dialog("请不要重复监听");
                    return;
                }
                setText("正在等待连接...", Color.BLUE);
                startRec();
            }
        });

        findViewById(R.id.clearcach).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  if(!enableClear){
                    dialog("文件接收中不能清理！");
                    return;
                }
              new ClearCachTask(InstallAct.this,v).execute();
            }
        });
        findViewById(R.id.firematch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FireMatchTask(InstallAct.this,v).execute();
            }
        });
        findViewById(R.id.touch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(InstallAct.this, TouchAct.class));
            }
        });

        findViewById(R.id.freefiles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     Intent intent = new Intent(InstallAct.this,FreeAct.class);
                     intent.putExtra("free_files",fileArr);
                     intent.putExtra("hostIP",hostIP);
                     startActivity(intent);

            }
        });


    }

    private void dialog(final String msg)
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(InstallAct.this).setTitle("温馨提示").setMessage(msg).show();
            }
        });

    }
    private void setText(final String text_, final int color)
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                textView.setTextColor(color);
                textView.setText(text_);
            }
        });
    }
  private void startRec()
  {
      enable = false;
      new Thread(new Runnable() {
          @Override
          public void run() {
              try {

                  byte[] buf = new byte[8096];
                  DatagramPacket packet = new DatagramPacket(buf, buf.length);
                  DatagramSocket responseSocket = new DatagramSocket(12596);
                  responseSocket.receive(packet);
                  hostIP = packet.getAddress().getHostAddress();

                  //test send

                 /* byte[] buf2 = "I receive the message2".getBytes();
                  DatagramSocket responseSocket2 = new DatagramSocket(12597);
                  DatagramPacket sendPacket = new DatagramPacket(buf2, buf2.length, packet.getAddress(), 12597);
                  // 发送消息
                  responseSocket2.send(sendPacket)test2;
                  responseSocket2.close();*/

                  String fromIP = packet.getSocketAddress().toString();
                  int index1 = fromIP.indexOf('/');
                  int index2 = fromIP.indexOf(':');
                  fromIP = fromIP.substring(index1+1,index2);
                  final byte[] content = packet.getData();
                  final int serverVersion = Integer.parseInt(new String(content,0,4));
                  final int curversion = getPackageManager().getPackageInfo(InstallAct.this.getPackageName(),0).versionCode;

                   fileArr = new String(Arrays.copyOfRange(content,4,packet.getLength()));

                  String[] files = fileArr.split("#",-1);


                  final String fileName = curversion<serverVersion?"Appinstall_"+serverVersion+".apk":files[0];


                  responseSocket.close();
                  final String finalFromIP = fromIP;

                  new Handler(Looper.getMainLooper()).post(new Runnable() {
                      @Override
                      public void run() {

                          AlertDialog dialog = new AlertDialog.Builder(InstallAct.this).setTitle("温馨提示").setMessage(curversion<serverVersion?"是否将AppInstall升级到新版本:"+serverVersion:"收到服务器推送的文件:\n"+fileName).setPositiveButton("接收", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {


                                  new Thread(new Runnable() {
                                      @Override
                                      public void run() {

                                          try {
                                              enableClear = false;
                                              Socket socket = new Socket(finalFromIP,curversion<serverVersion?12593:12594);
                                              InputStream inputStream = socket.getInputStream();
                                              final File file = new File("/sdcard/install_"+fileName);
                                              if(file.exists())
                                              {
                                                  file.delete();
                                              }
                                              FileOutputStream fileOutputStream = new FileOutputStream("/sdcard/install_"+fileName);
                                              byte[] buff = new byte[4096];
                                              int len = 0;
                                              long size = 0;
                                              while((len=inputStream.read(buff))>0)
                                              {
                                                  fileOutputStream.write(buff,0,len);
                                                  fileOutputStream.flush();
                                                  size+=len;

                                                  setText("正在接收文件:\n   "+(size/1024/1024)+"M",Color.GREEN);

                                              }

                                              fileOutputStream.close();
                                              enable = true;
                                              enableClear = true;
                                              final File file2 = new File("/sdcard/install_"+fileName);
                                              if(file2.exists())
                                              {
                                                  if(file2.length()==0)
                                                  {
                                                      setText("文件接收失败",Color.RED);
                                                      return;
                                                  }
                                              }
                                              else{
                                                  setText("文件接收失败",Color.RED);
                                                  return;
                                              }

                                              new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      setText("文件接收完毕\n文件路径：\n手机存储根目录下 install_"+fileName, Color.GREEN);
                                                      try {

                                                          Intent intent = new Intent();
                                                          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                                              intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                              Uri contentUri = FileProvider.getUriForFile(InstallAct.this, "com.example.cgodawson.appinstal.fileprovider",file2);
                                                              intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                                                          } else {
                                                              intent.setDataAndType(Uri.fromFile(file2), "application/vnd.android.package-archive");
                                                              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                          }
                                                          startActivity(intent);
                                                      }catch (Exception e)
                                                      {
                                                          setText("无法自动安装，请到存储根目录下寻找 install_"+fileName+"手动安装", Color.RED);
                                                      }
                                                  }
                                              });


                                          } catch (final Exception e) {
                                              e.printStackTrace();
                                              enable = true;
                                              enableClear = true;
                                              setText("操作失败", Color.RED);
                                          }


                                      }
                                  }).start();



                              }
                          }).setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  enable = true;
                                  enableClear = true;
                                  setText("状态：未监听服务器", Color.RED);
                              }
                          }).setCancelable(false).show();
                          dialog.setCanceledOnTouchOutside(false);
                      }
                  });

              }catch (Exception e)
              {
                  e.printStackTrace();
              }

          }}).start();
  }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==10088)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                startRec();
            }
        }
    }

    private void toast(final String msg)
  {
      new Handler(Looper.getMainLooper()).post(new Runnable() {
          @Override
          public void run() {

              Toast.makeText(InstallAct.this,msg,Toast.LENGTH_SHORT).show();
          }
      });
  }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_install, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
