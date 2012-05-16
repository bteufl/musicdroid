package at.tugraz.ist.musicdroid;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FolderBrowserActivity extends ListActivity{
	private List<String> item = null;
	private List<String> path = null;
	private String root="/";
	private TextView myPath;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.folderbrowser);
        myPath = (TextView)findViewById(R.id.path);
        getDir(root);
    }
    
    private void getDir(String dirPath)
    {
    	myPath.setText(dirPath);
    	
    	item = new ArrayList<String>();
    	path = new ArrayList<String>();
    	
    	File f = new File(dirPath);
    	File[] files = f.listFiles();
    	
    	if(!dirPath.equals(root))
    	{
    		item.add(root);
    		path.add(root);
    		
    		item.add("../");
    		path.add(f.getParent());
            
    	}
    	
    	for(int i=0; i < files.length; i++)
    	{
			File file = files[i];
			
			if(file.isDirectory()) {    				
				item.add(file.getName() + "/");
				path.add(file.getPath());
			}
			/*else {  // only directories!!
				item.add(file.getName());
				path.add(file.getPath());
			}*/
    	}

    	ArrayAdapter<String> fileList =
    		new ArrayAdapter<String>(this, R.layout.row, item);
    	setListAdapter(fileList);
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		File file = new File(path.get(position));
		
		if (file.isDirectory())
		{
			if(file.canRead())
				getDir(path.get(position));
			else
			{
				new AlertDialog.Builder(this)		
				.setTitle("[" + file.getName() + "] folder can't be read!")
				.setPositiveButton("OK", 
						new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
							}
						}).show();
			}
		}
		/*else
		{
			new AlertDialog.Builder(this)
				.setIcon(R.drawable.musicdroid_launcher)
				.setTitle("[" + file.getName() + "]")
				.setPositiveButton("OK", 
						new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
							}
						}).show();
		}*/
	}
	
	public void onOkClick(View v)
    {
    	Intent result = new Intent();
    	result.putExtra("location", myPath.getText());
    	setResult(Activity.RESULT_OK, result);
    	finish();
    }
	
	public void onNewFolderClick(View v)
    {
		String currentPath = myPath.getText().toString();
    	String newPath = currentPath + "/" + "new_folder" + "/";
		File directory = new File(newPath);	
		String postfix = "0";
		while(directory.exists())
		{
			int temp = Integer.parseInt(postfix);
			temp++;
			postfix = Integer.toString(temp);
			
			newPath = currentPath + "/" + "new_folder_" + postfix + "/";
			directory = new File(newPath);	
		}
		
		directory.mkdirs();	
		getDir(currentPath);
    }
	
	public void onCancelClick(View v)
    {	
    	setResult(Activity.RESULT_CANCELED);
    	finish();
    }
	
	@Override
	public void onBackPressed() {
		String currentPath = myPath.getText().toString();
		File directoy = new File(currentPath);
		String parent = directoy.getParent();
		
		if(parent == null)
		{
			setResult(Activity.RESULT_CANCELED);
			finish();		
		}
		else
		{
			getDir(parent);			
		}
		
	}

}