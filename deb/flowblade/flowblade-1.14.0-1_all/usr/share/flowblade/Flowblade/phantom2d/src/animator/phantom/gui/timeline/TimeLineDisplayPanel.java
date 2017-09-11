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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

import animator.phantom.controller.GUIComponents;
import animator.phantom.controller.PreviewController;
import animator.phantom.controller.ProjectController;
import animator.phantom.controller.TimeLineController;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.AnimFrameGUIParams;
import animator.phantom.gui.GUIColors;

//--- This class displays timeline scale.
public class TimeLineDisplayPanel extends JPanel implements MouseListener, MouseMotionListener
{

	//--- Scale mark sizes
	private static final float SMALL_MARK_HEIGHT = 4;
	private static final int BIG_MARK_HEIGHT = 17;
	private static final int CURRENT_FRAME_HEIGHT = 22;
	private static final int PREVIEW_H1 = 20;
	private static final int PREVIEW_H2 = 22;

	//--- Minimum amount of pixels per unit for draw
	private static final int MIN_PIXELS_FOR_FRAME_DRAW = 7;
	private static final int MIN_PIXELS_FOR_SECOND_STEP = 50;
	
	private static final Color FRAME_POINTER_COLOR = new Color( 184, 51, 51 );
	private static final Color SCALE_TICKS_COLOR = new Color( 138, 143, 147 );
	private static final Color PREVIEW_AREA_COLOR = new Color( 92, 111, 190 );

	private static final int TIMECODE_Y = 17 ;
	private static final Font TIME_CODE_FONT = new Font("Monospaced", Font.PLAIN, 12 );


	//-------------------------------------------- CONSTRUCTOR
	public TimeLineDisplayPanel()
	{
		setPreferredSize( new Dimension( AnimFrameGUIParams.getTimeEditRightColWidth(),
						AnimFrameGUIParams.TE_SCALE_DISPLAY_HEIGHT) );

		//--- MouseListener, MouseMotionListener
		addMouseListener( this );
		addMouseMotionListener( this );
	}

	//----------------------------------------- MOUSE EVENTS
	//--- Handle mouse press on panel area
	public void mousePressed(MouseEvent e)
	{
		requestFocusInWindow();
		PreviewController.stopPlayback();
	}

	//--- Handle mouse dragging
	public void mouseDragged(MouseEvent e)
	{
		int frame = getFrame( e.getX() );
		TimeLineController.setCurrentFrame( frame );
		UpdateController.updateMovementDisplayers();
		repaint();
	}
	//--- Notify clip and set clipBeingEdited to null
	public void mouseReleased(MouseEvent e)
	{
		int frame = getFrame( e.getX() );
		TimeLineController.setCurrentFrame( frame );
		UpdateController.updateCurrentFrameDisplayers( false );
		repaint();
	}

