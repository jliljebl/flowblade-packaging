package animator.phantom.renderer.param;

/*
    Copyright Janne Liljeblad 

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
import java.util.Vector;

import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.parent.TransformClone;

/** 
* A <code>Vector</code> of  <code>AnimatedValue</code> parameters defining an animated affine transform.
*/ 
public class AnimatedImageCoordinates
{
	/**
	* X position of animated object.
	*/
	public AnimatedValue x;
	/**
	* Y position of animated object.
	*/
	public AnimatedValue y;
	/**
	* X scale of animated object.
	*/
	public AnimatedValue xScale;
	/**
	* Y scale of animated object.
	*/
	public AnimatedValue yScale;
	/**
	* Anchor point X coordinate offset from topleft corner of an untransformed rectangular animated object.
	*/
	public AnimatedValue xAnchor;
	/**
	* Anchor point Y coordinate offset from topleft corner of an untransformed rectangular animated object.
	*/
	public AnimatedValue yAnchor;
	/**
	* Rotation of animated object.
	*/
	public AnimatedValue rotation;
	/**
	* X position default value.
	*/
	public static final float xDefault = 0.0f;
	/**
	* Y position default value.
	*/
	public static final float yDefault = 0.0f;
	/**
	* X scale default value.
	*/
	public static final float xScaleDefault = 100.0f;
	/**
	* Y scale default value.
	*/
	public static final float yScaleDefault = 100.0f;
	/**
	* Anchor point X coordinate offset default value.
	*/
	public static final float xAnchorDefault = 0.0f;
	/**
	* Anchor point Y coordinate offset default value.
	*/
	public static final float yAnchorDefault = 0.0f;
	/**
	* Rotation default value.
	*/
	public static final float rotationDefault = 0.0f;

	/**
	* Internally used flag for transform clones that are needed when parenting.
	*/
	protected boolean IS_TRANSFORM_CLONE = false;


	//-------------------------------------------------------- CONSTRUCTORS
	/**
	* Creates object with default values for all parameters.
	*/
	public AnimatedImageCoordinates( ImageOperation iop )
	{
		this.x = new AnimatedValue(iop, xDefault);
		this.y = new AnimatedValue(iop, yDefault);
		this.xScale = new AnimatedValue(iop, xScaleDefault);
		this.yScale = new AnimatedValue(iop, yScaleDefault);
		this.xAnchor = new AnimatedValue(iop, xAnchorDefault);
		this.yAnchor = new AnimatedValue(iop, yAnchorDefault);
		this.rotation = new AnimatedValue(iop, rotationDefault);
	}

	/**
	* Creates object with specified values.
	*/
	public AnimatedImageCoordinates(	ImageOperation iop,
						float Fx,
						float Fy,
						float FxScale,
						float FyScale,
						float FxAnchor,
						float FyAnchor,
						float Frotation )
	{
		this.x = new AnimatedValue(iop, Fx);
		this.y = new AnimatedValue(iop, Fy);
		this.xScale = new AnimatedValue(iop, FxScale);
		this.yScale = new AnimatedValue(iop, FyScale);
		this.xAnchor = new AnimatedValue(iop, FxAnchor);
		this.yAnchor = new AnimatedValue(iop, FyAnchor);
		this.rotation = new AnimatedValue(iop, Frotation);
	}
	/**
	* Retuns all parameters in Vector. Used internally when registering paramters.
	*/
	public Vector<AnimatedValue> getParamsVector()
	{
		Vector<AnimatedValue> retVec = new Vector<AnimatedValue> ();
		retVec.add( x );
		retVec.add( y );
		retVec.add( xScale );
		retVec.add( yScale );
		retVec.add( xAnchor );
		retVec.add( yAnchor );
		retVec.add( rotation );
		return retVec;
	}
	
	/**
	* Returns X scale in normalized form.
	*/
	public float getNormXScale( int frame ){ return xScale.getValue( frame ) / xScaleDefault; }
	/**
	* Returns Y scale in normalized form.
	*/
	public float getNormYScale( int frame ){ return yScale.getValue( frame ) / yScaleDefault; }
	/**
	* Returns position.
	*/
	public Point2D.Float getPos( int frame )
	{
		return new Point2D.Float( x.getValue( frame ), y.getValue( frame ) );
	}

	/**
	* Retunrs true if object is transform clone.
	*/
	public boolean isTransformClone(){ return IS_TRANSFORM_CLONE; }

	/**
	* Returns temporary object used to create movement parent functionality.
	*/
	public TransformClone getTransformClone( int frame )
	{	
		ImageOperation iop = x.getIOP();
		return new TransformClone( 	iop,
						x.getValue( frame ),
						y.getValue( frame ),
						xScale.getValue( frame ),
						yScale.getValue( frame ),
						xAnchor.getValue( frame ),
						yAnchor.getValue( frame ),
						rotation.getValue( frame ) );
	}

	/**
	* Sets all params to accept values outside clip area.
	*/
	protected void setAllValuesTypeFreeSet()
	{
		x.setFreeSet();
		y.setFreeSet();
		xScale.setFreeSet();
		yScale.setFreeSet();
		xAnchor.setFreeSet();
		yAnchor.setFreeSet();
		rotation.setFreeSet();
	}

}//end class
