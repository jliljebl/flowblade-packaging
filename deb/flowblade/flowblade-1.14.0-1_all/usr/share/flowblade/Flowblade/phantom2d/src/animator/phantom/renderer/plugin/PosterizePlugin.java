package animator.phantom.renderer.plugin;

import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.PosterizeFilter;

public class PosterizePlugin extends PhantomPlugin
{
	public IntegerParam numLevels;

	public PosterizePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Posterize" );

		numLevels = new IntegerParam( 6 );
		registerParameter( numLevels );
	}

	public void buildEditPanel()
	{
		IntegerNumberEditor numLevelsE = new  IntegerNumberEditor( "Levels", numLevels );
		addEditor( numLevelsE );
	}

	public void doImageRendering( int frame )
	{
		PosterizeFilter f = new PosterizeFilter();
		f.setNumLevels(numLevels.get());

		applyFilter( f );
	}

}//end class
