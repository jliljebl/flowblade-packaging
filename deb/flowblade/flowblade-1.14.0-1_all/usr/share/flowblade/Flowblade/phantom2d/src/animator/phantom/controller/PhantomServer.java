package animator.phantom.controller;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URL;

import org.w3c.dom.Document;

import animator.phantom.blender.Blender;
import animator.phantom.project.MovieFormat;
import animator.phantom.project.Project;
import animator.phantom.undo.PhantomUndoManager;
import animator.phantom.xml.PhantomXML;

public class PhantomServer extends Application 
{
	private PhantomServerSocketListener socketlistener;
	private String socketNumberFile;
	
	public PhantomServer( String[] args )
	{
		socketNumberFile = args[1];
		System.out.println( socketNumberFile );
	}
			
	public void startUp()
	{
		System.out.println( "//----------------------- THREADED BRACH -------------------------------//" );

		//System.out.println("LD Library Path:" + System.getProperty("java.library.path"));

		//--- Lets find out where we are and set paths.
		ClassLoader loader = getClass().getClassLoader();
		URL urlToThisClass = loader.getResource( CLASS_PATH_TO_THIS );

		if( urlToThisClass.getProtocol().equals( FILE_URL_PROTOCOL ) )
		{
			System.out.println( "App running in file system.");
			
		}
		else if( urlToThisClass.getProtocol().equals( JAR_URL_PROTOCOL ) )
		{
			System.out.println( "App running in jar.");
			inJar = true;
		}
		else
		{
			System.out.println( "Unable to determine run environment.");
			System.exit(1);
		}

		//--- Get home path and set RESOURCE_PATH, PERSISTANCE_PATH and LANG_PATH
		String urlPath = urlToThisClass.getPath();
		urlPath = urlPath.substring( 0, urlPath.length() - CLASS_PATH_TO_THIS.length() - 1 );
		if( inJar )
			urlPath = urlPath.substring( 5, urlPath.length() - JAR_PATH_PART.length() );

		String homePath = urlPath;

		if( !inJar )
		{
			RESOURCE_PATH = homePath + RESOURCE_PATH;
			PERSISTANCE_PATH = homePath + PERSISTANCE_PATH;
			LANG_PATH = homePath + LANG_PATH;
		}

		FORMAT_PATH = homePath + FORMAT_PATH;
		System.out.println("app home path:" + homePath );
		System.out.println("FORMAT_PATH:" + FORMAT_PATH );

		//--- Read editor persistance for lang, recent documents, plugin dir, import dir etc...
		if( !inJar )
		{
			EditorPersistance.read( PERSISTANCE_PATH + EditorPersistance.DOC_NAME, false );
		}
		else 
		{
			//--- if running in jar we might have to create prefdoc in file system out side jar
			filePrefPathForJar = homePath + PERSISTANCE_PATH_IN_FILESYSTEM_FOR_JAR;
			File prefTest = new File( filePrefPathForJar );
			if( prefTest.exists() )
			{
				EditorPersistance.read( filePrefPathForJar, false );
			}
			else
			{
				//--- Load default prefs from jar, the write to file system and read again from file system
				EditorPersistance.read( PERSISTANCE_PATH_IN_JAR + EditorPersistance.DOC_NAME, true );
				EditorPersistance.write( filePrefPathForJar );
				EditorPersistance.read( filePrefPathForJar, false );
			}
		}
		
		//--- Start bringing app up
		AppUtils.printTitle("PHANTOM SERVER" );

		MemoryManager.initMemoryManager();
		PhantomUndoManager.init();
		
		RenderModeController.reset();
		RenderModeController.setFrameName( "frame" );
		
		// We need dummy project present for some object creation
		MovieFormat format = MovieFormat.DEFAULT;
		Project project = new Project( "untitled.phr", format );
		ProjectController.setProject( project );
		
		IOPLibraryInitializer.init();
		
		socketlistener = new PhantomServerSocketListener(this);
		socketlistener.createSocket();
		int port = socketlistener.getLocalPort();
		socketlistener.start();

		writeSocketFile( port, socketNumberFile );
		
		AppUtils.printTitle( "PHANTOM SERVER RUNNING!" );
		
		//test();
	}

	public void loadProject( String path )
	{
		File loadFile = new File( path );
		Document doc = PhantomXML.loadXMLDoc( loadFile.getAbsolutePath() );
		Project project = PhantomXML.loadProject( doc );

		AppUtils.printTitle("OPEN PROJECT " + project.getName() );

		//--- Set project.
		ProjectController.reset();
		ProjectController.setProject( project );
		
		//--- Blender
		Blender.initBlenders();

		//--- Notify MemoryManager to start guessing
		MemoryManager.calculateCacheSizes();
	}

	public void setRenderFolder( String path )
	{
		RenderModeController.setWriteFolder( new File(path) );
	}
	
	/*
	public void updateParamValue( int nodeId, String paramId, Vector<String> valueTokens )
	{
		RenderFlow flow = ProjectController.getFlow();
		ImageOperation iop = flow.getNode( nodeId ).getImageOperation();
		Param p = iop.getParam( paramId );
		PhantomServerParameter.writeParamValue( p, valueTokens );
	}
	*/
	public void renderFrame( int frame )
	{
		WriteRenderThread wrt = new WriteRenderThread( frame, frame + 1, RenderModeController.getFrameName() );
		wrt.setUpdateRenderWindow( false );
		wrt.start();
	}
	
	public void shutdown()
	{
		System.out.println("Phantom server shutdown");
		System.exit(0);
	}

		/*
	private void test()
	{
		loadProject( "/home/janne/test/servertest/simpleproject.phr" );
		setRenderFolder( "/home/janne/test/phantom_server_frames/" );
		int nodeId = 0;
		String paramId = "4";
		Vector<String> valueTokens = new Vector<String>();
		valueTokens.add("255");
		valueTokens.add("136");
		valueTokens.add("136");

		//<p blue="136" green="136" id="4" name="Color" red="136" t="ColorParam"/>
		//updateParamValue( nodeId, paramId, valueTokens );
		renderFrame( 3 );
	}
*/
	private void writeSocketFile( int port, String path )
	{
	    BufferedWriter writer = null;
	    try 
	    {
	        File portFile = new File( path );

	        String portNumber = Integer.toString( port );
	        writer = new BufferedWriter(new FileWriter( portFile ));
	        writer.write( portNumber );
	    } 
	    catch (Exception e) 
	    {
	        e.printStackTrace();
	    } 
	    finally 
	    {
	        try 
	        {
	            // Close the writer regardless of what happens...
	            writer.close();
	        } 
	        catch (Exception e){}
	    }
	}

}//end class
