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
* A parameter that captures change of a integer value in time. Uses internally a float animated
* value <code>AnimatedValue</code> parameter, which is rounded using Math.round() to get output value.
*/
public class IntegerAnimatedValue extends AnimatedValue implements KeyFrameParam
{
	//---------------------------------------- CONSTRUCTORS
	public IntegerAnimatedValue(){}
	//--- AnimatedValue with given default value is created.
	/*
	public IntegerAnimatedValue( int defaultValue )
	{
		super( (float) defaultValue );
	}
	*/
	//--- Ceates AnimatedValue that has range of acceptable values.
	public IntegerAnimatedValue(  int defaultValue, int minValue, int maxValue  )
	{
		super( (float) defaultValue, (float) minValue, (float) maxValue  );
	}
	/*
	//--- AnimatedValue with given default value is created.
	public IntegerAnimatedValue( ImageOperation iop, int defaultValue )
	{
		super( iop, (float) defaultValue );
	}
	//--- Ceates AnimatedValue that has range of acceptable values.
	public IntegerAnimatedValue( ImageOperation iop, int defaultValue, int minValue, int maxValue  )
	{
		super( iop, (float) defaultValue, (float) minValue, (float) maxValue  );
	}
	*/
	//------------------------------------------------------- VALUE ACCESS METHODS
	public int getIntValue( int movieFrame )
	{
		float val = getValue( movieFrame );
		return (int) Math.round( (double) val );
	}

	/*
	//--- Returns values for other points than full frames. Used to achieve motion blur.
	public float getIntValue( float movieFrame )
	{
		float val = getValue( movieFrame );
		return (int) Math.round( (double) val );
	}
	*/
	//--- Set value in given frame. if keyframe exists set value, else create keyframe.
	public void setValue( int movieFrame, int value )
	{
		setValue( movieFrame, (float) value );
	}
	/*
	//--- Creates a new keyframe and places it in correct place.
	public void addKeyFrame( int movieFrame, int value )
	{
		addKeyFrame(  movieFrame, (float) value );
	}
	*/
	/**
	* Returns this as Param when handled as KeyFrameParam
	*/
	public Param getAsParam(){ return this; }

}//end class