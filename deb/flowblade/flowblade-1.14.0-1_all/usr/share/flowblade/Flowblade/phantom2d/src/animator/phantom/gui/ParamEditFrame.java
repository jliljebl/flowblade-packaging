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

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import animator.phantom.controller.Application;
import animator.phantom.paramedit.ParamEditResources;
import animator.phantom.paramedit.panel.ParamEditPanel;
import animator.phantom.renderer.ImageOperation;

//--- GUI area that holds edit panel, switches, outputs number selector and name label.
public class ParamEditFrame extends JPanel
{
	//--- The ImageOperation that has is being edited
	private ImageOperation editIOP;

	private static JPanel emptyPanel;

	private static final int EMPTY_LABEL_HEIGHT = 200;
	private static final int NAME_LABEL_HEIGHT = 30;
	private static final int NODE_WIDTH = 60;
	private static final int HEIGHT_PAD = 0;
	private static final int AREA_BORDER_GAP = 5;

	public ParamEditFrame()
	{
		setLayout(new BoxLayout( this, BoxLayout.Y_AXIS));
		editIOP = null;
		buildEmptyPanel();
		displayGUI();
		setPreferredSize( new Dimension( Application.SMALL_WINDOW_WIDTH - 10, Application.getParamEditHeight() - HEIGHT_PAD ));
		
		Border b1 = BorderFactory.createLineBorder( GUIColors.frameBorder );
		Border b2 = BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder( 0, AREA_BORDER_GAP + 4, AREA_BORDER_GAP + 2, AREA_BORDER_GAP), b1 );
		
		setBorder( b2 );
	}

	//--- The ImageOperation being edited has been changed.
	public void displayGUI()
	{
		removeAll();
		if( editIOP == null )
		{
			 add( emptyPanel );
		}
		else
		{
			JPanel editPanel = editIOP.reGetEditFrame();
			
			add( editPanel );
			editPanel.setVisible( true );
		}

		validate();
		repaint();
	}

	//--- Build panel for empty selection
	private void buildEmptyPanel()
	{
		JLabel emptyLabel = new JLabel( "");
		emptyLabel.setSize( new Dimension( ParamEditResources.PARAM_COLUMN_WIDTH * 2 , EMPTY_LABEL_HEIGHT ) );

		JLabel name = new JLabel();
		name.setText( "" );
		name.setPreferredSize( new Dimension( ParamEditResources.PARAM_COLUMN_WIDTH * 2 - NODE_WIDTH, NAME_LABEL_HEIGHT ));
		name.setFont( GUIResources.BASIC_FONT_12 );
		name.setHorizontalAlignment( SwingConstants.CENTER );

		JPanel namePanel = new JPanel();
		namePanel.setLayout(new BoxLayout( namePanel, BoxLayout.X_AXIS));
		namePanel.add( Box.createRigidArea( new Dimension( 5, 0 ) ) );
		namePanel.add( Box.createHorizontalGlue() );
		namePanel.add( name );
		namePanel.add( Box.createHorizontalGlue() );

		emptyPanel = new JPanel();
		emptyPanel.add( Box.createRigidArea( new Dimension( 0, 5 ) ) );
		emptyPanel.add( namePanel );
		emptyPanel.add( Box.createRigidArea( new Dimension( 0, 5 ) ) );
		emptyPanel.add( emptyLabel );
	}

	//--- The ImageOperation being edited has been changed.
	public void display( ImageOperation editIOP )
	{
		//--- Capture new edit iop
		this.editIOP = editIOP;
		//--- Display GUI with new iop.
		displayGUI();
	}
	
	//--- Current frame in time line editing has been changed.
	public void currentFrameChanged()
	{
		//--- Check if display possible.
		if( editIOP == null ) 
			return;
		//--- Display framevalues
		ParamEditPanel editPanel = editIOP.getEditPanel();
		editPanel.currentFrameChanged();
	}

	public void undoDone()
	{
		if( editIOP == null ) 
			return;
		ParamEditPanel editPanel = editIOP.getEditPanel();
		editPanel.undoDone();
	}
	
	//--- Get Image Operation being edited.
	public ImageOperation getIOP(){ return editIOP; }

}//end class 