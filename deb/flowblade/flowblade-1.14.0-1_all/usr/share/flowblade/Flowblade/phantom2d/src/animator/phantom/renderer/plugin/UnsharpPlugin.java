package animator.phantom.renderer.plugin;

import animator.phantom.paramedit.IntegerNumberEditor;
import animator.phantom.paramedit.IntegerValueSliderEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.IntegerParam;

import com.jhlabs.image.UnsharpFilter;

public class UnsharpPlugin extends PhantomPlugin
{
	public IntegerParam amount;
	public IntegerParam radius;
	public IntegerParam threshold;

	public UnsharpPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Unsharp" );

		amount = new IntegerParam( 50,0,100 );
		radius = new IntegerParam( 2 );
		threshold = new IntegerParam( 1,0, 255 );

		registerParameter( radius );
		registerParameter( amount );
		registerParameter( threshold );
	}

	public void buildEditPanel()
	{
		IntegerNumberEditor radiusE = new IntegerNumberEditor( "Seed", radius );
		IntegerValueSliderEditor amountE = new IntegerValueSliderEditor( "Amount" , amount );
		IntegerValueSliderEditor thresholdE = new IntegerValueSliderEditor( "Threshold" , threshold );

		addEditor( radiusE );
		addRowSeparator();
		addEditor( amountE );
		addRowSeparator();
		addEditor( thresholdE );
	}

	public void doImageRendering( int frame )
	{
		UnsharpFilter filter = new UnsharpFilter();
		filter.setAmount( (float) amount.get() / 100.0f );
		filter.setRadius( (float) radius.get() );
		filter.setThreshold( threshold.get() );

		applyFilter( filter );
	}

}//end class
