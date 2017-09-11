package animator.phantom.renderer.plugin;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import animator.phantom.gui.view.editlayer.PolyLineEditLayer;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;

public class PolyLineMaskPlugin extends PolyLinePlugin
{

	public PolyLineMaskPlugin()
	{
		initPlugin( MASK );
	}

	public void buildDataModel()
	{
		setName( "PolyLineMask" );
		
		registerPathParams();
	}

	public void buildEditPanel()
	{
		
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
 		return new PolyLineEditLayer( this );
	}

}//end class
