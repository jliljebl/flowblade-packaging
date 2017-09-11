package animator.phantom.renderer.plugin;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.IntegerParam;

public class CanvasPlugin extends PhantomPlugin
{
	public IntegerParam width = new IntegerParam( 100 );
	public IntegerParam height = new IntegerParam( 100 );

	public CanvasPlugin()
	{
		initPlugin( STATIC_SOURCE );
	}

	public void buildDataModel()
	{
		setName( "Canvas" );

		registerParameter( width );
		registerParameter( height );
	}

	public void buildEditPanel()
	{
		IntegerNumberEditor wedit = new IntegerNumberEditor( "Width" , width );
		IntegerNumberEditor hedit = new IntegerNumberEditor( "Height", height );

		addEditor( wedit );
		addRowSeparator();
		addEditor( hedit );
	}

	public void doImageRendering( int frame )
	{
		int w = width.get();
		int h = height.get();

		BufferedImage img = getFlowImage();

		if( img == null )
		{
			img = PluginUtils.createCanvas( w, h );

		}
		else//--- img from flow.is top left cropped
		{
			BufferedImage newImage = PluginUtils.createTransparentCanvas( w, h );
			Graphics2D gc = newImage.createGraphics();
			gc.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER ) );
			gc.drawRenderedImage( img, null );
			gc.dispose();
			img = newImage;
		}

		sendStaticSource( img, frame );
	}

}//end class