	//--- Mouse events that are not handled.
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}

	private int getFrame( int x )
	{
		float pixPerFrame = TimeLineController.getCurrentScaleMultiplier();
		int xOffset = Math.round( pixPerFrame * TimeLineController.getTimeLinePosition() );
		int frame = Math.round( ( x + xOffset) / pixPerFrame );
		//--- clamp into movie length.
		if( frame < 0 ) frame = 0;
		if( frame > ( ProjectController.getLength() - 1 ) ) frame =  ProjectController.getLength() - 1;
		return frame;
	}

	//----------------------------------------- GRAPHICS
	public void paintComponent( Graphics g )
	{
		Dimension d = getSize();
	 
		float pixPerFrame = TimeLineController.getCurrentScaleMultiplier();
		int timeLinePos = TimeLineController.getTimeLinePosition();

		g.setColor( GUIColors.darker );
		g.fillRect( 0, 0, d.width, d.height );

		Graphics2D g2 = (Graphics2D) g;
		drawTimelineScale( g2 );
		
		//--- Draw preview area
		if( PreviewController.getStartFrame() != -1  && PreviewController.getCurrentPreviewSize() == GUIComponents.viewEditor.getScreenSize())
		{
			int inX = Math.round( pixPerFrame * 
				( PreviewController.getStartFrame() - timeLinePos ) );
			int outX = Math.round( pixPerFrame * 
				( PreviewController.getEndFrame() - timeLinePos ) );
			g.setColor( PREVIEW_AREA_COLOR );
			g.fillRect( inX, PREVIEW_H1, outX - inX, PREVIEW_H2 );
		}

		//--- Draw frame pointer
		g.setColor( FRAME_POINTER_COLOR );
		int framePointerX = Math.round( ( TimeLineController.getCurrentFrame() - timeLinePos ) *
					pixPerFrame );

		g.fillRect(  framePointerX - 1, 0, 5, CURRENT_FRAME_HEIGHT - 1 );
		g.setColor( SCALE_TICKS_COLOR );
		g.drawLine( framePointerX + 1 , 0, framePointerX + 1 , CURRENT_FRAME_HEIGHT - 1 );
	}

	//--- Draws image of timeline in given scale
	private void drawTimelineScale( Graphics2D g )
	{
		//--- scale and position
		float pixPerFrame = TimeLineController.getCurrentScaleMultiplier();
		int timeLinePos = TimeLineController.getTimeLinePosition();
		int movieLength = ProjectController.getLength();

		int xOffset = Math.round( pixPerFrame * timeLinePos );

		//-- top line
		g.setColor( SCALE_TICKS_COLOR );
		Line2D.Float topline = new Line2D.Float();
		float drawX = ( new Integer( movieLength ) ).floatValue() * pixPerFrame - xOffset;
		topline.setLine(0, 0, drawX, 0 );
		g.draw( topline );

		//--- Draw frame marks
		drawFrameMarks( g, pixPerFrame, xOffset, movieLength );
		drawSecondMarks( g, pixPerFrame, xOffset, movieLength );

		//--- Draw timecodes.
		drawTimeNumbers( g, pixPerFrame, xOffset );
	} 
	
	//--- Draws small mark for each frame
	private void drawFrameMarks( Graphics2D g, float pixelsPerFrame, int xOff, int framesInMovie )
	{
		//--- If frames come too close to each other, don't draw them.
		if( pixelsPerFrame < MIN_PIXELS_FOR_FRAME_DRAW ) return;

		//--- Draw frames.
		Line2D.Float line = new Line2D.Float();
		float drawX;
		float iFloat;
		for( int i = 0; i < framesInMovie + 1; i++ )
		{
			iFloat = ( new Integer( i ) ).floatValue();
			drawX = iFloat * pixelsPerFrame - xOff;
			line.setLine(drawX, 0, drawX, SMALL_MARK_HEIGHT );
			g.draw( line );
		}
	}

	//--- Draws desired size mark for each second
	private void drawSecondMarks( Graphics2D g, float pixelsPerFrame, int xOff, int framesInMovie )
	{
		int mark_height = BIG_MARK_HEIGHT;
	
		Line2D.Float line = new Line2D.Float();
		int framesPerSecond = ProjectController.getFramesPerSecond();
		int secondsInMovie = framesInMovie / framesPerSecond;
		float drawX;
		int step = getSecondStep(pixelsPerFrame);
		for( int i = 0; i < secondsInMovie + 1; i = i + step )
		{
			drawX = i * pixelsPerFrame * framesPerSecond - xOff;
			line.setLine(drawX, 0, drawX, mark_height );
			g.draw( line );
		}
	}

	private void drawTimeNumbers( Graphics2D g, float pixelsPerFrame, int xOff )
	{
		//--- Determine how many numbers at most from end in form hh:mm:ss:ff are drawn
		int framesPerSecond = ProjectController.getFramesPerSecond();
		int secondsInMovie = ProjectController.getLength() / framesPerSecond;

		//--- draw Time code for every second
		int x, frame, step;
		step = getSecondStep( pixelsPerFrame );
		for( int i = 0; i < secondsInMovie; i = i + step )
		{
			frame = i * framesPerSecond;
			x = Math.round( pixelsPerFrame * frame ) + 2 - xOff;
			renderTimeCode( g, i, x );
		}
	}

	private int getSecondStep( float pixelsPerFrame )
	{
		float framesForMinStep = MIN_PIXELS_FOR_SECOND_STEP / pixelsPerFrame;
		int step = (int) Math.floor(framesForMinStep / (float) ProjectController.getFramesPerSecond()) + 1;
		return step;
	}

	private void renderTimeCode( Graphics2D g, int seconds, int x )
	{
		StringBuilder sb = new StringBuilder();
		sb.append( seconds );
		sb.append( "s" );

		g.setFont( TIME_CODE_FONT );
		g.drawString( sb.toString(), x, TIMECODE_Y );
	}
	
	public static String parseTimeCodeString( int frame, int maxNumberCount, int framesPerSecond )
	{
		int hours = frame / ( 3600 * framesPerSecond );
		int mFrames = frame % ( 3600 * framesPerSecond );
		int minutes = mFrames / ( 60 * framesPerSecond );
		int sFrames = mFrames % ( 60 * framesPerSecond );
		int seconds = sFrames / framesPerSecond;
		int frames = sFrames % framesPerSecond;
	
		//--- Create time code String
		StringBuilder sb = new StringBuilder();
		if( maxNumberCount == 8 )
		{
			appendNumberPair( hours, sb );
			sb.append( ":" );
		}

		if( maxNumberCount >= 6 )
		{
			appendNumberPair( minutes, sb );
			sb.append( ":" );
		}

		appendNumberPair( seconds, sb );
		sb.append( ":" );

		appendNumberPair( frames, sb );
		
		return sb.toString();
	}

	private static void appendNumberPair( int value, StringBuilder sb )
	{
		if( value < 10 )
		{
			sb.append( "0" );
			sb.append( value );
		}
		else sb.append( value );
	}

}//end class
