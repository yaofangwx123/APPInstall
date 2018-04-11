package com.example.cgodawson.appinstall;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import java.io.File;

/**
 * Created by CG_Dawson on 2018/3/23.
 */

public class ClearCachTask extends AsyncTask<Void,String,Void> {
    private AlertDialog dialog;
    private Context mContext;
    private int mDeleteCount;
    private View mBoundView;
    public ClearCachTask(Context context,View view)
    {
        mContext = context;
        mBoundView = view;
    }
    @Override
    protected Void doInBackground(Void... voids) {

        File file = new File("/sdcard");
        if(!file.exists())
        {
            return null;
        }
        File[] files = file.listFiles();
        if(files==null)
        {
            return null;
        }
        for(File f:files)
        {
            if(f.getName().startsWith("install_"))
            {
                publishProgress(f.getName());
                f.delete();
                mDeleteCount++;
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mBoundView.setEnabled(false);
        dialog = new AlertDialog.Builder(mContext).setTitle("缓存清理").setMessage("正在计算").create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mBoundView.setEnabled(true);
        dialog.dismiss();
        Toast.makeText(mContext,"一共清理了【"+mDeleteCount+"】",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        dialog.setMessage("正在删除:\n"+values[0]);
    }


}
