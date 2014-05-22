package com.example.com.programmingthetux.commands;

import com.example.com.programmingthetux.tutorial.MainActivity;


public class Ping extends Command {
	
	public Ping() {
		
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
