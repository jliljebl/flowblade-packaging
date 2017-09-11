package animator.phantom.renderer.plugin;


import com.jhlabs.image.OilFilter;


import animator.phantom.plugin.PhantomPlugin;

public class TestPlugin  extends PhantomPlugin
{

	public TestPlugin()
	{
		initPlugin( FILTER );
	}

	@Override
	public void buildDataModel() 
	{
		setName("Test");
	}

	@Override
	public void buildEditPanel() 
	{

	}

	public void doImageRendering( int frame )
	{
		OilFilter f = new OilFilter();

		applyFilter( f );
	}
	
}//end class
