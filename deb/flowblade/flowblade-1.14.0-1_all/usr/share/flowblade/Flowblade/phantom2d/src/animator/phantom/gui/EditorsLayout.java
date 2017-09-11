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
import java.awt.Toolkit;

public class EditorsLayout implements LayoutManager
{
	private int GAP = 0;
	private int RIGHT_GAP = 3;

	public EditorsLayout(){}

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
					case 0:
						c.setBounds(	GAP,
								GAP,
								containerSize.width - GAP * 2 - RIGHT_GAP,
								containerSize.height - GAP * 2 );
						Container bigEdit = (Container) c;
						Component editor = bigEdit.getComponent( 0 );//--- There will be one. This is needed for scroll panes to work
						editor.setPreferredSize( new Dimension( containerSize.width - GAP * 2 - 1 - RIGHT_GAP, containerSize.height - GAP * 2 - 1) );
						break;
					default:
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
	public Dimension preferredLayoutSize(Container cont )
	{ 
		return new Dimension( Toolkit.getDefaultToolkit().getScreenSize().width -  ContentPaneLayout.LEFT_WIDTH,
					Toolkit.getDefaultToolkit().getScreenSize().height - 2 );
	}
	public Dimension minimumLayoutSize(Container cont ){ return new Dimension( 100, 100 ); }

}//end class
