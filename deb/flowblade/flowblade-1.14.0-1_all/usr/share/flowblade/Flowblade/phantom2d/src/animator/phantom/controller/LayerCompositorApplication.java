package animator.phantom.controller;

/*
    Copyright Janne Liljeblad.

    This file is part of Phantom2D.

    Phantom2D is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Phantom2D is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Phantom2D.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.plaf.metal.MetalTheme;

import animator.phantom.blender.Blender;
import animator.phantom.gui.LayerCompositorFrame;
import animator.phantom.project.LayerCompositorProject;
import animator.phantom.project.MovieFormat;
import animator.phantom.project.Project;
import animator.phantom.undo.PhantomUndoManager;


//--- Logic and application wide state, including app initializing, window management, opening projects and render aborts.
public class LayerCompositorApplication extends AbstractApplication implements WindowListener
{
	//--- There can only be one.
    public static LayerCompositorApplication app;
	public static LayerCompositorApplication getApplication(){ return app; }

	//--- Flag to load plugins when opening default project
	public static boolean pluginsLoaded = false;

	//--- Windows
	private LayerCompositorFrame animatorFrame;

	//--- Window params.
	public static int SMALL_WINDOW_WIDTH = 320;

	private ProjectOpenUpdate popenUpdate;

	public LayerCompositorApplication(){ app = this; }

	public void startUp(String profileUnderScoreDesc, String diskCacheDirPath)
	{
		AppUtils.printTitle("LAYER COMPOSITOR 2D" );

		//--- Detect runtime env
		detectJarAndPaths();

		MLTFrameServerController.init( homePath, diskCacheDirPath );

		System.out.println("");
		System.out.println("APP ROOT PATH:" + homePath );

		//--- User prefs etc.
	 	readEditorPersistance();

		MemoryManager.initMemoryManager();

		//--- Theme needs to be initialized before window is created.
		MetalTheme appTheme = new DarkTheme();

		//--- Create window
		animatorFrame = new LayerCompositorFrame();
		animatorFrame.setVisible( false );
		animatorFrame.addWindowListener( this );

		//--- Look and feel
		setLAF( appTheme, animatorFrame );

		MovieFormat startupFormat = getStartupFormat( profileUnderScoreDesc );

		//--- There is always a document open.
		if (startupFormat != null )
		{
			Project project = new Project( "untitled.phr", startupFormat );
			openProject( project );
		}
		else
		{
			openDefaultProject();
		}

		//--- Display info window on first run.
		if( EditorPersistance.getBooleanPref( EditorPersistance.FIRST_RUN ) )
		{
			EditorPersistance.setPref( EditorPersistance.FIRST_RUN, false );
			EditorPersistance.write();
		}

		AppUtils.printTitle( "APPLICATION LOADED!" );
	}

	public void openDefaultProject()
	{
		RenderModeController.reset();

		MovieFormat format = MovieFormat.DEFAULT;
		Project project = new Project( "untitled.phr", format );
		openProject( project );
	}

	public void openProject( Project project )
	{
		AppUtils.printTitle("OPEN PROJECT " + project.getName() );

		//--- Block cache updates
		projectLoading = true;

		//--- Save view editor size for auto size setting for project dimensions.
		if (GUIComponents.animatorFrame != null)
		{
			Dimension viewPortSize = GUIComponents.animatorFrame.getViewEditorSize();
			EditorsController.setViewEditorSize(viewPortSize);
		}

		//--- (re-)read editor persistance for recent documents
		if( !inJar )
			EditorPersistance.read( PERSISTANCE_PATH + EditorPersistance.DOC_NAME, false );
		else
			EditorPersistance.read( filePrefPathForJar, false );

		//--- reset some global data.
		GUIComponents.LCReset();
		TimeLineController.reset();
		PreviewController.reset();

		//--- Set project.
		ProjectController.reset();
		ProjectController.setProject( project );
		AppData.setProject( project );
		
		//--- 
		AppData.setLayerProject( new LayerCompositorProject() );
		

		//--- Blender
		Blender.initBlenders();

		//--- Editor data.
		IOPLibraryInitializer.init();
		TimeLineController.init();

		//--- Undo
		PhantomUndoManager.init();

		//--- Windows
		animatorFrame.initializeEditor();

		//--- display grey first
		GUIComponents.viewEditor.setDisplayWaitIcon( true );

		//--- Notify MemoryManager to start guessing
		MemoryManager.calculateCacheSizes();

		//---
		animatorFrame.setVisible( true );
		//GUIComponents.renderFlowPanel.setIgnoreRepaint( false );// bugs when not visible?

		ProjectController.updateProjectInfo();

		//--- First render for view editor
		EditorsController.fillViewEditor();
		EditorsController.displayCurrentInViewEditor( false );
		int viewSize = GUIComponents.viewSizeSelector. getViewSize();
		EditorsController.setViewSize( viewSize );

		//--- Display loaded clips
		TimeLineController.loadClips();
		TimeLineController.initClipEditorGUI();

		//--- Set cache sizes with current information.
		MemoryManager.initCache();

		GUIComponents.animatorFrame.centerViewEditor();
		//--- Unblock cache and view editor updates.
		projectLoading = false;

		popenUpdate = new ProjectOpenUpdate();
		popenUpdate.start();
	}


	//---------------------------------------------- Hnadled WINDOW EVENTS
	public void windowClosing(WindowEvent e)
	{
		LayerCompositorMenuActions.quit();//catch for close confirmation
	}

}//end class
