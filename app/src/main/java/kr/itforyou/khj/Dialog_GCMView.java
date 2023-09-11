package kr.itforyou.khj;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class Dialog_GCMView extends Activity {
	
	private TextView m_popupTitle = null;
	private WebView m_popupMsg = null;
	private Button m_popupOpenApp = null;
	private Button m_popupClose = null;
	
	private boolean autohide = false;

    private PowerManager m_PowerManager;
    private WakeLock m_WakeLock;
    private static final int m_nAutoHide = 100;
    private int m_nTimeOut = 5000;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);      
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.popup_gcm_dialog);

        String title = getIntent().getStringExtra("title");
        String msg = getIntent().getStringExtra("msg");
        String button_string = getIntent().getStringExtra("button_string");
        final String url = getIntent().getStringExtra("url");
        autohide = getIntent().getBooleanExtra("autohide", false);
        
        if(title == null || msg == null){
        	finish();
        	return;
        }

        m_popupTitle = (TextView)findViewById(R.id.popupTitle);
        m_popupMsg = (WebView)findViewById(R.id.popupMsg);
        m_popupOpenApp = (Button)findViewById(R.id.popupOpenApp);
        m_popupClose = (Button)findViewById(R.id.popupClose);
        
        m_popupTitle.setText(Html.fromHtml(title));
        m_popupMsg.loadDataWithBaseURL("", msg, "text/html", "UTF-8", null);
        
        m_popupClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Dialog_GCMView.this.finish();
			}
		});
        
        findViewById(R.id.popupAutoHideCancelLayout).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				autohide = false;
				IHandler.removeMessages(m_nAutoHide);
			}
		});
        
        if(button_string != null && button_string.length() > 0 && url != null && url.length() > 0){
        	m_popupOpenApp.setText(button_string);
            m_popupOpenApp.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				IHandler.removeMessages(m_nAutoHide);


    				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    				startActivity(intent);
    				finish();
    			}
    		});
        }
        else{
        	m_popupOpenApp.setVisibility(View.GONE);
        }
	}
	
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

        String title = getIntent().getStringExtra("title");
        String msg = getIntent().getStringExtra("msg");
        String button_string = getIntent().getStringExtra("button_string");
        final String url = getIntent().getStringExtra("url");

        if(title == null || msg == null){
        	finish();
        	return;
        }
        
        m_popupTitle.setText(Html.fromHtml(title));
        m_popupMsg.loadDataWithBaseURL("", msg, "text/html", "UTF-8", null);

        try {

            if(m_WakeLock == null){
                m_PowerManager = (PowerManager) getSystemService(POWER_SERVICE);
                m_WakeLock = m_PowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, getClass()
                        .getName());
            }
            	
            m_WakeLock.acquire();
		} catch (Exception e) {
        	m_WakeLock = null;
			e.printStackTrace();
		}

		IHandler.removeMessages(m_nAutoHide);
        if(autohide){
        	IHandler.sendEmptyMessageDelayed(m_nAutoHide, m_nTimeOut);
        }
        else{
			IHandler.removeMessages(m_nAutoHide);
        }

        
        if(button_string != null && button_string.length() > 0 && url != null && url.length() > 0){
        	m_popupOpenApp.setText(button_string);
            m_popupOpenApp.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				IHandler.removeMessages(m_nAutoHide);


    				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    				startActivity(intent);
    				finish();
    			}
    		});
        }
        else{
        	m_popupOpenApp.setVisibility(View.GONE);
        }
	}

	@Override
	public void onBackPressed() {
    	super.onBackPressed();
	}
	
	@Override
	public void onStart(){
		super.onStart();

        try {

            if(m_WakeLock == null){
                m_PowerManager = (PowerManager) getSystemService(POWER_SERVICE);
                m_WakeLock = m_PowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, getClass()
                        .getName());
            }
            	
            m_WakeLock.acquire();
		} catch (Exception e) {
        	m_WakeLock = null;
			e.printStackTrace();
		}

		IHandler.removeMessages(m_nAutoHide);
        if(autohide){
        	IHandler.sendEmptyMessageDelayed(m_nAutoHide, m_nTimeOut);
        }
        else{
			IHandler.removeMessages(m_nAutoHide);
        }
	}
	
    @Override
    protected void onPause() {
        super.onPause();

        try {
        	if(m_WakeLock != null)
        		m_WakeLock.release();
        	m_WakeLock = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		IHandler.removeMessages(m_nAutoHide);
    }
	
    @Override
    protected void onResume() {
        super.onResume();

		IHandler.removeMessages(m_nAutoHide);
        if(autohide){
        	IHandler.sendEmptyMessageDelayed(m_nAutoHide, m_nTimeOut);
        }
        else{
			IHandler.removeMessages(m_nAutoHide);
        }
    }

	private Handler IHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case m_nAutoHide:	//TimerClose
				{
					finish();
				}
				break;
			}
			return false;
		}
	});


}
