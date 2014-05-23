package com.example.com.programmingthetux.commands;

import com.example.com.programmingthetux.tutorial.MainActivity;

public class Pwd extends Command {

	public Pwd() {
		
	}
	
	@Override
	public int execute(MainActivity ctx, String[] parameters) {
		ctx.appendOutput(ctx.getCurWrkDir().toString());
		return 0;
	}
	
	@Override
	public boolean takeParameters() {
		return false;
	}

}
