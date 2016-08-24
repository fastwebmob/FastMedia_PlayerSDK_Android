package cn.com.fastweb.fastmedia.demo;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.fastweb.fastmedia.demo.SampleListActivity.Parameter;
import cn.com.fastweb.fastmedia.sdk.demo.R;
import cn.com.fastweb.fastmedia.sdk.player.FWLivePlayer;
import cn.com.fastweb.fastmedia.sdk.player.FWLivePlayerCallback;
import cn.com.fastweb.fastmedia.sdk.util.Stats;

@SuppressLint("ClickableViewAccessibility")
public class PlayActivity extends Activity implements SurfaceTextureListener,OnTouchListener{
	
	private static final String TAG = PlayActivity.class.getSimpleName();
	
	private static final int MSG_FADE_OUT = 1;
	
	private static final int MSG_UPDATE_UI = 2;
	
	// For use within demo app code.
	public static final String CONTENT_ID_EXTRA = "content_id";
	public static final String CONTENT_TYPE_EXTRA = "content_type";
	public static final String PROVIDER_EXTRA = "provider";

	private FWLivePlayer player = null;

	private VideoTextureView textureView;
	
	private ImageButton startBtn;
	
	private boolean ismute = false;
    
	private ScrollView statsTxSV; 
	private TextView statsTx; 
	private TextView timeTx;
	
	private float[] scale = {1.0f,0.75f,0.5f};
	
	private int count = -1;
	
	private Parameter parameter;
	
	private LinearLayout mediaControllerLL;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		FrameLayout baseLayout = (FrameLayout)findViewById(R.id.base_layout);
		baseLayout.setOnTouchListener(this);
		
		Intent intent = getIntent();
	    Uri contentUri = intent.getData();
	    parameter = (Parameter) intent.getSerializableExtra("Parameter");
		player = new FWLivePlayer();
		player.registerPhoneListener(this);
		player.setDataSource(contentUri.toString());
		textureView = (VideoTextureView) findViewById(R.id.main_video_texture);
		textureView.setSurfaceTextureListener(this);
		initWithParameter();
		
	    mediaControllerLL = (LinearLayout) findViewById(R.id.media_controller_ll);
	    mediaControllerLL.setVisibility(View.GONE);
		
		player.prepareAsync();
		player.setFWLivePlayerListener(new FWLivePlayerCallback() {
			@Override
			public void onPrepared(FWLivePlayer mp) {
				player.start();
				mediaControllerLL.setVisibility(View.VISIBLE);
				
				handler.sendEmptyMessageDelayed(MSG_FADE_OUT, 5000);
			}
			
			@Override
			public void onVideoSizeChanged(FWLivePlayer mp, int width, int height) {
				textureView.setVideoWidthHeightRatio(width/(height *1.0f));
			}
			
			@Override
			public void onError(FWLivePlayer mp, int code, String reason) {
				Toast.makeText(PlayActivity.this, reason, Toast.LENGTH_SHORT).show();
			}

		});
	    
