package com.example.com.programmingthetux.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.example.com.programmingthetux.tutorial.MainActivity;

public class GenericRunner extends Command {
	private MainActivity ctx;
	
	@Override
	public int execute(final MainActivity context, String[] parameters) {
		this.ctx = context;
		int returnCode = 1;
		//assume that the first parameter is the command to run
		if (parameters != null && parameters.length > 0) {
			String command = parameters[0];
			if (parameters.length > 1) {
				parameters = Arrays.copyOfRange(parameters, 1, parameters.length);
			} else {
				parameters = new String[] {};
			}
			try {
				File f = new File(command).getCanonicalFile();
				if (f.isFile() && f.canExecute() && f.canRead()) {
					//run this command and capture all output
					Process p = new ProcessBuilder(command).redirectErrorStream(true).start();
					StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT", this);

					// start gobbler
					outputGobbler.start();
					
					//Since it's possible that the program running may not terminate without
					//the user pressing ^C (or some other key combo) we need to write out lines
					//as they come, instead of waiting for the process to terminate.
					
					//so long as the process is not yet terminated and there is more output
					//left keep printing lines.
					while (!isProcessTerminated(p) && !outputGobbler.isFinished()) {
						String line = outputGobbler.getOutputLine();
						if (line != null) {
							ctx.appendOutput(line);
						}
					}
					
					//write out any remaining lines
					for (String line: outputGobbler.getOutputLines()) {
						ctx.appendOutput(line);
					}
					
					//TODO get the real return code for this process
					returnCode = p.exitValue();

				} else {
					context.appendOutput("error: " + command + ": can not find program");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		return returnCode;
		
	}

	@Override
	public boolean takeParameters() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public void writeLine(String line) {
		if (line != null)
			ctx.appendOutput(line);
	}
	
	/**
	 * Test if the given process has terminated.
	 * @param p the Process to test
	 * @return true if this process has terminated, false otherwise
	 */
	public boolean isProcessTerminated(Process p) {
		boolean processTerminated = false;
		try {
			p.exitValue();
			processTerminated = true;
		} catch (IllegalThreadStateException e) {}
		return processTerminated;
	}

	private class StreamGobbler extends Thread {
		private InputStream is;
	    private String type;
	    private GenericRunner context;
//	    private ArrayList<String> output = new ArrayList<String>();
	    private ConcurrentLinkedQueue<String> output = new ConcurrentLinkedQueue<String>();
	    private boolean isFinished = false;

	    public StreamGobbler(InputStream is, String type, GenericRunner context) {
	        this.is = is;
	        this.type = type;
	        this.context = context;
	    }
	    
	    public ConcurrentLinkedQueue<String> getOutputLines() {
	    	return output;
	    }
	    
	    //TODO make this thread safe
	    public String getOutputLine() {
	    	String line = null;
	    	if (!output.isEmpty()) {
	    		line = output.poll();
	    	}
	    	return line;
	    }
	    
	    public boolean isFinished() {
	    	return isFinished;
	    }
	    
	    @Override
	    public void run() {
	        try {
	            InputStreamReader isr = new InputStreamReader(is);
	            BufferedReader br = new BufferedReader(isr);
	            String line = null;
	            while ((line = br.readLine()) != null) {
	                output.add(line);
	            }
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	        	isFinished = true;
	        }
	    }
	}
}
