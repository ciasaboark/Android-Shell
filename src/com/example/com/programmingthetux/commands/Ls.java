package com.example.com.programmingthetux.commands;

import java.io.File;

import com.example.com.programmingthetux.tutorial.MainActivity;

public class Ls extends Command {

	public Ls() {
		
	}
	
	@Override
	public int execute(MainActivity ctx, String[] parameters) {
		File file;
		File[] list;
		
		int returnCode = 1;
		if (parameters.length == 0) {
			file = new File(ctx.getCurWrkDir());
			list = file.listFiles();
			
			if(list.length > 0) {
				for(int i = 0; i < list.length; i++) {
					ctx.appendOutput(list[i].toString());
				}
			}
			else if(list.length > 0) {
				file = new File(parameters[0]);
				
				if (file != null) {
					list = file.listFiles();
					
					if(list.length > 0) {
						for(int i = 0; i < list.length; i++) {
							ctx.appendOutput(list[i].toString());
						}
					}
				}
			}
		}
		return 0;
	}

	@Override
	public boolean takeParameters() {
		return true;
	}

}
