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

import giotto2D.filters.AbstractFilter;

import java.awt.image.BufferedImage;

//--- Fron GIMP
public class ColorBalance extends AbstractFilter
{
	public static final int CYAN_RED = 0;
	public static final int MAGENTA_GREEN = 1;
	public static final int YELLOW_BLUE = 2;

	private float[] cyan_red = new float[3];
	private float[] magenta_green = new float[3];
	private float[] yellow_blue = new float[3];
	
	private int[] r_lookup = new int[256];
	private int[] g_lookup = new int[256];
	private int[] b_lookup = new int[256];
	
	private static float[] highlights_add = new float[256];
	private static float[] midtones_add = new float[256];
	private static float[] shadows_add = new float[256];
	
	private static float[] highlights_sub = new float[256];
	private static float[] midtones_sub = new float[256];
	private static float[] shadows_sub = new float[256];

	static
	{
		for (int i = 0; i < 256; i++)
		{
			highlights_add[i] = (float) (1.075 - 1 / ((double) i / 16.0 + 1 ));
			shadows_sub[255 - i] = (float) (1.075 - 1 / ((double) i / 16.0 + 1 ));
		
			midtones_add[i] = (float)( 0.667 * (1.0 - SQR(( (double) i - 127.0) / 127.0)) );
			midtones_sub[i] = (float)( 0.667 * (1.0 - SQR(( (double) i - 127.0) / 127.0)) );

			shadows_add[i] = (float)( 0.667 * (1.0 - SQR(((double) i - 127.0) / 127.0)) );
			highlights_sub[i] = (float)( 0.667 * (1.0 - SQR(((double) i - 127.0) / 127.0)) );
		}
	}

	private static double SQR( double d ){ return d * d; }

	public ColorBalance()
	{
		init();
		createLookupTables();
	}

	private void init()
	{
		for ( int range = 0; range < 3; range++ )
		{
			cyan_red[range]      = 0.0f;
			magenta_green[range] = 0.0f;
			yellow_blue[range]   = 0.0f;
		}
	}

	//--- value range -100, 100
	public void setRangeValue( int tonerange, int balanceChannel, float value, boolean computeLookUps )
	{
		float[] channelVals;
		if( balanceChannel == CYAN_RED )
			channelVals = cyan_red;
		else if( balanceChannel == MAGENTA_GREEN )
			channelVals = magenta_green;
		else //YELLOW_BLUE
			channelVals = yellow_blue;

		channelVals[ tonerange ] = value;
		if( computeLookUps ) createLookupTables();
	}

	public void setAllTonesValue( int balanceChannel, float value, boolean computeLookUps )
	{
		float[] channelVals;
		if( balanceChannel == CYAN_RED )
			channelVals = cyan_red;
		else if( balanceChannel == MAGENTA_GREEN )
			channelVals = magenta_green;
		else //YELLOW_BLUE
			channelVals = yellow_blue;

		channelVals[ SHADOWS ] = value;
		channelVals[ MIDTONES ] = value;
		channelVals[ HIGHLIGHTS ] = value;

		if( computeLookUps ) createLookupTables();
	}

