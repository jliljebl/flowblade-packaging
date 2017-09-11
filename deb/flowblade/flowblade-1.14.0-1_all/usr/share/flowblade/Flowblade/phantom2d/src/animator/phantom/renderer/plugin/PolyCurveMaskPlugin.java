package animator.phantom.renderer.plugin;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import animator.phantom.gui.view.editlayer.PolyCurveEditLayer;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;

public class PolyCurveMaskPlugin extends PolyCurvePlugin
{

	public PolyCurveMaskPlugin()
	{
		initPlugin( MASK );
	}

	public void buildDataModel()
	{
		setName( "PolyCurveMask" );
		
		registerPathParams();
	}

	public void buildEditPanel()
	{
		//--- no editors that are not autocreated for plugin type MASK
	}

	public void renderMask( float frame, Graphics2D maskGraphics, int width, int height )
	{
		if( closed.get() == false ) return;

		maskGraphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		maskGraphics.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );

		GeneralPath mask = getShape( frame );
		maskGraphics.setColor( Color.white );
		maskGraphics.fill( mask );
	}

	public ViewEditorLayer getEditorLayer()
	{
 		return new PolyCurveEditLayer( this );
	}

}//end class
