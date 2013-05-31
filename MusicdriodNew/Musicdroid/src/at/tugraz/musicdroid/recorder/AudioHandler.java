package at.tugraz.musicdroid.recorder;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;
import at.tugraz.musicdroid.R;
import at.tugraz.musicdroid.helper.Helper;

public class AudioHandler {
	public static AudioHandler instance = null;
	private RecorderLayout layout = null;
	private Context context = null;
	private String path = null;
	private String filename = null;
	private boolean init = false;
	private Recorder recorder = null;
	private Player player = null;
	private AudioVisualizer visualizer = null;
	//private Player = null;
	
	private AudioHandler()
	{
		this.path = Environment.getExternalStorageDirectory().getAbsolutePath();
		this.filename = "test.mp3";
	}
	
	public static AudioHandler getInstance() {
        if (instance == null) {
            instance = new AudioHandler();
        }
        return instance;
    }
	
	public void init(Context context, RecorderLayout layout)
	{
		if(init) return;
		
		this.context = context;
		this.layout = layout;
		visualizer = new AudioVisualizer(context);
		recorder = new Recorder(context, layout, visualizer);
		player = new Player(layout);
		init = true;
	}
	
	public boolean startRecording()
	{
		File check = new File(path+'/'+filename);
		if(check.exists())
		{
			showDialog();
		}
		else
		{
			recorder.record();
		}
		return true;
	}
	
	public void stopRecording()
	{
		recorder.stopRecording();
	}
	
	public void playRecording()
	{
		player.playRecordedFile();
	}
	
	private void showDialog()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder
				.setMessage(R.string.dialog_warning_file_overwritten_at_record)
				.setCancelable(true)
				.setPositiveButton(R.string.dialog_abort,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								layout.resetLayoutToRecord();
							}
						})
				.setNegativeButton(R.string.dialog_continue,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								recorder.record();
							}
						});
		AlertDialog alertNewImage = alertDialogBuilder.create();
		alertNewImage.show();
	}
	
	public void setFilename(String f)
	{
		this.filename = f;
		layout.updateFilename(Helper.getInstance().removeFileEnding(this.filename));
	}
	
	public String getFilenameFullPath()
	{
		return path + "/" + filename;
	}
	
	public String getFilename()
	{
		return filename;
	}
}
