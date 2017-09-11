package animator.phantom.renderer.plugin;


import java.awt.image.BufferedImage;

import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;

public class TransparentBGPlugin  extends PhantomPlugin
{
	
	public TransparentBGPlugin()
	{
		initPlugin( STATIC_SOURCE );
	}

	public void buildDataModel()
	{
		setName( "TransparentBG" );
	}

	public void buildEditPanel()
	{
	}
	
	public void doImageRendering( int frame )
	{
		BufferedImage img = PluginUtils.createTransparentScreenCanvas();


		sendStaticSource( img, frame );
	}
}//end class
