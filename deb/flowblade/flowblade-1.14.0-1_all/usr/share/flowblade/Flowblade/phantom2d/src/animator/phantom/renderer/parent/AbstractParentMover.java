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

import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.param.AnimatedImageCoordinates;

public abstract class AbstractParentMover
{
	public static String[] types = { "all movement","coordinate plane","x,y","rotation","scale" };
	/*
	public static String[] infos = { 
		"Child follows all animation","Parent becomes background plate",
			"Child follows movement","Child follows rotation", "Child follows scaling" };
	*/
	
	protected AnimatedImageCoordinates unTransformed;
	//protected int type;
	protected ImageOperation parent;

	public void setParent( ImageOperation p )
	{
		parent = p;
	}

	public static AbstractParentMover getMover( int typeId )
	{
		switch( typeId )// typeId, see above String[] types
		{
			case 0:
				return new MoveParentMover();
			case 1:
				return new CoordinateParentMover();
			case 2:
				return new XYParentMover();
			case 3:
				return new RotationParentMover();
			case 4:
				return new ScaleParentMover();
			default:
				 return null;
		}
	}

	public TransformClone doTransform( ImageOperation child, int frame )
	{
		unTransformed = child.getCoords();
		return transform( 	child.getCoords().getTransformClone( frame ),
					parent.getCoords().getTransformClone( frame ) );
	}

	public TransformClone doLoopedTransform( ImageOperation child, int realFrame, int loopedFrame )
	{
		unTransformed = child.getCoords();
		return transform( 	child.getCoords().getTransformClone( loopedFrame ),
					parent.getCoords().getTransformClone( realFrame ) );
	}
	
	protected abstract TransformClone transform( TransformClone child,TransformClone parent );

	//--- Overload to provide view editor functionality
	//public TransformClone getChildCoords( TransformClone transformed ){ return  null; }

	public AnimatedImageCoordinates getUntransformed(){ return unTransformed; }

	//--- Extending ParentMovers overload these to support view editor in which we know transformed value
	//--- from gui and have to get child value so gui is useful.
	//--- default: transformed value == child value
	public float getChildX( float transval, float yval, int frame ){ return transval; }
	public float getChildY( float transval, float xval, int frame ){ return transval; }
	public float getChildXScale( float transval, int frame ){ return transval; }
	public float getChildYScale( float transval, int frame ){ return transval; }
	public float getChildAnchorX( float transval, int frame ){ return transval; }
	public float getChildAnchorY( float transval, int frame ){ return transval; }
	public float getChildRotation( float transval, int frame ){ return transval; }

}//end class