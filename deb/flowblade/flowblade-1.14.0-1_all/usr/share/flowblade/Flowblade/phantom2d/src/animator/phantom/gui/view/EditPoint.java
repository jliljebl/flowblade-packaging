package animator.phantom.gui.view;

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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Vector;

import animator.phantom.controller.GUIComponents;

/**
* A point that can be hit with a mouse press. Point has rotation for display purposes,
* hit detection does not rotate. GUI output type can be selected.
*/
public class EditPoint
{
	//--- Edit point coornitaes in real space when detecting hits
	//--- Edit points with coordinates in panel space are created for drawing.
	/**
	* X position.
	*/ 
	public float x = 0;
	/**
	* Y position.
	*/ 
	public float y = 0;
	private float rotation = 0;

	private boolean isHittable = true;

	private Point2D.Float moveStartPos = new Point2D.Float( x, y );
	
	private static int AREA_SIDE_HALF = 4;

	private Color pColor = Color.white;

	private Rectangle legalArea = null;
	
	/**
	* A rotating line box GUI representation.
	*/
	public static final int MOVE_HANDLE = 0;
	/**
	* A solid ball GUI representation.
	*/
	public static final int ROTATE_HANDLE = 1;
	/**
	* A red solid unrotating rectangle GUI representation.
	*/
	public static final int CONTROL_POINT = 2;
	/**
	* A point with no GUI representation.
	*/
	public static final int INVISIBLE_POINT = 3;
	private int displayType = MOVE_HANDLE;
	/**
	* No parameter constructor.
	*/
	public EditPoint(){}

	/**
	* Constructor with type.
	* @param type Display type.
	*/
	public EditPoint( int type )
	{
		this.displayType = type;
	}
	/**
	* Constructor with position.
	* @param p Position
	*/	
	public EditPoint( Point2D.Float p )
	{
		this.x = p.x;
		this.y = p.y;
	}
	/**
	* Constructor with position.
	* @param x X position
	* @param y Y Position
	*/
	public EditPoint( float x, float y )
	{
		this.x = x;
		this.y = y;
	}
	/**
	* Sets position.
	* @param newPos New position
	*/
	public void setPos( Point2D.Float newPos )
	{
		this.x = newPos.x;
		this.y = newPos.y;

		legalizePosition();
	}
	/**
	* Sets position.
	* @param x New X position
	* @param y New Y Position
	*/
	public void setPos( float x, float y )
	{
		this.x = x;
		this.y = y;

		legalizePosition();
	}
	/**
	* Returns position.
	*/
	public Point2D.Float getPos(){ return new Point2D.Float( x, y ); }

	/**
	* Sets rotation.
	*@param r New displayed rotation.
	*/
	public void setRotation( float r ){ rotation = r; }
	/**
	* Returns rotation.
	*/
	public float getRotation(){ return rotation; }

	/**
	* Saves position as move start position. Position later used with method <code>setMoveDelta( Point2D.Float delta )</code>
	*/
	public void recordStartPos(){ moveStartPos = new Point2D.Float( x, y ); }
	/**
	* Sets move start position.
	* @param sPos Sets start position used with method <code>setMoveDelta( Point2D.Float delta )</code>
	*/
	public void setMoveStartPos( Point2D.Float sPos ){ moveStartPos = sPos; }
	/**
	* Sets position to be move start position + delta.
	* @param delta Position delta.
	*/
	public void setMoveDelta( Point2D.Float delta )
	{
		x = moveStartPos.x + delta.x;
		y = moveStartPos.y + delta.y;

		legalizePosition();
	}
	/**
	* Sets display color.
	* @param newColor New displayed color.
	*/
	public void setColor( Color newColor ){ pColor = newColor; }
	/**
	* Sets display type.
	* @param displayType New display type.
	*/
	public void setDisplayType( int displayType )
	{
		 this.displayType = displayType;
	}
	/**
	* Sets area inside which user can move point.
	*/
	public void setLegalArea( Rectangle r )
	{ 
		legalArea = r; 
		legalizePosition();
	}
	/**
	* Returns display type.
	*/
	public int getDisplayType(){ return displayType; }
	/**
	* Setting this false will make return value for <code>hit()</code> always be false.
	* @param val If true point can be hit.
	*/
	public void setHittable( boolean val){ isHittable = val; }
	/**
	* Returns true if position inside point hit area. With correction for scale because we are using movie space points as input.
	* @param testX X position of test position
	* @param testY Y position of test position
	*/
	public boolean hit( float testX, float testY )
	{
		if( !isHittable ) return false;
		float scale = getScale();
		if( 	testX >= x - (int)((float)AREA_SIDE_HALF * (1.0f / scale) ) 
			&& testX <= x + (int)((float)AREA_SIDE_HALF  * (1.0f / scale) ) 
			&& testY >= y - (int)((float)AREA_SIDE_HALF * (1.0f / scale) ) 
			&& testY <= y +  (int)((float)AREA_SIDE_HALF * (1.0f / scale) )  ) return true;

		return false;
	}

