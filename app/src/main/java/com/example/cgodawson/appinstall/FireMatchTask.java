package com.example.cgodawson.appinstall;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by CG_Dawson on 2018/3/23.
 */

public class FireMatchTask extends AsyncTask<Void,String,Void> {
    private AlertDialog dialog;
    private Context mContext;
    private int mDeleteCount;
    private View mBoundView;
    public FireMatchTask(Context context, View view)
    {
        mContext = context;
        mBoundView = view;
    }
    @Override
    protected Void doInBackground(Void... voids) {

        getConnectDevice();
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mBoundView.setEnabled(false);
        dialog = new AlertDialog.Builder(mContext).setTitle("清除蓝牙配对").setMessage("正在计算").create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mBoundView.setEnabled(true);
        dialog.dismiss();
        Toast.makeText(mContext,"一共解除了【"+mDeleteCount+"】个设备",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        dialog.setMessage("正在解除:\n"+values[0]);
    }

    private void getConnectDevice()
    {


        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        //Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter的Class对象
        try {//得到连接状态的方法
           /* Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
            method.setAccessible(true);
            int state = (int) method.invoke(adapter, (Object[]) null);
*/
                Set<BluetoothDevice> devices = adapter.getBondedDevices();

                for(BluetoothDevice device : devices){
                    //Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);


                    boolean res = removeBond(device);


                    if(!res)
                    {
                        publishProgress(device.getName()+"【失败】");
                        Thread.sleep(2000);
                        continue;
                    }
                    publishProgress(device.getName()+"【成功】");
                    mDeleteCount++;
                   /* boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                    if(isConnected){
                        publishProgress(device.getName());
                        removeBond(device);
                        mDeleteCount++;
                        break;
                    }*/
                }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private boolean removeBond(BluetoothDevice btDevice) {
        try {
            Method removeBondMethod = BluetoothDevice.class.getMethod("removeBond");
            Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
            return returnValue.booleanValue();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
       return false;

    }
}
