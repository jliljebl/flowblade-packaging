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

import animator.phantom.renderer.param.AnimatedImageCoordinates;

public class MoveParentMover extends AbstractParentMover
{
	public MoveParentMover()
	{
		//type = 0;
	}

	protected TransformClone transform( TransformClone child, TransformClone parent )
	{
		float x = child.x.getValue( 0 ) + parent.x.getValue( 0 );
		float y = child.y.getValue( 0 ) + parent.y.getValue( 0 );
		float xScale  = child.xScale.getValue( 0 ) * ( parent.xScale.getValue( 0 ) 
				/ AnimatedImageCoordinates.xScaleDefault );
		float yScale = child.yScale.getValue( 0 ) * ( parent.yScale.getValue( 0 ) 
				/ AnimatedImageCoordinates.yScaleDefault );
		float xAnchor = child.xAnchor.getValue( 0 );
		float yAnchor = child.yAnchor.getValue( 0 );
		float rotation = child.rotation.getValue( 0 ) + parent.rotation.getValue( 0 ) ;

		//--- Set transformed values.
		child.x.setValue( 0, x );
		child.y.setValue( 0, y );
		child.xScale.setValue( 0, xScale );
		child.yScale.setValue( 0, yScale );
		child.xAnchor.setValue( 0, xAnchor);
		child.yAnchor.setValue( 0, yAnchor);
		child.rotation.setValue( 0, rotation );

		return child;
	}

	//--- Reverse for ViewEditor gui.
	public float getChildX( float transval, float xval, int frame )
	{
		return transval - parent.getCoords().x.getValue( frame );
	}

	public float getChildY( float transval, float yval, int frame )
	{
		return transval - parent.getCoords().y.getValue( frame );
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