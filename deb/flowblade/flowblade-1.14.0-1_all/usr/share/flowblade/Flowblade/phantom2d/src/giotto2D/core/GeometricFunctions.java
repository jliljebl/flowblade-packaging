package giotto2D.core;

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
import java.awt.geom.Rectangle2D;
import java.util.Vector;

public class GeometricFunctions
{
	public static final int CLOCKWISE = 1;
	public static final int COUNTER_CLOCKWISE = 2;

	//--- retunrs distance between two point.
	public static float distance( Point2D.Float p1, Point2D.Float p2 )
	{
		Double d = new Double( Math.sqrt((p2.x-p1.x)*(p2.x-p1.x) + (p2.y-p1.y)*(p2.y-p1.y)));
		return d.floatValue();
	}

	//--- Returns smaller angle in corner point when connected to points p1, p2
	public static float getAngleInDeg( Point2D.Float p1, Point2D.Float corner, Point2D.Float p2 )
	{
		float side1 = distance( p1, corner );
		float side2 = distance( p2, corner );
		float oppositeSide = distance( p1, p2 );

		float angleCos = ( (side1*side1) + (side2*side2) - (oppositeSide*oppositeSide) ) /
					(2*side1*side2);
		double angleInRad = Math.acos( (double) angleCos );
		return ( new Double( Math.toDegrees( angleInRad ) )).floatValue();
	}

	//--- Returns true if poits are in clockwise order.
	public static boolean pointsClockwise( Point2D.Float p1, Point2D.Float p2, Point2D.Float p3 )
	{
		Point2D.Float edge1 = new Point2D.Float();
		Point2D.Float edge2 = new Point2D.Float();

		edge1.x = p1.x-p2.x;
		edge1.y = p1.y-p2.y;
		edge2.x = p3.x-p2.x;
		edge2.y = p3.y-p2.y;

		if (( (edge1.x * edge2.y) - (edge1.y * edge2.x)) >= 0) return true;
		return false;
	}

	//-- returns direction of points path.
	public static int getPointsDirection( Point2D.Float p1, Point2D.Float p2, Point2D.Float p3 )
	{
		if( pointsClockwise( p1, p2, p3 ) ) return CLOCKWISE;
		else return COUNTER_CLOCKWISE;
	}	

	//--- Returns true if point inside convex polygon.
	//--- Points are interpreted to be in order.
	//--- Polygon has to be convex or this will produce false negetives.
	//--- dirTestFirstPointIndex gives index of first point of three
	//--- used to get polygon direction. Needed because sometimes
	//--- first three points are in line
	public static boolean pointInConvexPolygon( Point2D.Float testPoint,
							Vector<Point2D.Float> points,
							int dirTestFirstPointIndex )
	{
		//--- Polygon has to have  >2 points 
		if( !(points.size() > 2 ) ) return false;
		//--- Get direction
		int dir = getPointsDirection(	points.elementAt( dirTestFirstPointIndex ),
						points.elementAt( dirTestFirstPointIndex + 1 ),
						points.elementAt( dirTestFirstPointIndex + 2 ) );
		//--- dir with two point and test point must always be same 
		//--- if point is inside polygon.
		for( int i = 0 ; i < points.size() - 1; i++ )
		{
			Point2D.Float p1 = points.elementAt( i );
			Point2D.Float p2 = points.elementAt( i + 1 );
			if( getPointsDirection( p1, p2, testPoint ) != dir ) return false;
		}
		Point2D.Float p1 = points.elementAt( points.size() -1 );
		Point2D.Float p2 = points.elementAt( 0 );
		if( getPointsDirection( p1, p2, testPoint ) != dir ) return false;

		return true;
	}
	
	//--- Area test using 0 as first test inded
	public static boolean pointInConvexPolygon( Point2D.Float testPoint,
							Vector<Point2D.Float> points )
	{
		return pointInConvexPolygon( testPoint, points, 0 );
	}

