package cn.com.fastweb.fastmedia.demo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.fastweb.fastmedia.demo.Samples.Sample;
import cn.com.fastweb.fastmedia.sdk.demo.R;

public class SampleListActivity extends Activity{
	
	EditText scaleEt,cacheTimeEt,volumeEt,timeoutEt,recountEt,stuckTimeEt,maxBeginTimeEt;
	Switch fristImageSt,testModelSt,backgroundplaySt;
	private EditText mUrlEt;
	private Button goBt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);
		
		scaleEt      = (EditText)findViewById(R.id.sample_scale_et);
		cacheTimeEt  = (EditText)findViewById(R.id.sample_cachetime_et);
		volumeEt     = (EditText)findViewById(R.id.sample_volume_et);
		timeoutEt    = (EditText)findViewById(R.id.sample_timeout_et);
		recountEt    = (EditText)findViewById(R.id.sample_recount_et);
		stuckTimeEt  = (EditText)findViewById(R.id.sample_stucktime_et);
		maxBeginTimeEt = (EditText)findViewById(R.id.sample_maxbegin_et);
		fristImageSt = (Switch)findViewById(R.id.sample_fristimage_st);
		testModelSt  = (Switch)findViewById(R.id.sample_testmodel_st);
		backgroundplaySt = (Switch)findViewById(R.id.sample_backgroundplay_st);
		
		scaleEt.setText(1.0 + "");
		cacheTimeEt.setText(1000 + "");
		volumeEt.setText(0.5 + "");
		timeoutEt.setText(10000 + "");
		recountEt.setText(10 + "");
		stuckTimeEt.setText(10000 + "");
		maxBeginTimeEt.setText(10000 + "");
		fristImageSt.setChecked(true);
		testModelSt.setChecked(false);
		backgroundplaySt.setChecked(true);
		
		mUrlEt = (EditText) findViewById(R.id.main_url_et);
		goBt = (Button) findViewById(R.id.main_play_bt);
		goBt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(TextUtils.isEmpty(mUrlEt.getText().toString())){
					Toast.makeText(SampleListActivity.this, "Url is empty", Toast.LENGTH_LONG).show();
					return;
				}
				onSampleSelected(new Sample("test url ",mUrlEt.getText().toString()));
			}
		});
		
		ListView  listView = (ListView) findViewById(R.id.sample_listview);
		listView.setAdapter(new SampleAdapter(this, Arrays.asList(Samples.MISC)));
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Sample sam = Arrays.asList(Samples.MISC).get(position);
				onSampleSelected(sam);
//				if(TextUtils.isEmpty(mUrlEt.getText().toString())){
					mUrlEt.setText(sam.uri);
//				}
			}
        	
		});
	}
	
	  private void onSampleSelected(Sample sample) {
		    Intent mpdIntent = new Intent(this, PlayActivity.class)
		        .setData(Uri.parse(sample.uri))
		        .putExtra("Parameter", createParameter())
		        .putExtra(PlayActivity.CONTENT_ID_EXTRA, sample.contentId)
		        .putExtra(PlayActivity.PROVIDER_EXTRA, sample.provider);
		    startActivity(mpdIntent);
		  }
	  
	  
	  private Parameter createParameter() {
		  Parameter parameter = new Parameter();
			parameter.scale     = getFloat(getStringFromEt(scaleEt     ),1.0f);
			parameter. cachime  = getInt(getStringFromEt(cacheTimeEt ),1000);
			parameter.volume    = getFloat(getStringFromEt(volumeEt    ),0.5f);
			parameter. timeout  = getInt(getStringFromEt(timeoutEt   ),10000);
			parameter. recount  = getInt(getStringFromEt(recountEt   ),10);
			parameter. stuckTime= getInt(getStringFromEt(stuckTimeEt ),10000);
			parameter. maxbeginTime = getInt(getStringFromEt(maxBeginTimeEt ),10000);
			parameter.fristImage= fristImageSt.isChecked();
			parameter.enableBackgrounPlay = backgroundplaySt.isChecked();
			parameter.testModel = testModelSt.isChecked();
		return parameter;
	}
	  
	  private float getFloat(String s,float def){
		  try {
			return Float.parseFloat(s);
		} catch (NumberFormatException e) {
		}
		  return def;
	  }
	  
	  private int getInt(String s,int def){
		  try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
		}
		  return def;
	  }


	private static final class SampleAdapter extends BaseAdapter {

		    private final Context context;
		    private final List<Sample> samples;

		    public SampleAdapter(Context context, List<Sample> samples) {
		      this.context = context;
		      this.samples = samples;
		    }

			@Override
			public int getCount() {
				return samples.size();
			}

			@Override
			public Sample getItem(int position) {
				return samples.get(position);
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				 View view = convertView;
			      if (view == null) {
			        view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent,
			            false);
			      }
			      TextView url = (TextView) view.findViewById(android.R.id.text1);
			      TextView name = (TextView) view.findViewById(android.R.id.text2);
			      url.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
			      name.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
			      url.setText(getItem(position).uri);
			      name.setText(getItem(position).name);
			      return view;
			}
	  }
	  
	  private String getStringFromEt(EditText et){
		  return et.getText().toString();
	  }
	  
	public static class Parameter implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = -8584390808663339983L;
		
		public float scale       ;
		public int cachime       ;
		public float volume      ;
		public int timeout       ;
		public int recount       ;
		public int stuckTime     ;
		public int maxbeginTime     ;
		public boolean fristImage;
		public boolean enableBackgrounPlay;
		public boolean testModel ;
		
		public Parameter(float scale, int cachime, float volume, int timeout,
				int recount, int stuckTime,int maxbeginTime, boolean fristImage,boolean enableBackgrounPlay,
				boolean testModel) {
			super();
			this.scale = scale;
			this.cachime = cachime;
			this.volume = volume;
			this.timeout = timeout;
			this.recount = recount;
			this.stuckTime = stuckTime;
			this.maxbeginTime = maxbeginTime;
			this.fristImage = fristImage;
			this.enableBackgrounPlay = enableBackgrounPlay;
			this.testModel = testModel;
		}
		
		public Parameter() {
			super();
		}
		  
	  }

}
