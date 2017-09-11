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
* A boolean value parameter.
* @see <code>BooleanComboBox</code>, <code>CheckBoxEditor</code>
*/
public class BooleanParam extends Param
{
	private boolean value;

	/**
	* No params constructor.
	*/
	public BooleanParam(){}

	/**
	* Constructor with default value.
	* @param  defaultValue Default value of paramter.
	*/
	public BooleanParam( boolean defaultValue )
	{
		value = defaultValue;
	}

	/**
	* Constructor with default value and user displayed name. When this parameter is placed in an editor, name might be changed. 
	* @param defaultValue Default value of paramter.
	* @param pName Paramter name.
	*/
	public BooleanParam( boolean defaultValue, String pName )
	{
		value = defaultValue;
		String name = getXMLName( pName );
		setParamName( name );
	}

	/**
	* Sets parameter value.
	* @param  newValue New value of paramter.
	*/
	public void set( boolean newValue ){ value = newValue; }

	/**
	* Gets parameter value.
	* @return Paramter value.
	*/
	public boolean get(){ return value; }

}//end class