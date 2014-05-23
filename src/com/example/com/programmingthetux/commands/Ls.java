package com.example.com.programmingthetux.commands;

import java.io.File;
import java.io.FilePermission;
import java.security.AccessController;

import com.example.com.programmingthetux.tutorial.MainActivity;

public class Ls extends Command {

	public Ls() {
		
	}
	
	@Override
	/*
	 * If more than one directory is given, just list the contents of the first one 
	 * and discard the second. Further more if no director is given at all just diplay the files 
	 * in the present working directory.
	 * 
	 * TO-DO: Ls is only displaying the contents of a directory. Still need to add the options for file 
	 * permissions and inodes. 
	 */
	public int execute(MainActivity ctx, String[] parameters) {
		File file;
		File[] list = null;
		
		int returnCode = 1;
		if (parameters.length == 0) { //list the current directory
			file = new File(ctx.getCurWrkDir());
			list = file.listFiles();
			try {
				if(list.length > 0) {
					for(int i = 0; i < list.length; i++) {
						ctx.appendOutput(list[i].toString());
					}
				}
			} catch (Exception e) { 
				ctx.appendOutput("insufficient privileges");
			}
		} else if(parameters.length > 0) {
				file = new File(parameters[0]);
				if(file.isDirectory()) {
					try {
						if (file != null) {
						list = file.listFiles();
					
							if(list.length > 0) {
								for(int i = 0; i < list.length; i++) {
								ctx.appendOutput(list[i].toString());
								}
							}
						}
					} catch (Exception e) {
						ctx.appendOutput("insufficient privileges");
					}
				}
				else {
					ctx.appendOutput("No such directory exits");
				}					
			}
		return returnCode;
	}

	@Override
	public boolean takeParameters() {
		return true;
	}

}
