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

import giotto2D.core.GTTObject;
import giotto2D.core.GTTOval;
import giotto2D.core.GTTRectangle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.plugin.AbstractPluginEditLayer;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedImageCoordinates;
import animator.phantom.renderer.param.IntegerParam;
import animator.phantom.renderer.plugin.editlayer.ShapeMaskPluginEditLayer;

public class ShapeMaskPlugin extends PhantomPlugin
{
	public IntegerParam shapeType = new IntegerParam( 0 );

	//--- Unscaled width of shape, not user settable.
	public static final int WIDTH = 200;
	public static final int HEIGHT = 200;

	public ShapeMaskPlugin()
	{
		initPlugin( MASK );
	}

	public void buildDataModel()
	{
		setName( "ShapeMask" );
		registerParameter( shapeType );
 		registerCoords();
	}

	public void buildEditPanel()
	{
		String[] options = { "Rectangle","Oval" };
		IntegerComboBox shapeSelect = new IntegerComboBox( shapeType,
									"Mask shape",
									 options );
		addCoordsEditors();
		addEditor( shapeSelect );
	}

	public void renderMask( float frame, Graphics2D maskGraphics, int canvasWidth, int canvasHeight )
	{
		AnimatedImageCoordinates coords = getCoords();

		float x = coords.x.getValue( frame );
		float y = coords.y.getValue( frame );
		float xScale = coords.xScale.getValue( frame ) / AnimatedImageCoordinates.xScaleDefault;// normalize
		float yScale = coords.yScale.getValue( frame ) / AnimatedImageCoordinates.yScaleDefault;// normalize
		float xAnch = coords.xAnchor.getValue( frame );
		float yAnch = coords.yAnchor.getValue( frame );
		float rotation = coords.rotation.getValue( frame );

		GTTObject mask;
		if( shapeType.get() == 0 ) 
			mask = new GTTRectangle( WIDTH, HEIGHT );
		else 
			mask = new GTTOval( WIDTH, HEIGHT );

		mask.setScale( xScale, yScale );
		mask.setRotation( rotation );
		mask.setPos( x, y );
		mask.setAnchorPoint( xAnch, yAnch );
		mask.setFillPaint( Color.white );
		mask.setApplyCompositeToContext( false );

		mask.draw( maskGraphics, new Rectangle( 0, 0, canvasWidth,  canvasHeight ));
	}
/*
	public ViewEditorLayer getEditorLayer()
	{
 		return new ShapeMaskEditLayer( this );
	}
*/
	public AbstractPluginEditLayer getPluginEditLayer()
	{
		return new ShapeMaskPluginEditLayer( this );
	}

}//end class