package animator.phantom.gui.view.editlayer;

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

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Vector;

import animator.phantom.gui.view.EditPoint;
import animator.phantom.gui.view.ViewRenderUtils;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.param.AnimatedImageCoordinates;

/**
* A rectangle shape of <code>EditPoints</code> that has an achor point.
* <p>
* Object updates values of <code>AnimatedValue</code> parameters of 
* an <code>AnimatedImageCoordinates</code> object. Use to edit affine transformable rectangular image sources.
*/
public class AnchorRectEditShape extends AnimCoordsEditShape
{
	//--- Rectangle 
	private Rectangle rectSize;

	//--- Edit point identifiers.
	private final int TOP_LEFT_CORNER = 0;
	private final int TOP_Y_SCALE_HANDLE = 1;
	private final int TOP_RIGHT_CORNER = 2;
	private final int LEFT_X_SCALE_HANDLE = 3;
	private final int BOTTOM_RIGHT_CORNER = 4;
	private final int BOTTOM_Y_SCALE_HANDLE = 5;
	private final int BOTTOM_LEFT_CORNER = 6;
	private final int RIGHT_X_SCALE_HANDLE = 7;

	//--- Editpoint identifying vectors.
	private Vector<EditPoint> handles = new Vector<EditPoint>();
	private Vector<EditPoint> corners = new Vector<EditPoint>();
	private Vector<EditPoint> widthHandles = new Vector<EditPoint>();
	private Vector<EditPoint> heightHandles = new Vector<EditPoint>();

	/**
	* Constructor with untransformed size, <code>ImageOperation</code> and <code>AnimatedImageCoordinates</code> objects
	*/
	public AnchorRectEditShape( Rectangle rectSize, ImageOperation iop, AnimatedImageCoordinates animCoords )
	{
		this.rectSize = rectSize;
		this.iop = iop;

		for( int i = 0; i < 8; i++ )
		{
			editPoints.add( new EditPoint() );
			if( i % 2 == 0 ) corners.add( editPoints.elementAt( i ) );
			else handles.add( editPoints.elementAt( i ) );
		}
			

		widthHandles.add(  editPoints.elementAt( LEFT_X_SCALE_HANDLE ) );
		widthHandles.add(  editPoints.elementAt( RIGHT_X_SCALE_HANDLE ) );

		heightHandles.add(  editPoints.elementAt( TOP_Y_SCALE_HANDLE ) );
		heightHandles.add(  editPoints.elementAt( BOTTOM_Y_SCALE_HANDLE ) );

		resetEditPoints();
	}

	//--- Point type tests.
	/**
	* Returns true if edit point is in rectangle's corner.
	*/
	public boolean isCornerPoint( EditPoint p ){ return corners.contains( p ); }
	/**
	* Returns true if edit point is in untransformed rectangle's vertical side.
	*/
	public boolean isWidthHandle( EditPoint p  ){ return widthHandles.contains( p ); }
	/**
	* Returns true if edit point is in untransformed rectangle's horizontal side.
	*/
	public boolean isHeightHandle( EditPoint p ){ return heightHandles.contains( p ); }

	//-------------------------------------------------- SHAPE TRANSFORMING
	/**
	* Places editpoints so that they form the untransformed rectangle. Called before points are affine transformed
	* to be in the correct place a frame.
	*/
	public void resetEditPoints()
	{
		//--- w and h in float
		float width = (float) rectSize.width;
		float height = (float) rectSize.height;

		//--- Place edipoints
		editPoints.elementAt( TOP_LEFT_CORNER ).setPos( 0,0 );
		editPoints.elementAt( TOP_Y_SCALE_HANDLE ).setPos( width/2, 0 );
		editPoints.elementAt( TOP_RIGHT_CORNER).setPos( width, 0 );
		editPoints.elementAt( LEFT_X_SCALE_HANDLE).setPos( width, height / 2 );
		editPoints.elementAt( BOTTOM_RIGHT_CORNER).setPos( width, height);
		editPoints.elementAt( BOTTOM_Y_SCALE_HANDLE).setPos( width/2, height );
		editPoints.elementAt( BOTTOM_LEFT_CORNER).setPos( 0, height);
		editPoints.elementAt( RIGHT_X_SCALE_HANDLE).setPos( 0, height / 2 );
	}

	/**
	* Transforms shape by values of provided <code>AnimatedImageCoordinates</code> object in the given frame.
	*/
	public void transformShape( int frame )
	{
		super.transformShape( frame );
		
		//--- In this shape we also need to rotate points.
		float curRotation = animCoords().rotation.getValue( frame );
		setEditPointsRotations( curRotation );
	}
	/**
	* Returns true if point is inside the rectangle.
	*/ 
	public boolean pointInArea( Point2D.Float p )
	{
		Vector<Point2D.Float> points = ViewRenderUtils.getFloatPointsVec( editPoints );
		return GeometricFunctions.pointInConvexPolygon( p, points, 1 );// value 1, see GeomFunctions
	}
	/**
	* Returns reference to <code>Rectangle</code> provided for size of untransformed object. 
	*/
	public Rectangle getRect(){ return rectSize; }

}//end class
