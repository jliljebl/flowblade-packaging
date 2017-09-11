package animator.phantom.controller;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Component;
import java.io.File;
import java.net.URL;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import java.awt.event.WindowEvent;

import animator.phantom.project.MovieFormat;

public class AbstractApplication
{
	public static final String FILE_URL_PROTOCOL = "file";
	public static final String JAR_URL_PROTOCOL = "jar";
	public static final String CLASS_PATH_TO_THIS = "animator/phantom/controller/Application.class";
	public static final String JAR_PATH_PART = "/Phantom2D.jar!";

	public static String PERSISTANCE_PATH_IN_FILESYSTEM_FOR_JAR = "/phantomeditor.xml";
	public static String PERSISTANCE_PATH_IN_JAR = "res/persistance/";
	public static String RESOURCE_PATH = "/res/";
	public static String PERSISTANCE_PATH = RESOURCE_PATH + "persistance/";
	public static String LANG_PATH = RESOURCE_PATH + "lang/";
	public static String FORMAT_PATH = RESOURCE_PATH + "format/";

	public static boolean inJar = false;
	public static String filePrefPathForJar;

	public static final int PREVIEW_RENDER = 0;
	public static final int WRITE_RENDER = 1;
	public static int currentRenderType = -1;
	public static boolean projectLoading = false;

	public String homePath;

	public void detectJarAndPaths()
	{
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
			System.out.println( "This is not running in file system or jar?");
		}

		//--- Get home path and set RESOURCE_PATH, PERSISTANCE_PATH and LANG_PATH, FORMAT_PATH
		//--- if were not running in jar
		String urlPath = urlToThisClass.getPath();
		urlPath = urlPath.substring( 0, urlPath.length() - CLASS_PATH_TO_THIS.length() - 1 );
		if( inJar )
			urlPath = urlPath.substring( 5, urlPath.length() - JAR_PATH_PART.length() );

		homePath = urlPath;

		RESOURCE_PATH = homePath + RESOURCE_PATH;
 		PERSISTANCE_PATH = homePath + PERSISTANCE_PATH;
 		LANG_PATH = homePath + LANG_PATH;
 		FORMAT_PATH = homePath + FORMAT_PATH;
	 }

	 public void readEditorPersistance()
	 {
		 //--- Read editor persistence for lang, recent documents, plugin dir, import dir etc...
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
	 }

	 public void setLAF( MetalTheme appTheme, Component appFrame )
	 {
		 try {
			 MetalLookAndFeel.setCurrentTheme( appTheme );
			 UIManager.setLookAndFeel(new MetalLookAndFeel());
			 SwingUtilities.updateComponentTreeUI( appFrame );

		 } catch(Exception e) {
			 e.printStackTrace();
			 System.out.println("Error setting LAF: " + e);
		 }
	 }

	 public MovieFormat getStartupFormat( String profileUnderScoreDesc )
	 {
		 MovieFormat startupFormat = null;
		 if (profileUnderScoreDesc != null)
		 {
			 for( int i = 0; i < MovieFormat.formats.size(); i++ )
			 {
				 if (profileUnderScoreDesc.equals(MovieFormat.formats.elementAt( i ).getUnderscrorName() ))
				 {
					 startupFormat = MovieFormat.formats.elementAt( i );
				 }
			 }
		 }
		 return startupFormat;
	 }

  	//--- We're blocking some updates that will fire when project data is created.
 	public static boolean isLoading(){ return projectLoading; }

 	//--- layout calculations need this
 	public static int getParamEditHeight()
 	{
 		int SCREEN_HEIGHT = getUsableScreen().height;
 		return SCREEN_HEIGHT / 2;
 	}

 	//--- layout calculations need this
 	public static Dimension getUsableScreen()
 	{
 		return Toolkit.getDefaultToolkit().getScreenSize();
 	}

 	//--- Pass control to the one who set it's self being doing rendering
 	public static void renderAbort()
 	{
 		System.out.println( "Application.renderAbort()" );
 		if( currentRenderType == WRITE_RENDER ) RenderModeController.frameRendererAborted();
 	}
 	//--- A code initiating rendering sets this when it starts rendering
 	//--- so we can return to correct place after abort.
 	public static void setCurrentRenderType( int rType ) { currentRenderType = rType; }

 	//-------------------------------------------------- PATH, JAR HANDLING
 	public static String getResourcePath(){ return RESOURCE_PATH; }
 	public static String getFormatPath(){ return FORMAT_PATH; }
 	public static boolean inJar(){ return inJar; }

	//--- Unused WindowListener methods
	public void windowActivated(WindowEvent e){}
	public void windowClosed(WindowEvent e){}
	public void windowDeactivated(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowOpened(WindowEvent e) {}

	class ProjectOpenUpdate extends Thread
	{
		public ProjectOpenUpdate()
		{

		}

		//--- Render, set and display bg image.
		public void run()
		{
			try
			{
				Thread.sleep(500);
				GUIComponents.viewSizeSelector.setSelected(0);
				GUIComponents.viewSizeSelector.setSelected(8);
				Thread.sleep(1000);
				GUIComponents.viewEditor.setDisplayWaitIcon( false );
				GUIComponents.viewEditor.repaint();
			}
			catch(Exception e){}
		}
	}//end class ProjectOpenUpdate

}//end class
