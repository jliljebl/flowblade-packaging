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

public class ButtonsRowLayout implements LayoutManager
{
	private int PREVIEW_BUTTONS_WIDTH = 350;

	public ButtonsRowLayout(){}

	//--- This all depends on order that components are added into container
    public void layoutContainer(Container cont)
	{
		synchronized ( cont.getTreeLock() )
		{
			Dimension containerSize = cont.getSize();
			int comps = cont.getComponentCount();
			for (int i = 0 ; i < comps ; i++)
			{
				Component c = cont.getComponent(i);
				switch( i )
				{
					//--- View buttons
					case 0:
						c.setBounds(	0,
										0,
										containerSize.width - PREVIEW_BUTTONS_WIDTH,
										containerSize.height);


						break;
					//--- Preview Buttons
					case 1:
					c.setBounds(	containerSize.width - PREVIEW_BUTTONS_WIDTH,
									0,
									PREVIEW_BUTTONS_WIDTH,
									containerSize.height);
						break;

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
