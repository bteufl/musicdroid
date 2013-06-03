package at.tugraz.musicdroid.recorder;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import at.tugraz.musicdroid.R;
import at.tugraz.musicdroid.RecorderActivity;
import at.tugraz.musicdroid.dialog.ChangeFilenameDialog;
import at.tugraz.musicdroid.helper.Helper;


public class RecorderLayout extends Handler implements OnClickListener, OnLongClickListener {
	private Context context = null;
	private TextView filenameTextView = null;
	private ImageButton recordImageButton = null; 
	private ImageButton playImageButton = null;
	private View progressBarView = null;
	private TextView recordDurationTextView = null;
	private RelativeLayout progressBarBoxRelativeLayout = null;
	private RelativeLayout addToSoundMixerBoxRelativeLayout = null;
	private boolean isRecording = false;
	private boolean isPlaying = false;
	private int trackDuration = 0;
	private int pixelPerSecond = 0;
	
	public RecorderLayout() {
	}
	
	public void init(Context context)
	{
		this.context = context;
		 
		filenameTextView = (TextView) ((RecorderActivity)context).findViewById(R.id.microphone_filename);
		recordImageButton = (ImageButton) ((RecorderActivity)context).findViewById(R.id.microphone_record_button);
		recordDurationTextView = (TextView) ((RecorderActivity)context).findViewById(R.id.microphone_duration_text);
		playImageButton = (ImageButton) ((RecorderActivity)context).findViewById(R.id.microphone_play_button);
		progressBarView = (View) ((RecorderActivity)context).findViewById(R.id.microphone_progress_bar);
		progressBarBoxRelativeLayout = (RelativeLayout) (View) ((RecorderActivity)context).findViewById(R.id.microphone_progress_bar_box); 
		addToSoundMixerBoxRelativeLayout = (RelativeLayout) (View) ((RecorderActivity)context).findViewById(R.id.microphone_add_to_sound_mixer_box);
		
		
		if(recordDurationTextView == null)
		{
			Log.i("RecorderLayout", "Text View is Null");
		}
		else
		{
		  Log.i("RecorderLayout", "Text View is not Null");
		}
		
		reorderToRecordLayout();
		
		filenameTextView.setOnLongClickListener(this);
		recordImageButton.setColorFilter(Color.RED);
		recordImageButton.setOnClickListener(this);
		playImageButton.setOnClickListener(this);
		addToSoundMixerBoxRelativeLayout.setOnClickListener(this);
		recordDurationTextView.setText("00:00");
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.microphone_record_button)
		{
			handleOnRecordClick();
		}
		if(v.getId() == R.id.microphone_play_button)
		{
			Log.i("PLAY BUTTON", "PRESSED");
			handleOnPlayClick();
		}
		if(v.getId() == R.id.microphone_add_to_sound_mixer_box)
		{
			handleOnAddToSoundmixerClick();
		}
	}


	@Override
	public boolean onLongClick(View v) {
		if(v.getId() == R.id.microphone_filename)
		{
			handleOnFilenameLongClick();
		}
		return false;
	}
	
	@Override
	public void handleMessage(Message msg) {
		Bundle b = msg.getData();
		if(b.containsKey("duration"))
		{
			int key = b.getInt("duration");
			Log.i("RecorderLayout", "MessageDuration = " + key);
			recordDurationTextView.setText(Helper.getInstance().durationStringFromInt(key));	
		}
		else if(b.containsKey("trackposition"))
		{
			int position = b.getInt("trackposition");
			Log.i("RecorderLayout", "Message = Trackposition, Position = " + position + " PPS = " + pixelPerSecond);
			LayoutParams params = (LayoutParams) progressBarView.getLayoutParams();
			params.width = pixelPerSecond*position;
			progressBarView.setLayoutParams(params);
		}
	}
	
	private void handleOnFilenameLongClick()
	{
		ChangeFilenameDialog dialog = new ChangeFilenameDialog();
		dialog.show(((RecorderActivity)context).getFragmentManager(), null);
	}
	
	private void handleOnRecordClick()
	{			
		if(!isRecording)
		{
		   isRecording = true;
		   if(playImageButton.getVisibility() == View.VISIBLE)
			   reorderToRecordLayout();
		   
		   AudioHandler.getInstance().startRecording();
		   recordImageButton.setImageDrawable(context.getResources().getDrawable(R.drawable.pause_button));
		   recordImageButton.setColorFilter(Color.WHITE);
		}
		else if(isRecording)
		{
			isRecording = false;
			AudioHandler.getInstance().stopRecording();
		    recordImageButton.setImageDrawable(context.getResources().getDrawable(R.drawable.record_button));
		    recordImageButton.setColorFilter(Color.RED);

		    reorderToPlayLayout();
		}
	}

	
	private void handleOnPlayClick()
	{
		if(!isPlaying)
		{				
			isPlaying = true;
			AudioHandler.getInstance().playRecording();
			playImageButton.setImageDrawable(context.getResources().getDrawable(R.drawable.pause_button));
		}
		else if(isPlaying)
		{
			isPlaying = false;
			playImageButton.setImageDrawable(context.getResources().getDrawable(R.drawable.play_button));
		}		
	}
	
	private void handleOnAddToSoundmixerClick()
	{
		((RecorderActivity)context).returnToMainActivtiy();
	}
	
	
	
	public void handlePlaySoundComplete()
	{
		isPlaying = false;
		playImageButton.setImageDrawable(context.getResources().getDrawable(R.drawable.play_button));
	}
	
	public void resetLayoutToRecord()
	{
	   isRecording = false;
	   recordImageButton.setImageDrawable(context.getResources().getDrawable(R.drawable.record_button));
	   recordImageButton.setColorFilter(Color.RED);
	   if(!recordDurationTextView.getText().equals("00:00"))
		   reorderToPlayLayout();
	}
	
	public void updateFilename(String filename)
	{
		if(filenameTextView == null) return;
		filenameTextView.setText(filename);
	}
	
	
	public void setDurationText(int duration)
	{
		recordDurationTextView.setText(""+duration);
	}
	
	public void setTrackDuration(int duration)
	{
		trackDuration = duration;
		int width = progressBarBoxRelativeLayout.getWidth();//((LayoutParams)progressBarBoxRelativeLayout.getLayoutParams()).width;
		pixelPerSecond = width/duration;
		Log.i("RecorderLayout", "setTrackDuration: width = " + width + " duration = " + duration);
	}
	
	private void reorderToRecordLayout()
	{		
		addToSoundMixerBoxRelativeLayout.setVisibility(View.INVISIBLE);
		progressBarView.setVisibility(View.GONE);
		progressBarBoxRelativeLayout.setVisibility(View.GONE);
		playImageButton.setVisibility(View.GONE);
	

		((LayoutParams)recordImageButton.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
		((LayoutParams)recordImageButton.getLayoutParams()).addRule(RelativeLayout.CENTER_IN_PARENT);
	}
	
	private void reorderToPlayLayout()
	{   
		addToSoundMixerBoxRelativeLayout.setVisibility(View.VISIBLE);
		progressBarView.setVisibility(View.VISIBLE);
		progressBarBoxRelativeLayout.setVisibility(View.VISIBLE);
		playImageButton.setVisibility(View.VISIBLE);
		

		((LayoutParams)recordImageButton.getLayoutParams()).addRule(RelativeLayout.CENTER_IN_PARENT,0);
		((LayoutParams)recordImageButton.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	}
	

	public void reset()
	{
		recordDurationTextView = null;
	}
	


}