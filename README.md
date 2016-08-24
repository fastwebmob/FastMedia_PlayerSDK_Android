#简介
快网FastMediaPlayer提供基于智能FastMedia调度系统的高效可靠的传输、转码、分发的一站式直播点播服务平台，目前支持Http＋Flv的流媒体直播。提供播放器SDK，具有低延迟、高安全、高并发、易接入、多终端、控制接口多、统计信息详细等特点;
##亮点介绍
    1 具有低延迟、低卡顿、播放流畅的特点。
    2 平台适应性：android、iOS、PC  
    3 播放场景定制功能  
	  3.1 支持首帧渲染  
      3.2 支持缓存功能
      3.3 后台中断播放  
    4 播放卡顿智能调整功能
      4.1 支持卡顿暂停
      4.2 支持最大卡顿时间设置
      4.3 支持卡顿信息统计功能
    5 网络监控
      5.1 支持超时时间设置
      5.2 支持重连次数设置
      5.3 实时监控网络状态
    6 灵活的播放视图层
      6.1 提供一站式播放控制器快速集成
      6.2 提供灵活的播放界面，可以灵活实现画中画等各种特效
    7 其它特点
      7.1 支持静音，音量百分比调节功能
      7.2 提供大量播放状态统计信息
      7.3 硬解码，降低cpu功耗和发热
##FastMediaPlayer SDK概述
	低延时直播体验，配合快网FastMedia调度系统，可以享受视频加速服务
	支持android 4.1以上版本
	支持Http＋flv（封装aac＋h264）格式
	小于200KB的超轻量级直播sdk；

##FWPlayer API
###Player Status
	public enum STATE{
		ERROR,         // 播放异常             
		READY,         // 播放器准备就绪          
		CONNECTING,    // 正在与媒体服务器建立连接     
		BUFFERING,     // 正在缓冲网络媒体数据       
		PLAYING,       // 播放器正在播放           
		REBUFFERING,   // 播放过程中缓冲网络媒体数据    
		STOPING,       // 播放器正在停止          
		STOPED         // 播放器已经停止      
	}

    
###PlayerControl API
| @function                 | @abstract           | @param    |
| ------------------------- |:-------------:| :-----:|
| void prepareAsync()   | Begin to play of the current item | -- |
| void start()          | Start playing |   -- |
| void stop()          | Stop playing |   -- |
| void release()          | release all resource |   -- |
| void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) | call when surface is available     |   surfaceTexture,video width,video height |
| void onSurfaceTextureDestroyed(SurfaceTexture surface) | call when surface is destroyed     |   SurfaceTexture |
| void setDataSource(String path) | Set player play address      |   play address|
| void reusmeRender() | Pause decode and render picture when app enter backgroud| -- |
| void pauseRender() | Resume decode and render picture when app will enter foreground | -- |
| void setEnableFastOpen(boolean enableFastOpen) | Set the first frame to quickly render |flag(Bool)|
| void setCacheTime(int cacheTime) |Set the media data cache time | cache time(ms) |
| void setMaxStuckDuration(int maxStuckDuration) | If stuck total time than the maximum stuck time, the player will be re loaded play address | time(ms) |
| void setConnectTimeout(float time)| Set timeout with server connection| time(ms) |
| void setRetryTime(int times) | Set the number of times to reconnect to the server|times|
| void setMaxBeginTimeout(int max) | If the player does not play in the first maximum time, then the player will reload the play address in any case. | times |
| void setEnableBackgroundPlaying(boolean enable) | whether enable background playing | -- |
| void registerPhoneListener(Context context) |set phone call listener | context |
| void setVolume(float f) | Set player volume |volume(0-1.0)|
| boolean isPlaying()| Is in playing state | -- |
| void mute() | Set player mute |  -- |
| void unmute() | Set player unmute | -- |
| long getCurrentPosition() | Get the current play time |--|
| STATE getPlayerStatus() |Get the current play status|--|


## Player callback interface API(common)
	public interface FWLivePlayerCallback {

	  public void onPrepared(FWLivePlayer mp);
	
	  public void onVideoSizeChanged(FWLivePlayer mp, int width, int height);
	
	  public void onError(FWLivePlayer mp, int code, String reason);

	}

| @function                 | @abstract                          | @param    |
| ------------------------- |:----------------------------------:| :--------:|
|void onPrepared(FWLivePlayer mp)|--|--|
|void onVideoSizeChanged(FWLivePlayer mp, int width, int height)|--|video width , video height|
|void onError(FWLivePlayer mp, int code, String reason)|--|error code , error reason|

## API instructions
Api使用步骤：

1.创建player

  	创建player对象
	
	FWLivePlayer player = new FWLivePlayer();

2.创建用户界面，使用TextureView或SurfaceView作为视频渲染的载体，并实现其回调接口，以TextureView为例：
	textureView.setSurfaceTextureListener(this);
	回调接口实现如下（可自行添加相关业务逻辑）：
	
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

2.设置player播放
	
	//设置播放的url
	player.setDataSource(url);
	//设置电话处理
	player.registerPhoneListener(context);
	//设置参数
	player.setEnableFastOpen(parameter.fristImage);
	player.setCacheTime(parameter.cachime);
	player.setVolume(parameter.volume);
	player.setRetryTime(parameter.recount);
	player.setConnectTimeout(parameter.timeout);
	player.setMaxBeginTimeout(parameter.maxbeginTime);
	player.setMaxStuckDuration(parameter.stuckTime);
	player.setEnableBackgroundPlaying(parameter.enableBackgrounPlay);
	//开始准备		
	player.prepareAsync();
	player.setFWLivePlayerListener(new FWLivePlayerCallback() {
		@Override
		public void onPrepared(FWLivePlayer mp) {
			//prepare ok，开始播放
			player.start();
		}
	    @Override
		public void onVideoSizeChanged(FWLivePlayer mp, int width, int height) {
		//动态调整video size
		}
		@Override
	   public void onError(FWLivePlayer mp, int code, String reason) {
	   }
 	 });
 	 
3.应用权限相关 
  
  	 <uses-permission android:name="android.permission.INTERNET" />
  	 <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
  
  其他具体请参照demo中用法实现