package animator.phantom.renderer.plugin;

import giotto2D.filters.merge.ImageToAlpha;

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.IntegerParam;

public class ImageToAlphaPlugin extends PhantomPlugin
{
	public IntegerParam operationType = new IntegerParam( 1 );
	
	public String[] opTypeNames = { "use red channel",
					"use green channel",
					"use blue channel",
					"desaturate first", };

	public ImageToAlphaPlugin()
	{
		initPlugin( FILTER, SINGLE_INPUT );
	}

	public void buildDataModel()
	{
		setName( "ImageToAlpha" );
		registerParameter( operationType );
	}

	public void buildEditPanel()
	{
		IntegerComboBox opSelect = new IntegerComboBox( operationType,
								"Select operation type",
								opTypeNames );
		addEditor( opSelect );
	}

	public void doImageRendering( int frame )
	{
		int channel = operationType.get();
		boolean desaturateFirst = false;
		if( channel == 3 ) desaturateFirst = true;
		BufferedImage img = getFlowImage();
		ImageToAlpha.filter( img, img, channel, desaturateFirst );

		sendFilteredImage( img, frame );
	}

}//end class
