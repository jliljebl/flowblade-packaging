package animator.phantom.renderer.plugin;

import animator.phantom.paramedit.AnimValueSliderEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;

import com.jhlabs.image.LevelsFilter;

public class LevelsPlugin extends PhantomPlugin
{
	public AnimatedValue lowLevel ;
	public AnimatedValue highLevel;
	public AnimatedValue lowOutputLevel;
	public AnimatedValue highOutputLevel;

	public LevelsPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Levels" );

		lowLevel = new AnimatedValue( 0.0f, 0.0f, 255.0f );
		highLevel = new AnimatedValue( 255.0f, 0.0f, 255.0f );
		lowOutputLevel = new AnimatedValue( 0.0f, 0.0f, 255.0f );
		highOutputLevel = new AnimatedValue( 255.0f, 0.0f, 255.0f );

		registerParameter( lowLevel );
		registerParameter( highLevel );
		registerParameter( lowOutputLevel );
		registerParameter( highOutputLevel );
	}

	public void buildEditPanel()
	{
		AnimValueSliderEditor low = new AnimValueSliderEditor( "Input low", lowLevel );
		AnimValueSliderEditor high = new AnimValueSliderEditor( "Input high", highLevel );
		AnimValueSliderEditor lowOut = new AnimValueSliderEditor("Output low", lowOutputLevel );
		AnimValueSliderEditor highOut = new AnimValueSliderEditor( "Output high", highOutputLevel );

		addEditor( low );
		addRowSeparator();
		addEditor( high);
		addRowSeparator();
		addEditor( lowOut);
		addRowSeparator();
		addEditor( highOut);
	}

	public void doImageRendering( int frame )
	{
		LevelsFilter levelsFilter = new LevelsFilter();
   		levelsFilter.setLowLevel( lowLevel.getValue( frame ) / 255.0f );
		levelsFilter.setHighLevel( highLevel.getValue( frame ) / 255.0f );
		levelsFilter.setLowOutputLevel( lowOutputLevel.getValue( frame ) / 255.0f );
		levelsFilter.setHighOutputLevel( highOutputLevel.getValue( frame ) / 255.0f );

		applyFilter( levelsFilter );
	}

}//end class
