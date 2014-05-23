package com.example.com.programmingthetux.commands;

import java.io.File;
import java.io.FilePermission;
import java.security.AccessController;

import android.util.Log;

import com.example.com.programmingthetux.tutorial.MainActivity;

public class Cd extends Command {

	public Cd() {
		
	}
	
	@Override
	public int execute(MainActivity ctx, String[] parameters) {
		int returnCode = 1;
		if (parameters.length == 0) {
			ctx.appendOutput("cd: error: no parameters given");
		} else if (parameters.length > 1) {
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < parameters.length; i++) {
				sb.append(parameters[i] + " ");
			}
			ctx.appendOutput("cd: " + sb.toString() + " : too many arguments given");
		} else {
			//TODO check that a directory with the given name exists
			File f = null;
			try {
				if (parameters[0].startsWith("/")) {
					f = new File(parameters[0]).getCanonicalFile();
				} else {
					f = new File(ctx.getCurWrkDir() + parameters[0]).getCanonicalFile();
					Log.d("CD", f.getCanonicalPath() + "   " + f.toString());
					if (f.getCanonicalPath().equals("/")) {
						//resolving '/somedir/..' to a canonical path will result in a path of '' instead of '/'
						f = new File("/");
					}
				}
				if (f.isDirectory()) {
					try {
						AccessController.checkPermission(new FilePermission(ctx.getCurWrkDir() + parameters[0], "read"));
						returnCode = 0;
						ctx.setCurWrkDir(f.getCanonicalPath() + (f.getCanonicalPath().endsWith("/") ? "" : "/"));
						ctx.appendOutput(""); //append an empty string so the new directory shows
					} catch (SecurityException e) {
						ctx.appendOutput("cd: " + parameters[0] + ": no read permissions");
					}
				} else {
					ctx.appendOutput("cd: " + parameters[0] + ": no such directory");
				}
			} catch (Exception e) {
				//we should be dropped here due to security exceptions or IO exceptions
				ctx.appendOutput("cd: " + parameters[0] + ": can not open");
			}
			
		}
		return returnCode;
	}

	@Override
	public boolean takeParameters() {
		return true;
	}

}
