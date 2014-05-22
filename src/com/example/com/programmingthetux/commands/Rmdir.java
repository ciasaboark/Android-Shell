package com.example.com.programmingthetux.commands;

import com.example.com.programmingthetux.tutorial.MainActivity;

public class Rmdir extends Command {

	public Rmdir() {
		
	}

	@Override
	public int execute(MainActivity ctx, String[] parameters) {
		try {
			
		} catch(Exception e) { //can throw a file not found exception
			
		}
		return 0;
	}

	@Override
	public boolean takeParameters() {
		return true;
	}

}
