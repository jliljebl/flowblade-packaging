package animator.phantom.renderer.imagefilter;

/*
    Copyright Janne Liljeblad 2006,2007,2008

    This file is part of Phantom2D.

    Phantom2D is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Phantom2D is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Phantom2D.  If not, see <http://www.gnu.org/licenses/>.
*/

import giotto2D.filters.AbstractFilter;
import giotto2D.filters.color.ColorBalance;
import giotto2D.filters.color.LiftGainGamma;
import giotto2D.libcolor.GiottoColorSpace;
import giotto2D.libcolor.GiottoHSL;
import giotto2D.libcolor.GiottoRGB;

import java.awt.image.BufferedImage;
import java.util.Vector;

import animator.phantom.controller.TimeLineController;
import animator.phantom.controller.UpdateController;
import animator.phantom.paramedit.imagefilter.ColorCorrectorEditPanel;
import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.param.AnimatedValue;

public class ColorCorrectorIOP extends ImageOperation
{
	//--- Params used to convert distance values from ColorWheelEditor to effect strength
	public static float SHADOWS_DIST_MULT = 0.75f;
	public static float MID_DIST_MULT = 125.0f;
	public static float HI_DIST_MULT = 0.5f;

	//--- Params used to convert LGG user values to effect strength
	public static double LIFT_CONV = 0.5 / 127.0;
	public static double GAIN_CONV = 0.5 / 127.0; 
	public static double GAMMA_CONV = 0.5 / 127.0; 

	public AnimatedValue lift;
	public AnimatedValue gain;
	public AnimatedValue gamma;

	public AnimatedValue shadowHue;
	public AnimatedValue shadowDistance;

	public AnimatedValue midHue;
	public AnimatedValue midDistance;

	public AnimatedValue hiHue;
	public AnimatedValue hiDistance;

	//--- used run time, not saved
	private float shadowCyRe;
	private float shadowMaGr;
	private float shadowYeBl;

	private float midCyRe;
	private float midMaGr;
	private float midYeBl;

	private float hiCyRe;
	private float hiMaGr;
	private float hiYeBl;

	private LiftGainGamma lggFilter;
	private ColorBalance cbFilter;

	public ColorCorrectorIOP()
	{
		name = "ColorCorrector";
		makeAvailableInFilterStack = true;

		lggFilter = new LiftGainGamma();
		cbFilter = new ColorBalance();

		lift = new AnimatedValue( this, 0, -127, 127 );
		lift.setParamName( "Lift" );
		gain  = new AnimatedValue( this, 0, -127, 127 );
		gain.setParamName( "Gain" );
		gamma  = new AnimatedValue( this, 0, -127, 127 );
		gamma.setParamName( "Gamma" );

		shadowHue = new AnimatedValue( this, 0 );
		shadowDistance = new AnimatedValue( this, 0 );
		midHue = new AnimatedValue( this, 0 );
		midDistance = new AnimatedValue( this, 0 );
		hiHue = new AnimatedValue( this, 0 );
		hiDistance = new AnimatedValue( this, 0 );

		shadowHue.setParamName( "shadowhue" );
		shadowDistance.setParamName( "shadowdistance" );
		midHue.setParamName( "midhue" );
		midDistance.setParamName( "middistance" );
		hiHue.setParamName( "hihue" );
		hiDistance.setParamName( "hidistance" );

		registerParameter( shadowHue );
		registerParameter( shadowDistance );
		registerParameter( midHue);
		registerParameter( midDistance );
		registerParameter( hiHue);
		registerParameter( hiDistance );

		registerParameter( lift );
		registerParameter( gain );
		registerParameter( gamma );
	}

	public ParamEditPanel getEditPanelInstance()
	{
		return new ColorCorrectorEditPanel( this );
	}

	public void doImageRendering( int frame, Vector<BufferedImage> sourceImages )
	{
		lggFilter.setValues( lift.getValue( frame ) * LIFT_CONV, gain.getValue( frame ) * GAIN_CONV, gamma.getValue( frame ) * GAMMA_CONV);
		lggFilter.filter( renderedImage );

		//--- Calculates filter input values for current user input values
		setShadowCorrection( shadowHue.getValue( frame ), shadowDistance.getValue( frame ) );
		setHighLightCorrection( hiHue.getValue( frame ), hiDistance.getValue( frame ) );
		setMidtoneCorrection( midHue.getValue( frame ), midDistance.getValue( frame ) );

		//--- Sets filter input values.
		cbFilter.setRangeValue( AbstractFilter.SHADOWS, ColorBalance.CYAN_RED, shadowCyRe, false );
		cbFilter.setRangeValue( AbstractFilter.SHADOWS, ColorBalance.MAGENTA_GREEN, shadowMaGr, false );
		cbFilter.setRangeValue( AbstractFilter.SHADOWS, ColorBalance.YELLOW_BLUE, shadowYeBl, false );

		cbFilter.setRangeValue( AbstractFilter.MIDTONES, ColorBalance.CYAN_RED, midCyRe, false );
		cbFilter.setRangeValue( AbstractFilter.MIDTONES, ColorBalance.MAGENTA_GREEN, midMaGr, false );
		cbFilter.setRangeValue( AbstractFilter.MIDTONES, ColorBalance.YELLOW_BLUE, midYeBl, false );

		cbFilter.setRangeValue( AbstractFilter.HIGHLIGHTS, ColorBalance.CYAN_RED, hiCyRe, false );
		cbFilter.setRangeValue( AbstractFilter.HIGHLIGHTS, ColorBalance.MAGENTA_GREEN, hiMaGr, false );
		cbFilter.setRangeValue( AbstractFilter.HIGHLIGHTS, ColorBalance.YELLOW_BLUE, hiYeBl, false );

		cbFilter.createLookupTables();

		cbFilter.filter( renderedImage );
	}

