package animator.phantom.controller;

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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Vector;

import animator.phantom.gui.flow.FlowBox;
import animator.phantom.project.Bin;
import animator.phantom.project.Project;
import animator.phantom.renderer.FileSource;
import animator.phantom.renderer.RenderFlow;

//--- Project logic and state + access for other logic and GUI.
public class ProjectController
{
	//--- MAIN DATA, MAIN DATA, MAIN DATA, MAIN DATA, MAIN DATA, MAIN DATA, MAIN DATA
	//--- Project currently being edited.
	private static Project project = null;
	//--- Flag to turn motion blur off globally, transient, affects preview render only.
	private static boolean motionBlur = true;
	//--- Clears all class data members.
	private static String loadPath = null;
	
	public static void reset()
	{
		project = null;
		motionBlur = true;
		loadPath = null;
	}
	//--- sets project to be edited
	public static void setProject( Project newProject )
	{
		//--- Set project.
		project = newProject;
	}
	public static void setLoadPath( String path )
	{
		loadPath = path;
	}
	public static String getLoadPath()
	{
		return loadPath;
	}
	
	//--- returns current project
	public static Project getProject(){ return project; }
	//--- Get and set for global motion blur
	public static boolean getMotionBlur(){ return motionBlur; }
	public static void setMotionBlur( boolean val ){ motionBlur = val; }


	//--- PROJECT DATA INTERFACE, PROJECT DATA INTERFACE, PROJECT DATA INTERFACE
	public static String getName(){ return project.getName(); }
	public static void changeName( String newName )
	{
		project.setName( newName );
		GUIComponents.animatorFrame.setTitle( ProjectController.getName() + " - Phantom2D" );
	}
	public static int getFramesPerSecond(){ return project.getFramesPerSecond(); }
	public static void setFramesPerSecond( int fps ){ project.setFramesPerSecond( fps );  }
	public static int getLength(){ return project.getLength(); }
	public static void setLength( int length ){ project.setLength( length ); }
	public static Dimension getScreenSize(){ return project.getScreenDimensions(); }
	public static void setScreenSize( Dimension size ){ project.setScreenDimensions( size );  }
	public static BufferedImage getScreenSample()
	{
		return new BufferedImage( 	project.getScreenDimensions().width,
						project.getScreenDimensions().height,
						BufferedImage.TYPE_INT_ARGB );
	}

	//--- RENDER FLOW, RENDER FLOW, RENDER FLOW, RENDER FLOW, RENDER FLOW, RENDER FLOW
	public static RenderFlow getFlow(){ return project.getRenderFlow(); }
	public static Vector<FlowBox> getBoxes(){ return project.getBoxes(); }


	//--- BIN MANAGEMENT, BIN MANAGEMENT, BIN MANAGEMENT, BIN MANAGEMENT, BIN MANAGEMENT
	//--- Adds FileSource to bin.

	//--- Adds multiple FileSources to bin.
	public static void addFileSourceVectorToBin( Vector<FileSource> vec, Bin bin )
	{
		bin.addFileSourceVector( vec );
	}

	public static void deleteFileSourceVectorFromBin( Vector<FileSource> vec, Bin bin )
	{
		bin.removeFileSourceVector( vec );
	}
	public static void deleteFileSourceVector( Vector<FileSource> vec )
	{
		project.deleteFileSourcesFromProject( vec );
	}
	//--- Adds new bin to project.
	public static void addBin( Bin bin ){ project.addBin( bin ); }
	//--- Returns Vector of all bins in project.
	public static Vector<Bin> getBins(){ return project.getBins(); }
	//--- Deletes bin from project.
	public static void deleteBin( Bin bin ){ project.deleteBin( bin ); }
	//--- 
	public static void displayFileSourceInfo( FileSource fs )
	{
		/*
		if( fs != null )
		{
			String info = 	// width x height
				fs.getName() + " " 
				+ ( new Integer( fs.getImageWidth() )).toString() 
				+ " x " + ( new Integer( fs.getImageHeight() )).toString();
			GUIComponents.projectPanel.setInfoLabelText( info );
			GUIComponents.projectPanel.setThumbIcon( fs );
		}
		else GUIComponents.projectPanel.setInfoLabelText( "" );
		*/
	}


	//--- FILE SOURCES, FILE SOURCES, FILE SOURCES, FILE SOURCES, FILE SOURCES
	//--- Adds file sources to project
	public static void addFileSourcesToProject( Vector<FileSource> addFileSources )
	{
		project.addFileSourcesToProject( addFileSources );
	}
	//--- Returns all file sources.
	public static Vector<FileSource> getFileSources(){ return project.getFileSources(); }

	public static void updateProjectInfo()
	{
		//--- Display project info
		String info = project.getName() + ",  " + Integer.toString(project.getScreenDimensions().width)
				+ " x " + Integer.toString(project.getScreenDimensions().height) + ",  "
				+ Integer.toString(project.getFramesPerSecond()) + " fps,  "
				+ Integer.toString(project.getLength()) + " frames, "
				+ Integer.toString(ProjectController.getFileSources().size()) + " media source(s)";
		GUIComponents.projectInfoLabel.setText( info );
	}
	
}//end class