	private static float getScale(){ return GUIComponents.viewEditor.getScale(); }
	/**
	* Paints point.
	* @param g2 Graphics contaxt that point is draw on.
	*/
	public void paintPoint( Graphics2D g2 )
	{
		switch( displayType )
		{
			case MOVE_HANDLE:
				paintMoveHandle( g2 );
				break;

			case ROTATE_HANDLE:
				paintRotateHandle( g2 );
				break;
	
			case CONTROL_POINT:
				paintControlPoint( g2 );
				break;

			case INVISIBLE_POINT:
				break;

			default:
				paintMoveHandle( g2 );
		}
	}

	private void paintMoveHandle( Graphics2D g2 )
	{
		Point2D.Float p1 = new Point2D.Float( x - AREA_SIDE_HALF, y - AREA_SIDE_HALF );
		Point2D.Float p2 = new Point2D.Float( x + AREA_SIDE_HALF, y - AREA_SIDE_HALF );
		Point2D.Float p3 = new Point2D.Float( x + AREA_SIDE_HALF, y + AREA_SIDE_HALF );
		Point2D.Float p4 = new Point2D.Float( x - AREA_SIDE_HALF, y + AREA_SIDE_HALF );
	
		Point2D.Float midP = new Point2D.Float( x, y );

		p1 = GeometricFunctions.rotatePointAroundPoint( rotation, p1, midP );
		p2 = GeometricFunctions.rotatePointAroundPoint( rotation, p2, midP );
		p3 = GeometricFunctions.rotatePointAroundPoint( rotation, p3, midP );
		p4 = GeometricFunctions.rotatePointAroundPoint( rotation, p4, midP );

		Vector<Point2D.Float> points = new Vector<Point2D.Float>();
		points.add( p1 );
		points.add( p2 );
		points.add( p3 );
		points.add( p4 );

		ViewRenderUtils.drawPolygon( g2, pColor, points );
	}

	private void paintRotateHandle( Graphics2D g2 )
	{
		g2.setColor( pColor );
		g2.fillOval( (int) x - AREA_SIDE_HALF,
				(int) y - AREA_SIDE_HALF,
                              AREA_SIDE_HALF * 2,
                              AREA_SIDE_HALF * 2 );
	}
	
	private void paintControlPoint( Graphics2D g2 )
	{
		g2.setColor( Color.red );
		g2.fillRect( (int) x - AREA_SIDE_HALF + 1,
				(int) y - AREA_SIDE_HALF + 1,
                              AREA_SIDE_HALF * 2 - 2,
                              AREA_SIDE_HALF * 2  -2 );
	}

	private void legalizePosition()
	{
		if( legalArea == null )
			return;

		if( x < legalArea.x ) x = legalArea.x;
		if( x > legalArea.x + legalArea.width ) x = legalArea.x + legalArea.width;

		if( y < legalArea.y ) y = legalArea.y;
		if( y > legalArea.y + legalArea.height ) y = legalArea.y + legalArea.height;
	}

}//end class