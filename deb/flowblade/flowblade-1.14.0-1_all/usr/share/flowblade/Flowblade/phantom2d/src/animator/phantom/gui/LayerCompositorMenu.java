package animator.phantom.gui;

/*
    Copyright Janne Liljeblad 2006,2007,2008

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import animator.phantom.controller.EditorPersistance;
import animator.phantom.controller.FlowController;
import animator.phantom.controller.IOPLibraryInitializer;
import animator.phantom.controller.LayerCompositorMenuActions;
import animator.phantom.controller.PreviewController;
import animator.phantom.controller.ProjectController;
import animator.phantom.controller.RenderModeController;
import animator.phantom.controller.TimeLineController;
import animator.phantom.controller.UserActions;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.project.MovieFormat;
import animator.phantom.renderer.FileSource;
import animator.phantom.renderer.IOPLibrary;
import animator.phantom.renderer.ImageOperation;

public class LayerCompositorMenu extends JMenuBar implements ActionListener, MenuBarCallbackInterface
{
	//--- File
	JMenuItem openProject;
	JMenuItem openRecent;
	JMenuItem saveProject;
	JMenuItem saveProjectAs;
	JMenuItem closeProject;
	JMenuItem quit;

	//--- Edit
	JMenuItem undo;
	JMenuItem redo;
	JMenuItem deleteSelected;
	JMenuItem renameSelected;
	JMenuItem enableSelected;
	JMenuItem disableSelected;
	JMenuItem cloneSelected;
	JMenuItem memorySettings;
	JMenuItem diskCache;
	JMenuItem editorLayout;
	JMenuItem projectSettings;
	JMenuItem kfPreferences;

	//--- Layer Menu
	JMenu newLayer;
	JMenu addJustmentLayer;
	JMenu mediaMenu;

	//--- Project Menu
	JMenuItem addImage;
	JMenuItem addImageSequence;
	JMenuItem addVideo;
	JMenuItem noRefs;

	//--- Render
	JMenuItem threadsSettings;
	JMenuItem previewCurrent;
	JMenuItem previewFromCurrent;
	JMenuItem previewFromStart;
	JMenuItem renderMovie;

	//--- Help
	JMenuItem keyboardShortcuts;
	JMenuItem about;

	//----
	private static int SHORT_CUT_PLACE = 25;

	//---
	Vector<JMenuItem> newProjectItems = new Vector<JMenuItem>();

	public LayerCompositorMenu()
	{
		super();

		//---------------------------------- File menu
		JMenu fileMenu = new JMenu("File");

		JMenu newSelect = new JMenu( "New" );
		JMenuItem customItem = new JMenuItem( "custom" );
		customItem.addActionListener(this);
		newSelect.add( customItem );
		newProjectItems.add( customItem );
		for( int i = 0; i < MovieFormat.formats.size(); i++ )
		{

			JMenuItem addItem = new JMenuItem( MovieFormat.formats.elementAt( i ).getName() );
			addItem.addActionListener(this);
			newSelect.add( addItem );
			newProjectItems.add( addItem );
		}
		fileMenu.add( newSelect );

		openProject = new JMenuItem("Open...");
		openProject.addActionListener(this);
		fileMenu.add(openProject);

		openRecent = new JMenu( "Open Recent" );
		updateRecentMenu();
		fileMenu.add( openRecent );

		fileMenu.addSeparator();

		saveProject = new JMenuItem( "Save" );
		saveProject.setAccelerator( KeyStroke.getKeyStroke( "ctrl pressed S"  ) );
		saveProject.addActionListener(this);
		fileMenu.add( saveProject );

		saveProjectAs = new JMenuItem("Save As...");
		saveProjectAs.addActionListener(this);
		fileMenu.add( saveProjectAs );

		fileMenu.addSeparator();

		closeProject = new JMenuItem( "Close" );
		closeProject.addActionListener(this);
		fileMenu.add( closeProject );

		quit = new JMenuItem( "Quit" );
		quit.addActionListener(this);
		quit.setAccelerator( KeyStroke.getKeyStroke( "ctrl pressed Q"  ) );
		fileMenu.add( quit );

		//------------------------------------ Edit menu
		JMenu editMenu = new JMenu("Edit");

		undo = new JMenuItem("Undo");
		undo.addActionListener(this);
		undo.setAccelerator( KeyStroke.getKeyStroke( "ctrl pressed Z"  ) );
		editMenu.add( undo );

		redo = new JMenuItem("Redo");
		redo.addActionListener(this);
		redo.setAccelerator( KeyStroke.getKeyStroke( "ctrl shift pressed Z"  ) );
		editMenu.add( redo );

		editMenu.addSeparator();

		deleteSelected = new JMenuItem("Delete");
		deleteSelected.addActionListener(this);
		editMenu.add( deleteSelected );

		renameSelected = new JMenuItem("Rename");
		renameSelected.addActionListener(this);
		editMenu.add( renameSelected );
		
		cloneSelected = new JMenuItem("Clone");
		cloneSelected.addActionListener(this);
		editMenu.add( cloneSelected );
		
		editMenu.addSeparator();
		
		memorySettings = new JMenuItem("Memory Manager...");
		memorySettings.addActionListener(this);
		editMenu.add( memorySettings );

		diskCache = new JMenuItem("Disk Cache...");
		diskCache.addActionListener(this);
		editMenu.add( diskCache );

		/*
		editorLayout = new JMenuItem("Panel sizes...");
		editorLayout.addActionListener(this);
		editMenu.add( editorLayout );

		kfPreferences = new JMenuItem("Keyframe Preferences...");
		kfPreferences.addActionListener(this);
		editMenu.add( kfPreferences );
		*/


		//------------------------------------ Layer menu
		JMenu layerMenu = new JMenu("Layer");
		JMenu newLayer = new JMenu("New");
		
		mediaMenu = new JMenu("Media");
		newLayer.add( mediaMenu );
		JMenu addJustmentLayerMenu = new JMenu("Adjustmnent Layer");
		buildIOPMenu( addJustmentLayerMenu, this );		
		newLayer.add( addJustmentLayerMenu );

		layerMenu.add( newLayer );
		
		JMenu layerEffect = new JMenu("Layer Effect");
		fillLayerEffectMenu( layerEffect );
		layerMenu.add( layerEffect );
		
		JMenu layerMask = new JMenu("Layer Mask");
		fillLayerMaskMenu( layerMask );
		layerMenu.add( layerMask );
		

		updateAppMediaMenu();
		
		
		
		//--------------------------- Project menu
		JMenu projectMenu = new JMenu("Project");
		
		addVideo = new JMenuItem("Add Video Clips...");
		addVideo.addActionListener(this);
		projectMenu.add( addVideo );

		addImage  = new JMenuItem("Add Images...");
		addImage.addActionListener(this);
		projectMenu.add( addImage );

		addImageSequence  = new JMenuItem("Add Image Sequence...");
		addImageSequence.addActionListener(this);
		projectMenu.add( addImageSequence );

		projectMenu.addSeparator();

		projectSettings = new JMenuItem("Project Settings...");
		projectSettings.addActionListener(this);
		projectMenu.add( projectSettings);
		

		//--- ------------------------------- Render menu
		JMenu renderMenu = new JMenu("Render");
		threadsSettings = new JMenuItem("Rendering Settings...");
		threadsSettings.addActionListener(this);
		renderMenu.add( threadsSettings );

		renderMenu.addSeparator();

		previewFromCurrent = new JMenuItem("Preview From Current" );
		previewFromCurrent.addActionListener(this);
		previewFromCurrent.setAccelerator( KeyStroke.getKeyStroke( "F9"  ) );
		renderMenu.add( previewFromCurrent );

		previewFromStart = new JMenuItem("Preview From Start" );
		previewFromStart.addActionListener(this);
		previewFromStart.setAccelerator( KeyStroke.getKeyStroke( "F10"  ) );
		renderMenu.add( previewFromStart );

		previewCurrent = new JMenuItem("Preview Current Frame" );
		previewCurrent.addActionListener(this);
		previewCurrent.setAccelerator( KeyStroke.getKeyStroke( "F11"  ) );
		renderMenu.add( previewCurrent );

		renderMenu.addSeparator();

		renderMovie = new JMenuItem("Render...");
		renderMovie.addActionListener(this);
		renderMovie.setAccelerator( KeyStroke.getKeyStroke( "F12"  ) );
		renderMenu.add( renderMovie );

		
		//----------------------------------- Help menu
		JMenu helpMenu = new JMenu("Help");

		keyboardShortcuts = new JMenuItem("Keyboard Shortcuts");
		keyboardShortcuts.addActionListener(this);
		helpMenu.add( keyboardShortcuts );

		helpMenu.addSeparator();

		about = new JMenuItem("About");
		about.addActionListener(this);
		helpMenu.add( about );

		add( fileMenu );
		add( editMenu );
		add( layerMenu );
		add( projectMenu );
		add( renderMenu );
		add( helpMenu );
	}

	//--- Adds key short cut to give place in string
	private static String getItemString( String item, String shortCut, int correction )
	{
		int middleStringLength = SHORT_CUT_PLACE - item.length() + correction;
		StringBuffer rBuf = new StringBuffer(item);
		for( int i = 0; i < middleStringLength; i++ ) rBuf.append(" ");
		rBuf.append( shortCut );
		return rBuf.toString();
	}

	//--- Builds "ImageOperation" menu using input from IOPLibrary
	public void buildIOPMenu( JMenu iopMenu, ActionListener listener )
	{
		Vector<String> groups = IOPLibrary.getGroupKeys();
		Vector<JMenu> groupMenus = getNodesMenus( groups, IOPMenuItem.ADJUSTMENT_LAYER_ITEM  );
		for( JMenu subMenu : groupMenus )
			iopMenu.add( subMenu );
	}

	private void fillLayerEffectMenu( JMenu effectMenu )
	{
		Vector<String> groups = IOPLibraryInitializer.getLayerEffectGroups();
		Vector<JMenu> groupMenus = getNodesMenus( groups, IOPMenuItem.LAYER_EFFECT_ITEM  );
		for( JMenu subMenu : groupMenus )
			effectMenu.add( subMenu );
	}

	private void fillLayerMaskMenu( JMenu maskMenu )
	{
		Vector<String> groups = IOPLibraryInitializer.getMasktGroups();
		Vector<JMenu> groupMenus = getNodesMenus( groups, IOPMenuItem.LAYER_MASK_ITEM  );
		for( JMenu subMenu : groupMenus )
			maskMenu.add( subMenu );	
	}
	@SuppressWarnings("unchecked")
	public Vector<JMenu> getNodesMenus( Vector<String> groups, int menuItemType )
	{
		Vector<JMenu> groupMenus = new Vector<JMenu>();

		for( String group : groups )
		{
			@SuppressWarnings("rawtypes")
			Vector iops = IOPLibrary.getGroupContents( group );
			Collections.sort( iops );
			JMenu subMenu = new JMenu( getItemString( group,"",0 ));
			for( int j = 0; j < iops.size(); j++ )
			{
				Object o = iops.elementAt( j );
				IOPMenuItem item = null;
				if( o instanceof ImageOperation )
				{
					ImageOperation iop = (ImageOperation) o;
					item = new IOPMenuItem( iop.getName(), iop.getClass().getName(), menuItemType );
				}
				else
				{
					PhantomPlugin p = (PhantomPlugin) o;
					item = new IOPMenuItem( p.getIOP().getName(), p.getClass().getName(), menuItemType );
				}
				item.addActionListener(this);
				subMenu.add( item );
			}
			groupMenus.add( subMenu );
		}
		return groupMenus;
	}

	public void updateRecentMenu()
	{
		openRecent.removeAll();
		Vector<File> recents = EditorPersistance.getRecentProjects();
		if( recents.size() == 0 )
		{
			RecentMenuItem none = new RecentMenuItem("none", null);
			none.setEnabled( false );
			openRecent.add( none );
		}
		else
		{
			for( File f : recents )
			{
				RecentMenuItem addr = new RecentMenuItem( f.getName(), f );
				addr.addActionListener(this);
				openRecent.add( addr );
			}
		}
	}

	public void updateAppMediaMenu()
	{
		mediaMenu.removeAll();



		Vector<FileSource> fileSources = ProjectController.getFileSources();

		if (fileSources.size() > 0)
		{
			LayerCompositorMenu.fillFileSourcesMenu(mediaMenu, this);
		}
		else
		{
			noRefs = new JMenuItem("No media sources");
			noRefs.setEnabled(false);
			noRefs.setFont(GUIResources.BASIC_FONT_ITALIC_14);
			mediaMenu.add(noRefs);
		}
	}

	public static void fillFileSourcesMenu( JMenu menu, ActionListener listener  )
	{
		Vector<FileSource> fileSources = ProjectController.getFileSources();
		if( fileSources.size() > 0 )
		{
			for( FileSource f : fileSources )
			{
				System.out.print(f.getName());
				MediaMenuItem addr = new MediaMenuItem( f.getName(), f );
				addr.addActionListener( listener );
				menu.add( addr );
			}
		}
	}

	//--------------------------------------------- MENU ACTIONS
	public void actionPerformed(ActionEvent e)
	{
		//-------------------------------------------- File menu
		for( int i = 0; i < newProjectItems.size(); i++ )
		{
			if( e.getSource() == newProjectItems.elementAt( i ) )
			{
				LayerCompositorMenuActions.newProject( i );
				return;
			}
		}
		if( e.getSource() == openProject ) LayerCompositorMenuActions.openProject();
		if( e.getSource() instanceof RecentMenuItem )
		{
			RecentMenuItem r = ( RecentMenuItem )e.getSource();
			if( r.getFile() == null ) return;
			LayerCompositorMenuActions.openProjectFile( r.getFile() );
			return;
		}
		if( e.getSource() == saveProject ) LayerCompositorMenuActions.saveProject();
		if( e.getSource() == saveProjectAs ) LayerCompositorMenuActions.saveProjectAs();
		if( e.getSource() == closeProject ) LayerCompositorMenuActions.close();
		if( e.getSource() == quit ) LayerCompositorMenuActions.quit();

		//--------------------------------------------- Edit menu
		if( e.getSource() == undo ) LayerCompositorMenuActions.undo();
		if( e.getSource() == redo ) LayerCompositorMenuActions.redo();
		if( e.getSource() == deleteSelected) LayerCompositorMenuActions.delete();
		if( e.getSource() == renameSelected ) LayerCompositorMenuActions.renameSelected();
		if( e.getSource() == cloneSelected ) LayerCompositorMenuActions.cloneSelected();
		if( e.getSource() == disableSelected ) LayerCompositorMenuActions.disableSelected();
		if( e.getSource() == enableSelected ) LayerCompositorMenuActions.enableSelected();
		if( e.getSource() == projectSettings ) LayerCompositorMenuActions.setProjectProperties();
		if( e.getSource() == kfPreferences ) LayerCompositorMenuActions.keyframePreferences();
		if( e.getSource() == memorySettings ) LayerCompositorMenuActions.setMemorySettings();
		if( e.getSource() == diskCache ) LayerCompositorMenuActions.diskCacheDialog();
		if( e.getSource() == editorLayout ) LayerCompositorMenuActions.setFlowWidth();

		//--------------------------------------------- Media menu
		if( e.getSource() == addImage )
		{
			new Thread()
			{
				public void run()
				{
					UserActions.addSingleFileSources(FileSource.IMAGE_FILE, -1, -1);
				}
			}.start();
		}
		if( e.getSource() == addImageSequence )
		{
			new Thread()
			{
				public void run()
				{
					UserActions.addFileSequenceSource();
				}
			}.start();
		}
		if( e.getSource() == addVideo )
		{
			new Thread()
			{
				public void run()
				{
					UserActions.addSingleFileSources(FileSource.VIDEO_FILE, -1, -1);
				}
			}.start();
		}
		if( e.getSource() instanceof MediaMenuItem )
		{
			MediaMenuItem source =  ( MediaMenuItem ) e.getSource();
			LayerCompositorMenuActions.addFileSourceLayer( source.getFileSource() );
			//FlowController.addToCenterFromFileSource( source.getFileSource() );
		}

		if( e.getSource() instanceof IOPMenuItem )
		{
			IOPMenuItem source = ( IOPMenuItem )e.getSource();
			if (source.getItemType() == IOPMenuItem.ADJUSTMENT_LAYER_ITEM)
				LayerCompositorMenuActions.addIOP( source.getIopClassName() );
			if (source.getItemType() == IOPMenuItem.LAYER_EFFECT_ITEM)
				LayerCompositorMenuActions.addLayerEffect( source.getIopClassName() );
			if (source.getItemType() == IOPMenuItem.LAYER_MASK_ITEM)
				LayerCompositorMenuActions.addLayerMask( source.getIopClassName() );
		}

		//-------------------------------------------------- Render menu.
		if( e.getSource() == threadsSettings ) LayerCompositorMenuActions.setThreadsAndBlenders();
		if( e.getSource() == previewCurrent ) PreviewController.renderAndDisplayCurrent();
		if( e.getSource() == previewFromStart ) PreviewController.renderAndPlayRange( 0, ProjectController.getLength() - 1 );
		if( e.getSource() == previewFromCurrent ) PreviewController.renderAndPlayRange( TimeLineController.getCurrentFrame(), ProjectController.getLength() - 1 );
		if( e.getSource() == renderMovie ) RenderModeController.writeMovie();

		//----------------------------------------------------- Help menu
		if( e.getSource() == keyboardShortcuts  ) LayerCompositorMenuActions.displayKeyboardShortcuts();
		if( e.getSource() == about ) LayerCompositorMenuActions.displayAbout();
	}

}//end class
