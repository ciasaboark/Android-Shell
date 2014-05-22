package com.example.com.programmingthetux.commands;

import com.example.com.programmingthetux.tutorial.MainActivity;

public class Echo extends Command {

	public Echo() {
		
	}
	
	@Override
	public int execute(MainActivity ctx, String[] parameters) {
		return 0;
	}

	@Override
	public boolean takeParameters() {
		return true;
	}
}
