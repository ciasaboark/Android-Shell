package com.example.com.programmingthetux.commands;

import com.example.com.programmingthetux.tutorial.MainActivity;

import android.widget.TextView;

public class Clear extends Command {

	public Clear() {
		
	}

	@Override
	public int execute(MainActivity ctx, String[] parameters) {
		ctx.clearOutput();
		return 0;
	}

	@Override
	public boolean takeParameters() {
		//The GNU version of clear will happily take (and ignore) arguments
		return true;
	}

}
