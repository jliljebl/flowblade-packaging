package animator.phantom.gui;

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

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.filechooser.FileFilter;

import animator.phantom.controller.AppUtils;

public class SelectFileFilter extends FileFilter 
{
    private Hashtable<String, SelectFileFilter> filters = null;

    public SelectFileFilter(){}

	public boolean accept(File f) 
	{
		if(f != null) 
		{
			if(f.isDirectory()) 
			{
				return true;
			}
		
			String extension = AppUtils.getExtension(f);
			if(extension != null && filters.get( extension ) != null )
			{
				return true;
			}
		}
		return false;
	}

	public String getDescription()
	{
		String desc = new String();
		Enumeration<String> e = filters.keys();
		boolean notFirst = false;
		while( e.hasMoreElements() )
		{
			if( notFirst ) desc += ", ";
			if( !notFirst ) notFirst = true;
			desc +=  (String) e.nextElement();
		}
		return desc;
	}

	public void addExtension(String extension) 
	{
		if(filters == null) filters = new Hashtable<String, SelectFileFilter>(5);
		filters.put(extension.toLowerCase(), this);
	}

}//end class
