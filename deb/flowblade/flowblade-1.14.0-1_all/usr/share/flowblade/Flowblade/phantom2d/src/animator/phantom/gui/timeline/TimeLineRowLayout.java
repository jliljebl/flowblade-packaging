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

import animator.phantom.gui.AnimFrameGUIParams;

public class TimeLineRowLayout implements LayoutManager
{
	//--- Components
	private Component leftComponent;
	private Component rightComponent;
	
	//--- Constructor
	public TimeLineRowLayout( Component leftComponent, Component rightComponent ) 
	{
		this.leftComponent = leftComponent;
		this.rightComponent = rightComponent;
	}

	//--- Layout is performed here
    	public void layoutContainer(Container cont) 
	{
		synchronized ( cont.getTreeLock() )
		{
			Dimension containerSize = cont.getSize();

			int comps = cont.getComponentCount();
			for (int i = 0 ; i < comps ; i++) 
			{
				Component c = cont.getComponent(i);
				if( c == leftComponent )
				{
					c.setBounds( 	0,
							0,
						 	AnimFrameGUIParams.TE_LEFT_COLUMN_WIDTH,
							containerSize.height );
				}
				if( c == rightComponent )
				{
					c.setBounds( 	AnimFrameGUIParams.TE_LEFT_COLUMN_WIDTH, 
							0,
							containerSize.width - AnimFrameGUIParams.TE_LEFT_COLUMN_WIDTH,
							containerSize.height );
				}
			}
		}
    	}

	//--- noop
	public void addLayoutComponent(String name, Component comp) {}
	//--- noop
	public void removeLayoutComponent(Component comp) {}
	//--- send rubbish and ignore it
	public Dimension preferredLayoutSize( Container cont ){ return new Dimension(1, 1 ); }
	//--- send rubbish and ignore it
	public Dimension minimumLayoutSize( Container cont ){ return new Dimension(1, 1 ); }

}//end class
