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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.com.programmingthetux.commands.Cat;
import com.example.com.programmingthetux.commands.Cd;
import com.example.com.programmingthetux.commands.Clear;
import com.example.com.programmingthetux.commands.Command;
import com.example.com.programmingthetux.commands.Date;
import com.example.com.programmingthetux.commands.Echo;
import com.example.com.programmingthetux.commands.Find;
import com.example.com.programmingthetux.commands.GenericRunner;
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
	private HashMap<String, Command> map = new HashMap<String, Command>();
	private String bash_prompt = "%u: %s "; //%s will be replaced by the working directory
	//this can be updated to reflect the apps current working directory
	private String curWrkDir = "/";	 
	private LimitedQueue<String> outputLines = new LimitedQueue<String>(200);
	private StringBuilder commandText = new StringBuilder();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		
		if (savedInstanceState != null) {
			 super.onRestoreInstanceState(savedInstanceState);
			  outputLines.clear();
			  String oldOutput = savedInstanceState.getString("outputLines");
			  if (oldOutput != null) {
				  String[] lines = oldOutput.split("\n");
				  for (String line: lines) {
					  //
					  outputLines.add(line);
				  }
			  }
		}



		
		final ZanyEditText command_text = (ZanyEditText) findViewById(R.id.command);
		command_text.setImeActionLabel("execute", KeyEvent.KEYCODE_ENTER);
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
		        } else if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
		        		(keyCode == KeyEvent.KEYCODE_DEL)) {
		        	Log.d(TAG, "backspace pressed");
					removeLastChar();
		        }
		        return false;
		    }
		});
		
		command_text.addTextChangedListener(new TextWatcher() {
			  @Override
			  public void afterTextChanged(Editable e) {
				  //nothing to do here
			  }

			  @Override
			  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			    //nothing needed here...
			  }

			  @Override
			  public void onTextChanged(CharSequence s, int start, int before, int count) {
				  String textFromEditView = command_text.getText().toString();
				  if (!textFromEditView.equals("")) {
					  commandText.append(textFromEditView);
					  appendChar(textFromEditView);
					  ZanyEditText command_text = (ZanyEditText) findViewById(R.id.command);
					  command_text.setText("");
				  }
			  }
			});
		
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
		
//		String prompt_string = buildPromptString(default_command.get_current_directory());
//		prompt.setText(prompt_string); 
		
		//if this was the first time the activity was created then append an empty line
		//otherwise just use the lines already there
		if (savedInstanceState == null || outputLines.isEmpty()) {
			appendPrompt();
		} else {
			appendOutput(null);
		}
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
		

		String command_string = commandText.toString();
		commandText = new StringBuilder();
		List<String> args = new ArrayList<String>();
		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(command_string);
		//pull out the command
		String cmd = null;
		try {
			m.find();
			cmd = m.group(1);
		} catch (IllegalStateException e) {}

		//pull out the arguments
		while (m.find()) {
			args.add(m.group(1).replace("\"", ""));
		}

		current_command = map.get(cmd);
		if(current_command == null) {
			//not a built in command, try farming out to the GenericRunner
			String[] argsArray = new String[args.size() +1];
			argsArray[0] = cmd;
			for (int i = 1; i < args.size(); i++) {
				argsArray[i] = args.remove(0);
			}
			new GenericRunner().execute(this, argsArray);
		} else {
			String[] argsArray = args.toArray(new String[ args.size() ]);
			current_command.execute(this, argsArray);
		}
		
		
		this.appendPrompt();
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
	
	/**
	 * Add a line of text to the output log and update the view.
	 * @param output the line of text to append to the output log. If
	 * null then no line is appended, but the view is still redrawn.
	 */
	public void appendOutput(String output) {
		if (output != null) {
			outputLines.add(output);
		}

		TextView prompt = (TextView) findViewById(R.id.update_text);
		prompt.setText(outputLines.toString());

		//scroll the view down in a separate thread. This makes sure that the new
		//line of text is applied before scrolling, and should reduce activity on
		//the main thread
		final ScrollView sv = (ScrollView) findViewById(R.id.output_scrollview);
		sv.post(new Runnable() {
			public void run() {
	            sv.fullScroll(View.FOCUS_DOWN);
	            findViewById(R.id.command).requestFocus();
	        }
	    });
	}
	
	/**
	 * Removes the last char from the current output line (if one exists)
	 */
	public void removeLastChar() {
		//pull out the last line in the output, strip the char, and place
		//the line back in the queue
		if (!outputLines.isEmpty()) {
			StringBuilder sb = new StringBuilder("");
			String line = outputLines.tail();
			if (line != null && !line.equals("")) {
				sb.append(line.substring(0, line.length() - 1));
			}
			outputLines.add(sb.toString());
		}
		
		//remove the last char from the current command string
		if (commandText.length() != 0) {
				commandText.deleteCharAt(commandText.length() - 1);
		}
		
		TextView prompt = (TextView) findViewById(R.id.update_text);
		prompt.setText(outputLines.toString());
		
		final ScrollView sv = (ScrollView) findViewById(R.id.output_scrollview);
		sv.post(new Runnable() {
			public void run() {
	            sv.fullScroll(View.FOCUS_DOWN);
	            findViewById(R.id.command).requestFocus();
	        }
	    });
	}
	
	public void appendChar(String ch) {
		//pull out the last line in the output, append the char, and place it back in the queue
		if (ch != null && !ch.equals("")) {
			StringBuilder sb = new StringBuilder();
			String line = null;
			if ((line = outputLines.tail()) != null) {
				sb.append(line);
			}
			sb.append(ch);
			outputLines.add(sb.toString());
		}
		
		TextView prompt = (TextView) findViewById(R.id.update_text);
		prompt.setText(outputLines.toString());
		
		final ScrollView sv = (ScrollView) findViewById(R.id.output_scrollview);
		sv.post(new Runnable() {
			public void run() {
	            sv.fullScroll(View.FOCUS_DOWN);
	            findViewById(R.id.command).requestFocus();
	        }
	    });
		
	}
	
	/**
	 * Append a prompt to the output
	 */
	public void appendPrompt() {
		String ps1 = buildPromptString(curWrkDir);
		assert ps1 != null;
		appendOutput(ps1);
	}
	
	public void clearOutput() {
		outputLines.clear();
	}

	public String getCurWrkDir() {
		return curWrkDir;
	}

	public void setCurWrkDir(String curWrkDir) {
		this.curWrkDir = curWrkDir;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  super.onSaveInstanceState(savedInstanceState);
	  //store the current output lines as a single string.
	  //TODO save as array list or some other supported data type
	  savedInstanceState.putString("outputLines", outputLines.toString());
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	 
	}
	
	private class LimitedQueue<E> extends LinkedList<E> {

		// Auto generated UID
		private static final long serialVersionUID = -7509308122323737391L;
		private final int limit;

	    public LimitedQueue(int limit) {
	        this.limit = limit;
	    }

	    @Override
	    public boolean add(E o) {
	        super.add(o);
	        while (size() > limit) {
	        	super.remove();
	        }
	        return true;
	    }
	    
	    public E tail() {
	    	E tailObj = this.removeLast();
	    	return tailObj;
	    }
	    
	    @Override
	    public String toString() {
	    	StringBuilder sb = new StringBuilder();
	    	for (E line: this) {
	    		if (line instanceof String) {
	    			sb.append(line + "\n");
	    		} else {
	    			sb.append(line.toString() + "\n");
	    		}
	    	}
	    	return sb.toString();
	    }
	}
}
