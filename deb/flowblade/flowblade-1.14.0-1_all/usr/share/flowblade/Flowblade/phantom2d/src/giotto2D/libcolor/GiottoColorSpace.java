package  giotto2D.libcolor;

/*
    Copyright Janne Liljeblad 2008.

    This file is part of JFilters.

    JFilters is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JFilters is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JFilters.  If not, see <http://www.gnu.org/licenses/>.
*/

import giotto2D.core.GiottoMath;

//--- Colorspace functions converted from gimpcolorspace.c
public class GiottoColorSpace
{
	//--- Toneranges.
	//public static final int SHADOWS = 0;
	//public static final int MIDTONES = 1;
	//public static final int HIGHLIGHTS = 2;

	//--- Visual intensities of colors.
	public static final double RGB_INTENSITY_RED = 0.30;
	public static final double RGB_INTENSITY_GREEN = 0.59;
	public static final double RGB_INTENSITY_BLUE = 0.11;

	//--- Hue ranges
	/*
	public static final int ALL_HUES = 0;
	public static final int RED_HUES = 1;
	public static final int YELLOW_HUES = 2;
	public static final int CYAN_HUES = 3;
	public static final int BLUE_HUES = 4;
	public static final int MAGENTA_HUES = 5;
	*/
	public static void hsl_to_rgb( GiottoHSL hsl, GiottoRGB rgb)
	{
		if (hsl.s == 0.0 )
		{
			/*  achromatic case  */
			rgb.r = hsl.l;
			rgb.g = hsl.l;
			rgb.b = hsl.l;
		}
		else
		{
			double m1, m2;
			
			if (hsl.l <= 0.5)
				m2 = hsl.l * (1.0 + hsl.s);
			else
				m2 = hsl.l + hsl.s - hsl.l * hsl.s;
			
			m1 = 2.0 * hsl.l - m2;
			
			rgb.r = hsl_value( m1, m2, hsl.h * 6.0 + 2.0 );
			rgb.g = hsl_value( m1, m2, hsl.h * 6.0 );
			rgb.b = hsl_value( m1, m2, hsl.h * 6.0 - 2.0 );
		}
		
		//rgb.a = hsl.a;
	}

	public static double hsl_value( double n1, double n2, double hue )
	{
		double val;
		
		if (hue > 6.0)
			hue -= 6.0;
		else if (hue < 0.0)
			hue += 6.0;
		
		if (hue < 1.0)
			val = n1 + (n2 - n1) * hue;
		else if (hue < 3.0)
			val = n2;
		else if (hue < 4.0)
			val = n1 + (n2 - n1) * (4.0 - hue);
		else
			val = n1;
		
		return val;
	}
	/**
	* jimp_rgb_to_hsl_int:
	*
	* The function converts the valaues in JimpRGBInt to values in JimpHSLInt to the corresponding HLS
	* value with ranges:  H [0, 360], L [0, 255], S [0, 255].
	**/
	public static void rgb_to_hsl_int( GiottoRGBInt from, GiottoHSLInt to )
	{
		int    r, g, b;
		double h, s, l;
		int    min, max;
		int    delta;
	
		r = from.r;
		g = from.g;
		b = from.b;
	
		if (r > g)
		{
			max = GiottoMath.MAX (r, b);
			min = GiottoMath.MIN (g, b);
		}
		else
		{
			max = GiottoMath.MAX (g, b);
			min = GiottoMath.MIN (r, b);
		}
	
		l = (max + min) / 2.0;
	
		if (max == min)
		{
			s = 0.0;
			h = 0.0;
		}
		else
		{
			delta = (max - min);
		
		if (l < 128)
			s = 255 * (double) delta / (double) (max + min);
		else
			s = 255 * (double) delta / (double) (511 - max - min);
		
		if (r == max)
			h = (g - b) / (double) delta;
		else if (g == max)
			h = 2 + (b - r) / (double) delta;
		else
			h = 4 + (r - g) / (double) delta;
		
		h = h * 42.5;
	
		if (h < 0)
			h += 255;
		else if (h > 255)
			h -= 255;
		}
	
		to.h = (int) GiottoMath.ROUND (h);
		to.s = (int) GiottoMath.ROUND (s);
		to.l = (int) GiottoMath.ROUND (l);
	}
/*
	public static void hsl_to_rgb_int ( GiottoHSLInt from, GiottoRGBInt to )
	{
		double h, s, l;
		
		h = (double) from.h;
		s = (double) from.s;
		l = (double) from.l;
		
		if (s == 0)
		{

			to.r = (int) l;
			to.g = (int) l;
			to.b = (int) l;
		}
		else
		{
			double m1, m2;
			
			if (l < 128)
				m2 = (l * (255 + s)) / 65025.0;
			else
				m2 = (l + s - (l * s) / 255.0) / 255.0;
			
			m1 = (l / 127.5) - m2;

			to.r = hsl_value_int (m1, m2, h + 85);
			to.g = hsl_value_int (m1, m2, h);
			to.b = hsl_value_int (m1, m2, h - 85);
		}
	}
	*/

