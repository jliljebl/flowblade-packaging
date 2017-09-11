package animator.phantom.renderer.plugin;

import animator.phantom.paramedit.NoParamsPanel;
import animator.phantom.plugin.PhantomPlugin;

import com.jhlabs.image.SolarizeFilter;

public class SolarizePlugin extends PhantomPlugin
{
	public SolarizePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Solarize" );
	}

	public void buildEditPanel()
	{
		addEditor( new  NoParamsPanel("Solarize") );
	}

	public void doImageRendering( int frame )
	{
		SolarizeFilter f = new SolarizeFilter();

		applyFilter( f );
	}

}//end class