	    startBtn = (ImageButton) findViewById(R.id.start_btn);
	    startBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(player.isPlaying()){
					player.stop();
				} else {
					initWithParameter();
					player.prepareAsync();
				}
			}
		});
	    
	    timeTx = (TextView) findViewById(R.id.time_tx);
	    
	    Button muteBtn = (Button) findViewById(R.id.mute_btn);
	    muteBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ismute = !ismute;
				if (ismute) {
					player.unmute();
				} else {
					player.mute();
				}
			}
		});
	    
	     statsTx = (TextView) findViewById(R.id.stats_tx);
	     statsTxSV = (ScrollView) findViewById(R.id.stats_tx_sv);
	     statsTxSV.setVisibility(View.GONE);
	     ImageButton  infoBtn = (ImageButton) findViewById(R.id.info_btn);
	     infoBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				statsTxSV.setVisibility(statsTxSV.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
			}
		});
	     
	     ImageButton  resizeBtn = (ImageButton) findViewById(R.id.resize_btn);
	     resizeBtn.setOnClickListener(new View.OnClickListener() {
				
			@Override
			public void onClick(View v) {
				count++;
				if(count%4 == 3){
					textureView.setScaleX(parameter.scale);
					textureView.setScaleY(parameter.scale);
				}else{
					textureView.setScaleX(scale[count%4]);
					textureView.setScaleY(scale[count%4]);
				}
			}
		});
	     
	     ImageButton fullscreenBt = (ImageButton) findViewById(R.id.fullscreen_btn);
	     fullscreenBt.setOnClickListener(new View.OnClickListener() {
				
			@Override
			public void onClick(View v) {
				if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}else{
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				}
			}
		});
	     
	     findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	     
	     timer.schedule(task,1000,1000);
	}
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			if(getActionBar() != null){
				getActionBar().hide();
			}
		}else{
			if(getActionBar() != null){
				getActionBar().show();
			}
		}
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
		player.onSurfaceTextureAvailable(surface, width, height);
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		return player.onSurfaceTextureDestroyed(surface);
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
		
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
		
	}

	@Override
	public void finish() {
		Log.d(TAG, "finish.........");
		if (player != null) {
			player.stop();
			player.release();
		}
		super.finish();
	}
	
    private void initWithParameter(){
    	if(parameter == null){
    		Log.e(TAG, "parameter is null");
    		return;
    	}
    	player.setEnableFastOpen(parameter.fristImage);
    	player.setCacheTime(parameter.cachime);
		textureView.setScaleX(parameter.scale == 0 ? 1 : parameter.scale);
		textureView.setScaleY(parameter.scale == 0 ? 1 : parameter.scale);
		player.setVolume(parameter.volume);
		
		player.setRetryTime(parameter.recount);
		player.setConnectTimeout(parameter.timeout);
		player.setMaxBeginTimeout(parameter.maxbeginTime);
		player.setMaxStuckDuration(parameter.stuckTime);
		player.setEnableBackgroundPlaying(parameter.enableBackgrounPlay);
    }

	
	private Timer timer = new Timer();
	private TimerTask task = new TimerTask( ) {

	    public void run ( ) {
	        Message message = new Message( );
	        message.what = MSG_UPDATE_UI;
	        handler.sendMessage(message);
	    }

	};

	private final Handler handler = new Handler( ) {

	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	            case MSG_UPDATE_UI:
	            	statsTx.setText(formatStatsInfo());
	            	timeTx.setText(generateTime(player.getCurrentPosition()));
	            	if(player.isPlaying()){
	            		startBtn.setImageResource(R.drawable.ic_media_pause);
	            	}else {
	            		startBtn.setImageResource(R.drawable.ic_media_play);
	            	}
	                break;
	            case MSG_FADE_OUT:
	            	mediaControllerLL.setVisibility(View.GONE);
	                break;
	        }
	        super.handleMessage(msg);
	    }
	};
	
	protected void onDestroy ( ) {
	    if (timer != null) {
	        timer.cancel( );
	        timer = null;
	    }
	    super.onDestroy( );
	}
	
    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
}
	
	
	@Override
	public void onBackPressed() {
		if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			return;
		}
		super.onBackPressed();
	}
	
	
    private int getScreenOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }
    
    public String formatStatsInfo() {
		return  "Duration(current - begin) (ms): " + player.getPlayDuration()  +"\n"
				+ "Flv loaded: audio =" + Stats.currentLoadedAudioFlvTags + " video = " + Stats.currentLoadedVideoFlvTags  +"\n"
				+ "Flv remained: aac =" + player.getAudioBufferedSize() + " h264 = " + player.getVideoBufferedSize()  +"\n"
				+ "H264NalAllLoadNum = " + Stats.H264NalAllLoadNum + " invaild = " + Stats.H264NalAllInvaildNum  + "used = " + Stats.H264NalAllUsedNum +"\n"
				+ "H264CurrentPictures_dropped = " + Stats.H264CurrentPictures_dropped + " all = " + Stats.H264CurrentPictures_all +"\n"
				+ "Network AVG Rate(KB): media = " + Stats.getFormatMediaNetworkRate() + " audio = " + Stats.getFormatAudioNetworkRate() + " video = " + Stats.getFormatVideoNetworkRate() +"\n"
				+ "Realtime Rate(KB) = " + Stats.formatSpeed() +"\n"
				+ "First TCP packet(ms) = " + Stats.getFirstTcpPacketTime() +"\n"
				+ "First flv timestamp(ms) audio packet = " + Stats.getFirstAudioPacketTime() + " video packet = " + Stats.getFirstVideoPacketTime() +"\n"
				+ "First picture timestamp = " +  Stats.getFirstVideoFrameTime()  +"\n"
				+ "Cache bufferd spend time = " + Stats.bufferspendTime +"\n"
				+ "Real FPS: " + Stats.fps +"\n"
				+ "System OS: " + Stats.getSystemOS() + "\n"
				+ "Device model: " + Stats.getDeviceModel() + "\n"
				+ "Server IP: " + Stats.serverIp + "\n"
				+ "Local IP: " + Stats.getLocalIp(this) + "\n"
				+ "DNS：" + Stats.getLocalDns() + "\n"
				+ "NetWork type：" + Stats.getNetType(this) + "\n"
				+ "Reconnecting remained times: " + player.getRemainedReconnectTimes() + "\n"
				+ "Player status: " + FWLivePlayer.playerState + "\n"
				+ "Stuck times: " + Stats.stuckTimes + "\n"
				+ "Stuck duration(ms): " + Stats.stuckDuration + "\n"
				+ "Cache time(ms): " + player.getBufferedTime() + "\n"
				+ "Stuck percent(%): " + Stats.getStuckPercent()  + "\n"
				+ "Connecting Failed percent(ms): " + player.getConnectFailPercent() + "\n"
				+ "Flv Metadata :" + player.getMetadata();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.base_layout:
			handler.removeMessages(MSG_FADE_OUT);
			mediaControllerLL.setVisibility(View.VISIBLE);
			handler.sendEmptyMessageDelayed(MSG_FADE_OUT, 5000);
			break;

		default:
			break;
		}
		return false;
	}
	
}
