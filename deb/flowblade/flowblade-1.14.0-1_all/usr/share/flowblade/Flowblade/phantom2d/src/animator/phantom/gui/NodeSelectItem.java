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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import animator.phantom.controller.Application;
import animator.phantom.controller.GUIComponents;
import animator.phantom.controller.ParamEditController;

public class NodeSelectItem extends JPanel implements MouseListener
{
	private boolean isSelected = false;
	private boolean draggable;
	private JLabel nameLabel;

	private static final int HEIGHT = 22;
	private static final int WIDTH = Application.SMALL_WINDOW_WIDTH - 40;
	private static final Dimension COMP_SIZE = new Dimension( WIDTH, HEIGHT );
	private static final Dimension NAME_SIZE = new Dimension( WIDTH, HEIGHT );
	private static final Dimension PAD = new Dimension( 5, 0 );

	private boolean isFirst = false;
	private boolean isPressed = false;
	private boolean dragOn = false;

	private Color textColor =  GUIColors.MEDIA_ITEM_TEXT_COLOR;
	
	private BufferedImage dragImg;
	
	public NodeSelectItem( String itemName, boolean draggable, MouseListener l )
	{
		this.draggable = draggable;

		nameLabel = new JLabel( itemName );
		nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		nameLabel.setPreferredSize( NAME_SIZE );
		nameLabel.setFont( GUIResources.BOLD_FONT_11 );

		setLayout( new BoxLayout( this , BoxLayout.X_AXIS) );
		add( Box.createRigidArea( PAD ) );
		add( nameLabel );
		add( Box.createHorizontalGlue() );
		add( Box.createRigidArea( PAD ) );

		setBackground( GUIColors.BIN_BG );
		setPreferredSize( COMP_SIZE );
		setMinimumSize( COMP_SIZE );
		addMouseListener( l );
		addMouseListener( this );

		setBackground( GUIColors.MEDIA_ITEM_BG );
	}

	public void setDragIcon( BufferedImage dragImg )
	{
		this.dragImg = dragImg;
	}
	
	public void setSelected( boolean isSelected )
	{
		this.isSelected = isSelected;
		if( isSelected) 
			setBackground( GUIColors.MEDIA_ITEM_SELECTED_BG );
		else  
			setBackground( GUIColors.MEDIA_ITEM_BG );
	}

	public void setName( String newName )
	{
		nameLabel.setText( newName );
	}

	public void setColor( Color c ){ textColor = c; }
	public boolean isSelected(){ return isSelected; }	
	public void setMouseListener( MouseListener l ){ addMouseListener( l ); }
	public void setFirst( boolean value ){ isFirst = value; }

	public void paintComponent( Graphics g )
	{
		nameLabel.setForeground( textColor );

		super.paintComponent( g );
		g.setColor( GUIColors.lineBorderColor );
		Dimension d = getSize();
		g.drawLine( 0, 0, 0, d.height - 1 );
		g.drawLine( 0, d.height - 1, d.width - 1, d.height - 1 );
		g.drawLine( d.width - 1, d.height - 1, d.width - 1, 0 );
		if( isFirst ) g.drawLine( 0, 0, d.width - 1, 0 );
	}

	public void mouseClicked(MouseEvent e)
	{
		requestFocusInWindow();
	}
	
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e)
	{
		if( isPressed && draggable )
		{

			Point hotSpot = new Point(0,0);  
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Cursor cursor = toolkit.createCustomCursor( dragImg, hotSpot, "dnd" );
			GUIComponents.animatorFrame.setCursor(cursor);
			dragOn = true;
		}
	}

	public void mousePressed(MouseEvent e)
	{
		requestFocusInWindow();
		setSelected( true );
		isPressed = true;
	}

	public void mouseReleased(MouseEvent e)
	{

		isPressed = false;

		Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		GUIComponents.animatorFrame.setCursor(normalCursor);

		Point pt = new Point(GUIComponents.filterStackTablePane.getLocation());
		SwingUtilities.convertPointToScreen(pt, GUIComponents.filterStackTablePane);

		// Add to filter stack if drop on top of it.
		if( dragOn && draggable )
		{
			if( e.getXOnScreen() > pt.x && e.getXOnScreen() < pt.x + GUIComponents.filterStackTablePane.getWidth()
				&& e.getYOnScreen() > pt.y && e.getYOnScreen() < pt.y + GUIComponents.filterStackTablePane.getHeight() )
			{
				//ParamEditController.addSelectedIOPToFilterStack();
			}
		}
		dragOn = false;
	}

}//end class
