package animator.phantom.gui.modals;

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

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import animator.phantom.gui.PHButtonFactory;


public class PHButtonRow extends JPanel
{
	private Vector<JButton> buttons = new Vector<JButton>();
	private static final int BUTTON_GAP = 8;

	/*
	public PHButtonRow( String[] buttonTexts, int width )
	{
		this( buttonTexts, width, false,  0, -1 );
	}

	public PHButtonRow( String[] buttonTexts, int width, boolean equalWidths )
	{
		this( buttonTexts, width, equalWidths, 0, -1 );
	}
	*/
	public PHButtonRow( String[] buttonTexts, int width, int minWidth, boolean equalWidths )
	{
		this( buttonTexts, width, equalWidths, 0, minWidth );
	}

	public PHButtonRow( String[] buttonTexts, int width, boolean equalWidths, int endPad )
	{
		this( buttonTexts, width, equalWidths, endPad, -1);
	}

	public PHButtonRow( String[] buttonTexts, int width, boolean equalWidths, int endPad, int minWidth )
	{
		int buttonWidth = 0;
		if( equalWidths )
		{
			buttonWidth = getMaxWidth( buttonTexts );
			if( minWidth != -1 && buttonWidth < minWidth )
			{
				buttonWidth = minWidth;
			}
		}

		setLayout( new BoxLayout( this, BoxLayout.X_AXIS ));
		add( Box.createHorizontalGlue() );
		for( int i = 0; i < buttonTexts.length; i++ )
		{
			JButton addButton;
			if( equalWidths || minWidth != -1 )
			{
				addButton = PHButtonFactory.getButton( buttonTexts[ i ], buttonWidth );
			}
			else
			{
				addButton = PHButtonFactory.getButton( buttonTexts[ i ] );
			}
			
			buttons.add( addButton );
			add( addButton );
			if( i != buttonTexts.length -1 ) add( Box.createRigidArea( new Dimension( BUTTON_GAP, 0 ) ) );
		}
		if( endPad != 0 )
			add( Box.createRigidArea( new Dimension( endPad, 0 ) ) );

		//int rowWidth = getRowWitdh( buttonTexts, equalWidths );
		//rowWidth = rowWidth + 2;
		//if( rowWidth > width )
		//	width = rowWidth;
		//setPreferredSize( new Dimension( width + 10, ROW_HEIGHT ) );
	}

	private int getMaxWidth( String[] buttonTexts )
	{
		int mWidth = 0;
		for( int i = 0; i < buttonTexts.length; i++ )
		{
			int w = PHButtonFactory.getPreferredWidth( buttonTexts[ i ] );

			if( w > mWidth )
				mWidth = w;
		}

		return mWidth;
	}

	public void setActionListener( ActionListener listener )
	{
		for( JButton b : buttons )
			b.addActionListener( listener );
	}

	//public JButton getButton( int index ){ return buttons.elementAt( index ); }

	public int buttonIndex( JButton button )
	{
		for( int i = 0; i < buttons.size(); i++ )
		{
			JButton b = buttons.elementAt( i );
			if( b == button ) return i;
		}
		return -1;
	}

}//end class