	public void createLookupTables()
	{
		float[][] cyan_red_transfer = new float[3][256];
		float[][] magenta_green_transfer = new float[3][256];
		float[][] yellow_blue_transfer = new float[3][256];
		int i,r_n, g_n, b_n;

		cyan_red_transfer[ SHADOWS ] = ( cyan_red[ SHADOWS ] > 0) ? shadows_add : shadows_sub;
		cyan_red_transfer[ MIDTONES ] = ( cyan_red[ MIDTONES ] > 0) ? midtones_add : midtones_sub;
		cyan_red_transfer[ HIGHLIGHTS ] = ( cyan_red[ HIGHLIGHTS ] > 0) ? highlights_add : highlights_sub;
		
		magenta_green_transfer[SHADOWS] = ( magenta_green[SHADOWS] > 0) ? shadows_add : shadows_sub;
		magenta_green_transfer[MIDTONES] = ( magenta_green[MIDTONES] > 0) ? midtones_add : midtones_sub;
		magenta_green_transfer[HIGHLIGHTS] = ( magenta_green[HIGHLIGHTS] > 0) ? highlights_add : highlights_sub;

		yellow_blue_transfer[SHADOWS] = ( yellow_blue[SHADOWS] > 0) ? shadows_add : shadows_sub;
		yellow_blue_transfer[MIDTONES] = ( yellow_blue[MIDTONES] > 0) ? midtones_add : midtones_sub;
		yellow_blue_transfer[HIGHLIGHTS] = ( yellow_blue[HIGHLIGHTS] > 0) ? highlights_add : highlights_sub;
		
		for (i = 0; i < 256; i++)
		{
			r_n = i;
			g_n = i;
			b_n = i;
			
			r_n +=(int) (cyan_red[SHADOWS] * cyan_red_transfer[SHADOWS][r_n]);
			r_n = clamp255 (r_n);
			r_n += (int) (cyan_red[MIDTONES] * cyan_red_transfer[MIDTONES][r_n]);
			r_n = clamp255 (r_n);
			r_n += (int)(cyan_red[HIGHLIGHTS] * cyan_red_transfer[HIGHLIGHTS][r_n]);
			r_n = clamp255 (r_n);
		
			g_n += (int) (magenta_green[SHADOWS] * magenta_green_transfer[SHADOWS][g_n]);
			g_n = clamp255 (g_n);
			g_n += (int) (magenta_green[MIDTONES] * magenta_green_transfer[MIDTONES][g_n]);
			g_n = clamp255 (g_n);
			g_n += (int) (magenta_green[HIGHLIGHTS] * magenta_green_transfer[HIGHLIGHTS][g_n]);
			g_n = clamp255 (g_n);
			
			b_n += (int) (yellow_blue[SHADOWS] * yellow_blue_transfer[SHADOWS][b_n]);
			b_n = clamp255 (b_n);
			b_n += (int) (yellow_blue[MIDTONES] * yellow_blue_transfer[MIDTONES][b_n]);
			b_n = clamp255 (b_n);
			b_n += (int) (yellow_blue[HIGHLIGHTS] * yellow_blue_transfer[HIGHLIGHTS][b_n]);
			b_n = clamp255 (b_n);
			
			r_lookup[i] = r_n;
			g_lookup[i] = g_n;
			b_lookup[i] = b_n;
		}
	}

	public void filter( BufferedImage img )
	{
		int r, g, b, a;
		int r_n, g_n, b_n;

 		int[] data = getBank( img );

		for( int i = 0; i < data.length; i++ )
		{
			a = ( data[ i ] >> alpha ) & 0xff;
			r = ( data[ i ] >> red ) & 0xff;
			g = ( data[ i ] >> green ) & 0xff;
			b = data[ i ] & 0xff;

			r_n = r_lookup[r];
			g_n = g_lookup[g];
			b_n = b_lookup[b];

			data[ i ] = a << alpha | r_n << red | g_n << green | b_n;
		}
	}

	//-- Applies filter only on semitransparent pixels.
	public void filterSemiTrans( BufferedImage img )
	{
		int r, g, b, a;
		int r_n, g_n, b_n;

 		int[] data = getBank( img );

		for( int i = 0; i < data.length; i++ )
		{
			a = ( data[ i ] >> alpha ) & 0xff;
			if( a == 0 || a == 255 ) continue;

			r = ( data[ i ] >> red ) & 0xff;
			g = ( data[ i ] >> green ) & 0xff;
			b = data[ i ] & 0xff;

			r_n = r_lookup[r];
			g_n = g_lookup[g];
			b_n = b_lookup[b];

			data[ i ] = a << alpha | r_n << red | g_n << green | b_n;
		}
	}

}//end class