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

import java.awt.image.BufferedImage;

import animator.phantom.renderer.FrameRenderer;
import animator.phantom.renderer.RenderFlow;
import animator.phantom.renderer.RenderNode;

public class FrameRendererThread extends Thread
{
	private MovieRenderer parentMovieRenderer;
	private FrameRenderer renderer;

	private int start; //inclusive
	private int end; //exclusive
	private int step;
	private int renderSize;
	private boolean isClone = false;
	private RenderFlow flow;

	public FrameRendererThread( RenderFlow renderFlow, MovieRenderer parentMovieRenderer, int renderSize, boolean useClone )
	{
		// Multiple threads need different flows using the same iops for per frame rendering state
		if( useClone )
		{
			this.flow = renderFlow.cloneFlow();
			this.isClone = true;
		}
		else
		{
			this.flow = renderFlow;
		}

		this.renderer = new FrameRenderer( this.flow, getId() );
		this.renderer.setHasCloneFlow( this.isClone );
		this.renderSize = renderSize;
		this.parentMovieRenderer = parentMovieRenderer;
	}

	public void setDebug( boolean val ){ this.renderer.setDebug( val ); }

	public void setFrameRangeAndStep( int start, int end, int step )
	{
		this.start = start;//start frame, inclusive
		this.end = end;//end frame, inclusive
		this.step = step;// step between rendererd frames
	}

	public void setStopNode( RenderNode node )
	{
		renderer.setStopNode( node );
	}

	public int getStartFrame(){ return start; }

	//--- Render, set and display bg image.
	public void run()
	{
		try
		{
			for( int i = start; i < end; i = i + step )
			{
				if( !MemoryManager.canRenderNextPreview() ) 
				{
					System.out.println("Preview render for frame "+ ( start + i ) + "denied by memory manager." );
					return;
				}
;
				//--- Render frame, from flow or single node.
				BufferedImage nextFrame = renderer.renderFrame( i );

				//--- Check against empty flow
				if( nextFrame == null ) 
					nextFrame = ProjectController.getScreenSample();

				//--- Get frame in version of desired size;
				nextFrame = MovieRenderer.getImageInSize( nextFrame, renderSize );

				//--- Deliver frame
				parentMovieRenderer.frameComplete( new NumberedFrame( i, nextFrame ), renderer.lastRendertime() );

				//--- Abort check
				if( FrameRenderer.abort() )
				{
					flow.emptyRenderedImages();//or they waste memory till next render
					return;
				}
			}
			flow.emptyRenderedImages();//or they waste memory till next render
		}
		catch( OutOfMemoryError e )
		{
			parentMovieRenderer.handlePreviewOutOfMemory();
			flow.emptyRenderedImages();//or they waste memory till next render
		}
	}

}//end class
