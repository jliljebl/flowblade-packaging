package animator.phantom.renderer.plugin;

import animator.phantom.paramedit.NoParamsPanel;
import animator.phantom.plugin.PhantomPlugin;

import com.jhlabs.image.DespeckleFilter;

public class DespecklePlugin extends PhantomPlugin
{

	public DespecklePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Despeckle" );
	}

	public void buildEditPanel()
	{
		addEditor( new NoParamsPanel("Despeckle") );
	}

	public void doImageRendering( int frame )
	{
		DespeckleFilter f = new DespeckleFilter();
		applyFilter( f );
	}

}//end class
