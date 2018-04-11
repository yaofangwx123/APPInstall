package com.example.cgodawson.touch;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.cgodawson.appinstall.R;

/**
 * Created by CG_Dawson on 2017/12/21.
 */

public class MPActivity extends Activity {
    public  View parent;
    public  TextView result;
    private MPaint mPaint;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mview);
        parent = findViewById(R.id.showparent);
        result = findViewById(R.id.result);
        mPaint = findViewById(R.id.mpaint);
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.seelog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText(Html.fromHtml(mPaint.getSB()));
                parent.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.clearlog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPaint.clearLog();
                parent.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.clearmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPaint.clearMap();
            }
        });
        hideBottomUIMenu();

    }
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    @Override
    public void onBackPressed() {
        if(parent.getVisibility()==View.VISIBLE)
        {
            parent.setVisibility(View.GONE);
        }
        else{
            finish();
        }
    }
}
