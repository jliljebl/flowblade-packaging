package animator.phantom.renderer.plugin;

import animator.phantom.paramedit.AnimValueSliderEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;

import com.jhlabs.image.HSBAdjustFilter;

public class HueSatBrightPlugin extends PhantomPlugin
{
	public AnimatedValue hue;
	public AnimatedValue saturation;
	public AnimatedValue brightness;

	public HueSatBrightPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "HueSatBright" );

		hue = new AnimatedValue( 0.0f, 0.0f, 360.0f );
		saturation = new AnimatedValue( 0.0f, -100.0f, 100.0f );
		brightness = new AnimatedValue( 0.0f, -100.0f, 100.0f );

		registerParameter( hue );
		registerParameter( saturation );
		registerParameter( brightness );
	}

	public void buildEditPanel()
	{
		AnimValueSliderEditor hueEdit = new AnimValueSliderEditor( "Hue", hue );
		AnimValueSliderEditor satEdit = new AnimValueSliderEditor( "Saturation", saturation );
		AnimValueSliderEditor brightEdit = new AnimValueSliderEditor( "Brightness", brightness );

		addEditor( hueEdit );
		addRowSeparator();
		addEditor( satEdit );
		addRowSeparator();
		addEditor( brightEdit );
	}

	public void doImageRendering( int frame )
	{
		HSBAdjustFilter hsbFilter = new HSBAdjustFilter();
		hsbFilter.setHFactor( ( hue.getValue( frame ) ) / 360.0f );
		hsbFilter.setSFactor( ( saturation.getValue( frame ) ) / 100.0f );
		hsbFilter.setBFactor( ( brightness.getValue( frame ) ) / 100.0f );

		applyFilter( hsbFilter );
	}

}//end class
