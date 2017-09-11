package animator.phantom.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class PhantomServerSocketListener extends Thread
{
    private PhantomServer server;
    private ServerSocket serverSocket;
    
    private final static String OK = "OK\n";
	private static final String ERROR = "ERROR";
	
    private final static String SHUTDOWN = "SHUTDOWN";
    private final static String LOAD = "LOAD";
    private final static String PARAM_VALUE = "PARAM_VALUE";
    private final static String RENDER_FRAME = "RENDER_FRAME";
    private final static String SET_FOLDER = "SET_FOLDER";
    
	public PhantomServerSocketListener(PhantomServer server)
	{
		this.server = server;
	}
	
	public void createSocket()
	{
		try
		{
			serverSocket = new ServerSocket(0);
			System.out.println("Phantom server listening on port: " + serverSocket.getLocalPort());
		}
		catch (IOException e) 
		{
			System.out.println("IOException");
			e.printStackTrace();
		}
	}
	
	public int getLocalPort(){ return serverSocket.getLocalPort(); }

	public void run()
	{
		Socket client  = null;
		BufferedReader in = null;
        PrintWriter out  = null;
		String line  = null;

		try
		{
			  client = serverSocket.accept();
		} 
		catch (IOException e) 
		{
		    System.out.println("Accept failed");
		    System.exit(-1);
		}

	    System.out.println("A client connection accepted");

	    try
	    {
			   in = new BufferedReader(new InputStreamReader( client.getInputStream()) );
			   out = new PrintWriter( client.getOutputStream(), true );
		} 
	    catch (IOException e) 
		{
			    System.out.println("Read failed");
			    System.exit(-1);
		}

	    System.out.println("Streams opened successfully");

	    boolean running = true;
	    while( running )
	    {
	    	try
	    	{
	    		System.out.println("running");
	    		line = in.readLine();
	    		if ( line == null )
	    		{
	    			 System.out.println("line == null");
	    			running = false;
		    	}
		    	else
		    	{
		    		String answer = handleCommand(line);
		    		out.println(answer);
		    	}
	    	} 
	    	catch (IOException e) 
	    	{
			        System.out.println("Read failed");
			        System.exit(-1);
			}
		}
        System.exit(0);
	}
	
	private String handleCommand( String commandLine )
	{
		try
		{
	            System.out.println("Received: " + commandLine);

	            String[] tokens = commandLine.split("\\s");
	            String command = tokens[0];

	            if(command.equals(LOAD))
	            {
	            	String path = tokens[1];
		            server.loadProject( path );
	            }
	            else if(command.equals(PARAM_VALUE))
	            {
	    			System.out.println("render frame");
	    			PhantomServerParameter.setParamValue( tokens );
	            }
	            else if(command.equals(RENDER_FRAME))
	            {
	    			System.out.println("render frame");
	            	int frame = Integer.parseInt( tokens[1] );
	    			System.out.println(frame);
		            server.renderFrame(frame);
	            }
	            else if(command.equals(SET_FOLDER))
	            {
	            	server.setRenderFolder( tokens[1] );
	            }
	            else if(command.equals(SHUTDOWN))
	            {
					System.exit(0);
	            }
	            else
	            {
	            	return ERROR + " unrecognized command";
	            }
		}
		catch (Exception e) 
		{
			System.out.println("Exception");
			e.printStackTrace();
			return "ERROR";
		}
		
		return OK;
	}

}//end class
