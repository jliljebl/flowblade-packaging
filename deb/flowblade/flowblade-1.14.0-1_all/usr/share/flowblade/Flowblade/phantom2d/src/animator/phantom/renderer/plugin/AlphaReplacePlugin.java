package animator.phantom.renderer.plugin;

import giotto2D.filters.merge.AlphaReplace;

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.NoParamsPanel;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;

public class AlphaReplacePlugin extends PhantomPlugin
{

	public AlphaReplacePlugin()
	{
		initPlugin( FILTER, MERGE_INPUTS );
	}

	public void buildDataModel()
	{
		setName( "AlphaReplace" );
	}

	public void buildEditPanel()
	{
		addEditor( new NoParamsPanel("AlphaReplace") );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage mergeImage = getMergeImage();
		BufferedImage renderedImage = getFlowImage();

		if( renderedImage == null ) 
			renderedImage = PluginUtils.createScreenCanvas();
		if( mergeImage == null ) return;

		//--- MergeImage must be made same size as blend destination ( = renderedImage ) 
		if( mergeImage.getWidth() != renderedImage.getWidth() 
				|| mergeImage.getHeight() != renderedImage.getHeight() )
		{
			mergeImage = PluginUtils.getAlphaCopy(renderedImage.getWidth(), renderedImage.getHeight(), mergeImage );
		}

		AlphaReplace.filter( renderedImage, mergeImage );

		sendFilteredImage( renderedImage, frame );
	}

}//end class
