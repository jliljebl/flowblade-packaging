package animator.phantom.gui.timeline;

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

import animator.phantom.controller.Application;
import animator.phantom.gui.AnimFrameGUIParams;
import animator.phantom.gui.AnimatorFrameLayout;

public class TimeLineEditorLayout implements LayoutManager
{
	private int GAP = AnimFrameGUIParams.OUT_BORDER_WIDTH + 2;
	private int UP_GAP = 2;
	private int SCALE_INSET = 0;
	private int DOWN_INSET = 2;
	private int SMALL_WINDOW_WIDTH;

	public TimeLineEditorLayout( int SMALL_WINDOW_WIDTH )
	{
		this.SMALL_WINDOW_WIDTH = SMALL_WINDOW_WIDTH;
	}

	//--- This all depends on that components are added in right order.
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
					//--- scale
					case 0:
						c.setBounds( 	GAP,
								UP_GAP,
								containerSize.width - ( GAP * 2 ) - SCALE_INSET,
								AnimFrameGUIParams.TE_SCALE_DISPLAY_HEIGHT );
						break;
					//--- editor
					case 1:
						c.setBounds( 	GAP,
								AnimFrameGUIParams.TE_SCALE_DISPLAY_HEIGHT + UP_GAP,
								containerSize.width - ( GAP * 2 ),
								containerSize.height - AnimFrameGUIParams.TE_SCALE_DISPLAY_HEIGHT - UP_GAP - DOWN_INSET );
					default:
						break;

				}
			}
		}
    	}

	//--- noop
	public void addLayoutComponent(String name, Component comp) {}
	//--- noop
	public void removeLayoutComponent(Component comp) {}

	public Dimension preferredLayoutSize(Container cont ){ return new Dimension( 	
											Application.getUsableScreen().width 
											- SMALL_WINDOW_WIDTH
											- AnimFrameGUIParams.TE_WIDTH_PAD,
											Application.getUsableScreen().height
											- AnimatorFrameLayout.VIEW_H
											- AnimFrameGUIParams.TE_HEIGHT_PAD ); }

	public Dimension minimumLayoutSize(Container cont ){ return new Dimension( 100, 100 ); }

}//end class
