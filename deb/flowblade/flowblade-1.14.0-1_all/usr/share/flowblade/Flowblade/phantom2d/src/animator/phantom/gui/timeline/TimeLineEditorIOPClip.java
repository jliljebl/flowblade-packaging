package animator.phantom.gui.timeline;

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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;

import animator.phantom.controller.KeyStatus;
import animator.phantom.controller.TimeLineController;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.AnimFrameGUIParams;
import animator.phantom.gui.GUIColors;
import animator.phantom.gui.GUIResources;
import animator.phantom.gui.GUIUtils;
import animator.phantom.renderer.IOPLibrary;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.param.AnimationKeyFrame;

public class TimeLineEditorIOPClip
{
	//--- Image opearation being edited.
	private ImageOperation iop;
	private Color clipColor;
	private static final int DRAW_HEIGHT = AnimFrameGUIParams.TE_ROW_HEIGHT - 10;
	//--- This is used to create a handles on clips ends to facilitate clip end dragging.
	private static final int CLIP_END_HANDLE_WIDTH = 7;//in pix
	//--- Keyframe icon
	private static BufferedImage keyFrameImg =
		GUIUtils.getBufferedImageFromFile( new File( GUIResources.keyFrameSmall ));
	//--- The x coordinate of edit starting mouse press.
	private int editStartX;
	//--- The amount of sideways movement of mouse since beginning of edit.
	private int editDelta;
	//--- States
	private static final int NOT_BEING_EDITED = 0;
	private static final int MOVING = 1;
	private static final int CLIP_START_MOVING = 2;
	private static final int CLIP_END_MOVING = 3;
	private int state = NOT_BEING_EDITED;
	private Color currentColor = null;

	//---------------------------------------------------- CONSTRUCTOR
	public TimeLineEditorIOPClip( ImageOperation iop )
	{
		this.iop = iop;
		//--- Initialize draw key frames vector.
		iop.createKeyFramesDrawVector();
		
		int boxType = IOPLibrary.getBoxType( iop );
		if ( boxType == IOPLibrary.BOX_SOURCE )
			clipColor = GUIColors.sourceClipColor;
		else if ( boxType == IOPLibrary.BOX_MERGE )
			clipColor = GUIColors.mergeClipColor;
		else if ( boxType == IOPLibrary.BOX_ALPHA )
			clipColor = GUIColors.alphaClipColor;
		else if ( boxType == IOPLibrary.BOX_MEDIA )
			clipColor = GUIColors.mediaClipColor;
		else
			clipColor = GUIColors.filterClipColor;
	}

	public ImageOperation getIOP(){ return iop; }
	//--- returns  true if this is being edited.
	public boolean isBeingEdited()
	{
		if( state == NOT_BEING_EDITED ) return false;
		else return true;
	}
	
	//-------------------------------------------- MOUSE EVENTS
	//--- Handle mouse press on clip y area on panel.
	public void mousePressed(MouseEvent e)
	{
		//--- Get x. y means nothing.
		int x = e.getX();
		//--- Check if clip pressed
		//--- Calculalate clip start and end positions.
		int pos = TimeLineController.getTimeLinePosition();
		float fmCurrent = TimeLineController.getCurrentScaleMultiplier();
		int clipStartX = Math.round( fmCurrent * ( iop.getClipStartFrame() - pos ) );
		//--- Term + 1 because endframe inclusive.
		int clipEndX =  Math.round( fmCurrent * ( iop.getClipEndFrame() - pos + 1 ) );
		//--- If x not in clip, return.
		if( !( x >= clipStartX && x<= clipEndX ) )
		{
			TimeLineController.unselectAllClips();
			TimeLineController.clipEditorRepaint();
 			return;
		}
		//--- Handle selection state changes
		if( !KeyStatus.ctrlIsPressed() )
		{
			TimeLineController.setAsSingleSelectedClip( iop );
		}
		else
		{
			TimeLineController.addToSelectedClips( iop );
		}
		//--- Set start position set delta 0.
		editStartX = x;
		editDelta = 0;
		//--- Set clip moving
		state = MOVING;
		//--- Check if clip start handle pressed, if so set CLIP_START_MOVING
		if( x >= clipStartX && x <= ( clipStartX + CLIP_END_HANDLE_WIDTH ) )
			state = CLIP_START_MOVING;
		//--- Check if clip start handle pressed, if so set CLIP_END_MOVING
		if( x >= ( clipEndX - CLIP_END_HANDLE_WIDTH  ) && x <= clipEndX )
			state = CLIP_END_MOVING;

		TimeLineController.clipEditorRepaint();
	}

	//--- Handle mouse dragging
	public void mouseDragged(MouseEvent e)
	{
		//--- If clip is not being edited, this means nothing.
		if( state == NOT_BEING_EDITED) return;
		//--- Calculate movement during edit
		editDelta = e.getX() - editStartX;
	}
	//--- Notify clip and set clipBeingEdited to null
	public void mouseReleased(MouseEvent e)
	{
		//--- If this clip is not being edited, release means nothing.
		if( state == NOT_BEING_EDITED ) return;
		//--- Calculate and set new beginframe
		float frameMultiplier = TimeLineController.getCurrentScaleMultiplier();
		//--- Get frame delta that happened during edit.
		int frameDelta = Math.round( editDelta / frameMultiplier );
		//--- If clip was moved set new start frame
		if( state == MOVING )
		{
			int startFrame = iop.getBeginFrame();
			iop.setBeginFrame( startFrame + frameDelta );
		}
		//---- if start of clip was being dragged set new start place for clip
		else if( state == CLIP_START_MOVING )
		{
			int clipStartWas = iop.getClipStartFrame();
			iop.setClipStartFrame( clipStartWas + frameDelta );
		}
		//--- Clips end was being dragged, set it into correct position.
		else
		{
			// state was CLIP_END_MOVING
			int clipEndWas = iop.getClipEndFrame();
			iop.setClipEndFrame( clipEndWas + frameDelta );
		}
		//--- Set state.
		state = NOT_BEING_EDITED;
		UpdateController.updateCurrentFrameDisplayers( false );
	}
	