	public static int hsl_value_int( double n1, double n2, double hue)
	{
		double value;
		
		if (hue > 255)
			hue -= 255;
		else if (hue < 0)
			hue += 255;
		
		if (hue < 42.5)
			value = n1 + (n2 - n1) * (hue / 42.5);
		else if (hue < 127.5)
			value = n2;
		else if (hue < 170)
			value = n1 + (n2 - n1) * ((170 - hue) / 42.5);
		else
			value = n1;
		
		return (int) GiottoMath.ROUND( value * 255.0);
	}

	/*
	public static void rgb_to_hsv_int( GiottoRGBInt from, GiottoHSVInt to )
	{
		double  r, g, b;
		double  h, s, v;
		int     min;
		double  delta;
		
		r = (double) from.r;
		g = (double) from.g;
		b = (double) from.b;
	
		if (r > g)
		{
			v = GiottoMath.MAX(r, b);
			min = (int) GiottoMath.MIN(g, b);
		}
		else
		{
			v = GiottoMath.MAX (g, b);
			min = (int) GiottoMath.MIN (r, b);
		}
		
		delta = v - min;
		
		if (v == 0.0)
			s = 0.0;
		else
			s = delta / v;
		
		if (s == 0.0) h = 0.0;
		else
		{
			if (r == v)
				h = 60.0 * (g - b) / delta;
			else if (g == v)
				h = 120 + 60.0 * (b - r) / delta;
			else
				h = 240 + 60.0 * (r - g) / delta;
		
			if (h < 0.0)
				h += 360.0;
			if (h > 360.0)
				h -= 360.0;
		}
		
		to.h = (int) Math.round(h);
		to.s = (int) Math.round(s * 255.0);
		to.v = (int) Math.round(v);
	}
*/
	
	//--- gimp_hsv_to_rgb_int:
	//---
	//--- The arguments are in the following ranges:  H [0, 360], S [0, 255], V [0, 255].
/*
gimp_hsv_to_rgb_int (gint *hue,
		     gint *saturation,
		     gint *value)
*/
/*
	public static void hsv_to_rgb_int ( GiottoHSVInt from, GiottoRGBInt to )
	{
		double h, s, v, h_temp;
		double f, p, q, t;
		int i;
		
		if ( from.s == 0)
		{
			to.r = from.v;
			to.g = from.v;
			to.b = from.v;
		}
		else
		{
			h = from.h;
			s = from.s / 255.0;
			v = from.v / 255.0;
		
			if (h == 360)
				h_temp = 0;
			else
				h_temp = h;
			
			h_temp = h_temp / 60.0;
			i = (int) Math.floor (h_temp);
			f = h_temp - i;
			p = v * (1.0 - s);
			q = v * (1.0 - (s * f));
			t = v * (1.0 - (s * (1.0 - f)));
			
			switch (i)
			{
				case 0:
				to.r = (int)Math.round(v * 255.0);
				to.g = (int)Math.round (t * 255.0);
				to.b = (int)Math.round (p * 255.0);
				break;
			
				case 1:
				to.r = (int)Math.round (q * 255.0);
				to.g = (int)Math.round (v * 255.0);
				to.b = (int)Math.round (p * 255.0);
				break;
			
				case 2:
				to.r = (int)Math.round (p * 255.0);
				to.g = (int)Math.round (v * 255.0);
				to.b = (int)Math.round (t * 255.0);
				break;
			
				case 3:
				to.r = (int)Math.round (p * 255.0);
				to.g = (int)Math.round (q * 255.0);
				to.b = (int)Math.round (v * 255.0);
				break;
			
				case 4:
				to.r = (int)Math.round(t * 255.0);
				to.g = (int)Math.round (p * 255.0);
				to.b = (int)Math.round (v * 255.0);
				break;
			
				case 5:
				to.r = (int)Math.round (v * 255.0);
				to.g = (int)Math.round (p * 255.0);
				to.b = (int)Math.round (q * 255.0);
				break;
			}
		}
	}
*/
}//end class
