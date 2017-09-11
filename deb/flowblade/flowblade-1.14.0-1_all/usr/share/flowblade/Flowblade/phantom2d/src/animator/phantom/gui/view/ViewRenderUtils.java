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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Vector;

/**
* Utility methods for drawing edit layers.
*/
public class ViewRenderUtils
{
	/**
	* Draws points into graphics context
	* @param g2 Graphics context that will be drawn on.
	* @param points Points that will be drawn.
	*/
	public static void drawPoints( Graphics2D g2, Vector<EditPoint> points )
	{
		for( EditPoint point : points )
			point.paintPoint( g2 );
	}
	/**
	* Draws points into graphics context in set color.
	* @param g2 Graphics context that will be drawn on.
	* @param points Points that will be drawn.
	* @param c Color that will be used for drawing
	*/
	public static void drawPoints( Graphics2D g2, Vector<EditPoint> points, Color c )
	{
		for( EditPoint point : points )
		{
			point.setColor( c );
			point.paintPoint( g2 );
		}
	}
	/**
	* Draws lines between points to form a closed polygon.
	* @param g2 Graphics context that will be drawn on.
	* @param points Points that will be connected with line.
	* @param c Color that will be used for drawing
	*/
	public static void drawPolygon( Graphics2D g2, Vector<EditPoint> points, Color c )
	{
		drawPolygon( g2, points, c, true );
	}
	/**
	* Draws lines between points to form a polygon, polygon may or may not be closed.
	* @param g2 Graphics context that will be drawn on.
	* @param points Points that will be connected with line.
	* @param c Color that will be used for drawing
	* @param closed If true polygon will bw closed between first and last point.
	*/
	public static void drawPolygon( Graphics2D g2, Vector<EditPoint> points, Color c, boolean closed )
	{
		for( int i = 0; i < points.size() - 1; i++ )
		{
			EditPoint start = points.elementAt( i );
			EditPoint end = points.elementAt( i + 1 );
			Line2D.Float line = new Line2D.Float(start.getPos(),end.getPos() );
			g2.setColor( c );
			g2.draw( line );
		}
		if( closed )
		{
			EditPoint start = points.elementAt( points.size() - 1 );
			EditPoint end = points.elementAt( 0 );
			Line2D.Float line = new Line2D.Float(start.getPos(),end.getPos() );
			g2.draw( line );
		}
	}
	/**
	* Draws lines between points to form a closed polygon.
	* @param g2 Graphics context that will be drawn on.
	* @param c Color that will be used for drawing
	* @param points Points that will be connected with line.
	*/
	public static void drawPolygon( Graphics2D g2, Color c, Vector<Point2D.Float> points )
	{
		drawPolygon( g2, c, points, true );
	}
	/**
	* Draws lines between points to form a polygon, polygon may or may not be closed.
	* @param g2 Graphics context that will be drawn on.
	* @param c Color that will be used for drawing
	* @param points Points that will be connected with line.
	* @param closed If true polygon will bw closed between first and last point.
	*/
	public static void drawPolygon( Graphics2D g2, Color c, Vector<Point2D.Float> points, boolean closed )
	{

		for( int i = 0; i < points.size() - 1; i++ )
		{
			Point2D.Float start = points.elementAt( i );
			Point2D.Float end = points.elementAt( i + 1 );
			Line2D.Float line = new Line2D.Float(start, end );
			g2.setColor( c );
			g2.draw( line );
		}
		if( closed )
		{
			Point2D.Float start = points.elementAt( points.size() - 1 );
			Point2D.Float end = points.elementAt( 0 );
			Line2D.Float line = new Line2D.Float(start, end );
			g2.draw( line );
		}
	}
	/**
	* Converts <code>EditPoint Vector</code> to <code>Point2D.Float Vector</code>.
	* @param points Points that will be converted
	*/
	public static Vector<Point2D.Float> getFloatPointsVec( Vector<EditPoint> points )
	{
		Vector<Point2D.Float> retVec = new Vector<Point2D.Float>();
		for( EditPoint point : points )
			retVec.add( point.getPos() );

		return retVec;
	}
	/**
	* Moves points.
	* @param points Points that will be translated.
	* @param x X translation
	* @param y Y translation
	*/
	public static void translate( Vector<EditPoint> editPoints, float x, float y)
	{
		for( EditPoint p : editPoints )
		{
			//--- scaled anchor offset
			p.x = p.x + x;
			p.y = p.y + y;
		}
	}
	/**
	* Scales point distances from anchor.
	* @param anchor Anchor point
	* @param points Points that will be scaled on vector pointing from anchor point.
	* @param xScale Value of scaling on x component of vector from anchor point.
	* @param yScale Value of scaling on y component of vector from anchor point.
	*/
	public static void scalePointsAroundAchor( 	EditPoint anchor,
							Vector<EditPoint> editPoints,
							float xScale,
							float yScale )
	{
		for( EditPoint p : editPoints )
		{
			//--- scaled anchor offset
			float xOff = ( p.x - anchor.x ) * xScale;
			float yOff = ( p.y - anchor.y ) * yScale;
			//--- Set pos
			p.setPos( anchor.x + xOff,  anchor.y + yOff );
		}
	}
	/**
	* Rotates points around anchor
	* @param anchor Anchor point
	* @param points Points that will be rotated around anchor point.
	* @param angle Angle in degrees.
	*/
	public static void rotatepointsAroundAnchor( 	EditPoint anchor,
							Vector<EditPoint> points,
							float angle )
	{
		for( EditPoint point : points )
		{
			Point2D.Float anchorP = anchor.getPos();
			Point2D.Float rotatedPos =
				GeometricFunctions.rotatePointAroundPoint( 	angle,
										point.getPos(),
										anchorP );
			point.setPos( rotatedPos );
		}
	}

}//end class