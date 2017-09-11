package animator.phantom.renderer.plugin;

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

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import animator.phantom.gui.view.SLine;
import animator.phantom.gui.view.SVec;
import animator.phantom.paramedit.AnimValueSliderEditor;
import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.paramedit.FloatNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.BooleanParam;
import animator.phantom.renderer.param.FloatParam;

public class SplitScreenPlugin extends PhantomPlugin
{
	private AnimatedValue amount;
	private FloatParam direction;
	private BooleanParam invert;

	public SplitScreenPlugin()
	{
		initPlugin( MERGE, MERGE_INPUTS );
	}

	public void buildDataModel()
	{
		setName( "SplitScreen" );

		amount = new AnimatedValue( 50, 0, 100 );
		direction = new FloatParam( 0 );
		invert = new BooleanParam(); 

		registerParameter( amount );
		registerParameter( direction );
		registerParameter( invert );
	}

	public void buildEditPanel()
	{
		AnimValueSliderEditor amountEdit = new AnimValueSliderEditor( "Amount", amount );
		FloatNumberEditor dirEdit = new FloatNumberEditor( "Angle", direction );
		CheckBoxEditor invertEdit = new CheckBoxEditor( invert, "Invert", true );

		addEditor( amountEdit );
		addRowSeparator();
		addEditor( dirEdit );
		addRowSeparator();
		addEditor( invertEdit );
	}

	public void renderMask( float frame, Graphics2D maskGraphics, int canvasWidth, int canvasHeight )
	{
		//--- Helper objects
		Point2D.Float topLeft = new Point2D.Float( 0, 0 );
		Point2D.Float topRight = new Point2D.Float( canvasWidth, 0 );
		Point2D.Float bottomLeft = new Point2D.Float( 0, canvasHeight );
		Point2D.Float bottomRight = new Point2D.Float( canvasWidth, canvasHeight );

		SLine diagonalDown = new SLine( topLeft, bottomRight );
		SLine diagonalUp = new SLine( bottomLeft, topRight );

		//--- get path line
		float angle = direction.get();
		Point2D.Float mid = new Point2D.Float(canvasWidth / 2.0f, canvasHeight / 2.0f );
		Point2D.Float rotS = new Point2D.Float(canvasWidth / 2.0f, 0 );
		Point2D.Float rotE = GeometricFunctions.rotatePointAroundPoint( angle, rotS, mid );
		SLine path = new SLine( mid, rotE ); 
		
		//--- Get path vector
		Point2D.Float start;
		Point2D.Float end;
		if( path.equals( diagonalDown ) ) 
		{
			if( angle > 269 )
			{
				start = topLeft;
				end = bottomRight;
			}
			else 
			{
				end = topLeft;
				start = bottomRight;
			}
		}
		else if( path.equals( diagonalUp ) )
		{
			if( angle > 179 )
			{
				start = bottomLeft;
				end = topRight;
			}
			else 
			{
				end = bottomLeft;
				start = topRight;
			}
		}
		else if( angle >= 0 && angle < 90 )
		{
			start = path.getNormalProjectionPoint( topRight );
			end = path.getNormalProjectionPoint( bottomLeft );
		}
		else if (angle >= 90 && angle < 180 )
		{
			start = path.getNormalProjectionPoint( bottomRight );
			end = path.getNormalProjectionPoint( topLeft );
		}
		else if (angle >= 180 && angle < 270 )
		{
			start = path.getNormalProjectionPoint( bottomLeft );
			end = path.getNormalProjectionPoint( topRight );
		}
		else
		{
			start = path.getNormalProjectionPoint( topLeft );
			end = path.getNormalProjectionPoint( bottomRight );
		}

		Point2D.Float draw1;
		Point2D.Float draw2;
		if( amount.getValue( frame ) != 0 )
		{
			SVec pathVec = new SVec( start, end );
			SVec posVec = pathVec.getMultipliedSVec( (amount.getValue( frame ) /*+ 0.001f*/) / 100.0f );
			SVec posVec2 = posVec.getMultipliedSVec( 1.001f );//to get gradient

			draw1 = posVec.getEndPos();
			draw2 = posVec2.getEndPos();
		}
		else // hack fix, 0 amount did some interesting stuff
		{
			draw1 = new Point2D.Float(0.0f, -1.0f );
			draw2 =  new Point2D.Float(0.0f, -0.5f );
		}

		Color c1 = Color.white;
		Color c2 = Color.black;

		if( invert.get() )
		{
			c1 = Color.black;
			c2 = Color.white;
		}

		maskGraphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		maskGraphics.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
	
		//--- Create gradient paint
		GradientPaint gradient = new GradientPaint(	draw1.x,
								draw1.y,
								c1,
								draw2.x,
								draw2.y,
								c2,
								false );
		//--- Draw gradient and dispose.
		maskGraphics.setPaint( gradient );
		maskGraphics.fill( new Rectangle2D.Float( 0, 0, canvasWidth, canvasHeight ) );
	}

}//end class
