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
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

//import animator.phantom.controller.PreviewController;
import animator.phantom.controller.ProjectController;
import animator.phantom.controller.TimeLineController;
//import animator.phantom.controller.TimeLineController;
import animator.phantom.controller.UpdateController;

public class TimeLinePositionSlider extends JPanel implements MouseListener, MouseMotionListener
{
		public static final int BAR_WIDTH = 200;
		public static final int BAR_HEIGHT = 26; 
		private static final int END_PAD = 6;
		private static final int NOTHING_HIT = 0;
		private static final int POS_BAR_HIT = 3;
		
		private static final Color AREA_COLOR = new Color( 62, 62, 70 );
		private static final Color HANDLE_COLOR = new Color( 163, 168, 172 );
		private static final Color LINE_COLOR = new Color( 46, 52, 54 );
		
		private static final int HANDLE_Y = 10;
		private int hitState = NOTHING_HIT;

		private int inFrame;
		private int outFrame;
		
		private int pressFrame;
		private int startPosition;

		public TimeLinePositionSlider()
		{
			inFrame = 0;
			outFrame = ProjectController.getLength();

			setPreferredSize( new Dimension( BAR_WIDTH,  BAR_HEIGHT ) );
			setMaximumSize(  new Dimension( BAR_WIDTH,  BAR_HEIGHT ) );

			addMouseListener( this );
			addMouseMotionListener( this );
		}

		public void mousePressed( MouseEvent e )
		{
			int x = e.getX();
			int y = e.getY();
			 
			int inX = getX( inFrame  );
			int outX = getX( outFrame );

			hitState = NOTHING_HIT;
			if( y >= (HANDLE_Y - 3 ))
			{
				if ( x > inX && x < outX )
				{
					hitState = POS_BAR_HIT;
					pressFrame = getFrame( x );
					startPosition = TimeLineController.getTimeLinePosition();
				}
			}
		
			repaint();
		}

		public void mouseDragged( MouseEvent e )
		{
			int x = e.getX();
			
			inFrame = TimeLineController.getTimeLinePosition();
			outFrame = TimeLineController.getLastFrame();
			
			if( hitState == POS_BAR_HIT )
			{
				int currentFrame = getFrame( x );
				int newPosition = startPosition + ( currentFrame - pressFrame );
				TimeLineController.setTimeLinePosition( newPosition );
			}

			UpdateController.updateMovementDisplayers();

		}

		public void mouseReleased( MouseEvent e )
		{
			int x = e.getX(); 
			 
			inFrame = TimeLineController.getTimeLinePosition();
			outFrame = TimeLineController.getLastFrame();

			if( hitState == POS_BAR_HIT )
			{
				int currentFrame = getFrame( x );
				int newPosition = startPosition + ( currentFrame - pressFrame );
				TimeLineController.setTimeLinePosition( newPosition );
			}

			hitState = NOTHING_HIT;

			UpdateController.updateCurrentFrameDisplayers( false );
		}

		//--- Mouse events that are not handled.
		public void mouseClicked(MouseEvent e){}
		public void mouseEntered(MouseEvent e){}
		public void mouseExited(MouseEvent e){}
		public void mouseMoved(MouseEvent e){}

		public static int getFrame( int x )
		{
			x = legalizeX( x );
			x = x - END_PAD;
			int active_width = BAR_WIDTH - 2 * END_PAD;
			float normalizedPos = (float)x / (float)active_width;
			
			return (int) ((float) ProjectController.getLength() * normalizedPos );
		}

		public static int legalizeX( int x )
		{
			if( x < END_PAD )
				return END_PAD;
			else if (x > BAR_WIDTH - END_PAD)
				return BAR_WIDTH - END_PAD;
			else
				return x;
		}

		public static int getX( int frame )
		{
			float active_width = BAR_WIDTH - 2 * END_PAD;
			float normalizedFrame = (float) frame / (float) ProjectController.getLength();
			return END_PAD + ((int) ( normalizedFrame * active_width ) );
		}

		public void paintComponent( Graphics g )
		{
			g.setColor( AREA_COLOR );
			g.fillRect( 0,0, BAR_WIDTH, BAR_HEIGHT );	

			int inX = getX( 0  );
			int outX = getX( ProjectController.getLength() );
			g.setColor( LINE_COLOR );
			g.fillRect( inX, HANDLE_Y + 2, outX - inX, 2 );
			
			inFrame = TimeLineController.getTimeLinePosition();
			outFrame = TimeLineController.getLastFrame();
			inX = getX( inFrame  );
			outX = getX( outFrame );
			g.setColor( HANDLE_COLOR );
			g.fillRect( inX, HANDLE_Y, outX - inX, 4 );
		}

}//end class