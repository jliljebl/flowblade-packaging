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

import java.io.File;

import animator.phantom.gui.modals.render.RenderWindow;
import animator.phantom.renderer.FrameRenderer;

//--- This class is used to set render parameters for preview and write rendering.
//--- Write rendering features are super set of preview render features.
//--- Write render features named write<x> and on apply when doing write render.
public class RenderModeController
{

	//--- If false no motion blurs are done even if iop is defined to have motion blur.
	//--- This is swicthed back and forth depending if for eg. ViewEditor image is rendered.
	//--- This is not user settable, it is rendering global state.
	//--- When rendering preview or write render this is set to PreviewController.globalMotionBlur whitch
	//--- is the user visible global flag.
	private static boolean globalMotionBlur = true;

	//--- Different rendermodes
	//--- NORMAL == flow is rendered as data indicates
	public static final int NORMAL = 0;
	//--- DRAFT == no motion blur is rendered and render interpolation is always NEAREST_NEIGHGOR
	public static final int DRAFT = 1;
	//--- 
	private static int renderMode = NORMAL;

	//--- Write render parameters.
	//--- This is set true when doing writerender. ImageOperation needs this to decide interpolation.
	//private static boolean writeRender = false;
	//--- DRAFT or NORMAL
	private static int writeQuality = NORMAL;
	//--- output frame file name part before the number
	private static String frameName = null;
	//--- Target folder to write frames into
	private static File targetFolder = null;
	//--- flar for encoding movie after frames are written
	//private static boolean createMovie = false;
	//private static String outFrameType = null;
	//--- Range
	public static int writeRangeStart = -1;
	public static int writeRangeEnd = -1;
	//--- zero padding, value < 3 means no padding 
	private static int zeroPadDigits = 3;
	//---- Number of threags used for rendering frame ranges.
	private static int renderThreadsCount = 2;
	//--- Number of blanders available for rendering
	private static int blendersCount = 2;

	public static void reset()
	{
		targetFolder = null;
		frameName = "frame";
	}

	//--- This is called before doing any rendering. Preview and write renders set values to their
	//--- specified values. ImageOperation then uses this and other data ( ProjectController.getMotionBlur() )
	//--- in methods getBlendMode(), getInterpolation() and getMotionBlur()
	//--- to inform extending classes how to render data.
	public static void setGlobalRenderMode( int mode )
	{
		if( mode == DRAFT )
			globalMotionBlur = false;

		else//NORMAL
			globalMotionBlur = true;

		renderMode = mode;
	}
	
	//--- Returns render mode
	public static int getRenderMode(){ return renderMode; }
	//--- Get methods for render settins.
	public static boolean getGlobalMotionBlur()
	{ 
		if( !ProjectController.getMotionBlur() ) return false;

		return globalMotionBlur;
	}

	public static void setWriteQuality( int val ){ writeQuality = val; }
	public static int getWriteQuality(){ return writeQuality; }

	public static void setFrameName( String name ){ frameName = name; }
	public static String getFrameName(){ return frameName; }

	public static void setWriteFolder( File folder ){ targetFolder = folder; }
	public static File getWriteFolder(){ return targetFolder; }

	public static void setZeroPadding( int val ){ zeroPadDigits = val; }
	public static int getZeroPadding(){ return zeroPadDigits; }

	public static void writeMovie(){ writeRange( 0, ProjectController.getLength() ); }
	private static void writeRange( int startFrame, int endFrame )//endframe exclusive
	{
		writeRangeStart = startFrame;
		writeRangeEnd = endFrame;
		GUIComponents.animatorFrame.setEnabled( false );
		GUIComponents.renderWindow = new RenderWindow();
	}

	public static void disposeRenderWindow()
	{
		GUIComponents.renderWindow.setVisible( false );
		GUIComponents.renderWindow.dispose();
		GUIComponents.renderWindow = null;
		GUIComponents.animatorFrame.setEnabled( true );
	}

	public static void startWriteRender()
	{

		//--- Prepare render
		RenderModeController.setWriteRenderParams();
		Application.setCurrentRenderType( Application.WRITE_RENDER );
		int renderFramesCount =  writeRangeEnd - writeRangeStart;
		GUIComponents.renderWindow.getPanel().pbar.setPiecesCount( renderFramesCount );
		GUIComponents.renderWindow.getPanel().renderStart( renderFramesCount );

		//--- Launch write
		//--- will call closeWriteRender() when fished
		WriteRenderThread writeThread = new WriteRenderThread( writeRangeStart, writeRangeEnd, frameName );
		writeThread.start();
	}

	public static void closeWriteRender()
	{
		GUIComponents.renderWindow.getPanel().renderingDone();
	}

	public static void stopWriteRender()
	{
		FrameRenderer.requestAbort();
	}
	
	public static void setWriteRange( int startFrame, int endFrame )
	{
		writeRangeStart = startFrame;
		writeRangeEnd = endFrame;
	}	
	//--- Called before doing write render
	public static void setWriteRenderParams()
	{
		int mode = writeQuality;
		setGlobalRenderMode( mode );
	}

	//--- 
	public static void frameRendererAborted() //need for this anymore is highly suspect 
	{
		PreviewController.setLocked( false );
	}

	public static int getRenderThreadsCount(){ return renderThreadsCount; }
	public static void setRenderThreadsCount( int newValue){ renderThreadsCount = newValue; }
	public static int getBlendersCount(){ return blendersCount; }
	public static void setBlendersCount( int newValue ){ blendersCount = newValue; }

}//--- end class
