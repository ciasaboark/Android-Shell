package com.example.com.programmingthetux.commands;

import android.widget.TextView;

public class Rm extends Command {
	
	public Rm() {
		
	}

	@Override
	public void execute(TextView view, String[] parameters) {
		try {
			
		} catch(Exception e) { //can throw a file not found exception
			
		}

	}

	@Override
	public boolean takeParameters() {
		return true;
	}

}
