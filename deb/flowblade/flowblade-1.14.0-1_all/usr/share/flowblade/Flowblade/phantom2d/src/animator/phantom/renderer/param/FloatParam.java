package animator.phantom.renderer.param;

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


/**
* A float value parameter. Can be set to have a range of accepted values.
* @see <code>FloatNumberEditor</code> 
*/
public class FloatParam extends Param
{
	//--- Value 
	private float value;
	private boolean hasRange = false;
	private float min = 0;
	private float max = 0;
	
	public FloatParam()
	{
		this( 0 );
	}
	public FloatParam( float defaultValue )
	{ 
		value = defaultValue;
	}
	public FloatParam( float defaultValue, float min, float max )
	{
		value = defaultValue;
		this.min = min;
		this.max = max;
		hasRange = true;
	}
	public float get(){ return value; }
	public void set( float newValue )
	{ 
		value = newValue;
		if( hasRange )
		{
			if( value < min ) value = min;
			if( value > max ) value = max;
		}
	}

	/*
	public void setRange( float min_, float max_ )
	{
		min = min_;
		max = max_;
		hasRange = true;
	}

	public boolean hasRange(){ return hasRange; }
	*/
	public float getMinValue(){ return min; }
	public float getMaxValue(){ return max; }	
	
}//end class