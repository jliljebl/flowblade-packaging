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

import animator.phantom.controller.EditorPersistance;

public class AnimatorFrameLayout implements LayoutManager
{
	private Component viewArea;
	private Component editorArea;
	private Component bottomRow;

	private static Dimension containerSize;

	public static int VIEW_H = EditorPersistance.getIntPref( EditorPersistance.LAYOUT_MID );
	public static int MIDDLE_ROW_HEIGHT = 0;
	public static int BOTTOM_ROW_HEIGHT = 44;
	public static int VIEW_PAD = 12;
	
	public AnimatorFrameLayout( 	Component viewArea,
									Component editorArea,
									Component bottomRow )
	{
		this.viewArea = viewArea;
		this.editorArea = editorArea;
		this.bottomRow = bottomRow;
	}

    public void layoutContainer(Container cont)
	{
		synchronized (cont.getTreeLock())
		{
			containerSize = cont.getSize();

			int comps = cont.getComponentCount();

			for (int i = 0 ; i < comps ; i++)
			{
				Component c = cont.getComponent(i);
				if( c == viewArea ) layoutViewPane(c);
				if( c == editorArea ) layoutEditorPane(c);
				if( c == bottomRow ) layoutBottomRow(c);
			}
		}
    }

	private void layoutViewPane( Component pane )
	{
		pane.setBounds(VIEW_PAD, 0, containerSize.width - VIEW_PAD, VIEW_H );
	}

	private void layoutEditorPane( Component pane )
	{
		pane.setBounds( 0, VIEW_H + MIDDLE_ROW_HEIGHT, containerSize.width - 6, containerSize.height - VIEW_H - MIDDLE_ROW_HEIGHT - BOTTOM_ROW_HEIGHT);
	}
	private void layoutBottomRow(Component pane )
	{
		int editorHeight = containerSize.height - VIEW_H - MIDDLE_ROW_HEIGHT - BOTTOM_ROW_HEIGHT;

		pane.setBounds( 0, VIEW_H + MIDDLE_ROW_HEIGHT + editorHeight, containerSize.width, BOTTOM_ROW_HEIGHT );
	}

	//--- noop
	public void addLayoutComponent(String name, Component comp) {}
	//--- noop
	public void removeLayoutComponent(Component comp) {}
	//--- Preferred size is as big as possible
	public Dimension preferredLayoutSize(Container target)
	{
		return new Dimension( Toolkit.getDefaultToolkit().getScreenSize().width -  ContentPaneLayout.LEFT_WIDTH - 10,
					Toolkit.getDefaultToolkit().getScreenSize().height - 2 );
	}
	//--- Minimum size of layout is minimum size of Container that is being layed out.
	public Dimension minimumLayoutSize(Container target) { return new Dimension( 500, 500 ); }

}//end class
