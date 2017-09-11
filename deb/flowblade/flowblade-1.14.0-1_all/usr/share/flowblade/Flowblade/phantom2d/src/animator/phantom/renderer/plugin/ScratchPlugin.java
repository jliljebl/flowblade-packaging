package animator.phantom.renderer.plugin;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import animator.phantom.paramedit.AnimValueSliderEditor;
import animator.phantom.paramedit.ParamColorSelect;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.plugin.PluginUtils;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.ColorParam;

import com.jhlabs.image.ScratchFilter;

public class ScratchPlugin extends PhantomPlugin
{
	private AnimatedValue density;
	private AnimatedValue angle;
	private AnimatedValue angleVariation;
	private AnimatedValue width;
	private AnimatedValue length;
	private ColorParam fgColor;
	private ColorParam bgColor;

	public ScratchPlugin()
	{
		initPlugin( STATIC_SOURCE );
	}

	public void buildDataModel()
	{
		setName( "Scratch" );

		density = new AnimatedValue( 10, 0, 100 );
		angle = new AnimatedValue( 0, 0, 90 );
		angleVariation = new AnimatedValue( 100, 0, 100 );
		width = new AnimatedValue( 5, 0, 100 );
		length = new AnimatedValue( 50, 0, 100 );

		fgColor = new ColorParam( Color.white );
		bgColor = new ColorParam( Color.black );

		registerParameter( density );
		registerParameter( angle );
		registerParameter( angleVariation );
		registerParameter( width );
		registerParameter( length );
		registerParameter( fgColor );
		registerParameter( bgColor );
	}

	public void buildEditPanel()
	{
		AnimValueSliderEditor aEdit = new  AnimValueSliderEditor( "Angle" , angle );
		AnimValueSliderEditor dEdit = new  AnimValueSliderEditor( "Density", density );
		AnimValueSliderEditor avEdit = new  AnimValueSliderEditor( "Angle Variation", angleVariation );

		AnimValueSliderEditor wEdit = new  AnimValueSliderEditor( "Width" , width );
		AnimValueSliderEditor lEdit = new  AnimValueSliderEditor( "Length", length );
	
		ParamColorSelect bgEdit = new ParamColorSelect( bgColor, "Background Color" );
		ParamColorSelect fgEdit = new ParamColorSelect( fgColor, "Foreground Color" );

		addEditor( aEdit );
		addRowSeparator();
		addEditor( dEdit );
		addRowSeparator();
		addEditor( avEdit );
		addRowSeparator();
		addEditor( wEdit );
		addRowSeparator();
		addEditor( lEdit );
		addRowSeparator();
		addEditor( bgEdit );
		addRowSeparator();
		addEditor( fgEdit );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage img = PluginUtils.createFilterStackableCanvas( this );
		Graphics2D gc = img.createGraphics();
		gc.setColor( bgColor.get() );
		gc.fillRect( 0, 0, img.getWidth(), img.getHeight()  );
		gc.dispose();

		ScratchFilter filter = new ScratchFilter();
		filter.setAngleVariation( angleVariation.get( frame ) / 100.0f );
		filter.setWidth( width.get( frame ) / 10.0f  );
		filter.setLength( length.get( frame )  / 100.0f  );
		filter.setAngle((float) Math.toRadians( (double) angle.get( frame ) ) );
		filter.setDensity( density.get( frame )  / 100.0f );
		filter.setColor( fgColor.get().getRGB() );
		PluginUtils.filterImage( img, filter );

		sendStaticSource( img, frame );
	}

}//end class
