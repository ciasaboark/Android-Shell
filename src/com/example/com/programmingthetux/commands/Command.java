package com.example.com.programmingthetux.commands;

import com.example.com.programmingthetux.tutorial.MainActivity;

public abstract class Command {
	
	/* This method will take the parameters passed into the execute function
	 * and parse the parameters. I am not sure how I want to implement this method.
	 * Coming soon!
	 */
	public void parseCommands(String parameters) {
		/* To Do: Split up the commands into an array or something */
	}
	
	
	/* This method should never be called directly unless good reason. It will be
	 * called automatically during runtime execution. Also if the command expects parameters,
	 * make sure to check them against null in case the user does not give any parameters. 
	 * If you explicitly return false in the takeParameters() method then do not use the parameters
	 * argument at all. There is not telling what they will be 
	 * */
//	public abstract void execute(TextView view, String[] parameters);
	public abstract int execute(MainActivity context, String[] parameters);
	
	/* Return a boolean value stating if this command can take parameters. 
	 * All commands are expected to work fine without parameters as well. 
	 */
	public abstract boolean takeParameters();
}
