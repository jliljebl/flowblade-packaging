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

import java.awt.geom.Point2D;

import animator.phantom.controller.TimeLineController;
import animator.phantom.gui.view.EditPointShape;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.param.AnimatedImageCoordinates;
import animator.phantom.renderer.parent.AbstractParentMover;
import animator.phantom.renderer.parent.NoActionParentMover;
import animator.phantom.renderer.parent.TransformClone;

/**
* A shape made of editpoints that has its transformation controlled by
* an <code>AnimatedImageCoordinates</code> object. Has extending classes in different shapes.
* This class handles transformation between real(child coordinate value) space and parent
* transformed coordinate space.
*/
public abstract class AnimCoordsEditShape extends EditPointShape
{
	/**
	* <code>ImageOperation</code> that has its <code>AnimatedImageCoordinates</code> object's values transformed.
	*/
	protected ImageOperation iop;

	/**
	* Captures values of <code>AnimatedImageCoordinates</code> object before mouse move.
	*/
	public TransformClone moveStartValues = null;

	/**
	* Sets edit points into untransformed positions.
	*/
	public abstract void resetEditPoints();

	/**
	* In given frame transforms shape by values from method <code>animCoords()</code> which 
	* may give parent transformed  <code>AnimatedImageCoordinates</code> object.
	*/
	public void transformShape( int frame )
	{
		resetEditPoints();
		//--- Movement
		float x = animCoords().x.getValue( frame );
		float y = animCoords().y.getValue( frame );
		//--- Unmoved anchor offset from origo
		float xOff = animCoords().xAnchor.getValue( frame );
		float yOff = animCoords().yAnchor.getValue( frame );
		//--- Set anchor place
		ANCHOR_POINT.setPos( x, y );
		//--- Set editpoints places
		translateEditPoints( x - xOff, y - yOff );
		//--- Scale and rotate
		scale( animCoords().getNormXScale( frame ), animCoords().getNormYScale( frame ) );
		rotate( animCoords().rotation.getValue( frame ) );
	}

	/**
	* Saves <code>AnimatedImageCoordinates</code> object move start values for mouse move delta calculations.
	*/
	public void saveMoveStartValues( int frame )
	{
		moveStartValues = animCoords().getTransformClone( frame );
	}

	/**
	* Returns position of anchor point.
	*/
	public Point2D.Float getPosition( int frame )
	{
		return animCoords().getPos( frame );
	}

	/**
	* Returns <code>AnimatedImageCoordinates</code> object from edit target <code>ImageOperation</code> 
	* or a parent transformed <code>TransformClone</code> from its parent <code>ImageOperation</code>
	* if edit target <code>ImageOperation</code> has parent.
	*/
	public AnimatedImageCoordinates animCoords()
	{
		if( iop.parentMoverType == -1 ) return iop.getCoords();

		return parentMover().doTransform( iop, TimeLineController.getCurrentFrame() );
	}

	//------------------------------------------------ CONVERSION
	/**
	* Transforms point from parent transformed space to real space. If edit target <code>ImageOperation</code> has parent
	* its displayed edit shape is in parent transformed space 
	* but values changed by edit shape need to be in untransformed child space(real space).
	*/
	public Point2D.Float getChildPoint( Point2D.Float p )
	{
		return new Point2D.Float( getChildX( p.x, p.y, TimeLineController.getCurrentFrame() ),
						getChildY( p.y, p.x, TimeLineController.getCurrentFrame() )  );
	}
	/**
	* Translates distance between two points from parent transformed space to real space. If edit target <code>ImageOperation</code> has parent
	* its displayed edit shape is in parent transformed space 
	* but values changed by edit shape need to be in untransformed child space(real space).
	*/
	public float getChildLength( Point2D.Float p1, Point2D.Float p2 )
	{
		Point2D.Float pc1 = new Point2D.Float( getChildX( p1.x, p1.y, TimeLineController.getCurrentFrame() ),
						getChildY( p1.y, p1.x, TimeLineController.getCurrentFrame() )  );
		Point2D.Float pc2 = new Point2D.Float( getChildX( p2.x, p2.y, TimeLineController.getCurrentFrame() ),
						getChildY( p2.y, p2.x, TimeLineController.getCurrentFrame() )  );
		return GeometricFunctions.distance( pc1, pc2 );
	}
	/**
	* Transforms X position from parent transformed space to real space.
	* @param transval X position in parent transformed space.
	* @param yval Y position in parent transformed space.
	* @param frame Current frame. 
	*/
	public float getChildX( float transval, float yval, int frame ){ return parentMover().getChildX( transval, yval, frame ); }
	/**
	* Transforms Y position from parent transformed space to real space.
	* @param transval Y position in parent transformed space.
	* @param xval Y position in parent transformed space.
	* @param frame Current frame. 
	*/
	public float getChildY( float transval, float xval, int frame ){ return parentMover().getChildY( transval, xval, frame ); }
	/**
	* Transforms X scale from parent transformed space to real space.
	* @param transval X scale in parent transformed space.
	* @param frame Current frame. 
	*/
	public float getChildXScale( float transval, int frame ){ return parentMover().getChildXScale( transval, frame ); }
	/**
	* Transforms Y scale from parent transformed space to real space.
	* @param transval Y scale in parent transformed space.
	* @param frame Current frame. 
	*/
	public float getChildYScale( float transval, int frame ){ return parentMover().getChildYScale( transval, frame ); }
	/**
	* Transforms anchor X from parent transformed space to real space.
	* @param transval Anchor X in parent transformed space.
	* @param frame Current frame. 
	*/
	public float getChildAnchorX( float transval, int frame ){ return parentMover().getChildAnchorX( transval, frame ); }
	/**
	* Transforms anchor Y from parent transformed space to real space.
	* @param transval Anchor Y in parent transformed space.
	* @param frame Current frame. 
	*/
	public float getChildAnchorY( float transval, int frame ){ return parentMover().getChildAnchorY( transval, frame ); }
	/**
	* Transforms rotation from parent transformed space to real space.
	* @param transval Rrotation in parent transformed space.
	* @param frame Current frame. 
	*/
	public float getChildRotation( float transval, int frame ){ return parentMover().getChildRotation( transval, frame ); }
	
	//------------------------------------------------ PARENT MOVER
	/**
	* Returns a parent mover object used to transform coordinates between real(child coordinate value) and parent transformed spaces. If no 
	* parent exists, returns a no action parent mover that returns untransformed values both ways.
	*/
	public AbstractParentMover parentMover()
	{
		if( iop.parentMoverType == -1 ) 
			return new NoActionParentMover();

		AbstractParentMover parentMover = AbstractParentMover.getMover( iop.parentMoverType );
		parentMover.setParent( iop.parentIOP );
		return parentMover;
	}

}//end class
