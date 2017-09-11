package giotto2D.libcolor;

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

//--- Fron GIMP
//--- Color in Hue-Saturation-Luminance space
public class GiottoHSL
{
	public double h;//--range 0 -> 6
	public double s;
	public double l;
	public double a;

	public GiottoHSL(){}

	public GiottoHSL( double h, double s, double l, double a )
	{
		this.h  = h / 360.0;
		this.s  = s;
		this.l  = l;
		this.a  = a;
	}

}//end class