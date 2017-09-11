package animator.phantom.renderer.plugin;

import animator.phantom.paramedit.NoParamsPanel;
import animator.phantom.plugin.PhantomPlugin;

import com.jhlabs.image.SharpenFilter;

public class SharpenPlugin extends PhantomPlugin
{

	public SharpenPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Sharpen" );
	}

	public void buildEditPanel()
	{
		addEditor( new NoParamsPanel("Sharpen")  );
	}

	public void doImageRendering( int frame )
	{
		SharpenFilter sharpenFilter = new SharpenFilter();

		applyFilter( sharpenFilter );
	}

}//end class
