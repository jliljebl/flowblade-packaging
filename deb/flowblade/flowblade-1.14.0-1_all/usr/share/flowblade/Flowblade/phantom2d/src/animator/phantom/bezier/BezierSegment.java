package animator.phantom.bezier;

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

import animator.phantom.renderer.param.AnimationKeyFrame;


/*
A Bezier curve between two AnimationKeyFrame objects.

All Bezier values are expressed using following kind of bezier curve. 
Because x == time and this is used only for animated parameters.


   c2     EKF        ( c2.y = endKF.y, c2.x >= startKF.x )
   X----***X
       *
      *
     *
    *
   * 
X**------X         ( c1.y = startKF.y, c1.x <= endKF.x )
SKF     c1

*/
public class BezierSegment
{
	protected AnimationKeyFrame startKeyFrame;
	protected AnimationKeyFrame endKeyFrame;

	private Bez bezCurve;

	public BezierSegment( AnimationKeyFrame startKF, AnimationKeyFrame endKF )
	{
		startKeyFrame = startKF;
		endKeyFrame = endKF;

		Point2D.Float[] points = new Point2D.Float[ 4 ];
		points[ Bez.START_POINT ] = startFloatPoint();
		points[ Bez.CONTROL_POINT_1 ] = cp1();
		points[ Bez.CONTROL_POINT_2 ] = cp2();
		points[ Bez.END_POINT ] = endFloatPoint();

		bezCurve = new Bez( points );
	}

	//--- Return value in frame. Range checks must be done elsewhere.
	public float getSegmentValue( float frame )
	{
		return bezCurve.get( frame );
	}

	//--- Returns point for drawing bezier line.
	public Point2D.Float[] bezPoints( int numberOfPoints )
	{
		return bezCurve.bezPoints( numberOfPoints );
	}
	//--- Returns start, end and control points.
	private Point2D.Float startFloatPoint()
	{
		return new Point2D.Float((float)startKeyFrame.getFrame(), startKeyFrame.getValue() );
	}
	private Point2D.Float endFloatPoint()
	{
		return new Point2D.Float((float)endKeyFrame.getFrame(), endKeyFrame.getValue() );
	}
	private Point2D.Float cp1()
	{
		return new Point2D.Float( (float)startKeyFrame.getFrame() +
						timeDelta() * startKeyFrame.getTrailingTension(),
						startKeyFrame.getValue()
					);
	}
	private Point2D.Float cp2()
	{
		return new Point2D.Float( (float)endKeyFrame.getFrame() -
						timeDelta() * endKeyFrame.getLeadingTension(),
						endKeyFrame.getValue()
					);
	}
	private float timeDelta(){ return (float) ( endKeyFrame.getFrame() - startKeyFrame.getFrame() ); }

}//end class