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

import java.awt.geom.Point2D;

/**
* A mathematical vector representation.
*/
public class SVec extends SLine
{
	/**
	* Start point
	*/
	protected Point2D.Float startP;
	/**
	* End point.
	*/
	protected Point2D.Float endP;
	/**
	* Direction at creation time.
	*/
	protected float origDir;
	/**
	* Length at creation time.
	*/
	protected float origLength;

	//----------------------------------------------------- CONSTRUCTOR
	/**
	* Constructor with float coordinates.
	* @param x1 Start point x
	* @param y1 Start point y
	* @param x2 End point x
	* @param y2 End point y
	*/
	public SVec(  float x1, float y1, float x2, float y2 )
	{
		this( new Point2D.Float( x1, y1 ), new Point2D.Float( x2, y2 ) );
	}
	/**
	* Constructor with start and end points.
	* @param sp Start point
	* @param ep End point
	*/
	public SVec( Point2D.Float sp, Point2D.Float ep )
	{
		super( sp.x, sp.y, ep.x, ep.y );
		startP = new Point2D.Float(sp.x, sp.y);
		endP = new Point2D.Float(ep.x, ep.y);

		origDir = getDirection();
		origLength = getLength();
	}
	//--- returns 1 or -1. Used with var origiDir to determine direction.
	private float getDirection()
	{
		if( IS_VERTICAL )
			return ( startP.y - endP.y ) / Math.abs( ( startP.y - endP.y ) );
		else
			return ( startP.x - endP.x ) / Math.abs( ( startP.x - endP.x ) );
	}
	/**
	* Returns length of vector, negative length means that direction has reversed since creation time.
	*/
	//---
	public float getLength()
	{
		if( isZeroLength() ) return 0;//zero length vec direction coded as zero.

		// 1 or -1, 1 == same as when created, -1 different
		float currentDirection = getDirection() / origDir;
		float distance = GeometricFunctions.distance( startP, endP );
		return currentDirection * distance;
	}
	/**
	* Returns current length diveded by original length, negative means that direction has reversed since creation time.
	*/
	public float getScaleFactor()
	{
		if( origLength == 0 )
		{
			System.out.println("SVec.getScaleFactor(), origlength == 0");
			System.exit( 1 );
			return 0;
		}
		else return getLength() / origLength;
	}
	/**
	* Returns start position.
	*/
	public Point2D.Float getStartPos(){ return new Point2D.Float( startP.x,  startP.y ); }
	/**
	* Returns end position.
	*/
	public Point2D.Float getEndPos(){ return new Point2D.Float( endP.x,  endP.y ); }
	/**
	* Returns a new vector with same length and direction as this starting from given point.
	* @param p Start position of new vector.
	*/
	public SVec getMovedVec( Point2D.Float p )
	{
		float xDist;
		float yDist;
		if( ( endP.x - startP.x ) == 0 ) xDist = 0;
		else xDist = Math.abs( endP.x - startP.x ) * Math.abs( endP.x - startP.x ) / ( endP.x - startP.x );
		if( (endP.y - startP.y ) == 0 ) yDist = 0;
		else yDist = Math.abs( endP.y - startP.y ) * Math.abs( endP.y - startP.y ) / ( endP.y - startP.y );

		float newEndX = p.x + xDist;
		float newEndY = p.y + yDist;
		Point2D.Float endP = new Point2D.Float( newEndX, newEndY );
		return new SVec( p, endP );
	}
	/**
	* Sets start point to be normal projection point of given point.
	* @param p Point that is projected on vector to get new start point.
	*/	
	public Point2D.Float setSnappedStartPoint( Point2D.Float p )
	{
		startP = getNormalProjectionPoint( p );
		return new Point2D.Float( startP.x, startP.y );
	}
	/**
	* Sets end point to be normal projection point of given point.
	* @param p Point that is projected on vector to get new end point. 
	*/
	public Point2D.Float setSnappedEndPoint( Point2D.Float p )
	{
		endP = getNormalProjectionPoint( p );
		return new Point2D.Float( endP.x, endP.y );
	}
	/**
	* Sets end point to be start point.
	*/
	public void setZeroLength()
	{
		endP = new Point2D.Float( startP.x, startP.y );
	}
	/**
	* Return true if start point == end point.
	*/
	public boolean isZeroLength()
	{
		if(  startP.y == endP.y && startP.x == endP.x ) return true;
		return false;
	}
	/**
	* Returns new multiplied vector.
	* @param multiplier If value is more then 0 new vector is in the same direction if less then 0 direction is reversed.  
	*/
	public SVec getMultipliedSVec( float multiplier )
	{
	
		float xDist;
		float yDist;
		if( ( endP.x - startP.x ) == 0 ) xDist = 0;
		else xDist = Math.abs( endP.x - startP.x ) * Math.abs( endP.x - startP.x ) / ( endP.x - startP.x );
		if( (endP.y - startP.y ) == 0 ) yDist = 0;
		else yDist = Math.abs( endP.y - startP.y ) * Math.abs( endP.y - startP.y ) / ( endP.y - startP.y );

		float xMDist = xDist * multiplier;
		float yMDist = yDist * multiplier;
		float newEndX = startP.x + xMDist;
		float newEndY = startP.y + yMDist;

		return new SVec( startP, new Point2D.Float( newEndX, newEndY ) );
	}
	/**
	* Returns new vector that has its end point rotated around start point for given rotation.
	* @param rotation Rotation in degrees.
	*/	
	//--- Rotates Vec around start point
	public SVec getRotatedVec( float rotation )
	{
		Point2D.Float newEnd =
			GeometricFunctions.rotatePointAroundPoint( 	rotation,
									endP,
									startP );
		return new SVec( startP, newEnd );
	}
	/**
	* Returns new vector from start to given points normal projection point.
	* @param p Point that is projected to get new end point.
	*/
	public SVec getProjectionVec( Point2D.Float p )
	{
		Point2D.Float pn = getNormalProjectionPoint( p );
		return new SVec( getStartPos(), pn );
	}
	/**
	* Returns new vector from projection point to projected point.
	* @param p Point that is projected to get new start point. Its also the new end point.
	*/
	public SVec getDistanceVec( Point2D.Float p )
	{
		Point2D.Float pn = getNormalProjectionPoint( p );
		return new SVec( pn, p );
	}
	/**
	* Retuns combined vector, add vector starts from end point.
	* @param addVec Vector that is added to get new vector.
	*/
	public SVec getCombinedVec( SVec addVec )
	{
		Point2D.Float sP = addVec.getStartPos();
		Point2D.Float eP = addVec.getEndPos();
		float xD = eP.x - sP.x;
		float yD = eP.y - sP.y;
		float nex = endP.x + xD;
		float ney = endP.y + yD;
		return new SVec( getStartPos(), new Point2D.Float( nex, ney ) );
	}
	/**
	* Returns true if point is in the area that is bordered by perpencicular lines going through vector end points.
	* @param p Test point
	*/	
	public boolean pointInBetween( Point2D.Float p )
	{
 		SVec reverse = new SVec( endP, startP );
		Point2D.Float pp = getNormalProjectionPoint( p );
		SVec sToPP = new SVec( startP, pp );
		SVec eToPP = new SVec( endP, pp );

		if( getDirection() == sToPP.getDirection() &&
			reverse.getDirection() == eToPP.getDirection() ) return true;

		return false;
	}

	/*
	public void printDebug()
	{
		System.out.println( "sx:" + startP.x + ", sy:" + startP.y + ", ex:" +  endP.x + ", ey:" + endP.y + " length:" + getLength() );
	}
	*/
}//end class
