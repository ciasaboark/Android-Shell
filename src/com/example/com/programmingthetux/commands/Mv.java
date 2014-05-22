package com.example.com.programmingthetux.commands;

import com.example.com.programmingthetux.tutorial.MainActivity;

import android.widget.TextView;

public class Mv extends Command {

	public Mv() {
		
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
