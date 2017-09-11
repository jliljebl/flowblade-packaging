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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Collections;
import java.util.Vector;

import animator.phantom.renderer.FrameRenderer;
import animator.phantom.renderer.RenderFlow;
import animator.phantom.renderer.RenderNode;

//--- Renders a frame sequence or a single frame and if specified makes half or quarter size version of frame/s.
public class MovieRenderer
{
	public static final int FULL_SIZE = 0;
	public static final int HALF_SIZE = 1;
	public static final int QUARTER_SIZE = 2;
	public static final int DOUBLE_SIZE = 3;
	public static final int ONE_HALF_SIZE = 4;
	public static final int THREE_QUARTER_SIZE = 5;
	public static final int ONE_THREE_QUARTER_SIZE = 6;
	public static final int ONE_QUARTER_SIZE = 7;
	public static final int THIRD_SIZE = 8;

	private Vector<NumberedFrame> frames;
	private Vector<FrameRendererThread> threads;

	private boolean displayFrameInPreview = false;
	private boolean updateRange = false;
	private boolean updateRenderWindow = false;

 	private WriteRenderThread writer = null;
	private int multiFrameStartFrame = -1;
	private long startTime;

	private int renderSize;

	//-------------------------------------------------- CONSTRUCTOR
	public MovieRenderer( RenderFlow renderFlow, int renderSize, int threadCount )
	{
		if( threadCount < 1 )
		{
			System.out.println( "MovieRenderer threads < 1" );
			System.exit( 1 );
		}

		threads = new Vector<FrameRendererThread>();
		for( int i = 0; i < threadCount; i++ )
		{
			threads.add( new FrameRendererThread( renderFlow, this, renderSize, !( i == 0 ) ) );
		}

		frames = new Vector <NumberedFrame>();
		this.renderSize = renderSize;
	}

	public void setStopNode( RenderNode node )
	{
		for( int i = 0; i < threads.size(); i++ )
			threads.elementAt( i ).setStopNode( node );
	}

	public void setUpdateRange( boolean val )
	{
		updateRange = val;
	}

	public void setWriter( WriteRenderThread newWriter )
	{
		writer = newWriter;
	}

	public void setDisplayFrameInPreview( boolean val )
	{
		displayFrameInPreview = val;
	}


	public void setUpdateRenderWindow( boolean val )
	{
		updateRenderWindow = val;
	}

	public synchronized void frameComplete( NumberedFrame frame, int renderTime )
	{
		if( writer == null )
			frames.add( frame );
		else
			writer.writeFrame( frame );

		if( displayFrameInPreview )
			PreviewController.displayFrameWhileRendering( frame.getFrameImage() );
		
		if( updateRange )
			PreviewController.setRange( multiFrameStartFrame, multiFrameStartFrame + frames.size() );

		if( updateRenderWindow )
		{
			long now = System.currentTimeMillis();
			GUIComponents.renderWindow.getPanel().nextFrame( (int) (now - startTime), renderTime );
		}
	}

	public void handlePreviewOutOfMemory()
	{
		System.out.println( "Multiframe render out of memory, attempting to free some fmore by losing frames.");
		MemoryManager.handlePreviewOutOfMemory( frames );
		FrameRenderer.requestAbort();
	}

	//--- Renders and returns single frame.
	public BufferedImage renderSingleFrame( int frame )
	{
		FrameRendererThread thread = threads.elementAt( 0 );
		//thread.setDebug( true );
		thread.setFrameRangeAndStep( frame, frame + 1, 1 );
		thread.start();
		try
		{
			thread.join();
		}
		catch( InterruptedException e )
		{
			System.out.println( "InterruptedException in renderSingleFrame" + e.getMessage() );
			System.exit( 1 );//remove from prodution
		}
		BufferedImage retImg = frames.elementAt( 0 ).getFrameImage();
		if( retImg == null ) 
			return null;
		return getImageInSize( retImg, renderSize );
	}
	
