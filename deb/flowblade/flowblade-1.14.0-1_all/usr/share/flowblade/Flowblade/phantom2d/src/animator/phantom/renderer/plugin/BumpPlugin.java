package animator.phantom.renderer.plugin;

import animator.phantom.paramedit.NoParamsPanel;
import animator.phantom.plugin.PhantomPlugin;

import com.jhlabs.image.BumpFilter;

public class BumpPlugin extends PhantomPlugin
{
	public BumpPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Bump" );
	}

	public void buildEditPanel()
	{
		addEditor( new NoParamsPanel("Bump") );
	}

	public void doImageRendering( int frame )
	{
		BumpFilter f = new BumpFilter();

		applyFilter( f );
	}

}//end class
