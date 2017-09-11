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
* An integer value parameter. Can be set to have a range of accepted values.
* @see <code>IntegerNumberEditor</code>, <code>IntegerValueSliderEditor</code>, <code>IntegerComboBox</code>
*/
public class IntegerParam extends Param
{
	//--- Value 
	private int value;
	private boolean hasRange = false;
	private int min = 0;
	private int max = 0;
	
	/**
	* Constructor with default value of zero.
	*/
	public IntegerParam()
	{
		this( 0 );
	}
	/**
	* Constructor with default value.
	* @param defaultValue an int
	*/
	public IntegerParam( int defaultValue )
	{ 
		value = defaultValue;
	}
	/**
	* Constructor with default value and range.
	* @param defaultValue an int
	* @param min Smallest value in the accepted range.
	* @param max Largest value in the acceted range.
	*/
	public IntegerParam( int defaultValue, int min, int max )
	{
		value = defaultValue;
		this.min = min;
		this.max = max;
		hasRange = true;
	}
	/**
	* Returns int value.
	*/
	public int get(){ return value; }
	/**
	* Set value.
	* @param value an int
	*/
	public void set( int newValue )
	{ 
		value = newValue;
		if( hasRange )
		{
			if( value < min ) value = min;
			if( value > max ) value = max;
		}
	}

	/**
	* Set accepted range. Calling this after parameter has been set into an editor will result in undefined behaviour.
	* @param min Smallest value in the accepted range.
	* @param max Largest value in the acceted range.
	*/
	/*
	public void setRange( int min_, int max_ )
	{
		min = min_;
		max = max_;
		hasRange = true;
	}
	*/
	/**
	* Returns true if parameter has range.
	* @return True if paramter has an accepted range set.
	*/
	public boolean hasRange(){ return hasRange; }

	/**
	* Returns min value.
	* @return Minimum value of accepted range or undefined value if no range set.
	*/
	public int getMinValue(){ return min; }
	/**
	* Returns max value.
	* @return Maximum value of accepted range or undefined value if no range set.
	*/
	public int getMaxValue(){ return max; }	
	
}//end class