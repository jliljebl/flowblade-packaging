package animator.phantom.renderer.plugin;

import giotto2D.filters.merge.ColorPointKey;
import giotto2D.libcolor.GiottoRGBInt;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JButton;

import animator.phantom.controller.UpdateController;
import animator.phantom.gui.view.ColorPickListener;
import animator.phantom.paramedit.AnimValueSliderEditor;
import animator.phantom.paramedit.ButtonRow;
import animator.phantom.paramedit.ParamColorDisplay;
import animator.phantom.paramedit.SingleCurveEditor;
import animator.phantom.plugin.AbstractPluginEditLayer;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.param.AnimatedValue;
import animator.phantom.renderer.param.CRCurveParam;
import animator.phantom.renderer.param.ColorParam;
import animator.phantom.renderer.param.IntegerVectorParam;
import animator.phantom.renderer.plugin.editlayer.ColorDifferenceEditLayer;

public class ColorSampleKeyPlugin extends PhantomPlugin implements ActionListener, ColorPickListener
{
	//--- Key
	private IntegerVectorParam colors_red;
	private IntegerVectorParam colors_green;
	private IntegerVectorParam colors_blue;
	private AnimatedValue spread;
	private CRCurveParam alphaCurve;

	private ColorParam dispColor = new ColorParam( Color.black );
	private ParamColorDisplay cDisp;
	private Vector<GiottoRGBInt> colors = new Vector<GiottoRGBInt>();

	private JButton delLast = new JButton("Delete last sample");
	private JButton delAll = new JButton("Delete all samples");

	public ColorSampleKeyPlugin()
	{
		initPlugin( FILTER );
	}

	public void buildDataModel()
	{
		setName( "ColorSampleKey"  );
		colors_red = new IntegerVectorParam("colorsred");
		colors_green = new IntegerVectorParam("colorsgreen");
		colors_blue = new IntegerVectorParam("colorsblue");
		spread = new AnimatedValue( 5, 0, 127  );
		alphaCurve = new CRCurveParam("alpha");

		registerParameter( spread );
		registerParameter( alphaCurve );
		registerParameter( colors_red );
		registerParameter( colors_green );
		registerParameter( colors_blue );
	}

	public void buildEditPanel()
	{
		//--- Key
		cDisp = new ParamColorDisplay( dispColor, "Samples: 0", "keycolor" );
		AnimValueSliderEditor spreadEdit = new AnimValueSliderEditor( "Spread", spread );
		SingleCurveEditor aedit = new SingleCurveEditor( "Key value to Alpha", alphaCurve, 156, 171 );

		Vector<JButton> buttons = new Vector<JButton>();
		buttons.add( delAll );
		buttons.add( delLast );
		ButtonRow buttonRow = new ButtonRow( this, buttons );

		addEditor( cDisp );
		addRowSeparator();
		addEditor( buttonRow );
		addRowSeparator();
		addEditor( spreadEdit );
		addRowSeparator();
		addEditor( aedit );
	}

	public void doImageRendering( int frame )
	{
		BufferedImage img = getFlowImage();

		//--- At load colors are read into elemnts vecs and
		//--- and colors vec needs to be created.
		if( colors.size() != colors_red.get().size() )
			fillColors();

		//--- key
		ColorPointKey cpKey = new ColorPointKey();
		cpKey.setColorPoints( colors );
		int[] convTable = alphaCurve.curve.getCurveCopy( true );
		cpKey.setValToAlphaTable( convTable );
		cpKey.setSlopeWidth( (int) spread.getValue( frame ) );
		cpKey.createLookUps();
		cpKey.filter( img );

		sendFilteredImage( img, frame );
	}

	private void fillColors()
	{
		colors = new Vector<GiottoRGBInt>();

		Vector<Integer> cr = colors_red.get();
		Vector<Integer> cg = colors_green.get();
		Vector<Integer> cb = colors_blue.get();
		for( int i = 0; i < cr.size(); i++ )
		{
			GiottoRGBInt gc = new GiottoRGBInt( 	cr.elementAt( i ).intValue(),
								cg.elementAt( i ).intValue(),
								cb.elementAt( i ).intValue() );
			colors.add( gc );
		}
	}

	//--- key
	public void deleteLast()
	{
		Vector<Integer> cr = colors_red.get();
		Vector<Integer> cg = colors_green.get();
		Vector<Integer> cb = colors_blue.get();

		if( cr.size() == 0 ) return;

		cr.remove( cr.size() - 1 );
		cg.remove( cg.size() - 1 );
		cb.remove( cb.size() - 1 );

		fillColors();
		UpdateController.valueChangeUpdate( UpdateController.VIEW_EDIT );
		updateColorDisp();
	}


	//--- key 
	public void deleteAll()
	{
		colors_red.set( new Vector<Integer>() );
		colors_green.set( new Vector<Integer>() );
		colors_blue.set( new Vector<Integer>() );

		fillColors();
		UpdateController.valueChangeUpdate( UpdateController.VIEW_EDIT );
		updateColorDisp();
	}

	private void updateColorDisp()
	{
		cDisp.setText( "Samples: " + Integer.toString( colors.size() ) );
		if( colors.size() == 0 ) dispColor.set( Color.black );
		else
		{
			GiottoRGBInt c = colors.elementAt( colors.size() - 1 );
			Color col = new Color( c.r, c.g, c.b );
			dispColor.set( col );
		}
		cDisp.frameChanged();
	}

	public void colorPicked( Color c )
	{
		GiottoRGBInt gc = new GiottoRGBInt( c.getRed(), c.getGreen(), c.getBlue() );
		colors.add( gc );
		dispColor.set( c );

		colors_red.get().add( gc.r );
		colors_green.get().add( gc.g );
		colors_blue.get().add( gc.b );
		cDisp.setText( "Samples: " + Integer.toString( colors.size() ) );
	}

	public void actionPerformed( ActionEvent e )
	{
		if( e.getSource() == delLast ) deleteLast();
		if( e.getSource() == delAll ) deleteAll();
	}

	public AbstractPluginEditLayer getPluginEditLayer()
	{
		return new ColorDifferenceEditLayer( this, this );
	}

}//end class
