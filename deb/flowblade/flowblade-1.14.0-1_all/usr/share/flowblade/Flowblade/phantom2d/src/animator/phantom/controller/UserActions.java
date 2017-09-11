package animator.phantom.controller;

/*
    Copyright Janne Liljeblad

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

import java.awt.Cursor;
import java.awt.Point;
import java.io.File;
import java.util.Vector;

//import javax.swing.JLabel;
import javax.swing.JOptionPane;

import animator.phantom.controller.GUIComponents;
import animator.phantom.gui.GUIUtils;
import animator.phantom.gui.modals.DialogUtils;
import animator.phantom.gui.modals.MComboBox;
import animator.phantom.gui.modals.MInputArea;
import animator.phantom.gui.modals.MInputPanel;
import animator.phantom.renderer.FileSequenceSource;
import animator.phantom.renderer.FileSingleImageSource;
import animator.phantom.renderer.FileSource;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderNode;
import animator.phantom.renderer.VideoClipSource;
import animator.phantom.renderer.parent.AbstractParentMover;
//import animator.phantom.exec.*;

//--- Complex user initiated actions that are not in menu.
public class UserActions
{
	private static String[] loopOptions = { "no looping","loop","ping-pong" };

	public static void addSingleFileSources(int fileType, int mouseX, int mouseY)
	{
		try
		{
			//--- Get user selected file(s).
			File[] addFiles = GUIUtils.addFiles( GUIComponents.getAnimatorFrame(), "Select files", fileType );
			if( addFiles == null ) return;

			GUIComponents.animatorFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			//--- Create new FileSources.
			Vector<FileSource> addFileSources = new Vector<FileSource>();
			Vector<File> movies = new Vector<File>();
			for( int i = 0; i < addFiles.length; i++ )
			{
				String wrongChar = AppUtils.testStringForSequences(
									AppUtils.forbiddenFileNameChars,
									addFiles[ i ].getName() );
				if( wrongChar != null )
				{
					displayWrongFileChars( addFiles[ i ].getName(), wrongChar );
					continue;
				}

				String ext = AppUtils.getExtension( addFiles[ i ] );
				if( AppUtils.isMovieExtension( ext ) )
				{
					movies.add( addFiles[ i ] );
				}
				else
				{
					addFileSources.add( new FileSingleImageSource( addFiles[ i ] ) );
				}
			}

			if( movies.size() > 0 )
				importMovies( movies, addFileSources );

			//--- Add them to project data. Gives id's to filesources.
			ProjectController.addFileSourcesToProject( addFileSources );

			//--- Get image sizes and add GUI components to panels
			for( FileSource addFS : addFileSources )
			{
				addFS.firstLoadData();
				addFS.clearData();
			}
			mediaLoadUpdate();

			//--- MemoryManager needs to update cache.
			MemoryManager.fileSourcesAdded();
		}
		catch( Exception e )
		{
			e.printStackTrace(System.out);
			GUIComponents.animatorFrame.setCursor(Cursor.getDefaultCursor());
					String[] buttons = {"Ok"};
					String[] bLines = { "File import failed" };
					String[] tLines = {  "Phantom2D is unable to complete file import.",
								"It may be that you are trying to import corrupt files or",
								"files of a type that is not supported by Phantom2D." };
					DialogUtils.showTwoTextStyleDialog( JOptionPane.WARNING_MESSAGE, null, buttons, bLines, tLines );
		}
		GUIComponents.animatorFrame.setCursor(Cursor.getDefaultCursor());
	}

	public static void displayWrongFileChars( String fileName, String wrongChar )
	{
		String[] buttons = {"Ok"};
		String[] bLines = { "File "+ fileName + " contained forbidden character \"" + wrongChar + "\"" };
		String[] tLines = {"Some operating systems or programs may not work with it.",
					"Remove forbidden character from file name and try again." };
		DialogUtils.showTwoTextStyleDialog( JOptionPane.WARNING_MESSAGE, null, buttons, bLines, tLines );
	}

	public static void addFileSequenceSource()
	{
		try
		{
			//--- Get user selected file(s).
			File addFile = GUIUtils.addISingleImageFile( GUIComponents.getAnimatorFrame(), "Select first frame of sequence" );
			if( addFile == null ) return;

			String wrongChar = AppUtils.testStringForSequences(
									AppUtils.forbiddenFileNameChars,
									addFile.getName() );
			if( wrongChar != null )
			{
				String[] buttons = {"Ok"};
				String[] bLines = { "File "+ addFile.getName() + " contained forbidden character \"" + wrongChar + "\"" };
				String[] tLines = {"Some operating systems or programs may not work with it.",
							"Remove forbidden character from file name and try again." };
				DialogUtils.showTwoTextStyleDialog( JOptionPane.WARNING_MESSAGE, null, buttons, bLines, tLines );
			}
			FileSource addFS = new FileSequenceSource( addFile );
			Vector<FileSource> fsVec = new Vector<FileSource>();
			fsVec.add( addFS );

			//--- Add them to project data. Gives id's to filesources.
			ProjectController.addFileSourcesToProject( fsVec );
			addFS.firstLoadData();
			addFS.clearData();
			//FlowController.addToCenterFromFileSource( addFS );

			mediaLoadUpdate();

			//--- MemoryManager needs to update cache.
			MemoryManager.fileSourcesAdded();
			
		}
		catch( Exception e )
		{
					String[] buttons = {"Ok"};
					String[] bLines = { "File import failed" };
					String[] tLines = {     "Phantom2D is unable to complete file import.",
								"It may be that you are trying to import corrupt files or",
								"files of a type that is not supported by Phantom2D." };
					DialogUtils.showTwoTextStyleDialog( JOptionPane.WARNING_MESSAGE, null, buttons, bLines, tLines );
		}
	}

	private static void mediaLoadUpdate()
	{
		if (GUIComponents.renderFlowPanel != null)
		{
			//GUIComponents.renderFlowPanel.updateMediaMenu();
		}
		GUIComponents.animatorMenu.updateAppMediaMenu();
		ProjectController.updateProjectInfo();
	}

	public static void deleteFileSources()
	{
		/*
		Vector<FileSource> selected = bPanel.currentSelectPanel().getSelected();
		String fsString = null;
		if( selected.size() == 0 ) return;
		else if( selected.size() == 1 ) fsString = " file source";
		else fsString = " file sources";

		Vector<RenderNode> nodesWithFs = new Vector<RenderNode>();
		for( FileSource fs : selected )
			nodesWithFs.addAll( FlowController.getNodesWithFileSource( fs ) );

		int answer;
		String[] options = { "Cancel","Ok" };
		if( nodesWithFs.size() == 0 )
			answer = DialogUtils.showTwoTextStyleDialog( 	JOptionPane.WARNING_MESSAGE,
									"Confirm delete",
									options,
									"Confirm delete",
									"Delete " + selected.size() + fsString + " from bin." );
		else
			answer = DialogUtils.showTwoTextStyleDialog(
						JOptionPane.WARNING_MESSAGE,
						"Confirm delete",
						options,
						"Delete file sources and nodes",
						"There are nodes in the render flow that use deleted file sources." );

		if( answer == 0 || answer == JOptionPane.CLOSED_OPTION  ) return;

		ProjectController.deleteFileSourceVectorFromBin( selected, bPanel.currentBin() );
		ProjectController.deleteFileSourceVector( selected );
		MemoryManager.deleteFromViewCache( selected );
		FlowController.deleteVector( nodesWithFs );

		bPanel.currentSelectPanel().deleteSelected();
		bPanel.updateGUI();
		*/
	}

	//--- GUI for setting animation properties for iops.
	public static void manageAnimationSettings( ImageOperation iop )
	{
		Vector <ImageOperation> parentIops = ProjectController.getFlow().getAnimatebleIops();
		parentIops.remove( iop );
		int pselindex = 0;
		int typeselindex = 0;

		if( iop.parentNodeID != -1 )
		{
			ImageOperation piop = ( ProjectController.getFlow().getNode( iop.parentNodeID )).getImageOperation();
			pselindex = parentIops.indexOf( piop ) + 1;
			typeselindex = iop.parentMoverType;
		}

		String[] options = new String[parentIops.size() + 1];
		options[ 0 ] = "none";
		for( int i = 1; i < parentIops.size() + 1; i++ )
			options[ i ] = parentIops.elementAt( i - 1 ).getName();

		MComboBox parents = new MComboBox( "Select movement parent", options );
		MComboBox actions = new MComboBox( "Child follows", AbstractParentMover.types );
		parents.setSelectedIndex( pselindex );
		actions.setSelectedIndex( typeselindex );

		MInputArea area = new MInputArea( "Parent Settings" );
		area.add( parents );
		area.add( actions );

		//String[] loopOptions = { "no looping","loop","ping-pong" };
		MComboBox looping = new MComboBox( "Select looping mode", loopOptions );
		looping.setSelectedIndex( iop.getLooping() );

		MInputArea larea = new MInputArea( "Looping" );
		larea.add( looping );

		MInputPanel panel = new MInputPanel( "Animation Properties" );
		panel.add( area );
		panel.add( larea );

		DialogUtils.showMultiInput( panel );

		int p = parents.getSelectedIndex();
		int ac = actions.getSelectedIndex();
		if( p == 0 ) iop.setParentMover( -1, -1, null );
		else
		{
			ImageOperation parentIOP = parentIops.elementAt( p - 1 );
			if( isCyclicParenting( iop, parentIOP ) )
			{
				String[] tLines = {"Node/parent relationships can't form cycles.", "Parent set edit cancelled." };
				DialogUtils.showTwoStyleInfo( "Cyclic parenting detected", tLines, DialogUtils.WARNING_MESSAGE );
			}
			else
			{
				RenderNode node = ProjectController.getFlow().getNode( parentIOP );
				iop.setParentMover( ac, node.getID(), parentIOP  );
			}
		}

		iop.setLooping( looping.getSelectedIndex() );
		ParamEditController.reBuildEditFrame();
		UpdateController.valueChangeUpdate();
	}
	//--- Returns true if parenting list started from child includes cycle
	private static boolean isCyclicParenting( ImageOperation child, ImageOperation parent )
	{
		Vector<ImageOperation> list = new Vector<ImageOperation>();
		list.add( child );
		if( list.contains( parent ) )
			return true;
		list.add( parent );
		ImageOperation nextParent = parent.getParent();
		while( nextParent != null )
		{
			if( list.contains( nextParent ) )
				return true;
			list.add( nextParent );
			nextParent = nextParent.getParent();
		}
		return false;
	}

	//--- GUI for settings looping value for iops that dont have parents
	public static void manageLoopSettings( ImageOperation iop )
	{
		//String[] loopOptions = { "no looping","loop","ping-pong" };
		MComboBox looping = new MComboBox( "Select looping mode", loopOptions );
		looping.setSelectedIndex( iop.getLooping() );

		MInputArea larea = new MInputArea( "Looping" );
		larea.add( looping );

		MInputPanel panel = new MInputPanel( "Animation Properties" );
		panel.add( larea );

		iop.setLooping( looping.getSelectedIndex() );
		ParamEditController.reBuildEditFrame();
		UpdateController.valueChangeUpdate();
	}
	//---
	public static void importMovies( Vector<File> movies, Vector<FileSource> addFileSources  )
	{
		importMovies( movies, addFileSources, false );
	}

	//---
	public static void importMovies( Vector<File> movies, Vector<FileSource> addFileSources, boolean isReplaceImport )
	{
		for ( File movieClip : movies )
		{
			System.out.println("importMovies" + movieClip.getAbsolutePath());
			VideoClipSource movieSource = new VideoClipSource( movieClip );
			movieSource.loadClipIntoServer();
			if ( movieSource.getMD5id() != null )
			{
				addFileSources.add( movieSource );
			}
		}
	}

	public static void setImportSettings()
	{

	}

}//end class
