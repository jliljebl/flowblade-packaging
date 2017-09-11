package animator.phantom.paramedit;

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
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import animator.phantom.controller.UpdateController;
import animator.phantom.gui.GUIResources;
import animator.phantom.gui.modals.DialogUtils;
import animator.phantom.renderer.param.ColorParam;
import animator.phantom.renderer.param.ColorSelectTarget;

/**
* A GUI editor component for setting the value of a <code>ColoParam</code> parameter. Pressing button opens a color chooser dialog.
*/
public class ParamColorSelect extends JPanel implements ActionListener, UndoListener, ColorSelectTarget
{
	private ColorParam editColor;
	private JButton colorButton;

	private int COLOR_DISPLAY_WIDTH = 30;
	private int COLOR_DISPLAY_HEIGHT = ParamEditResources.PARAM_ROW_HEIGHT;

	/**
	* Constructor with parameter to be edited and label text.
	* @param editColor <code>ColoParam</code> that is edited with this editor.
	* @param info Displayed name for editor and parameter.
	*/
	public ParamColorSelect( ColorParam editColor, String info )
	{
		this.editColor = editColor;

		JLabel textLabel = new JLabel(info);
		textLabel.setFont( GUIResources.PARAM_EDIT_LABEL_FONT );
		editColor.setParamName( info );

		ImageIcon colorIcon = getColorIcon( editColor.get() );
		colorButton = new JButton( colorIcon );
		colorButton.setPreferredSize( new Dimension( COLOR_DISPLAY_WIDTH, COLOR_DISPLAY_HEIGHT ) );
		colorButton.addActionListener(this);

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout( new BoxLayout( leftPanel, BoxLayout.X_AXIS) );
		leftPanel.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );
		leftPanel.add( Box.createHorizontalGlue() );
		leftPanel.add( textLabel );
		leftPanel.add( Box.createRigidArea( new Dimension( ParamEditResources.PARAM_MID_GAP, 6 ) ) );

		JPanel rightPanel = new JPanel();		
		rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.X_AXIS) );
		rightPanel.setPreferredSize( ParamEditResources.EDIT_ROW_HALF_SIZE );
		rightPanel.add( colorButton );
		rightPanel.add( Box.createHorizontalGlue() );

		setLayout( new BoxLayout( this, BoxLayout.X_AXIS) );
		add( leftPanel );
		add( rightPanel );
		setPreferredSize( ParamEditResources.EDIT_ROW_SIZE );
		setMaximumSize( ParamEditResources.EDIT_ROW_SIZE );
	}

	private ImageIcon getColorIcon( Color c )
	{
		BufferedImage iconImg = new BufferedImage( COLOR_DISPLAY_WIDTH, COLOR_DISPLAY_HEIGHT, BufferedImage.TYPE_INT_ARGB );
		Graphics g = iconImg.getGraphics();
		g.setColor( c );
		g.fillRect( 0,0,COLOR_DISPLAY_WIDTH,COLOR_DISPLAY_HEIGHT);
		g.dispose();
		return new ImageIcon( iconImg );
	}

	/**
	* Called after undo has been done to set displayed color to current state.
	*/
	public void undoDone()
	{
		colorButton.setIcon( getColorIcon( editColor.get() ) );
	}

	/**
	* Handles button presses.
	*/
	public void actionPerformed(ActionEvent e)
	{
		DialogUtils.displayColorSelect( this, editColor.get() );
	}
	
	public void colorSelected( Color newColor )
	{
		if( newColor == null ) 
			return;
		else 
			editColor.set(newColor);

		colorButton.setIcon( getColorIcon( editColor.get() ) );
		UpdateController.valueChangeUpdate( UpdateController.PARAM_EDIT );
		editColor.registerUndo();
	}

}//end class