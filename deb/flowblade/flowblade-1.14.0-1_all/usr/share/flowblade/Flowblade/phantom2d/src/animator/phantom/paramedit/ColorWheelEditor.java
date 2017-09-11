package animator.phantom.paramedit;

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

import giotto2D.core.GeometricFunctions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import animator.phantom.gui.GUIColors;
import animator.phantom.gui.GUIResources;
import animator.phantom.gui.view.SVec;

/**
* A GUI component used to select a color. 
* <p>
* This component does NOT HANDLE undo registering or undo gui updates of edited values.
* It DOES handle undo view display update of the color select cursor.
*/ 
public class ColorWheelEditor extends JPanel implements MouseListener, MouseMotionListener, UndoListener
{
	/**
	* Mouse has been pressed or dragged.
	*/
	public static final int DRAG_EVENT = 1;
	/**
	* Mouse has been released.
	*/
	public static final int RELEASE_EVENT = 2;

	private static BufferedImage wheelImg = GUIResources.getResourceBufferedImage( GUIResources.colorWheel );
	private static final int PANEL_HEIGHT = 265;
	private static Dimension panelSize = new Dimension( ParamEditResources.EDIT_ROW_SIZE.width, PANEL_HEIGHT );
	private static final int IMG_H = 7;
	private static final int IMG_W = 17;
	private static final float MAX_DIST = 123.0f;
	private static final int BITMAP_WIDTH = 255;
	private static final int centerX = 1 + IMG_W + BITMAP_WIDTH /2;
	private static final int centerY = 1 + IMG_H + BITMAP_WIDTH /2;

	private int cursorX = centerX;
	private int cursorY = centerY;

 	private static Point2D.Float midP = new  Point2D.Float(centerX, centerY);
 	private static Point2D.Float twelweP = new  Point2D.Float(centerX, centerY - MAX_DIST);

	private ColorWheelListener listener;
	private UndoListener undoListener;

	/**
	* A constructor with listeners for value change and undo. This passes on the received undo events.  
	*/ 
	public ColorWheelEditor( ColorWheelListener listener, UndoListener undoListener )
	{
		this.listener = listener;
		this.undoListener = undoListener;

		addMouseListener( this );
		addMouseMotionListener( this );

 		setPreferredSize( panelSize );
		setMaximumSize( panelSize );
	}
	
	//----------------------------------------- MOUSE
	/**
	* Mouse event.
	*/
	public void mousePressed(MouseEvent e)
	{
		Point2D.Float mouseP = getLegalPoint( e.getX(), e.getY() );
		cursorX = (int) mouseP.x;
		cursorY = (int) mouseP.y;
		listener.valueChanged( getAngle( mouseP), getDistance( mouseP ), DRAG_EVENT );
		repaint();
	}
	/**
	* Mouse event.
	*/
	public void mouseDragged(MouseEvent e)
	{
		Point2D.Float mouseP = getLegalPoint( e.getX(), e.getY() );
		cursorX = (int) mouseP.x;
		cursorY = (int) mouseP.y;
		listener.valueChanged( getAngle( mouseP), getDistance( mouseP ), DRAG_EVENT );
		repaint();
	}
	/**
	* Mouse event.
	*/
	public void mouseReleased(MouseEvent e)
	{
		Point2D.Float mouseP = getLegalPoint( e.getX(), e.getY() );
		cursorX = (int) mouseP.x;
		cursorY = (int) mouseP.y;
		listener.valueChanged( getAngle( mouseP), getDistance( mouseP ), RELEASE_EVENT );
		repaint();
	}
	/**
	* A unhandled mouse event.
	*/
	public void mouseClicked(MouseEvent e){}
	/**
	* A unhandled mouse event.
	*/
	public void mouseEntered(MouseEvent e){}
	/**
	* A unhandled mouse event.
	*/
	public void mouseExited(MouseEvent e){}
	/**
	* A unhandled mouse event.
	*/
	public void mouseMoved(MouseEvent e){}

	/**
	* Sets cursor to a new place on the wheel.
	* @param hue Hue expressed as angle between 0 - 360.
	* @param strength "Strength" of hue which is generally interpreted as saturation, range is from 0.0 to 1.0.
	*/
	public void setCursor( float hue, float strength )
	{
		//--- get hue as clockwise angle from 12 o'clock
		hue = hue + 30.f;
		if( hue > 360.0f ) hue = hue - 360.0f;
		hue = 360 - hue;

		Point.Float vecP = 
			GeometricFunctions.rotatePointAroundPoint( 	hue,
									twelweP,
									midP );
		SVec tVec = new SVec( midP, vecP );
		SVec cursorVec = tVec.getMultipliedSVec( strength );

		Point2D.Float cPos = cursorVec.getEndPos();
		cursorX = (int) cPos.x;
		cursorY = (int) cPos.y;
		repaint();
	}

	private Point2D.Float getLegalPoint( int mouseX, int mouseY )
	{
		Point2D.Float mouseP = new Point2D.Float( mouseX, mouseY );
		SVec dVec = new SVec( midP, mouseP );
		float dist = dVec.getLength();
		if( dist < MAX_DIST ) return mouseP;

		SVec newVec = dVec.getMultipliedSVec( MAX_DIST / dist );
		return newVec.getEndPos();
	}
	
	private float getAngle(  Point2D.Float p )
	{
		float angle = 
			GeometricFunctions.getAngleInDeg(		twelweP,
									midP,
									p);
		boolean cw = GeometricFunctions.pointsClockwise(	twelweP,
									midP,
									p);
		//--- get full circle, not 2 halves
		if(cw) angle = 360.0f - angle;
		//--- covert to GiottoHSL hue
		angle = angle - 30.0f;
		if( angle  < 0.0f) angle = angle + 360.0f;

		return angle;
	}

	private float getDistance( Point2D.Float p )
	{
		SVec dVec = new SVec( midP, p );
		return dVec.getLength() / MAX_DIST;
	}

	/**
	* Passes undo event on to <code>UndoListener</code> provided in constructor. Called after user undo action. 
	*/
	public void undoDone()
	{
		undoListener.undoDone();
	}

	//--------------------------------------------- PAINT
	/**
	* Paint method.
	*/	
	public void paintComponent( Graphics g )
	{
		g.setColor( GUIColors.bgColor );
		g.fillRect(0,0, getWidth(), getHeight() );
		g.drawImage( wheelImg, IMG_W, IMG_H, null);
		g.setColor( Color.black );
		g.fillOval(cursorX - 4, cursorY - 4, 8 ,8);
	}

}//end class
