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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

public class TwoItemRowLayout implements LayoutManager
{
	private int leftWidth;
	private int rightWidth;
	private boolean setZeroChild;
	private int midGap = 0;// this is subtracted from left Component space

	public TwoItemRowLayout(int leftWidth, int rightWidth, int midGap, boolean setZeroChild)
	{
		this.leftWidth = leftWidth; // -1 == expand
		this.rightWidth = rightWidth;// -1 == expand
		this.midGap = midGap;
		this.setZeroChild = setZeroChild;
	}

	//--- This all depends on order that components are added into container
    	public void layoutContainer(Container cont) 
	{
		synchronized ( cont.getTreeLock() )
		{
			Dimension containerSize = cont.getSize();
			int comps = cont.getComponentCount();
			for (int i = 0 ; i < comps ; i++) 
			{
				int x = 0;
				int w = 0;
				int y = 0;
				int h = containerSize.height;
				Component c = cont.getComponent(i);
				switch( i )
				{

					case 0: // left panel
						if( leftWidth != -1 && rightWidth == -1 )
						{
							x = 0;
							w = leftWidth - midGap;
	
						}
						else if( leftWidth == -1 && rightWidth != -1 )
						{
							x = 0;
							w = containerSize.width - rightWidth - midGap;
						}
						else
						{
							x = 0;
							w = (containerSize.width - midGap) / 2;
						}


						break;

					case 1: // right panel
						if( leftWidth != -1 && rightWidth == -1 )
						{
							x = leftWidth;
							w = containerSize.width - leftWidth;
	
						}
						else if( leftWidth == -1 && rightWidth != -1 )
						{
							x = containerSize.width - rightWidth;
							w = rightWidth;
						}
						else
						{
							x = containerSize.width / 2 + (midGap / 2);
							w = (containerSize.width - midGap) / 2;
						}
						break;
					default:
						break;

				}
				c.setBounds( x, y, w, h );
				if( setZeroChild ) // scrollabe viewports need this to work as expected
				{
					Container c1 = (Container) c;
					Component child1 = c1.getComponent( 0 );
					child1.setPreferredSize( new Dimension( w, h ));
				}			
			}
		}
    	}

	//---------------------------------------------------- LAYOUT MANAGER METHODS
	//--- noop
	public void addLayoutComponent(String name, Component comp) {}
	//--- noop
	public void removeLayoutComponent(Component comp) {}
	public Dimension preferredLayoutSize(Container cont ){ return new Dimension( 100, 100 ); }
	public Dimension minimumLayoutSize(Container cont ){ return new Dimension( 100, 100 ); }

}//end class
