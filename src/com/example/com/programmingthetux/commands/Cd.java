package com.example.com.programmingthetux.commands;

import com.example.com.programmingthetux.tutorial.MainActivity;

public class Cd extends Command {

	public Cd() {
		
	}
	
	@Override
	public int execute(MainActivity ctx, String[] parameters) {
		// TODO Auto-generated method stub
		int returnCode = 0;
		if (parameters.length == 0) {
			ctx.appendOutput("cd: error: no parameters given");
			returnCode = 1;
		} else if (parameters.length > 1) {
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < parameters.length; i++) {
				sb.append(parameters[i] + " ");
			}
			ctx.appendOutput("cd: " + sb.toString() + " : too many arguments given");
		} else {
			//TODO check that a directory with the given name exists
			ctx.setCurWrkDir(ctx.getCurWrkDir() + parameters[0] + "/");
			ctx.appendOutput(""); //append an empty string so the new directory shows
		}
		return returnCode;
	}

	@Override
	public boolean takeParameters() {
		return true;
	}

}
