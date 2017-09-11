package animator.phantom.renderer.plugin;

import giotto2D.filters.color.Desaturate;

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.NoParamsPanel;
import animator.phantom.plugin.PhantomPlugin;

public class DesaturatePlugin extends PhantomPlugin
{
	public DesaturatePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Desaturate"  );
	}

	public void buildEditPanel()
	{
		addEditor( new NoParamsPanel("Desaturate") );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage img = getFlowImage();
		Desaturate.filter( img );
		sendFilteredImage( img, frame);
	}

}//end class
