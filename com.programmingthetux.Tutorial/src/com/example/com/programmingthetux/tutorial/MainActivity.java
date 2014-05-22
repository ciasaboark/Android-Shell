package com.example.com.programmingthetux.tutorial;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.com.programmingthetux.commands.*;

public class MainActivity extends Activity {
	
	private Command current_command = null;
	private Command default_command = null;
	private HashMap<String, Command> map = new HashMap<String, Command>();
	
	private EditText command_text = (EditText) findViewById(R.id.command);
	private TextView prompt = (TextView) findViewById(R.id.update_text);
	private String bash_prompt = "[user: %s] #"; //%s will be replaced by the working directory

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		String prompt_string = bash_prompt.replaceAll("%s", default_command.get_current_directory());
		prompt.setText(prompt_string); //set the shell prompt text "[user: /] #" 
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
		TextView prompt = (TextView) findViewById(R.id.update_text);
		
		String command_string = command_text.getText().toString();
	 	String result[] = command_string.split(" "); //split the string up by words
		
		
		if(result[0] != null) {
			current_command = map.get(result[0]);
			
			if(current_command == null) {
				prompt.setText(prompt.getText().toString() + "\n[user " + default_command.get_current_directory() + "] #" + " no such command");
			}
			else {
				
			}
		}
	}
}
