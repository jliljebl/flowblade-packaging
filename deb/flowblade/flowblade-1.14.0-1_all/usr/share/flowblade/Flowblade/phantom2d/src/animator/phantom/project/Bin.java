package animator.phantom.project;

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

import java.util.Vector;

import animator.phantom.renderer.FileSource;

public class Bin
{
	private String name;
	private Vector<FileSource> fileSources = new Vector<FileSource>();

	public Bin(){}

	public Bin( String name )
	{
		this.name = name;
	}

	public int getSize(){ return fileSources.size(); }
	//--- Get and set for name.
	public void setName( String name ){ this.name = name; }
	public String getName(){ return name; }
	//--- File management.
	public void addFileSource( FileSource f ){ fileSources.add( f ); }
	public void addFileSourceVector( Vector<FileSource> vec ){ fileSources.addAll( vec ); }
	public void removeFileSource( FileSource f ){ fileSources.remove( f ); }
	public void removeFileSourceVector( Vector<FileSource> vec){ fileSources.removeAll( vec ); }
	public Vector<FileSource> getFileSources(){ return fileSources; }

}//end class