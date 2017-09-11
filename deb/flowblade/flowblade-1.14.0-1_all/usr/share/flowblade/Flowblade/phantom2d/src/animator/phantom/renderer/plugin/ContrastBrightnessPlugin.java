package animator.phantom.renderer.plugin;

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;

import com.jhlabs.image.ContrastFilter;

public class ContrastBrightnessPlugin extends PhantomPlugin
{
	public AnimatedValue contrast;
	public AnimatedValue brightness;

	public ContrastBrightnessPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Contrast Brightness" );

		contrast = new AnimatedValue( 100.0f, 0.0f, 500.0f );
		brightness = new AnimatedValue( 100.0f, 0.0f, 500.0f );

		registerParameter( contrast );
		registerParameter( brightness );
	}

	public void buildEditPanel()
	{
 		AnimValueNumberEditor contrastE = new AnimValueNumberEditor( "Contrast", contrast );
		AnimValueNumberEditor brightnessE = new AnimValueNumberEditor( "Brightness", brightness );

		addEditor( contrastE );
		addRowSeparator();
		addEditor( brightnessE );
	}

	public void doImageRendering( int frame )
	{
		ContrastFilter contrastFilter = new ContrastFilter();
 		contrastFilter.setContrast( ( contrast.getValue( frame ) / 100.0f ) * 1.0f );
		contrastFilter.setBrightness( ( brightness.getValue( frame ) / 100.f ) * 1.0f );

		applyFilter( contrastFilter );
	}

}//end class