	//--- Returns Vector of BufferedImages from startFrame to endFrame, inclusive.
	public Vector <BufferedImage> renderFrameRangeToVector( int startFrame, int endFrame )
	{
		this.multiFrameStartFrame = startFrame;
		this.startTime = System.currentTimeMillis(); 

		int step = threads.size();// with four threads, each one renders every fourth frame etc...
		//--- Start threads.
		for( int i = 0; i < threads.size(); i++ )
		{
			FrameRendererThread thread = threads.elementAt( i );
			thread.setFrameRangeAndStep( startFrame + i, endFrame, step );
			thread.start();
		}

		//--- Join threads. Blocks until all threads stopped because done or aborted.
		try
		{
			for( int i = 0; i < threads.size(); i++ )
			{
				FrameRendererThread thread = threads.elementAt( i );
				thread.join();
			}
		}
		catch( InterruptedException e )
		{
			System.out.println( "InterruptedException in renderFrameRangeToVector" + e.getMessage() );
			System.exit( 1 );//remove from prodution
		}

		Collections.sort( frames );
		int framesCount = frames.size();
		Vector<BufferedImage> rVec = new Vector<BufferedImage>();
		for( int i = 0; i < framesCount; i++ )
		{
			NumberedFrame nf = frames.remove( 0 );
			rVec.add( nf.getFrameImage() );
		}
		return rVec;
	}

	//--- This is called after abort to get the already rendered frames.
	public Vector <BufferedImage> getFrames()
	{ 
		return new Vector<BufferedImage>(); 
	}

	//--- Returns image in user specified size.
	public static BufferedImage getImageInSize( BufferedImage img, int size )
	{
		System.out.println(size);
		if( size == FULL_SIZE ) return img;
		
		if( size == HALF_SIZE ) return getHalfImage( img );
		if( size == QUARTER_SIZE ) return getQuarterImage( img );
		if( size == THREE_QUARTER_SIZE ) return getScaledImage( img, 0.75f );
		if( size == ONE_HALF_SIZE ) return getScaledImage( img, 1.5f );
		if( size == ONE_THREE_QUARTER_SIZE ) return getScaledImage( img, 1.75f );
		if( size == ONE_QUARTER_SIZE ) return getScaledImage( img, 1.25f );
		if( size == THIRD_SIZE ) return getScaledImage( img, 0.33f );
		return getScaledImage( img, 2.0f );
	}

	//--- Scales image to halfSize by droppin 3 / 4 of pixels.
	public static BufferedImage getHalfImage( BufferedImage bImg )
	{
		int halfWidth = bImg.getWidth() / 2;
		int halfHeight = bImg.getHeight() / 2;
	
		BufferedImage halfImage = new BufferedImage( 	halfWidth,
								halfHeight,
								BufferedImage.TYPE_INT_ARGB );
	
		WritableRaster source = bImg.getRaster();
		WritableRaster target = halfImage.getRaster();

		int[] sourcePixel = new int[ 4 ];
		int i, j;

		for( i = 0; i < halfWidth; i++ )
		{
			for( j = 0; j < halfHeight; j++ )
			{
				//--- Copy 1 / 4 of pixels.
				source.getPixel( i * 2, j * 2, sourcePixel );
				target.setPixel( i, j, sourcePixel );
			}
		}
	
		return halfImage;
	}

	//--- Scales image to quarterSize by droppin 15 / 16 of pixels.
	public static BufferedImage getQuarterImage( BufferedImage bImg )
	{
		int quarterWidth = bImg.getWidth() / 4;
		int quarterHeight = bImg.getHeight() / 4;
	
		BufferedImage quarterImage = new BufferedImage( quarterWidth,
								quarterHeight,
								BufferedImage.TYPE_INT_ARGB );
	
		WritableRaster source = bImg.getRaster();
		WritableRaster target = quarterImage.getRaster();

		int[] sourcePixel = new int[ 4 ];
		int i, j;

		for( i = 0; i < quarterWidth; i++ )
		{
			for( j = 0; j < quarterHeight; j++ )
			{
				//--- Copy 1 / 16 of pixels.
				source.getPixel( i * 4, j * 4, sourcePixel );
				target.setPixel( i, j, sourcePixel );
			}
		}
	
		return quarterImage;
	}
	
	public static BufferedImage getScaledImage( BufferedImage img, float scale )
	{
		Image scaled = img.getScaledInstance( (int)(img.getWidth() * scale), (int)(img.getHeight() * scale), Image.SCALE_FAST );
		//--- scaled holds reference to original instance and must be left behind
		//--- so that both can be carbage collected.
		BufferedImage noReferenceImg = new BufferedImage( 	scaled.getWidth(null),
									scaled.getHeight(null),
									BufferedImage.TYPE_INT_ARGB );
		Graphics g = noReferenceImg.getGraphics();
		g.drawImage( scaled, 0, 0, null );
		return noReferenceImg;
	}

}//end class
