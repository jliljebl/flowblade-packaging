package animator.phantom.gui;

/*
    Copyright Janne Liljeblad

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

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class NodeSelectPanel extends JPanel implements MouseListener
{
	private Vector <NodeSelectItem> panels;
	private NodeSelectItem selected;
	private NodesPanel selectionListner;
	private boolean dragabble;
	private BufferedImage dragImg = null;
	private Color textColor =  GUIColors.MEDIA_ITEM_TEXT_COLOR;
	
	public NodeSelectPanel(NodesPanel selectionListner, boolean dragabble)
	{
		this.selectionListner = selectionListner;
		this.dragabble = dragabble;
	}

	public void init(Vector<String> items, BufferedImage dragImg )
	{
		this.dragImg = dragImg;
	
		removeAll();
		createPanels(items);
		addPanels();
		validate();
		repaint();
	}

	private void createPanels(Vector<String> items)
	{
		panels = new Vector<NodeSelectItem>();
		for( int i = 0; i < items.size(); i++ )
		{
			String name = items.elementAt( i );
			NodeSelectItem addPanel = new NodeSelectItem( name, dragabble, this );
			addPanel.setDragIcon( dragImg );
			addPanel.setColor( textColor );
			if( i == 0 ) addPanel.setFirst( true );
			
			panels.add( addPanel );
		}
	}
	
	private void addPanels()
	{
		JPanel p = new JPanel();
		p.setLayout( new BoxLayout( p,  BoxLayout.Y_AXIS ));
		for( NodeSelectItem panel : panels )
			p.add( panel );
		p.add( Box.createVerticalGlue() );

		add( p );
	}
	
	public int getSelectedIndex()
	{
		if( selected == null ) return -1;
		for( int i = 0; i < panels.size(); i++ )
		{
			NodeSelectItem panel = panels.elementAt( i );
			if( selected == panel ) return i;
		}
		
		return -1;
	}

	public void setSelected( int index )
	{
		selected =  panels.elementAt( index );
		selected.setSelected( true );
	}

	public void setColor( Color c ){ textColor = c; }
	
	//---------------------------------------- MOUSE EVENTS
	public void mouseClicked(MouseEvent e)
	{

		
		if (e.getClickCount() == 2 && !e.isConsumed()) {
		     e.consume();
		     System.out.println("double in panel");
		     selectionListner.tableDoubleClicked(this);
		}
		
		NodeSelectItem clickSource = ( NodeSelectItem ) e.getSource();
		if( clickSource == null ) return;
		else 
		{
			if ( selected != null )
			{
				selected.setSelected( false );
				selected.repaint();
			}
			selected = clickSource;
			selected.setSelected( true );
			clickSource.repaint();
			selectionListner.selectionChanged( this );
		}
	}
	
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e)
	{
		NodeSelectItem clickSource = ( NodeSelectItem ) e.getSource();
		if( clickSource == null ) return;
		else 
		{
			if ( selected != null )
			{
				selected.setSelected( false );
				selected.repaint();
			}
			selected = clickSource;
			selected.setSelected( true );
			clickSource.repaint();
			selectionListner.selectionChanged( this );
		}

	}
	public void mouseReleased(MouseEvent e){}

}//end class
