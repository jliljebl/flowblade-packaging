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

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import animator.phantom.gui.view.editlayer.GradientEditLayerP;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.paramedit.CheckBoxEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.BooleanParam;

public class GradientMaskPlugin extends PhantomPlugin
{
	//--- x, and y of gradient start point.
	public AnimatedValue x1;
	public AnimatedValue y1;
	//--- x, and y of gradient end point.
	public AnimatedValue x2;
	public AnimatedValue y2;

 	public BooleanParam cyclic;

	public GradientMaskPlugin()
	{
		initPlugin( MASK );
	}

	public void buildDataModel()
	{
		setName( "GradientMask" );

		//--- NOTE: THESE ARE INITLIZED HERE BECAUSE PARAMETER DEFAULT
		//--- VALUES DEPEND ON SCREEN SIZE WHITCH IS NOT KNOW UNTIL
		//--- INSTANTIATION TIME
		x1 = new  AnimatedValue( PluginUtils.getScreenSize().width / 2 );
		y1 = new  AnimatedValue( 0 );
		x2 = new  AnimatedValue( PluginUtils.getScreenSize().width / 2 );
		y2 = new  AnimatedValue( PluginUtils.getScreenSize().height );

		cyclic = new BooleanParam( false );
	
		registerParameter( x1 );
		registerParameter( y1 );
		registerParameter( x2 );
		registerParameter( y2 );
		registerParameter( cyclic );
	}

	public void buildEditPanel()
	{

		AnimValueNumberEditor xEdit1 = new AnimValueNumberEditor( "P1 X", x1 );
		AnimValueNumberEditor yEdit1 = new AnimValueNumberEditor( "P1 Y", y1 );
		AnimValueNumberEditor xEdit2 = new AnimValueNumberEditor( "P2 X", x2 );
		AnimValueNumberEditor yEdit2 = new AnimValueNumberEditor( "P2 Y", y2 );

		CheckBoxEditor cyclicBox = new CheckBoxEditor( cyclic, "Cyclic", true );

		addEditor( xEdit1 );
		addRowSeparator();
		addEditor( yEdit1 );
		addRowSeparator();
		addEditor( xEdit2 );
		addRowSeparator();
		addEditor( yEdit2 );
		addRowSeparator();
		addEditor( cyclicBox );
	}

	public void renderMask( float frame, Graphics2D gc, int width, int height )
	{
		gc.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		gc.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
	
		//--- Create gradient paint
		GradientPaint gradient = new GradientPaint(	x1.getValue( frame ),
								y1.getValue( frame ),
								Color.white,
								x2.getValue( frame ),
								y2.getValue( frame ),
								Color.black,
								cyclic.get());
		//--- Draw gradient and dispose.
		gc.setPaint( gradient );
		gc.fill( new Rectangle2D.Float( 0, 0, width, height ) );
	}

	public ViewEditorLayer getEditorLayer()
	{
		return new GradientEditLayerP( this );
	}

}//end class