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

import java.awt.image.BufferedImage;
import java.util.Vector;

import animator.phantom.renderer.FrameRenderer;

//--- This class provides control interface and logic for preview operations.
public class PreviewController
{
	//--- Preview params.
	private static boolean displayWhenRendering = true;
	private static int renderSize = MovieRenderer.FULL_SIZE; // used for View Editor current frame
	private static int previewSize = MovieRenderer.FULL_SIZE; // used for previews frame
	private static int quality = RenderModeController.NORMAL;

	//--- Rendering
	private static MovieRenderer movieRenderer;
	private static PreviewRenderThread renderThread;

	//--- Preview range
	private static int startFrame = -1;
	private static int endFrame = -1;//exclusive
	private static int length;

	//--- Rendered frames
	private static Vector <BufferedImage> frames = null;
	private static BufferedImage singleFrame = null;

	//--- Playback
	private static boolean playbackOn = false;
	private static boolean firstFrame = false;
	private static boolean loop = true;
	private static boolean locked = false;
	private static FrameDisplayTimer frameTimer;
	private static int frameDelay;
	private static long playbackStart;
	private static long lastFrameTime;
	private static int timesMissed;
	private static long FRAME_MISS_THRESHOLD = 5;

	public static void reset()
	{
		locked = false;
		displayWhenRendering = true;
		renderSize = MovieRenderer.FULL_SIZE;
		quality = RenderModeController.NORMAL;
		frames = null;
		singleFrame = null;
		startFrame = -1;
		endFrame = -1;//exclusive
	}

	public static void clear()
	{
		locked = false;
		frames = null;
		singleFrame = null;
		startFrame = -1;
		endFrame = -1;//exclusive
	}

	//------------------------------------------------ PARAMS INTERFACE
	//--- Locking
	public static void setLocked( boolean locked_ ){ locked = locked_; }
	public static boolean getIsLocked(){ return locked; }
	public static boolean getDisplayWhenRendering(){ return displayWhenRendering; }

	//------------------------------------------------------------------ DISPLAY
	//--- Reacts to current frame change in some way.
	//--- Tries to display image if there is no playback. if playback is on, this is called
	//--- because playback changed frame.
	public static void currentFrameChanged(){ if( !playbackOn ) displayCurrent(); }

	//--- Displays current frame preview or null.
	public static void displayCurrent()
	{
		int frame = TimeLineController.getCurrentFrame();

		if( frames != null )
		{
			int frameIndex = frame - startFrame;
			if( frameIndex < 0 || frameIndex > frames.size() - 1 )
				GUIComponents.previewUpdater.setFrame( null );
			else
				GUIComponents.viewEditor.setPreviewFrame( frames.elementAt( frameIndex ) );
		}
		else if( singleFrame != null )
		{
			if( frame == startFrame )
				GUIComponents.viewEditor.setPreviewFrame( singleFrame );
			else
				GUIComponents.previewUpdater.setFrame( null );
		}
		else GUIComponents.previewUpdater.setFrame( null );

		GUIComponents.viewEditor.repaint();
	}
	//--- Special method for render timer display
	public static void displayFrameWhileRendering( BufferedImage img )
	{
		GUIComponents.viewEditor.setPreviewFrame( img );
		GUIComponents.viewEditor.repaint();
		UpdateController.repaintFramePosititionDisplayers();
	}

	//--- Display single frame in current frame and set timeLine.
	public static void displayCurrentFramePreview( BufferedImage frameImg )
	{
		singleFrame = frameImg;
		frames = null;
		startFrame = TimeLineController.getCurrentFrame();
		length = 1;
		displayCurrent();
	}

	//---------------------------------------------------------- RENDER
	//--- f9 and f button call this.
	public static void renderAndDisplayCurrent()
	{
		if( locked ) return;
		locked = true;

		Application.setCurrentRenderType( Application.PREVIEW_RENDER );
		int currentFrame = TimeLineController.getCurrentFrame();

		movieRenderer = new MovieRenderer(  ProjectController.getFlow(), renderSize, 1 );

		RenderModeController.setGlobalRenderMode( quality );

		BufferedImage frameImg = movieRenderer.renderSingleFrame( currentFrame );
		displayCurrentFramePreview( frameImg );
		setRange( currentFrame, currentFrame + 1 );
		locked = false;
	}

	public static void renderAndPlay()
	{
		renderAndPlayRange( TimeLineController.getCurrentFrame(), ProjectController.getLength() - 1 );
	}

	public static void renderAndPlayRange()
	{
		if( startFrame != -1 && endFrame != -1 )
			renderAndPlayRange( startFrame, endFrame );
	}
	//--- f11 and button call this.
	public static void renderAndPlayRange( int start, int end )
	{
		if( locked ) return;
		locked = true;

		GUIComponents.viewEditor.setPreviewDisplay();

		Application.setCurrentRenderType( Application.PREVIEW_RENDER );

		previewSize = GUIComponents.viewControlButtons.getViewSize();

		movieRenderer = new MovieRenderer(  ProjectController.getFlow(), previewSize, RenderModeController.getRenderThreadsCount() );
		movieRenderer.setUpdateRange( true );

		frames = null;//so memory can be gc:d
		singleFrame = null;
		MemoryManager.previewRenderStarting();

		//RenderModeController.setWriteRender( false );//--- this does not write to files render
		RenderModeController.setGlobalRenderMode( quality );

		renderThread = new PreviewRenderThread( movieRenderer, start, end );
		renderThread.start();
		// thread calls playFrameRange(...) below when rendered.
		// Movierenderer calls  displayFrameWhileRendering(....)
		// Controls will be unlocked when movie stopped.
	}

