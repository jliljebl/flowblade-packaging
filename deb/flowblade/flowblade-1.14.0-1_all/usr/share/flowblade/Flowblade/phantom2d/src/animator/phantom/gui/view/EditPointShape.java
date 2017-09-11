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


import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

/**
* A shape made of <code>EditPoints</code> one of which may be the anchor point.
* <p>
* All edit layers must create and register one and only one editable shape by extending this class.
*/
public abstract class EditPointShape
{
	/**
	* Anchor point for scale and rotation transforms.
	*/
	protected EditPoint ANCHOR_POINT = new EditPoint();
	/**
	* Edit points in this shape.
	*/ 
	protected Vector<EditPoint> editPoints = new Vector<EditPoint>();

	//--------------------------------------------- ABSTRACT METHODS
	/**
	* Extending must override to provide functionality for aea hit test. Should return true if point inside shape.
	* @param p Hit test point.
	*/
	public abstract boolean pointInArea( Point2D.Float p );
	

	//--------------------------------------------------- TRANSFORMATIONS
	/**
	* Moves anchor and edit points by x and y.
	* @param x X translation.
	* @param y Y translation.
	*/
	public void translate( float x, float y )
	{
		ViewRenderUtils.translate( getAllPoints(), x, y);
	}
	/**
	* Translates editpoints but not anchorpoint.
	* @param x X translation for edit points, anchor not affected.
	* @param y Y translation for edit points, anchor not affected.
	*/
	public void translateEditPoints( float x, float y )
	{
		ViewRenderUtils.translate( editPoints, x, y);
	}

	/**
	* Scales edit points around anchor point.
	* @param scale Normalized scale value.
	*/
	public void scale( float scale )
	{
		 scale( scale, scale );
	}
	/**
	* Scales edit points around anchor point.
	* @param xScale Normalized scale value for x axel scaling.
	* @param yScale Normalized scale value for y axel scaling.
	*/
	public void scale( float xScale, float yScale )
	{
		ViewRenderUtils.scalePointsAroundAchor( ANCHOR_POINT, editPoints, xScale, yScale );
	}
	/**
	* Rotates editpoints around anchorpoint.
	* @param rotation Rotation in degrees.
	*/
	public void rotate( float rotation )
	{
		ViewRenderUtils.rotatepointsAroundAnchor( ANCHOR_POINT, editPoints, rotation );
	}
	/**
	* Sets point display rotation value for all edit points.
	* @param rotation Rotation in degrees.
	*/
	public void setEditPointsRotations( float rotation )
	{
		for( EditPoint p : editPoints )
			p.setRotation( rotation );
	}
	/**
	* Translates ANCHOR_POINT.
	* @param x X translation.
	* @param y Y translation.
	*/
	public void moveAnchorPoint( float x, float y )
	{
		ANCHOR_POINT.x = ANCHOR_POINT.x + x;
		ANCHOR_POINT.y = ANCHOR_POINT.y + y;
	}

	//--------------------------------------------- INTERFACE
	/**
	* Returns editpoints.
	*/
	public Vector<EditPoint> getEditPoints(){ return editPoints; }
	/**
	* Adds edit point as last in Vector.
	* @param p New edit point.
	*/
	public void addEditPoint( EditPoint p ){ editPoints.add( p ); }
	/**
	* Utility method that returns Vector of edit points between indexes.
	* @param start Start index, inclusive.
	* @param end End index, exclusive.
	*/
	public Vector<EditPoint> getEditPointsPart( int start, int end )
	{ 
		Vector<EditPoint> rVec = new Vector<EditPoint>();
		for( int i = start; i < end; i++ )
		{
			rVec.add( editPoints.elementAt( i ) );
		}
		return rVec;
	}

	/** 
	* Returns editpoints + ANCHOR_POINT.
	*/
	public Vector<EditPoint> getAllPoints()
	{
		Vector<EditPoint> retVec = new Vector<EditPoint>( editPoints );
		retVec.add( ANCHOR_POINT );
		return retVec;
	}
	/**
	* Returns anchor point.
	*/
	public EditPoint getAnchorPoint(){ return ANCHOR_POINT; }
	/**
	* Sets anchor point.
	* @param ap New anchor point.
	*/
	public void setAnchorPoint( EditPoint aP ){ ANCHOR_POINT = aP; }
	/**
	* Returns index of edit point.
	*/
	public int getIndexOfPoint( EditPoint point ){ return editPoints.indexOf( point ); }
	/**
	* Returns edit point with given index in Vector.
	* @param index Index of edit point.
	*/
	public EditPoint getEditPoint( int index ){ return editPoints.elementAt( index ); }
	/**
	* Sets display type for all points.
	* @param displayType New display type.
	*/
	public void setDisplayType( int displayType )
	{
		for( EditPoint p : editPoints )
			p.setDisplayType( displayType );
	}
	/**
	* Returns bounding box containing all points.
	*/
	public Rectangle2D.Float getBoundingBox()
	{
		float x_low = 1000000000;
		float x_high = -100000000;
		float y_low = 1000000000;
		float y_high = -100000000;

		for( EditPoint p : editPoints )
		{
			if( p.x < x_low ) x_low = p.x;
			if( p.x > x_high ) x_high = p.x;
			if( p.y < y_low ) y_low = p.y;
			if( p.y > y_high ) y_high = p.y;
		}

		return new Rectangle2D.Float( 	x_low, 
						y_low,
						x_high - x_low, 
						y_high - y_low );
	}

	/**
	* Sets area inside which user can move points.
	*/
	public void setPointsLegalArea( Rectangle r )
	{
		for( EditPoint p : editPoints )
			p.setLegalArea( r );
	}

}//interface