	//--- Returns rotated point
	public static Point2D.Float rotatePointAroundPoint( float rotationAngle,
								Point2D.Float p,
								Point2D.Float anchor )
	{
		Point2D.Float offSetPoint = new Point2D.Float( p.x - anchor.x, p.y - anchor.y );
		Point2D.Float rotatedPoint = rotatePointAroundOrigo( rotationAngle, offSetPoint );
		return new Point2D.Float( rotatedPoint.x + anchor.x, rotatedPoint.y + anchor.y );
	}

	//--- Returns rotated point.
	public static Point2D.Float rotatePointAroundOrigo( float rotationAngle, Point2D.Float p )
	{
		double angleRad = Math.toRadians( (double) rotationAngle );
		float sinVal = (float) Math.sin( angleRad );
		float cosVal = (float) Math.cos( angleRad );
		float newX = p.x * cosVal - p.y * sinVal;
		float newY = p.x * sinVal + p.y * cosVal;
		return new Point2D.Float( newX, newY );
	}

	//--- Returns size of bounding box for rotated rectangle.
	public static Rectangle2D.Float getBoundingSize( 	float Fwidth,
								float Fheight,
								float rotation )
	{	
		//--- Get size
		double width = (double) Fwidth;
		double height = (double) Fheight;
		//--- Rotation in radians.
		double rInRad = Math.toRadians( (double) rotation );
		//--- Sin and cosin for rotation.
		double sinR = Math.abs( Math.sin( rInRad ) );
		double cosR = Math.abs( Math.cos( rInRad ) );
		//--- Get width and height for bounding box after rotation.
		double nHeight = width * sinR + height * cosR;
		double nWidth = width * cosR + height * sinR;

		return new Rectangle2D.Float( 0, 0, (float) nWidth, (float) nHeight );
	}

	//--- Returns topleft corner offset from achorpoint for rotated rectangle.
	public static Point2D.Float getBoundingOffset(	Rectangle2D.Float rect,
							float anchorX,
							float anchorY,
							float rotation )
	{


		//--- Get size
		float width = rect.width;
		float height = rect.height;

		//--- Get corner points 
		Point2D.Float[] corners = new Point2D.Float[ 4 ];
		corners[ 0 ] = new Point2D.Float( -anchorX, -anchorY );
		corners[ 1 ] = new Point2D.Float( width - anchorX, -anchorY );
		corners[ 2 ] = new Point2D.Float( - anchorX, height -anchorY );
		corners[ 3 ] = new Point2D.Float( width - anchorX, height -anchorY );
		//--- Get rotated corner points
		corners[ 0 ] = rotatePointAroundOrigo( rotation, corners[ 0 ] );
		corners[ 1 ] = rotatePointAroundOrigo( rotation, corners[ 1 ] );
		corners[ 2 ] = rotatePointAroundOrigo( rotation, corners[ 2 ] );
		corners[ 3 ] = rotatePointAroundOrigo( rotation, corners[ 3 ] );
		//--- Get anchor point corresponding for rotated bounding box
		float minX = getMinX( corners );
		float minY = getMinY( corners );
		//---
		return new Point2D.Float( Math.abs( minX), Math.abs( minY ) );
	}

	//--- Returns smallest x in points array
	public static float getMinX( Point2D.Float[] points )
	{
		float minX = points[ 0 ].x;
		for( int i = 1; i < points.length; i++ )
			if( points[ i ].x < minX ) minX = points[ i ].x;
		return minX;
	}

	//--- Returns smallest y in points array.
	public static float getMinY( Point2D.Float[] points )
	{
		float minY = points[ 0 ].y;
		for( int i = 1; i < points.length; i++ ) 
			if( points[ i ].y < minY ) minY = points[ i ].y;
		return minY;
	}

	//--- Returns smallest x in points array
	public static float getMaxX( Point2D.Float[] points )
	{
		float maxX = points[ 0 ].x;
		for( int i = 1; i < points.length; i++ )
			if( points[ i ].x > maxX ) maxX = points[ i ].x;
		return maxX;
	}

	//--- Returns smallest y in points array.
	public static float getMaxY( Point2D.Float[] points )
	{
		float maxY = points[ 0 ].y;
		for( int i = 1; i < points.length; i++ ) 
			if( points[ i ].y > maxY ) maxY = points[ i ].y;
		return maxY;
	}

}//end class