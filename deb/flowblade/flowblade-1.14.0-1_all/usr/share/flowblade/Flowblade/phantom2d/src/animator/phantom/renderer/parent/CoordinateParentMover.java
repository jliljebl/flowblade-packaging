package animator.phantom.renderer.parent;

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

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import animator.phantom.gui.view.SVec;
import animator.phantom.renderer.param.AnimatedImageCoordinates;

public class CoordinateParentMover extends AbstractParentMover
{
	public CoordinateParentMover()
	{
		//type = 1;
	}

	protected TransformClone transform( TransformClone child, TransformClone parent )
	{
		//-- in parent coord plane
		Point2D.Float childpos = new Point2D.Float( parent.x.getValue( 0 ) + child.x.getValue( 0 ),
								parent.y.getValue( 0 ) + child.y.getValue( 0 ) );
		Point2D.Float parentpos = new Point2D.Float( parent.x.getValue( 0 ), parent.y.getValue( 0 ) );

		SVec xcomp = new SVec( parentpos, new Point2D.Float( childpos.x, parentpos.y ) );
		SVec ycomp = new SVec( parentpos, new Point2D.Float( parentpos.x, childpos.y ) );
		float parentRotation = parent.rotation.getValue( 0 );
		xcomp = xcomp.getRotatedVec( parentRotation );
		ycomp = ycomp.getRotatedVec( parentRotation );
		xcomp = xcomp.getMultipliedSVec( parent.xScale.getValue( 0 ) / 100.0f );
		ycomp = ycomp.getMultipliedSVec( parent.yScale.getValue( 0 ) / 100.0f  );
		SVec posVec = ycomp.getCombinedVec( xcomp );

		//--- Get child pos.
		float childX = posVec.getEndPos().x;
		float childY = posVec.getEndPos().y;

		//--- scale = child scale * parent scale
		float xScale = ( child.xScale.getValue( 0 ) *
					 parent.xScale.getValue( 0 ) ) / 100.0f;
		float yScale = ( child.yScale.getValue( 0 ) *
					 parent.yScale.getValue( 0 ) ) / 100.0f;

		//--- child anchor points are unaffected by parent anchor points.
		float xAnchor = child.xAnchor.getValue( 0 );
		float yAnchor = child.yAnchor.getValue( 0 );
		//--- child rotation = child rotation + parent rotation
		float rotation = child.rotation.getValue( 0 ) + parentRotation;
		//--- Set transformed values.
		child.x.setValue( 0, childX );
		child.y.setValue( 0, childY );
		child.xScale.setValue( 0, xScale );
		child.yScale.setValue( 0, yScale );
		child.xAnchor.setValue( 0, xAnchor );
		child.yAnchor.setValue( 0, yAnchor );
		child.rotation.setValue( 0, rotation );

		return child;
	}

	private float[] getReverseTransPoint( int frame, float[] srcP )
	{
		float[] dstP = new float[2];
		AffineTransform at = new AffineTransform();
		at.scale( 1 / ( parent.getCoords().xScale.getValue( frame ) / AnimatedImageCoordinates.xScaleDefault ),
				1 / (parent.getCoords().yScale.getValue( frame ) / AnimatedImageCoordinates.yScaleDefault ) );
		at.rotate( Math.toRadians( -parent.getCoords().rotation.getValue( frame ) ) );
		at.translate( - parent.getCoords().x.getValue( frame ), - parent.getCoords().y.getValue( frame ) );
		at.transform(	srcP,
                      		0,
                      		dstP,
				0,
                      		1);
		return dstP;
	}
	
	private float[] getPoint( float x, float y )
	{
		float[] p = new float[2];
		p[ 0 ] = x;
		p[ 1 ] = y;
		return p;
	}

	public float getChildX(  float transval, float yval, int frame )
	{
		float[] p = getPoint( transval, yval );
		float[] pt = getReverseTransPoint( frame, p );
		return pt[ 0 ];
	}

	public float getChildY(  float transval, float xval, int frame )
	{
		float[] p = getPoint( xval, transval );
		float[] pt = getReverseTransPoint( frame, p );
		return pt[ 1 ];
	}

	public float getChildXScale( float transval, int frame )
	{
		return transval * AnimatedImageCoordinates.xScaleDefault
			/ parent.getCoords().xScale.getValue( frame );
	}

	public float getChildYScale( float transval, int frame )
	{
		return transval * AnimatedImageCoordinates.yScaleDefault
			/ parent.getCoords().yScale.getValue( frame );
	}

	public float getChildRotation( float transval, int frame ){ return transval - parent.getCoords().rotation.getValue( frame ); }

}//end class