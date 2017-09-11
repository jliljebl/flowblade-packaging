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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ColorSelect extends JFrame implements ActionListener 
{
	private JColorChooser tcc;
	private PHButtonRow buttons;

	public ColorSelect() 
	{
		super("Select Color");
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout( p, BoxLayout.Y_AXIS));

		tcc = new JColorChooser( Color.white );
		tcc.setBorder( BorderFactory.createEmptyBorder( 12, 12, 0, 12 ) );
		tcc.setPreviewPanel( new JPanel() );
		
		String[] texts = { "Cancel","OK" };
		buttons = new PHButtonRow( texts, 500, true, 10 );
		buttons.setActionListener( this );

		p.add( tcc );
		p.add( Box.createRigidArea( new Dimension( 0, 12 ) ) );
		p.add( buttons );
		p.add( Box.createRigidArea( new Dimension( 0, 12 ) ) );
		getContentPane().add( p );
		pack();
	}

	public void setColor( Color c )
	{
		tcc.setColor( c );
	}

	public void actionPerformed( ActionEvent e )
	{
		int response = buttons.buttonIndex( (JButton) e.getSource() );
		Color selectedColor = tcc.getColor();
		if( response == 0 )
			selectedColor = null;//cancel, null signals that
		
		DialogUtils.colorSelected( selectedColor );
	}

}//end class