	public void paintClip( Graphics gNormal, float y  )
	{
		Graphics2D g = (Graphics2D) gNormal;
		//--- Get position and scale multiplier
		int pos = TimeLineController.getTimeLinePosition();
		float frameMultiplier = TimeLineController.getCurrentScaleMultiplier();
		//--- Calculate draw parameters based on input and state
		//--- Make edit delta to snap to closest frame.
		editDelta = Math.round( Math.round( editDelta / frameMultiplier) * frameMultiplier );
		//--- Free length of not
		boolean IS_FREE_LENGTH = iop.isFreeLength();
		//--- y and clip.
		float x;
		float length;
		if( state == NOT_BEING_EDITED )
		{
			//--- Get x from frame, panel pos and multiplier
			x = ( iop.getClipStartFrame() - pos )* frameMultiplier;
			//--- Calculate length.
			// Term + 1 because endframe inclusive.
			length = ( iop.getClipEndFrame() - iop.getClipStartFrame() + 1) * frameMultiplier;
			currentColor = clipColor;
		}
		else if( state == MOVING )
		{
			x = ( iop.getClipStartFrame() - pos ) * frameMultiplier + editDelta;
			length = ( iop.getClipEndFrame() - iop.getClipStartFrame() + 1 ) * frameMultiplier;
			currentColor = GUIColors.movingClipColor;
		}
		else if( state == CLIP_START_MOVING )
		{
			x = ( iop.getClipStartFrame() - pos ) * frameMultiplier + editDelta;
			//--- Get endX
			float endX = ( iop.getClipEndFrame() - pos ) * frameMultiplier;
			//--- Bounds check x for end, if not free length clip.
			if( !IS_FREE_LENGTH )
				if( x > endX ) x = endX;
			float iopStartX = ( iop.getBeginFrame() - pos ) * frameMultiplier;
			//--- Bounds check x for start, if not free length clip.
			if( !IS_FREE_LENGTH )
				if( x < iopStartX ) x = iopStartX;
			//--- Term + frameMultiplier because endframe inclusive.
			length = endX - x + frameMultiplier;
			currentColor = clipColor;
		}
		else 
		{
			// state is CLIP_END_MOVING
			//--- Get x
			x = ( iop.getClipStartFrame() - pos ) * frameMultiplier; 
			//--- Get endX
			float endX = ( iop.getClipEndFrame() - pos ) * frameMultiplier + editDelta;
			float iopEndX = ( iop.getEndFrame() - pos ) * frameMultiplier;
			//--- Bounds check endX for end, if not free length clip.
			if( !IS_FREE_LENGTH )
				if( endX > iopEndX ) endX = iopEndX;
			//--- Bounds check endX for start, if not free length clip.
			if( !IS_FREE_LENGTH )
				if( endX < x ) endX = x;
			//--- Term + frameMultiplier because endframe inclusive.
			length = endX - x + frameMultiplier;
			currentColor = clipColor;
		}
		//--- Convert to int.
		int ix = Math.round( x );
		int iy = Math.round( y + 10 );
		int ilength = Math.round( length );
		//--- Draw.outline
		g.setColor( GUIColors.clipOutLineColor );
		g.drawRect( ix, iy, ilength, DRAW_HEIGHT );
		//--- Fill color
		g.setColor( currentColor );
		g.fillRect( ix + 1, iy + 1 ,ilength - 2, DRAW_HEIGHT - 2 );
		//--- Handles
		if( state == CLIP_START_MOVING )
		{
			g.setColor( GUIColors.movingClipColor );
			g.fillRect( ix, iy, CLIP_END_HANDLE_WIDTH, DRAW_HEIGHT );
			g.setColor( GUIColors.clipOutLineColor );
			g.fillRect( ix + ilength - CLIP_END_HANDLE_WIDTH, iy,CLIP_END_HANDLE_WIDTH, DRAW_HEIGHT );
		}
		else if( state == CLIP_END_MOVING )
		{
			g.setColor( GUIColors.clipOutLineColor );
			g.fillRect( ix, iy, CLIP_END_HANDLE_WIDTH, DRAW_HEIGHT );
			g.setColor(  GUIColors.movingClipColor );
			g.fillRect( ix + ilength - CLIP_END_HANDLE_WIDTH, iy,CLIP_END_HANDLE_WIDTH, DRAW_HEIGHT );
		}
		else
		{
			g.setColor( GUIColors.clipOutLineColor );
			g.fillRect( ix, iy, CLIP_END_HANDLE_WIDTH, DRAW_HEIGHT );
			g.fillRect( ix + ilength - CLIP_END_HANDLE_WIDTH, iy,CLIP_END_HANDLE_WIDTH, DRAW_HEIGHT );
		}
		//--- KeyFrames, draw only for stopped clips.
		if( state == NOT_BEING_EDITED )
		{
			Vector <AnimationKeyFrame> keyFrames  = iop.getDrawKeyFrames();

			for( AnimationKeyFrame kf: keyFrames )
			{
				int kfMovieFrame = iop.getBeginFrame() + kf.getFrame();
				if( iop.frameInClipArea( kfMovieFrame ) )
				{
					int kfX = Math.round( ( kfMovieFrame - pos) * frameMultiplier );
					g.drawImage( keyFrameImg, kfX - 5, iy - 3, null );
				}
			}
		}
	}

}//end class
