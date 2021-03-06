package at.tugraz.ist.musicdroid;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class MidiPlayer {
	private DrawTonesView toneView;
	static final int SEMIQUAVER = 4;
	static final int QUAVER = 8;
	static final int CROTCHET = 16;
	static final int MINIM = 32;
	static final int SEMIBREVE = 64;
	private String pathToMidi;
	private Context context;
	private MediaPlayer mediaPlayer;
	

	
	public MidiPlayer(DrawTonesView tone, Context cxt){
		toneView = tone;
		context = cxt;
	}
	
	public String createMidifile(){
		File directory;
		File playFile;
		String path;
		directory = new File(Environment.getExternalStorageDirectory()+File.separator+"records"+File.separator+"Musicfiles");
		if (!directory.exists()){
			directory.mkdir();
		}
					
		path = directory.getAbsolutePath() + File.separator + "play.mid";
		playFile = new File(path);
		if(playFile.exists()){
			playFile.delete();
		}
		pathToMidi = path;
		return path;
	}
	
	public void writeToMidiFile(){
		MidiFile midiFile = new MidiFile();
		int numberOfTones = toneView.getTonesSize();
		List<Tone> tonesArray = toneView.getTonesList();
		String path = createMidifile();
		for(int counter= 0; counter < numberOfTones; counter++){
			Tone chordtones = (Tone) tonesArray.get(counter);
			ArrayList<Integer> midiValues = chordtones.getMidiValues();
			for(int innerCounterNoteOn = 0; innerCounterNoteOn < midiValues.size(); innerCounterNoteOn++){
		    	midiFile.noteOn (0, midiValues.get(innerCounterNoteOn), 127);
		    }
		    for(int innerCounterNoteOff = 0; innerCounterNoteOff < midiValues.size(); innerCounterNoteOff++){
		    	if (midiValues.size()>1){
		    		if (innerCounterNoteOff==0){
		    			midiFile.noteOff (CROTCHET, midiValues.get(innerCounterNoteOff));
		    		}
		    		midiFile.noteOff (0, midiValues.get(innerCounterNoteOff));
		    	} else {
		    		midiFile.noteOff (CROTCHET, midiValues.get(innerCounterNoteOff));
		    	}
		    	
		    	
		    }
					
		}
		
		try {
			File midi_File = new File (path);
			if(midi_File.exists()){
				midi_File.delete();
			}
			midiFile.writeToFile(path);
		} catch (IOException e) {
			Log.e("MidiPlayer", e.getMessage());
			e.printStackTrace();
		}
		
		
	}
	
	public void playMidiFile(){
		File file = new File(pathToMidi );
	    
	    if(!file.exists()){
	    	Log.e("MidiPlayer", "Midi File does not exist!");
	    	return;
	    
	    }
    	Uri myUri = Uri.fromFile(file);
    	mediaPlayer = new MediaPlayer();
    	mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    	try {
    		mediaPlayer.setDataSource(context, myUri);
    	} catch (IllegalArgumentException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} catch (SecurityException e) {
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
    		mediaPlayer.prepare();
    	} catch (IllegalStateException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	mediaPlayer.start();	
    }
	
	public void stopMidiPlayer(){
		mediaPlayer.stop();
	}
	
	public void releaseMidiPlayer(){
		mediaPlayer.release();
	}
	
}