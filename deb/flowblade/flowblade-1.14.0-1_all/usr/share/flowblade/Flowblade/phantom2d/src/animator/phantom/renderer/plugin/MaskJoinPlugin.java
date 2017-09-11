package animator.phantom.renderer.plugin;

import java.awt.image.BufferedImage;

import animator.phantom.paramedit.NoParamsPanel;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;

public class MaskJoinPlugin extends PhantomPlugin
{

	public MaskJoinPlugin()
	{
		initPlugin( STATIC_SOURCE );
	}

	public void buildDataModel()
	{
		setName( "MaskJoin" );
	}

	public void buildEditPanel()
	{
		addEditor( new NoParamsPanel("MaskJoin") );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage img = getFlowImage();

		if( img == null )
		{
			img = PluginUtils.createScreenCanvas();

		}

		sendStaticSource( img, frame );
	}

}//end class