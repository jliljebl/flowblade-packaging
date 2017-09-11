package animator.phantom.project;

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
import java.io.File;
import java.util.Vector;

import animator.phantom.gui.flow.FlowBox;
import animator.phantom.renderer.FileSource;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.RenderFlow;
import animator.phantom.renderer.RenderNode;


public class Project
{
	//--- Name of the project.
	private String name;	
	//--- File that project was last saved.
	private File saveFile;
	//--- File sources in this project.
	private Vector<FileSource> fileSources = new Vector<FileSource>();
	//--- The main datastructure of the application that contains the rendering structure as
	//--- specified by the user.
	private RenderFlow renderFlow = new RenderFlow();
	//--- Gui representations of RenderNodes.
	private Vector<FlowBox> flowBoxes = new Vector<FlowBox>();
	//--- Screen size
	private Dimension screenSize;
	//--- Default length of new movie.
	private int movieDefaultLength = 125;
	//--- Length of the movie in frames, default value is set in Project.
	private int lengthInFrames = movieDefaultLength;
	//--- Frames displayed per second in this project, default 25. 
	private int framesPerSecond = 25;
	//--- FlowBoxes are here when loded or saved, othewise handled elswhere.
	//public Vector<FlowBox> tempFlowBoxes = new Vector<FlowBox>();
	//--- Bins of media files.
	private Vector<Bin> bins;
	//--- Currently selected bin.
	private Bin currentBin;
	//--- Load clips
	private Vector<ImageOperation> loadClips = new Vector<ImageOperation>();
	//--- format name
	private String formatName;

	
	//--- Project xml files are aved with extension .phr
	public static final String PROJECT_FILE_EXTENSION = "phr";

	//--- Flags for printing debug info when loading or saving.
	//public static boolean IO_DEBUG = true;
	//public static boolean IO_DEBUG_VERBOSE = true;

	//--- This constructor called when projects are loaded.
	public Project(){}

	//--- This constructor called when projects created.
	public Project( String name, MovieFormat movieFormat )
	{
		//--- Capture data.
		this.name = name;
		
		framesPerSecond = movieFormat.getFPS();
		screenSize = movieFormat.getScreenSize();
		formatName = movieFormat.getName();
		
		bins = new Vector<Bin>();
		addBin( new Bin( "bin1" ) );

		System.out.println("PROJECT \"" + name + "\" CREATED IN FORMAT: "  + formatName );
	}

	//-------------------------------------------- PROJECT DATA INTERFACE
	//--- Project name.
	public void setName( String name ){ this.name = name; }
	public String getName(){ return name; }
	//--- flow
	public RenderFlow getRenderFlow(){ return renderFlow; }
	public void setRenderFlow( RenderFlow flow ){ renderFlow = flow; }
	public Vector<FlowBox> getBoxes(){ return flowBoxes; }
	public void setBoxes( Vector<FlowBox> boxes ){ flowBoxes = boxes; }
	//--- Movie Screen size, defensive copying.
	public Dimension getScreenDimensions(){ return new Dimension( screenSize.width, screenSize.height); }
	public void setScreenDimensions( Dimension d ){ screenSize = d; }
	//--- Frames per second.
	public int getFramesPerSecond(){ return  framesPerSecond; }
	public void setFramesPerSecond( int fps){ framesPerSecond = fps; }
	//--- Movie length
	public void setLength( int lengthInFrames ){ this.lengthInFrames = lengthInFrames;}
	public int getLength(){ return lengthInFrames; }
	//--- Last project save file. For Save as ans Save func.
	public File getSaveFile(){ return saveFile; }
	public void setSaveFile( File newSaveFile ){ saveFile = newSaveFile; }
	//---
	public String getFormatName(){ return formatName; }
	public void setFormatName( String fname ){ formatName = fname; }

	//----------------------------------------------- BINS
	public Vector<Bin> getBins(){ return bins; }
	public void setBins( Vector<Bin> newBins ){ bins = newBins; }
	public void addBin( Bin bin )
	{
		bins.add( bin );
	}
	//--- Delete bin and set new current bin.
	//--- 1 bin must remain.
	public void deleteBin( Bin bin )
	{
		int binIndex = bins.indexOf( bin );

		if( bins.size() > 1 ) bins.remove( bin );
		else return;

		if( currentBin == bin ) 
			if( binIndex > 0 ) currentBin = bins.elementAt( binIndex - 1 );
			else currentBin = bins.elementAt( 0 );
	}

	//----------------------------------------------- CLIPS
	public void setLoadClips( Vector<ImageOperation> clips ){ loadClips = clips; }
	public Vector<ImageOperation>  getLoadClips( ){ return loadClips; }

	//----------------------------------------------- FILE SOURCES
	//--- All filesources are given continually increasing id.
	public void addFileSourcesToProject( Vector<FileSource> addSources )
	{
		for( FileSource fs : addSources )
		{
			int nextFileSourceId = getNextFileSourceID();
			fs.setID( nextFileSourceId );
			fileSources.add( fs );
		}
	}
	public void deleteFileSourcesFromProject( Vector<FileSource> deleteSources )
	{
		fileSources.removeAll( deleteSources );
	}
	public Vector<FileSource> getFileSources(){ return fileSources; }
	public void setFileSources( Vector<FileSource> addFileSources )
	{
		fileSources = addFileSources;
	}
	//--- We're doing this dynamically
	private int getNextFileSourceID()
	{
		int nextFileSourceId = 0;
		for( FileSource fs : fileSources )
		{
			if( fs.getID() >= nextFileSourceId )
				nextFileSourceId =  fs.getID() + 1;
		}
		return nextFileSourceId;
	}

	public FileSource getFileSource( int id )
	{
		for( FileSource fs : fileSources )
			if( fs.getID() == id ) return fs;

		return null;
	}

	//------------------------------------------------ PARENTS
	public void setParents()
	{
		Vector<RenderNode> nodes = renderFlow.getNodes();
		for( RenderNode node : nodes )
		{
			ImageOperation iop = node.getImageOperation();
			iop.loadParentIOP( renderFlow );
		}
	}
	

}//end class
