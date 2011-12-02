package qdx.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class activity0 extends Activity {
	private static final String	TAG	= "activity0";
	private Intent intent = new Intent();
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.main);
        Button button = (Button) findViewById(R.id.startGame);
        Button help = (Button) findViewById(R.id.help);
        help.setOnClickListener(new Button.OnClickListener() {
        	@Override
        	public void onClick(View arg0){
        		intent.setClass(activity0.this, activity2.class);
        		startActivity(intent);
        		activity0.this.finish();
        	}
        });
		/* 监听button的事件信息 */
		button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String level = null;
				EditText  l = (EditText)findViewById(R.id.level);
				level = l.getText().toString();
				int lv = 1;
				try{
					lv = Integer.parseInt(level);
				}
				catch(NumberFormatException e){
					return;
				}
				if(lv >3) return;
				else{
					intent.setClass(activity0.this, activity1.class);
					intent.putExtra(qdx.game.activity1.Level, lv);
					startActivity(intent);
					activity0.this.finish();
				}
			}
		});
		
		
    }
    
    public void onStart()
	{
		super.onStart();
		Log.v(TAG, "onStart");
	}
	
	public void onResume()
	{
		
		super.onResume();
		Log.v(TAG, "onResume");
	}
	
	public void onPause()
	{//intent.putExtra(qdx.game.activity1.Level, 5);
		super.onPause();
		Log.v(TAG, "onPause");
	}
	
	public void onStop()
	{	
		super.onStop();
		Log.v(TAG, "onStop");
	}

	public void onDestroy()
	{
		super.onDestroy();
		Log.v(TAG, "onDestroy");
	}

	public void onRestart()	{
		
		super.onRestart();
		Log.v(TAG, "onReStart");
	}
}