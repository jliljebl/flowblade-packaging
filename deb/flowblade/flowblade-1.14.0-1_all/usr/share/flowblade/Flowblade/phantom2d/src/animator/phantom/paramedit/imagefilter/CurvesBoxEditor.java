package animator.phantom.paramedit.imagefilter;

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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Vector;

import animator.phantom.bezier.CRCurve;
import animator.phantom.bezier.CurvePoint;
import animator.phantom.paramedit.BoxEditor;
import animator.phantom.paramedit.BoxEditorListener;

/**
* A GUI editor component to edit internal CRCurve object.
*
* <p> 
* Use <code>SingleCurveEditor</code> instead to edit  <code>CRCurve</code> parameter instead.
*/
public class CurvesBoxEditor extends BoxEditor
{
	private Color CURVE_COLOR = Color.black;
	private static final int POINT_SIZE = 8;
	
	private CRCurve curve = null;

	public CurvesBoxEditor( CRCurve curve, int pixSize, int valueSize, BoxEditorListener listener )
	{
		super( pixSize, valueSize, listener );
		this.curve = curve;
	}

	public void setCurve( CRCurve curve_, Color curveColor )
	{
		curve = curve_;
		CURVE_COLOR = curveColor;
	}

	public void paint( Graphics g )
	{
		paintBG(  g );

		//--- Draw curves
		g.setColor( CURVE_COLOR );
		int[] cp = curve.getCurve( true );//we get 256 values
		Point p1 = new Point();
		Point p2 = new Point();
		for( int i = 0; i < cp.length - 1; i++ )
		{
			getBoxPanelPoint( p1, i, cp[ i ] );
			getBoxPanelPoint( p2, i + 1, cp[ i + 1 ] );
			g.drawLine( p1.x, p1.y, p2.x, p2.y ); //255 hardcoded
		}

		//--- Draw edit points
		Vector<CurvePoint> points = curve.getCurvePoints();
		for( CurvePoint p : points )
		{
			getBoxPanelPoint( p1, p.x, p.y );
			g.fillOval(  p1.x - POINT_SIZE / 2, p1.y - POINT_SIZE / 2, POINT_SIZE, POINT_SIZE );
		}
	}

}//end class