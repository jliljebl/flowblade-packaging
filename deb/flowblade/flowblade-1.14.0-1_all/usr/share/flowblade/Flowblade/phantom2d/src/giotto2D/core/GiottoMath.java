package giotto2D.core;

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

import java.util.Random;

//--- Math macros from gimpmath.h;.
//--- NOTE: if these are in innerloops using these will introduce avoidable overhead.
public class GiottoMath
{
	//--- Returns value squared.
	//public static double SQR( double d ){ return d * d; }

	//--- Returns value forced to be in defined range.
	/*
	public static int CLAMP255( int x )
	{
		if( x < 0 ) return 0;
		if( x > 255 ) return 155;
		return x;
	}
	*/
	//--- Returns value forced to be in defined range.
	public static int CLAMP( int x, int low, int high )
	{
		if( x < low ) return low;
		if( x > high ) return high;
		return x;
	}

	//--- Returns value forced to be in defined range.
	public static double CLAMP( double x, double low, double high )
	{
		if( x < low ) return low;
		if( x > high ) return high;
		return x;
	}

	public static int MAX( int v1, int v2 )
	{
		if( v1 > v2 ) return v1;
		return v2;
	}

	public static int MIN( int v1, int v2 )
	{
		if( v1 < v2 ) return v1;
		return v2;
	}

	public static double ROUND( double x ) 
	{
		return Math.round( x );
	}

	public static double g_rand_double_range( double low, double high, Random random )
	{
		double rand = random.nextDouble();
		double range = high - low;
		double value = range * rand;
		return low + value;
	}

	public static int g_rand_int_range( int low, int high, Random random )
	{
		double rand = random.nextDouble();
		int range = high - low;
		double value = range * rand;
		return low + (int) Math.floor( value );
	}

}//end class