package com.example.com.programmingthetux.commands;

import com.example.com.programmingthetux.tutorial.MainActivity;

public class Which extends Command {

	public Which() {
		
	}
	
	@Override
	public int execute(MainActivity ctx, String[] parameters) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean takeParameters() {
		return true;
	}

}