	//--- CALLBACK from editpanel
	//--- NOTE: Undos registered exceptionally here!!!
	public void colorCorrectionDone( int range, float angle, float distance )
	{

		System.out.println("angle:" + new Float( angle ).toString() );
		System.out.println("distance:" + new Float( distance ).toString() );


		int frame = TimeLineController.getCurrentFrame();
		if( range == AbstractFilter.SHADOWS )
		{
			shadowHue.setValue( frame, angle );
			shadowDistance.setValue( frame, distance );
			shadowHue.registerUndo();
			shadowDistance.registerUndo( false );
		}

		if( range == AbstractFilter.MIDTONES )
		{
			midHue.setValue( frame, angle );
			midDistance.setValue( frame, distance );
			midHue.registerUndo();
			midDistance.registerUndo( false );
		}

		if( range == AbstractFilter.HIGHLIGHTS )
		{
			hiHue.setValue( frame, angle );
			hiDistance.setValue( frame, distance );
			hiHue.registerUndo();
			hiDistance.registerUndo( false );
		}

		UpdateController.valueChangeUpdate();
	}

	//--- To not to make black different color because of correction,
	//--- max (of R-G-B)color is kept same, others are reduced.
	//--- Any correction darkens to shadows.
	public void setShadowCorrection( float angle, float distance )
	{
		GiottoRGB rgb = getRGB( angle );
		distance = distance * SHADOWS_DIST_MULT;
	
		int maxColor = AbstractFilter.RED;
		if( rgb.g >= rgb.r && rgb.g >= rgb.b ) maxColor =  AbstractFilter.GREEN;
		if( rgb.b >= rgb.r && rgb.b >= rgb.g ) maxColor =  AbstractFilter.BLUE;

		double valR = 0;
		double valG = 0;
		double valB = 0;

		double dR = 0;
		double dG = 0;
		double dB = 0;

		if( maxColor == AbstractFilter.RED)
		{
			dG = rgb.r - rgb.g;
			dB = rgb.r - rgb.b;

			valG = -100.f * distance * dG;
			valB = -100.f * distance * dB;
			valR = 0;
		}

		if( maxColor == AbstractFilter.GREEN)
		{
			dR = rgb.g - rgb.r;
			dB = rgb.g - rgb.b;

			valG = 0;
			valB = -100.f * distance * dB;
			valR = -100.f * distance * dR;
		}

		if( maxColor == AbstractFilter.BLUE)
		{
			dR = rgb.b - rgb.r;
			dG = rgb.b - rgb.g;

			valG = -100.f * distance * dG;
			valB = 0;
			valR = -100.f * distance * dR;
		}

		shadowCyRe = (float) valR;
		shadowMaGr = (float) valG;
		shadowYeBl = (float) valB;
	}

	//--- To not to make white different color because of correction,
	//--- min (of R-G-B)color is kept same, others are raised.
	//--- Any correction lightens to highlights.
	public void setHighLightCorrection( float angle, float distance )
	{
		GiottoRGB rgb = getRGB( angle );
		distance = distance * HI_DIST_MULT;
	
		int minColor = AbstractFilter.RED;
		if( rgb.g <= rgb.r && rgb.g <= rgb.b ) minColor =  AbstractFilter.GREEN;
		if( rgb.b <= rgb.r && rgb.b <= rgb.g ) minColor =  AbstractFilter.BLUE;

		double valR = 0;
		double valG = 0;
		double valB = 0;

		double dR = 0;
		double dG = 0;
		double dB = 0;

		if( minColor == AbstractFilter.RED)
		{
			dG = rgb.g - rgb.r;
			dB = rgb.b - rgb.r;

			valG = 100.f * distance * dG;
			valB = 100.f * distance * dB;
			valR = 0;
		}

		if( minColor == AbstractFilter.GREEN)
		{
			dR = rgb.r - rgb.g;
			dB = rgb.b - rgb.g;

			valG = 0;
			valB = 100.f * distance * dB;
			valR = 100.f * distance * dR;
		}

		if( minColor == AbstractFilter.BLUE)
		{
			dR = rgb.r - rgb.b;
			dG = rgb.b - rgb.b;

			valG = 100.f * distance * dG;
			valB = 0;
			valR = 100.f * distance * dR;
		}

		hiCyRe = (float) valR;
		hiMaGr = (float) valG;
		hiYeBl = (float) valB;
	}

	//--- Distance sets value range from -range/2 to range/2.
	//--- r, g, b are converted so that value 0 = -range/2, 0.5 = 0, 1.0 =  range/2,
	//--- may affect luminosity
	public void setMidtoneCorrection( float angle, float distance )
	{
		float range = distance * MID_DIST_MULT;
		float floor = -(range / 2);

		GiottoRGB rgb = getRGB( angle );
		double valR = floor + range * rgb.r;
		double valG = floor + range * rgb.g;
		double valB = floor + range * rgb.b;

		midCyRe = (float)valR;
		midMaGr = (float)valG;
		midYeBl = (float)valB;
	}

	private GiottoRGB getRGB( float angle )
	{
		GiottoHSL hsl = new GiottoHSL( angle, 1.0, 0.5, 1.0 );
		GiottoRGB rgb =  new GiottoRGB();
 		GiottoColorSpace.hsl_to_rgb( hsl, rgb);
		return rgb;
	}

}//end class
