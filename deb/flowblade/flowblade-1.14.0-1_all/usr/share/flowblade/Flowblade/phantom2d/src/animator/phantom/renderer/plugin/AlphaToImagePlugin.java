package animator.phantom.renderer.plugin;

import giotto2D.filters.merge.AlphaToImage;

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.NoParamsPanel;
import animator.phantom.plugin.PhantomPlugin;

public class AlphaToImagePlugin extends PhantomPlugin
{
	public AlphaToImagePlugin()
	{
		initPlugin( FILTER, SINGLE_INPUT );
	}

	public void buildDataModel()
	{
		setName( "AlphaToImage" );
	}

	public void buildEditPanel()
	{
		addEditor( new NoParamsPanel("AlphaToImage") );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage renderedImage = getFlowImage();
		AlphaToImage.filter( renderedImage, renderedImage );
		sendFilteredImage( renderedImage, frame );
	}

}//end class
