package animator.phantom.renderer.imagefilter;

/*
    Copyright Janne Liljeblad

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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.Vector;

import animator.phantom.bezier.CRCurve;
import animator.phantom.paramedit.imagefilter.CurvesEditPanel;
import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.param.CRCurveParam;

public class CurvesIOP extends ImageOperation
{
	public CRCurveParam gammac = new CRCurveParam("gamma");
	public CRCurveParam redc = new CRCurveParam("red");
	public CRCurveParam greenc = new CRCurveParam("green");
	public CRCurveParam bluec = new CRCurveParam("blue");

	public static final int alpha = 24;
	public static final int red = 16;
	public static final int green = 8;
	//public static final int alpha_mask = 0x00ffffff;

	public CurvesIOP()
	{
		name = "Curves";
		makeAvailableInFilterStack = true;

		registerParameter( gammac );
		registerParameter( redc );
		registerParameter( greenc );
		registerParameter( bluec );
	}

	public ParamEditPanel getEditPanelInstance()
	{
 		return new CurvesEditPanel( this );
	}

	public void doImageRendering( int frame, Vector<BufferedImage> sourceImages )
	{
		int[] dPix = getBank( renderedImage );

		int[] rLook = redc.curve.getCurveCopy( true );
		int[] gLook = greenc.curve.getCurveCopy( true );
		int[] bLook = bluec.curve.getCurveCopy( true );

		applyGammaToChannel( rLook );
		applyGammaToChannel( gLook );
		applyGammaToChannel( bLook );

		applyCurve( dPix, rLook, gLook, bLook );
	}

	private void applyCurve(int[] dPix, int[] rLook, int[] gLook, int[] bLook )
	{
		int a,r,g,b;
		for( int i = 0; i < dPix.length; i++ )
		{
			a = ( dPix[ i ] >> alpha ) & 0xff;
			r = ( dPix[ i ] >> red ) & 0xff;
			g = ( dPix[ i ] >> green ) & 0xff;
			b = dPix[ i ] & 0xff;

			dPix[ i ] = ( a << alpha ) | ( rLook[ r ] << red ) | gLook[ g ] << green | bLook[ b ];
		}
	}

	private void applyGammaToChannel( int[] channel )
	{
		int[] gamma = gammac.curve.getCurve( true );//very efficient...not. we calculate g 4 times per frame
		int[] linear = CRCurve.getLinerCurve();
		for( int i = 0; i < 256; i++ )
		{
			float gmul = (float) gamma[ i ] / (float) linear[ i ];
			float val = gmul * (float) channel[ i ];
			channel[ i ] = clamp( Math.round( val ) );
		}
	}

	private int clamp( int val )
	{
		if( val > 255 ) val = 255;
		else if( val < 0 ) val = 0;
		
		return val;
	}

	protected static int[] getBank( BufferedImage img )
	{
		WritableRaster imgRaster = img.getRaster();
		DataBufferInt dbuf = (DataBufferInt) imgRaster.getDataBuffer();
		return dbuf.getData( 0 );
	}

}//end class
