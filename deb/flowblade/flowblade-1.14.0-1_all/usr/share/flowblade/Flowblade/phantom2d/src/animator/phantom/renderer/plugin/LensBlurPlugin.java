package animator.phantom.renderer.plugin;

import animator.phantom.paramedit.AnimValueSliderEditor;
import animator.phantom.paramedit.IntegerValueSliderEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.LensBlurFilter;

public class LensBlurPlugin extends PhantomPlugin
{
	public IntegerParam radius;
	public IntegerParam sides;
	public IntegerParam bloom;
	public AnimatedValue bloomThreshold;

	public LensBlurPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "LensBlur" );

		radius = new IntegerParam(10,0,50);
		sides = new IntegerParam(5,3,12);
		bloom = new IntegerParam(2,0,8);
		bloomThreshold = new AnimatedValue(255, 0, 255);

		registerParameter( radius );
		registerParameter( sides );
		registerParameter( bloom );
		registerParameter( bloomThreshold );	
	}

	public void buildEditPanel()
	{
		IntegerValueSliderEditor radiusE = new IntegerValueSliderEditor( "Radius" , radius );
		IntegerValueSliderEditor sidesE = new IntegerValueSliderEditor( "Sides" , sides );
		IntegerValueSliderEditor bloomE = new IntegerValueSliderEditor( "Bloom" , bloom );
		AnimValueSliderEditor bloomThresholdE = new AnimValueSliderEditor(  "Threshold", bloomThreshold);

		addEditor( radiusE );
		addRowSeparator();
		addEditor( sidesE );
		addRowSeparator();
		addEditor( bloomE );
		addRowSeparator();
		addEditor( bloomThresholdE );
	}

	public void doImageRendering( int frame )
	{
		LensBlurFilter filter = new LensBlurFilter();
		filter.setRadius( (float)radius.get() );
		filter.setSides( sides.get() );
		filter.setBloom( (float)bloom.get() );
		filter.setBloomThreshold( bloomThreshold.get(frame) );

		applyFilter( filter );
	}

}//end class
