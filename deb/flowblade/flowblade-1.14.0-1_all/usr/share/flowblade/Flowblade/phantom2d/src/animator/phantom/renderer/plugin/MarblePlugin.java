package animator.phantom.renderer.plugin;

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;

import com.jhlabs.image.MarbleFilter;

public class MarblePlugin extends PhantomPlugin
{
	public AnimatedValue xScale;
	public AnimatedValue yScale;
	public AnimatedValue amount;
	public AnimatedValue turbulence;

	public MarblePlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Marble" );
		xScale = new AnimatedValue( 4.0f );
		yScale = new AnimatedValue( 4.0f);
		amount = new AnimatedValue( 1.0f) ;
		turbulence = new AnimatedValue( 1.0f);

		registerParameter(xScale);
		registerParameter(yScale);
		registerParameter(amount);
		registerParameter(turbulence);
	}

	public void buildEditPanel()
	{
		AnimValueNumberEditor xScaleE = new AnimValueNumberEditor( "X Scale", xScale );
		AnimValueNumberEditor yScaleE = new AnimValueNumberEditor( "Y Scale", yScale );
		AnimValueNumberEditor amountE = new AnimValueNumberEditor("Amount", amount );
		AnimValueNumberEditor turbulenceE = new AnimValueNumberEditor("Turbulence", turbulence );

		addEditor( xScaleE );
		addRowSeparator();
		addEditor( yScaleE );
		addRowSeparator();
		addEditor( amountE );
		addRowSeparator();
		addEditor( turbulenceE );
	}

	public void doImageRendering( int frame )
	{
		MarbleFilter mf = new MarbleFilter();
		mf.setXScale( xScale.getValue( frame ) );
		mf.setYScale( yScale.getValue( frame ) );
		mf.setAmount( amount.getValue( frame ));
		mf.setTurbulence( turbulence.getValue( frame ));

		applyFilter( mf );
	}

}//end class
