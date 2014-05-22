package com.example.com.programmingthetux.tutorial;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
	private Command current_command = null;
	private Command default_command = new Pwd();
	private HashMap<String, Command> map = new HashMap<String, Command>();
	private String bash_prompt = "%u: %s "; //%s will be replaced by the working directory
	//this can be updated to reflect the apps current working directory
	private String curWrkDir = default_command.get_current_directory();	 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		
		
		 EditText command_text = (EditText) findViewById(R.id.command);
		
		 
		 Button executeButton = (Button)findViewById(R.id.execute_button);
		 executeButton.setOnClickListener(new OnClickListener() {
			 @Override
			 public void onClick(View arg0) {
				 Log.d(TAG, "execute command clicked");
				 //TODO clear the command text, execute the command, and update the text view
			 }
		 });
		
		
		setContentView(R.layout.activity_main);
		
		/* Add the commands to the hashmap */
		map.put("cat",new Cat());
		map.put("cd",new Cd());
		map.put("clear",new Clear());
		map.put("date", new Date());
		map.put("echo", new Echo());
		map.put("find", new Find());
		map.put("help", new Help());
		map.put("less", new Less());
		map.put("ls", new Ls());
		map.put("mkdir", new Mkdir());
		map.put("mv", new Mv());
		map.put("open", new Open());
		map.put("ping", new Ping());
		map.put("pwd", new Pwd());
		map.put("rm", new Rm());
		map.put("rmdir", new Rmdir());
		map.put("tail", new Tail());
		map.put("which", new Which());
		map.put("whois", new Whois());
		
		default_command = map.get("pwd");
//		String prompt_string = buildPromptString(default_command.get_current_directory());
//		prompt.setText(prompt_string); 
		appendOutput("");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	public void processCommand(View view) {
		EditText command_text = (EditText) findViewById(R.id.command);
//		TextView prompt = (TextView) findViewById(R.id.update_text);
		
		String command_string = command_text.getText().toString();
	 	String result[] = command_string.split(" "); //split the string up by words
		
		
		if(result[0].equals("")) {
			//empty command given, just append a new prompt
			this.appendOutput("");
		} else {
			current_command = map.get(result[0]);
//			String prompt_string = buildPromptString(default_command.get_current_directory());
			if(current_command == null) {
				appendOutput("bash: " + result[0] + ": command not found");
			}
			else {
				if (current_command.execute(this, Arrays.copyOfRange(result, 1, result.length)) == 0) {
					//command executed properly, clear the command input box
					command_text.setText("");
				}
			}
		}
	}
	
	
	public String getUsername(){
	    AccountManager manager = AccountManager.get(this); 
	    Account[] accounts = manager.getAccountsByType("com.google"); 
	    List<String> possibleEmails = new LinkedList<String>();

	    for (Account account : accounts) {
	      // TODO: Check possibleEmail against an email regex or treat
	      // account.name as an email address only for certain account.type values.
	      possibleEmails.add(account.name);
	    }

	    if(!possibleEmails.isEmpty() && possibleEmails.get(0) != null){
	        String email = possibleEmails.get(0);
	        String[] parts = email.split("@");
	        if(parts.length > 0 && parts[0] != null)
	            return parts[0];
	        else
	            return null;
	    }else
	        return null;
	}
	
	public String buildPromptString(String cwd) {
		String userName = getUsername();
		String prompt_string = bash_prompt.replaceAll("%u", userName == null ? "shell" : userName);
		prompt_string = prompt_string.replaceAll("%s", cwd == null ? "?" : cwd);
		
		return prompt_string;
	}
	
	public void appendOutput(String output) {
		if (output == null) {
			Log.w(TAG, "sent null output to append");
		} else {
			String ps1 = buildPromptString(curWrkDir);
			 TextView prompt = (TextView) findViewById(R.id.update_text);
			 prompt.setText(prompt.getText().toString() + "\n" + ps1
						+ USER_PROMPT + " " + output);
		}
	}

	public String getCurWrkDir() {
		return curWrkDir;
	}

	public void setCurWrkDir(String curWrkDir) {
		this.curWrkDir = curWrkDir;
	}
}
