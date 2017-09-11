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

//--- A Bezier curve.
public class Bez
{
	public static final int START_POINT = 0;
	public static final int CONTROL_POINT_1 = 1;
	public static final int CONTROL_POINT_2 = 2;
	public static final int END_POINT = 3;

	private Point2D.Float[] cp;

	//--- Coefficients
	private float ax, bx, cx;
	private float ay, by, cy;

	private float error = 0.0001f; //--- frame time error less 1 / 10000  of a frame

	private int iter = 0;

	//--- cp[0] is the starting point
	//--- cp[1] is the first control point
	//--- cp[2] is the second control point
	//--- cp[3] is the end point
	public Bez( Point2D.Float[] curvePoints )
	{
		cp = curvePoints;

		cx = 3.0f * (cp[1].x - cp[0].x);
		bx = 3.0f * (cp[2].x - cp[1].x) - cx;
		ax = cp[3].x - cp[0].x - cx - bx;
		
		cy = 3.0f * (cp[1].y - cp[0].y);
		by = 3.0f * (cp[2].y - cp[1].y) - cy;
		ay = cp[3].y - cp[0].y - cy - by;
	}

	public float get( float frame )
	{
		iter = 0;
		return valueForFrame( frame, 0, 1 );
	}

	//--- Recursively bisect until x value (frame time) error is below limit.
	private float valueForFrame( float frame, float lowT, float highT )
	{
		iter++;
		if( iter == 30 ) abort();//--- to stop infinite loops.

		float newT = ( lowT + highT ) / 2;
		Point2D.Float val = pointOnCubicBezier( newT );
		if( Math.abs( val.x - frame ) < error ) return val.y;// This is the solution.
		//--- Recurse more
		if( val.x > frame ) return valueForFrame( frame, lowT, newT );
		else return valueForFrame( frame, newT, highT );
	}
	
	//--- 0<=t<=1
	public Point2D.Float pointOnCubicBezier( float t )
	{
		Point2D.Float result = new Point2D.Float();

		float tSquared = t * t;
		float tCubed = tSquared * t;
	
		result.x = (ax * tCubed) + (bx * tSquared) + (cx * t) + cp[0].x;
		result.y = (ay * tCubed) + (by * tSquared) + (cy * t) + cp[0].y;
	
		return result;
	}

	public Point2D.Float[] bezPoints( int pointsCount )
	{
		Point2D.Float[] curve = new Point2D.Float[ pointsCount ];
		float step = 1.0f / ( pointsCount - 1 );	
		for( int i = 0; i < pointsCount; i++ )
			curve[i] = pointOnCubicBezier( i * step );
		return curve;
	}

	public void print()
	{
		System.out.println("cp0 x:" + cp[ 0 ].x + ", y: " + cp[ 0 ].y );
		System.out.println("cp1 x:" + cp[ 1 ].x + ", y: " + cp[ 1 ].y );
		System.out.println("cp2 x:" + cp[ 2 ].x + ", y: " + cp[ 2 ].y );
		System.out.println("cp3 x:" + cp[ 3 ].x + ", y: " + cp[ 3 ].y );
	}

	private void abort()
	{
		System.out.println( "Bez: infinete loop!!!!" );
		print();
		Throwable t = new Throwable();
		t.printStackTrace();
		System.exit( 1 );
	}

}//end class
