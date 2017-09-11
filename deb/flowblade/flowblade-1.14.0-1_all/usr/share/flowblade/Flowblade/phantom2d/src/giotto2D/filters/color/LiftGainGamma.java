package giotto2D.filters.color;

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
/*
	NOTE: Ported from GIMP.
*/

import giotto2D.filters.AbstractFilter;

import java.awt.image.BufferedImage;

public class LiftGainGamma extends AbstractFilter
{
	private double lift = 0.0;// -0.5 to 0.5
	private double brightness = 0.0;// -0.5 to 0.5
	private double contrast = 0.0;// // -1.0 to 1.0

	private int[] lookUp = new int[256];

	public LiftGainGamma()
	{
		initlookUp();
	}

	public void setValues( double lift, double gain, double gamma )
	{
		this.lift = lift;
		this.brightness = gain;
		this.contrast = gamma;
		initlookUp();
	}

	//---- value: 0.0 -> 1.0 (normalized luma)
	private double lut_func ( double value )
	{
		double nvalue;
		double power;

		/* apply lift */
		value = clamp01( value + lift );
	
		/* apply brightness */
		/* 0.0 is neutral*/
		if( brightness < 0.0)
			value = value * (1.0 + brightness);//darkens image
		else
			value = value + ((1.0 - value) * brightness);
	
		/* apply contrast */
		if(contrast < 0.0)
		{
			if (value > 0.5)
				nvalue = 1.0 - value;
			else
				nvalue = value;
	
			if (nvalue < 0.0)
				nvalue = 0.0;
	
			nvalue = 0.5 * Math.pow (nvalue * 2.0 , (double) (1.0 + contrast));
			
			if (value > 0.5)
				value = 1.0 - nvalue;
			else
				value = nvalue;
		}
		else
		{
			if (value > 0.5)
				nvalue = 1.0 - value;
			else
				nvalue = value;
	
			if (nvalue < 0.0)
				nvalue = 0.0;
	
			power = (contrast == 1.0) ? 127 : 1.0 / (1.0 - contrast);
			nvalue = 0.5 * Math.pow (2.0 * nvalue, power);
	
			if (value > 0.5)
				value = 1.0 - nvalue;
			else
				value = nvalue;
		}
	
		return value;
	}

	protected void initlookUp()
	{
		for( int i = 0; i < 256; i++ )
		{
			double val = (double) i / 255.0;
			lookUp[ i ] = (int) (lut_func( val ) * 255.0);
		}
		
	}

	public BufferedImage filter( BufferedImage img )
	{
		int r, g, b, a;
 		int[] data = getBank( img );

		for( int i = 0; i < data.length; i++ )
		{
			a = ( data[ i ] >> alpha ) & 0xff;
			r = ( data[ i ] >> red ) & 0xff;
			g = ( data[ i ] >> green ) & 0xff;
			b = data[ i ] & 0xff;

			data[ i ] = a << alpha | lookUp[ r ] << red | lookUp[ g ] << green | lookUp[ b ];
		}
		return img;
	}

}//end class
