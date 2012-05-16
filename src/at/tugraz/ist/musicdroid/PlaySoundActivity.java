package at.tugraz.ist.musicdroid;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PlaySoundActivity extends Activity {

	public MediaPlayer mp_;
	private short state_ = 0;// stopped
	private Button but_paus_res_;

	private static PlaySoundActivity soundManager = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_sound);
		// mp_ = MediaPlayer.create(this, R.raw.iris);

		mp_ = new MediaPlayer();

		FileInputStream fis;
		try {
			// TODO Pfad als Parameter setzen
			fis = new FileInputStream(new File(
					"mnt/sdcard/bluetooth/FloSong.mp3"));

			FileDescriptor fileDescriptor = fis.getFD();

			mp_.setDataSource(fileDescriptor);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			mp_.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void onPlay(View v) {
		if (mp_ != null) {
			mp_.seekTo(0);
			mp_.start();
			state_ = 1;
			if (but_paus_res_ != null)
				but_paus_res_.setText("Pause");

		}
	}

	public void on_pause_resume(View v) {
		but_paus_res_ = (Button) v.findViewById(R.id.pauseResumeButton);
		if (mp_ != null) {
			if (but_paus_res_.getText().equals("Pause") && mp_.isPlaying()) {
				if (state_ == 1) {
					mp_.pause();
					but_paus_res_.setText("Resume");
					state_ = 2;
				}

			} else if (but_paus_res_.getText().equals("Resume")) {
				if (state_ == 2) {
					mp_.start();
					but_paus_res_.setText("Pause");
					state_ = 1;
				}
			}
		}

	}

	public void onStop(View v) {

		if (mp_ != null) {
			if (state_ == 1 || state_ == 2) {

				if (state_ == 1)
					mp_.pause();

				state_ = 0;
				mp_.seekTo(0);
			}
			but_paus_res_.setText("Pause");
		}

	}

	
	 public synchronized static PlaySoundActivity getInstance() { 
		 if(soundManager == null) 
		 { 
			 soundManager = new PlaySoundActivity(); 
		 } 
		 return soundManager; 
	 }
	 
	@Override
	public void onDestroy() {
		mp_.release();
		super.onDestroy();
	}

}
