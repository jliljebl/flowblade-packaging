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

public class LayerCompositorContentPaneLayout implements LayoutManager
{
	public static int LEFT_WIDTH = 500;
	private static int FLOW_BUTTONS_HEIGHT = 44;
	private static int INFO_LABEL_HEIGHT = 30;
	public static int MEDIA_PANEL_HEIGHT = 200;
	
	//--- Constructor
	public LayerCompositorContentPaneLayout(){}

	//--- Layout is performed here.
	//--- NOTE: This all depends on that components are added into container in case order.
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
					//--- Flow view
					case 0:
						c.setBounds( 	6,
										INFO_LABEL_HEIGHT,
										LEFT_WIDTH - 6,
										(containerSize.height - FLOW_BUTTONS_HEIGHT - INFO_LABEL_HEIGHT - 2) );
						break;
					//--- Flow buttons
					case 1: 
						c.setBounds( 	0,
										(containerSize.height - FLOW_BUTTONS_HEIGHT),
										LEFT_WIDTH,
										FLOW_BUTTONS_HEIGHT );
						break;
					//--- Big editors
					case 2:
						c.setBounds( 	LEFT_WIDTH + 1,
										0,
										containerSize.width - LEFT_WIDTH - 1,
										containerSize.height );
						break;
					//--- Project info label
					case 3:
							c.setBounds( 	6,
											0,
											LEFT_WIDTH - 6,
											INFO_LABEL_HEIGHT);
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
		return Toolkit.getDefaultToolkit().getScreenSize();
	}
	public Dimension minimumLayoutSize(Container cont ){ return new Dimension( 1152, 768 ); }

}//end class
