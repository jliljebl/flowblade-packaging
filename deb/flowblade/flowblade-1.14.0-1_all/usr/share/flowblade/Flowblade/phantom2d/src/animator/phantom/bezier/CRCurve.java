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

import java.awt.Point;
import java.util.Collections;
import java.util.Vector;

//--- A Catmull- Rom curve described as 256 wide look-up table with value range of 0 - 255
//--- Code in this class is modified from code in gimp-2.2.0/app/base/curves.c and .../curves.h, see original files for copyrights.
public class CRCurve
{
	public static final int CURVES_NUM_POINTS = 17;//this should be enough.
	private static final int X = 0;
	private static final int Y = 1;

	public static final float[][] CR_BASIS =
	{
		{ -0.5f,  1.5f, -1.5f,  0.5f },
		{  1.0f, -2.5f,  2.0f, -0.5f },
		{ -0.5f,  0.0f,  0.5f,  0.0f },
		{  0.0f,  1.0f,  0.0f,  0.0f },
	};
 
	private Vector<CurvePoint> points =  new Vector<CurvePoint>();
	private int[] curve = new int[256];

	public static int[] getLinerCurve()
	{
		int[] c = new int[256];
		for (int j = 0; j < 256; j++) c[j] = j;
		return c;
	}

	public CRCurve()
	{
		curveReset();
	}

	public int[] getCurve( boolean calculateFirst )
	{
		if( calculateFirst ) calculateCurve();
		return curve;
	}

	public int[] getCurveCopy( boolean calculateFirst )
	{
		if( calculateFirst ) calculateCurve();
		int[] copy = new int[ 256 ];
		for (int j = 0; j < 256; j++) copy[j] = curve[j];
		return copy;
	}

	public void curveReset()
	{
		//--- Write linear gradient map curve.
		for (int j = 0; j < 256; j++) curve[j] = j;
		//--- Clear points.
		points =  new Vector<CurvePoint>();
		//--- Create default end points.
		setCurvePoint( 0, 0 );
		setCurvePoint( 255, 255 );
	}

	public void clear()
	{
		points =  new Vector<CurvePoint>();
		curve = new int[256];
	}

	public void setCurvePoint( Point p  )
	{
		setCurvePoint( p.x, p.y );
	}

	public void addCurvePointOnLoad( CurvePoint p )
	{
		setCurvePoint( p.x, p.y );
	}

	public void setCurvePoint( int x, int y )
	{
		if( points.size() + 1 > CURVES_NUM_POINTS ) return;

		Vector<CurvePoint> killVec = new Vector<CurvePoint>(); 
		for( int i = 0; i <  points.size(); i++ )
			if( points.elementAt( i ).x == x ) killVec.add( points.elementAt( i ) );
		points.removeAll( killVec );

		points.add( new CurvePoint( x, y ) );
		Collections.sort( points );
	}
	public void removeRange( Point p1, Point p2 )
	{
		removeRange( p1.x, p2.x );
	}
	public void removeRange( int p1, int p2 )
	{
		if( p1 > p2 )
		{
			int temp = p1;
			p1 = p2;
			p2 = temp;
		}
		for( int i = p1; i <= p2; i++ )
		{
			removeCurvePoint( new Point( i, -1 ) );
		}
	}
	public void removeCurvePoint( Point p )
	{
		if( points.size() < 2 ) return;
		Vector<CurvePoint> killVec = new Vector<CurvePoint>();
		for( int i = 0; i <  points.size(); i++ )
			if( points.elementAt( i ).x == p.x ) killVec.add( points.elementAt( i ) );
		points.removeAll( killVec );
	}
	public Vector<CurvePoint> getCurvePoints(){ return points; }

	public void calculateCurve()
	{
		//---  Initialize boundary curve points
		if( points.size() != 0 )
		{
			for ( int i = 0; i < points.elementAt( 0 ).x; i++)
				curve[ i ] = points.elementAt( 0 ).y;

			for ( int i = points.lastElement().x; i < 256; i++)
				curve[ i ] = points.lastElement().y;
		}

		//--- Plot curves
		CurvePoint p1, p2, p3, p4;//--- curve points.
		for( int i = 0; i < points.size() - 1; i++)
		{
			if( i == 0 ) p1 = points.elementAt( i );
			else p1 = points.elementAt( i - 1 );
			p2 = points.elementAt(  i );
			p3 = points.elementAt(  i + 1 );
			if( i == ( points.size() - 2 ) ) p4 =  points.elementAt( points.size() - 1 );
			else p4 = points.elementAt( i + 2 );
		
			curvesPlotCurve( p1, p2, p3, p4);
		}
		
		//--- ensure that the control points are used exactly.
		int x, y;
		for( int i = 0; i < points.size(); i++)
		{
			x = points.elementAt( i ).x;
			y = points.elementAt( i ).y;
			curve[x] = y;
		}
	}

