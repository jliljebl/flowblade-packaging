package animator.phantom.renderer.plugin;

import giotto2D.filters.merge.AlphaDifference;
import giotto2D.filters.merge.AlphaExclusion;
import giotto2D.filters.merge.AlphaIntersection;
import giotto2D.filters.merge.AlphaPaint;
import giotto2D.filters.merge.AlphaReplace;
import giotto2D.filters.merge.AlphaUnion;
import giotto2D.filters.merge.ImageToAlpha;

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AlphaActionSelect;
import animator.phantom.paramedit.IntegerComboBox;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.IntegerParam;

public class ImageToAlphaMergePlugin extends PhantomPlugin
{
	//--- Alpha action types
	public static final int ALPHA_DESTINATION = 0;
	public static final int ALPHA_SOURCE = 1;
	public static final int ALPHA_OPAQUE = 2;
	public static final int ALPHA_UNION = 3;
	public static final int ALPHA_INTERSECTION = 4;
	public static final int ALPHA_EXCLUSION = 5;
	public static final int ALPHA_DIFFERENCE = 6;

	private IntegerParam alphaAction = new IntegerParam( ALPHA_SOURCE );
	public IntegerParam desatType = new IntegerParam( 1 );
	
	public String[] opTypeNames = { "use red channel",
					"use green channel",
					"use blue channel",
					"desaturate first", };

	public ImageToAlphaMergePlugin()
	{
		initPlugin( FILTER, MERGE_INPUTS );
	}

	public void buildDataModel()
	{
		setName( "ImageToAlphaMerge" );
		registerParameter( alphaAction );
		registerParameter( desatType );
	}

	public void buildEditPanel()
	{
		AlphaActionSelect actionSelect = new AlphaActionSelect( alphaAction );
		IntegerComboBox opSelect = new IntegerComboBox( desatType,
								"Select operation type",
								opTypeNames );
		addEditor( opSelect );
		addRowSeparator();
		addEditor( actionSelect );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage flowImage = getFlowImage();	
		BufferedImage mergeImage = getMergeImage();

		//--- Check input
		if( flowImage == null &&  mergeImage == null )
		{
			return;
		}
		if( flowImage == null &&  mergeImage != null )
		{
			flowImage = mergeImage;
			return;
		}
		if( flowImage != null && mergeImage == null )
		{
			return;
		}

		int channel = desatType.get();
		boolean desaturateFirst = false;
		if( channel == 3 ) 
			desaturateFirst = true;
		ImageToAlpha.filter( mergeImage, mergeImage, channel, desaturateFirst );

		//--- MergeImage must be made same size as blend destination ( = renderedImage ) 
		if( mergeImage.getWidth() != flowImage.getWidth() 
				|| mergeImage.getHeight() != flowImage.getHeight() )
		{
			mergeImage = PluginUtils.getAlphaCopy(flowImage.getWidth(), flowImage.getHeight(), mergeImage );
		}

		//--- Do user requested alpha action
		doAlphaAction( flowImage, mergeImage, alphaAction.get() );

		sendFilteredImage( flowImage, frame );
	}

	private void doAlphaAction( BufferedImage dest, BufferedImage source, int mode )
	{
		switch( mode )
		{
			case ALPHA_DESTINATION:
				return;
			case ALPHA_SOURCE:
				AlphaReplace.filter( dest, source );
				break;
			case ALPHA_OPAQUE:
				AlphaPaint.filter( dest, 255 );
				break;
			case ALPHA_UNION:
				AlphaUnion.filter( dest, source );
				break;
			case ALPHA_INTERSECTION:
				AlphaIntersection.filter( dest, source );
				break;
			case ALPHA_EXCLUSION:
				AlphaExclusion.filter( dest, source );
				break;
			case ALPHA_DIFFERENCE:
				AlphaDifference.filter( dest, source );
				break;
			default:
				break;
		}
	}

}//end class
