package animator.phantom.renderer.plugin;

import animator.phantom.paramedit.NoParamsPanel;
import animator.phantom.plugin.PhantomPlugin;

import com.jhlabs.image.GrayscaleFilter;

public class GreyscalePlugin extends PhantomPlugin
{
	public GreyscalePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Grayscale" );
	}

	public void buildEditPanel()
	{
		addEditor( new NoParamsPanel( "Grayscale" ) );
	}

	public void doImageRendering( int frame )
	{
		GrayscaleFilter f = new GrayscaleFilter();
		applyFilter( f );
	}

}//end class
