package com.example.com.programmingthetux.tutorial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.com.programmingthetux.commands.Cat;
import com.example.com.programmingthetux.commands.Cd;
import com.example.com.programmingthetux.commands.Clear;
import com.example.com.programmingthetux.commands.Command;
import com.example.com.programmingthetux.commands.Date;
import com.example.com.programmingthetux.commands.Echo;
import com.example.com.programmingthetux.commands.Find;
import com.example.com.programmingthetux.commands.Help;
import com.example.com.programmingthetux.commands.Less;
import com.example.com.programmingthetux.commands.Ls;
import com.example.com.programmingthetux.commands.Mkdir;
import com.example.com.programmingthetux.commands.Mv;
import com.example.com.programmingthetux.commands.Open;
import com.example.com.programmingthetux.commands.Ping;
import com.example.com.programmingthetux.commands.Pwd;
import com.example.com.programmingthetux.commands.Rm;
import com.example.com.programmingthetux.commands.Rmdir;
import com.example.com.programmingthetux.commands.Tail;
import com.example.com.programmingthetux.commands.Which;
import com.example.com.programmingthetux.commands.Whois;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	
	private static final String USER_PROMPT = "$";
	private static final String ROOT_PROMPT = "#";
	private String bash_prompt = "%u: %s "; //%s will be replaced by the working directory
	
	private Command current_command = null;
	private Command default_command = new Pwd();
	private HashMap<String, Command> map = new HashMap<String, Command>();
	
	//this can be updated to reflect the apps current working directory
	private String curWrkDir = "/";	 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);

		final EditText command_text = (EditText) findViewById(R.id.command);
		
		command_text.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
		        // If the event is a key-down event on the "enter" button
				Log.d(TAG, "key pressed");
		        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
		            (keyCode == KeyEvent.KEYCODE_ENTER)) {
		        	// Perform action on key press
		        	Log.d(TAG, "enter pressed");
		        	processCommand(v);
		        	return true;
		        }
		        return false;
		    }
		});
		
		/* Add the commands to the hashmap */
		map.put("cat",   new Cat());
		map.put("cd",    new Cd());
		map.put("clear", new Clear());
		map.put("date",  new Date());
		map.put("echo",  new Echo());
		map.put("find",  new Find());
		map.put("help",  new Help());
		map.put("less",  new Less());
		map.put("ls",    new Ls());
		map.put("mkdir", new Mkdir());
		map.put("mv",    new Mv());
		map.put("open",  new Open());
		map.put("ping",  new Ping());
		map.put("pwd",   new Pwd());
		map.put("rm",    new Rm());
		map.put("rmdir", new Rmdir());
		map.put("tail",  new Tail());
		map.put("which", new Which());
		map.put("whois", new Whois());
		
		default_command = map.get("pwd");
		appendOutput("");
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	public void processCommand(View view) {
		EditText command_text = (EditText) findViewById(R.id.command);
		
		String command_string = command_text.getText().toString();
		List<String> args = new ArrayList<String>();
		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(command_string);
		//pull out the command
		String cmd = null;
		try {
			m.find();
			cmd = m.group(1);
		} catch (IllegalStateException e) { }
		
		//pull out the arguments
		while (m.find()) {
			args.add(m.group(1).replace("\"", ""));
		}
		
		
		if(cmd == null) {
			//empty command given, just append a new prompt
			this.appendOutput("");
		} else {
			current_command = map.get(cmd);
			if(current_command == null) {
				appendOutput("bash: " + cmd + ": command not found");
			}
			else {
				String[] argsArray = args.toArray(new String[ args.size() ]);
				if (current_command.execute(this, argsArray) == 0) {
					//command executed properly, clear the command input box
					command_text.setText("");
				}
			}
		}
	}
	
	
	public String getUsername(){
		String username = null;
		AccountManager manager = AccountManager.get(this); 
	    Account[] accounts = manager.getAccountsByType("com.google"); 
	    List<String> possibleEmails = new LinkedList<String>();

	    for (Account account : accounts) {

	      possibleEmails.add(account.name);
	    }

	    if(!possibleEmails.isEmpty() && possibleEmails.get(0) != null){
	        String email = possibleEmails.get(0);
	        String[] parts = email.split("@");
	        if(parts.length > 0 && parts[0] != null) {
	            username = parts[0];
	        }
	    }
	    return username;
	}
	
	public String buildPromptString(String cwd) {
		String userName = getUsername();
		String prompt_string = bash_prompt.replaceAll("%u", userName == null ? "shell" : userName);
		prompt_string = prompt_string.replaceAll("%s", cwd == null ? "?" : cwd);
		prompt_string = prompt_string + " " + USER_PROMPT + " ";
		return prompt_string;
	}
	
	public void appendOutput(String output) {
		if (output == null) {
			Log.w(TAG, "sent null output to append");
		} else {
			String ps1 = buildPromptString(curWrkDir);
			 TextView prompt = (TextView) findViewById(R.id.update_text);
			 prompt.setText(prompt.getText().toString() + "\n" + ps1 + output);
			 final ScrollView sv = (ScrollView) findViewById(R.id.output_scrollview);
			 //scroll the view down in a separate thread. This makes sure that the new
			 //line of text is applied before scrolling, and should reduce activity on
			 //the main thread
			 sv.post(new Runnable() {
		        public void run()
		        {
		            sv.fullScroll(View.FOCUS_DOWN);
		            findViewById(R.id.command).requestFocus();
		        }
		    });
		}
	}
	
	public void clearOutput() {
		TextView tv = (TextView) findViewById(R.id.update_text);
		tv.setText(buildPromptString(this.curWrkDir));
	}

	public String getCurWrkDir() {
		return curWrkDir;
	}

	public void setCurWrkDir(String curWrkDir) {
		this.curWrkDir = curWrkDir;
	}
}
