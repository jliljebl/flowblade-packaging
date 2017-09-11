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

import java.awt.geom.Point2D;

/**
* A mathematical line representation using function y = mx + b.
*/
public class SLine
{
	//--- Values that define line in plain.
	/**
	* Slope.
	*/
	public float m;
	/** 
	* Y intercept.
	*/
	public float b;	
	/**
	* Flag for vertical lines to avoid divisions by zero.
	*/
	public boolean IS_VERTICAL = false;
	/**
	* Value that defines vertical line by the coordinate where line crosses x-axel.
	*/
	public float xIcept;

	//----------------------------------------------------- CONSTRUCTORS
	/** 
	* Constructs vertical line.
	* @param xIcept X coordinate where line crosses x-axel. 
	*/
	public SLine( float xIcept )
	{
		this.xIcept = xIcept;
		IS_VERTICAL = true;
	}
	/**
	* Constructor with slope and y intercept.
	* @param m Slope of line.
	* @param b Point where line crossses y-axel.
	*/
	public SLine( float m,  float b )
	{
		this.m = m;
		this.b = b;
	}
	/**
	* Constructor with two points.
	* @param p1 One of two points defining line
	* @param p2 One of two points defining line
	*/
	public SLine( Point2D.Float p1, Point2D.Float p2 )
	{
		this( p1.x, p1.y, p2.x, p2.y );
	}	
	/**
	* Constructor with coordinates of two points.
	* @param x1 X position of one of two points defining line
	* @param y1 Y position of one of two points defining line
	* @param x2 X position of one of two points defining line
	* @param y2 Y position of one of two points defining line
	*/
	public SLine( float x1, float y1, float x2, float y2 )
	{
		//--- Vertical line
		if( x1 == x2 )
		{
			IS_VERTICAL = true;
			xIcept = x1;
		}
		//--- all others
		else
		{
			//--- get slope
			m = (y2-y1) / (x2-x1);
			//--- get y intercept b
			//--- b = y - mx
			b = y1 - ( m * x1 );
		}
	}
	/**
	* Returns intersection point of provided line with this line or null is lines colinear.
	* @param iLine Intersecting line.
	*/
	public Point2D.Float getIntersectionPoint( SLine iLine )
	{
		//--- If both are vertical, no inter section
		if( iLine.IS_VERTICAL && IS_VERTICAL )
		{
			return null;
		}
		//-- If both have same slope and neither is vertical, no intersection
		//--- NOTE: vertical and horizontal lines both have m == 0, but
		//--- vertical has IS_VERTICAL == true too and m is really infinete.
		if( iLine.m == m && iLine.IS_VERTICAL == false && IS_VERTICAL == false )
		{
			return null;
		}
		//--- One line is vertical
		if( IS_VERTICAL ) return getISPForVertAndNonVert( this, iLine );
		if( iLine.IS_VERTICAL ) return getISPForVertAndNonVert( iLine, this );

		//--- Both lines are non-vertical
		float intersectX = ( iLine.b - this.b ) / ( this.m - iLine.m );
		float intersectY = intersectX * this.m + this.b;

		return new Point2D.Float( intersectX, intersectY );
	}
	
	//--- Returns intersection between vertical and non vertical lines.
	private static Point2D.Float getISPForVertAndNonVert( SLine vertical, SLine nonVertical )
	{
		float isY = nonVertical.m * vertical.xIcept + nonVertical.b;
		return new Point2D.Float( vertical.xIcept, isY );
	}

	/**
	* Returns point on this line and that is also on the line that is perpendicular with this and goes through provided point.
	* @param p Poimt that is projected on line.
	*/
	public Point2D.Float getNormalProjectionPoint( Point2D.Float p )
	{
		//--- This is vertical
		if( IS_VERTICAL )
			return new Point2D.Float( xIcept, p.y );

		SLine normalLine;

		//--- normal for this line is vertical through point p
		if( m == 0 )
			normalLine = new SLine( p.x );
		//--- normal for this has slope
		else
		{
			//--- In perpendicular lines m1 * m2 = -1
			float normalSlope = -1 / m;
			float normalB = p.y - normalSlope * p.x;
			normalLine = new SLine( normalSlope, normalB );
		}

		return getIntersectionPoint( normalLine );
	}

	/**
	* Returns colinear line that goes through given point.
	* @param p Point on colinear line.
	*/
	public SLine getColinearLineThrougPoint( Point2D.Float p )
	{
		//--- Vertical
		if( IS_VERTICAL )
			return new SLine( p.x );
		//--- Others
		float newB = p.y - this.m * p.x;
		return new SLine( m, newB );
	}
	/**
	* Returns true if lines are mathematically the same line. 
	* @param testLine Possibly the same line. 
	*/
	public boolean equals( SLine testLine )
	{
		if( testLine.m == m && testLine.b == b ) 
			return true;

		return false;
	}

}//end class

