package animator.phantom.renderer.plugin;

import animator.phantom.paramedit.AnimValueNumberEditor;
import animator.phantom.paramedit.BooleanComboBox;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.BooleanParam;

import com.jhlabs.image.EmbossFilter;

public class EmbossPlugin extends PhantomPlugin
{
	private float DEGREES_TO_RADIANS = (float) Math.PI / 180;

	private BooleanParam emboss;
	private AnimatedValue azimuth;
	private AnimatedValue elevation;
	private AnimatedValue width45;

	public EmbossPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "Emboss" );

		emboss = new BooleanParam(false);
		azimuth = new AnimatedValue( 135, 0.0f, 360.0f );
		elevation = new AnimatedValue( 30, 0.0f, 360.0f );
		width45 = new AnimatedValue( 3.0f );

		registerParameter( emboss );
		registerParameter( azimuth );
		registerParameter( elevation );
		registerParameter( width45 );
	}

	public void buildEditPanel()
	{
		AnimValueNumberEditor azimuthEdit = new AnimValueNumberEditor( "Azimuth", azimuth );
		AnimValueNumberEditor elevationEdit = new AnimValueNumberEditor( "Elevation", elevation );
		AnimValueNumberEditor widthEdit = new AnimValueNumberEditor( "Bump Height", width45 );
		BooleanComboBox embossEdit = new BooleanComboBox( emboss, "Emboss", "on", "off", true );

		addEditor( azimuthEdit );
		addRowSeparator();
		addEditor( elevationEdit );
		addRowSeparator();
		addEditor( widthEdit );
		addRowSeparator();
		addEditor( embossEdit );
	}

	public void doImageRendering( int frame )
	{
		EmbossFilter embossFilter = new EmbossFilter();
		embossFilter.setAzimuth( azimuth.get(frame) * DEGREES_TO_RADIANS );
		embossFilter.setElevation( elevation.get(frame) * DEGREES_TO_RADIANS );
		embossFilter.setBumpHeight( width45.get(frame) );
		embossFilter.setEmboss( emboss.get() );

		applyFilter( embossFilter );
	}

}//end class
