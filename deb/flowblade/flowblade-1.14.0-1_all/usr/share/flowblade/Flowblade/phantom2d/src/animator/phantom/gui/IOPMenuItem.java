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

import javax.swing.JMenuItem;

//--- Used to select iops to add to flow.
public class IOPMenuItem extends JMenuItem
{
	public static final int ADJUSTMENT_LAYER_ITEM = 1;
	public static final int LAYER_EFFECT_ITEM = 2;
	public static final int LAYER_MASK_ITEM = 3;
	
	private String iopClassName;
	private int itemType;
	
	public IOPMenuItem( String title, String iopClassName, int itemType )
	{
		super( title );
		this.iopClassName = iopClassName;
		this.itemType = itemType;
	}

	public String getIopClassName(){ return iopClassName; }
	public int getItemType() { return itemType; }
	
}//end class