	private void curvesPlotCurve ( 	CurvePoint    p1,
					CurvePoint    p2,
					CurvePoint    p3,
					CurvePoint    p4 )
	{
		float[][] geometry = new float[4][4];
		float[][] tmp1 = new float[4][4];
		float[][] tmp2 = new float[4][4];
		float[][] deltas = new float[4][4];
		float  x, dx, dx2, dx3;
		float  y, dy, dy2, dy3;
		float  d, d2, d3;
		int lastx, lasty;
		int newx, newy;

		int N = 1000;//calculate curve values

		//--- construct the geometry matrix from the segment
		for( int i = 0; i < 4; i++)
		{
			geometry[i][2] = 0;
			geometry[i][3] = 0;
		}
		//--- Get points X and Y
		geometry[0][X] = (float) p1.x;
		geometry[1][X] = (float) p2.x;
		geometry[2][X] = (float) p3.x;
		geometry[3][X] = (float) p4.x;

		geometry[0][Y] = (float) p1.y;
		geometry[1][Y] = (float) p2.y;
		geometry[2][Y] = (float) p3.y;
		geometry[3][Y] = (float) p4.y;

		//--- subdivide the curve N times (N = 1000 )
		//--- N can be adjusted to give a finer or coarser curve
		d = 1.0f / N;
		d2 = d * d;
		d3 = d * d * d;
		
		//--- construct a temporary matrix for determining the forward differencing deltas
		tmp2[0][0] = 0;     	tmp2[0][1] = 0;     	tmp2[0][2] = 0;    tmp2[0][3] = 1;
		tmp2[1][0] = d3;  	tmp2[1][1] = d2;   	tmp2[1][2] = d;    tmp2[1][3] = 0;
		tmp2[2][0] = 6.0f*d3; 	tmp2[2][1] = 2.0f*d2;	tmp2[2][2] = 0;    tmp2[2][3] = 0;
		tmp2[3][0] = 6.0f*d3; 	tmp2[3][1] = 0;     	tmp2[3][2] = 0;    tmp2[3][3] = 0;
		
		//--- compose the basis and geometry matrices
		curvesCRCompose( CR_BASIS, geometry, tmp1 );
		
		//--- compose the above results to get the deltas matrix
		curvesCRCompose( tmp2, tmp1, deltas );
		
		//--- extract the x deltas
		x = deltas[0][0];
		dx = deltas[1][0];
		dx2 = deltas[2][0];
		dx3 = deltas[3][0];
		
		//--- extract the y deltas
		y = deltas[0][1];
		dy = deltas[1][1];
		dy2 = deltas[2][1];
		dy3 = deltas[3][1];
		
		lastx = clamp( Math.round( x ) );
		lasty = clamp( Math.round( y ) );
		
		curve[ lastx ] = lasty;
		
		//--- Loop over the curve and build loopUpTable
		for( int i = 0; i < N; i++)
		{
			//--- increment the x values
			x += dx;
			dx += dx2;
			dx2 += dx3;
			
			//--- increment the y values
			y += dy;
			dy += dy2;
			dy2 += dy3;
			
			newx = clamp( Math.round( x ));
			newy = clamp( Math.round( y ));
			
			//--- if this point is different than the last one...then draw it
			if (( lastx != newx ) || ( lasty != newy))
				curve[ newx ] = newy;
			
			lastx = newx;
			lasty = newy;
		}
	}

	private int clamp( int val )
	{
		if( val > 255 ) val = 255;
		else if( val < 0 ) val = 0;
		
		return val;
	}

	//--- Fills ab using a and b 
	private void curvesCRCompose ( float[][] a, float[][] b, float[][] ab )
	{
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				ab[i][j] = (a[i][0] * b[0][j] +
					a[i][1] * b[1][j] +
					a[i][2] * b[2][j] +
					a[i][3] * b[3][j]);
			}
		}
	}

}//end class