	public static int getCurrentPreviewSize()
	{
		return previewSize;
	}

	//--- Abort handling start.
	//--- This is called when user pushes stop button
	public static void abortPreviewRender()
	{
		locked = false;
		FrameRenderer.requestAbort();
	}

	//------------------------------------------------------------ PLAYBACK
	public static void playFrameRange( Vector <BufferedImage> frames_, int startFrame_ )
	{
		GUIComponents.tlineControls.update();
		playRange( frames_, startFrame_ );
	}

	private static void playRange( Vector <BufferedImage> frames_, int startFrame_ )
	{
		TimeLineController.setCurrentFrame( startFrame );
		frames = frames_;
		startFrame = startFrame_;
		length = frames.size();
		playFromCurrent();
	}

	public static void stopPlaybackRequest()
	{
		if( playbackOn ) stopPlayback();
		GUIComponents.viewEditor.setViewEditorDisplay();
	}

	//--- Preview range
	public static int getStartFrame(){ return startFrame; }
	public static int getEndFrame(){ return endFrame; }
	public static void setRange( int start, int end )// with paint
	{
		startFrame = start;
		endFrame = end;
		TimeLineController.repaintTimeLineScaleDisplay();
	}

	//--- loop
	public static void setLoop( boolean val ){ loop = val; }

	//--- Starts displaying frames from current frame.
	public static void playFromCurrent()
	{
		if (startFrame == -1 || previewSize !=  GUIComponents.viewEditor.getScreenSize())
		{
			renderAndPlay();
			return;
		}
		playbackOn = true;
		GUIComponents.viewEditor.setPreviewDisplay();
		GUIComponents.previewControls.updatePlayButton();
		timesMissed = 0;
		frameDelay = 1000 / ProjectController.getFramesPerSecond();
		playbackStart = 0;
		frameTimer = new FrameDisplayTimer( frameDelay );
		locked = true;
		frameTimer.start();
	}

	//--- Handle play button press based on state.
	public static void playPressed()
	{
		if( playbackOn ) stopPlayback();
		else playFromCurrent();
	}
	//--- Called from FrameDisplayTimer witch handles timing.
	public static void nextFrame()
	{
		int frame = TimeLineController.getCurrentFrame();

		//---
		if( !firstFrame )
		{
			//--- Stop if end, loop handled in stopPlayback();
			if( frame == ( startFrame + length ) - 1 && singleFrame == null )
			{
				boolean wasStopped = playbackEndCheck();
				if( wasStopped ) playBackInfo();
				return;
			}
			else if( frame == ProjectController.getLength() - 1 )
			{
				stopPlayback();
				return;
			}
			//--- Next frame. This causes an attempt to display frame at currentFrameChanged() but that is discarded there.
			TimeLineController.changeCurrentFrame( 1 );

			//--- Chack for misses.
			long time = System.currentTimeMillis();
			if( ( time - lastFrameTime - frameDelay ) > FRAME_MISS_THRESHOLD )
				timesMissed++;
			lastFrameTime = time;
		}
		else
		{
			//--- Begin playback, set some values. Don't advance but display current frame.
			playbackStart = System.currentTimeMillis();
			lastFrameTime = playbackStart;
			firstFrame = false;
		}

		//--- Display current frame.
		frame = TimeLineController.getCurrentFrame();
		int frameIndex = frame - startFrame;

		if( frames != null && frameIndex >= 0 && frameIndex < frames.size() )
			GUIComponents.viewEditor.setPreviewFrame( frames.elementAt( frameIndex ) );
		else if( singleFrame != null && frame == startFrame )
			GUIComponents.viewEditor.setPreviewFrame( singleFrame );
		else
			GUIComponents.previewUpdater.setFrame( null );

		GUIComponents.viewEditor.repaint();
		UpdateController.repaintFramePosititionDisplayers();
	}

	//--- Checks if we shold loop or stop.
	private static boolean playbackEndCheck()
	{
		if( !loop )
		{
			stopPlayback();
			return true;
		}
		TimeLineController.setCurrentFrame( startFrame );
		if( frameTimer != null ) frameTimer.stop();
		playFromCurrent();
		return false;
	}

	//--- Stop and display info.
	public static void stopPlayback()
	{
		if( frameTimer != null ) frameTimer.stop();
		locked = false;
		playbackOn = false;
		GUIComponents.previewControls.updatePlayButton();
		GUIComponents.viewEditor.setViewEditorDisplay();
	}

	public static boolean playbackOn(){ return playbackOn; }

	private static void playBackInfo()
	{
		//--- Display info.
		long playbackEnd = System.currentTimeMillis();
		Long playbackDuration = new Long( playbackEnd - playbackStart );
		int duration = playbackDuration.intValue();
		System.out.println( "Playback, last frame:" +  TimeLineController.getCurrentFrame() + " ,duration:" + duration );
		System.out.println( "timesMissed:" + timesMissed );
	}

}//end class
