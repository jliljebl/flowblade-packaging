package animator.phantom.gui.view.component;

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import animator.phantom.controller.EditorsController;
import animator.phantom.controller.GUIComponents;
import animator.phantom.gui.GUIResources;
import animator.phantom.gui.view.editlayer.ViewEditorLayer;
import  animator.phantom.gui.timeline.TCDisplay;


public class ViewControlButtons extends JPanel implements ActionListener
{
	private ViewSizeSelector viewSizeSelect = new ViewSizeSelector();
	private LayerSelector layerSelect = new LayerSelector();

	public JToggleButton editorUpdates = new JToggleButton( GUIResources.getIcon(GUIResources.showViewEditUpdates ) );
	public JToggleButton viewFlow = new JToggleButton(  GUIResources.getIcon(GUIResources.viewFlow ) );
	public JToggleButton viewSelected = new JToggleButton(  GUIResources.getIcon( GUIResources.viewSelected ) );
	public JToggleButton viewColor = new JToggleButton(  GUIResources.getIcon( GUIResources.viewImagePressed ) );
	public JToggleButton allBoxes = new JToggleButton(  GUIResources.getIcon( GUIResources.allBoxes ) );

	public JLabel renderClock = new JLabel();
	public ImageIcon clockIcon = GUIResources.getIcon( GUIResources.renderClockTheme );

	public ModeButton move
		= new ModeButton(  GUIResources.getIcon( GUIResources.move ), ViewEditorLayer.MOVE_MODE );
	public ModeButton rotate
		= new ModeButton(  GUIResources.getIcon( GUIResources.rotate ), ViewEditorLayer.ROTATE_MODE );
	public ModeButton kfEdit
		= new ModeButton(  GUIResources.getIcon( GUIResources.kfEdit ), ViewEditorLayer.KF_EDIT_MODE );
	public ModeButton kfAdd
		= new ModeButton(  GUIResources.getIcon( GUIResources.kfAdd ), ViewEditorLayer.KF_ADD_MODE );
	public ModeButton kfRemove
		= new ModeButton(  GUIResources.getIcon( GUIResources.kfRemove ), ViewEditorLayer.KF_REMOVE_MODE );
	public ModeButton pickColor
		= new ModeButton(  GUIResources.getIcon( GUIResources.pickColor ), ViewEditorLayer.PICK_COLOR_MODE );
	public ModeButton custom1
		= new ModeButton(  GUIResources.getIcon( GUIResources.customButton ), ViewEditorLayer.CUSTOM_EDIT_MODE_1 );
	public ModeButton custom2
		= new ModeButton(  GUIResources.getIcon( GUIResources.customButton ), ViewEditorLayer.CUSTOM_EDIT_MODE_2 );
	public ModeButton custom3
		= new ModeButton(  GUIResources.getIcon( GUIResources.customButton ), ViewEditorLayer.CUSTOM_EDIT_MODE_3 );
	public ModeButton custom4
		= new ModeButton(  GUIResources.getIcon( GUIResources.customButton ), ViewEditorLayer.CUSTOM_EDIT_MODE_4 );
	public ModeButton custom5
		= new ModeButton(  GUIResources.getIcon( GUIResources.customButton ), ViewEditorLayer.CUSTOM_EDIT_MODE_5 );
	public ModeButton custom6
		= new ModeButton(  GUIResources.getIcon( GUIResources.customButton ), ViewEditorLayer.CUSTOM_EDIT_MODE_6 );
	public ModeButton custom7
		= new ModeButton(  GUIResources.getIcon( GUIResources.customButton ), ViewEditorLayer.CUSTOM_EDIT_MODE_7 );
	public ModeButton custom8
		= new ModeButton(  GUIResources.getIcon( GUIResources.customButton ), ViewEditorLayer.CUSTOM_EDIT_MODE_8 );

	// Since these are same as mode values this is basically data duplication, but used too much to remove.
	public static final int MOVE_B = 0;
	public static final int ROTATE_B = 1;
	public static final int KF_EDIT_B = 2;
	public static final int KF_ADD_B = 3;
	public static final int KF_REMOVE_B = 4;
	public static final int PICK_COLOR_B = 5;

	private static Vector<ModeButton> modeButtons = new Vector<ModeButton>();

	public JButton layerUp = new JButton( GUIResources.getIcon( GUIResources.layerUp ) );
	public JLabel layerText = new JLabel("");

	public ButtonGroup modes;
	private ButtonGroup bGroup;

	private JPanel sPanel;
	private JPanel lPanel;

	private JPanel staticPanel = new JPanel();
	private JPanel parent;

	public ViewControlButtons( JPanel parentContainer, TCDisplay timecodeDisplay )
	{
		parent = parentContainer;

		GUIResources.prepareMediumButton( editorUpdates, this, "View Editor updates on/off" );
		GUIResources.prepareMediumButton( layerUp, this, "Next Layer" );
		GUIResources.prepareMediumButton( move, this, "Move" );
		GUIResources.prepareMediumButton( rotate, this, "Rotate" );
		GUIResources.prepareMediumButton( kfEdit, this, "Edit Points" );
		GUIResources.prepareMediumButton( kfAdd, this, "Add Points" );
		GUIResources.prepareMediumButton( kfRemove, this, "Remove Points" );
		GUIResources.prepareMediumButton( viewColor, this, "View image / alpha" );
		GUIResources.prepareMediumButton( viewFlow, this, "View Composition" );
		GUIResources.prepareMediumButton( viewSelected, this, "View from node open in Property Editor" );
		GUIResources.prepareMediumButton( allBoxes, this, "Display bounding boxes" );
		GUIResources.prepareMediumButton( pickColor, this, "Select color" );

		GUIResources.prepareMediumButton( custom1, this, "Custom 1" );
		GUIResources.prepareMediumButton( custom2, this, "Custom 2" );
		GUIResources.prepareMediumButton( custom3, this, "Custom 3" );
		GUIResources.prepareMediumButton( custom4, this, "Custom 4" );
		GUIResources.prepareMediumButton( custom5, this, "Custom 5" );
		GUIResources.prepareMediumButton( custom6, this, "Custom 6" );
		GUIResources.prepareMediumButton( custom7, this, "Custom 7" );
		GUIResources.prepareMediumButton( custom8, this, "Custom 8" );

		editorUpdates.setSelectedIcon( GUIResources.getIcon(GUIResources.showViewEditUpdatesOff ) );
		move.setSelectedIcon( GUIResources.getIcon(  GUIResources.movePressed ) );
		rotate.setSelectedIcon( GUIResources.getIcon( GUIResources.rotatePressed ) );
		kfEdit.setSelectedIcon( GUIResources.getIcon(  GUIResources.kfEditPressed ) );
		kfAdd.setSelectedIcon( GUIResources.getIcon(  GUIResources.kfAddPressed ) );
		kfRemove.setSelectedIcon( GUIResources.getIcon(  GUIResources.kfRemovePressed ) );
		viewColor.setSelectedIcon( GUIResources.getIcon(  GUIResources.viewAlphaPressed ) );
		viewFlow.setSelectedIcon( GUIResources.getIcon(  GUIResources.viewFlowPressed ) );
		viewSelected.setSelectedIcon( GUIResources.getIcon(  GUIResources.viewSelectedPressed ) );
		allBoxes.setSelectedIcon( GUIResources.getIcon(  GUIResources.allBoxesPressed ) );
		pickColor.setSelectedIcon( GUIResources.getIcon(  GUIResources.pickColorPressed ) );
		custom1.setSelectedIcon( GUIResources.getIcon(  GUIResources.customPressed ) );
		custom2.setSelectedIcon( GUIResources.getIcon(  GUIResources.customPressed ) );
		custom3.setSelectedIcon( GUIResources.getIcon(  GUIResources.customPressed ) );
		custom4.setSelectedIcon( GUIResources.getIcon(  GUIResources.customPressed ) );
		custom5.setSelectedIcon( GUIResources.getIcon(  GUIResources.customPressed ) );
		custom6.setSelectedIcon( GUIResources.getIcon(  GUIResources.customPressed ) );
		custom7.setSelectedIcon( GUIResources.getIcon(  GUIResources.customPressed ) );
		custom8.setSelectedIcon( GUIResources.getIcon(  GUIResources.customPressed ) );


		//--- build vector with hardcoded indexes for button access
		modeButtons.add( move );
		modeButtons.add( rotate );
		modeButtons.add( kfEdit );
		modeButtons.add( kfAdd );
		modeButtons.add( kfRemove );
		modeButtons.add( pickColor );
		modeButtons.add( custom1 );
		modeButtons.add( custom2 );
		modeButtons.add( custom3 );
		modeButtons.add( custom4 );
		modeButtons.add( custom5 );
		modeButtons.add( custom6 );
		modeButtons.add( custom7 );
		modeButtons.add( custom8 );

		modes = new ButtonGroup();
		modes.add( viewFlow );
		modes.add( viewSelected );
		modes.setSelected( viewFlow.getModel(), true );

		layerText.setFont( GUIResources.BASIC_FONT_12 );

		sPanel = new  JPanel();
		sPanel.setLayout(  new BoxLayout( sPanel, BoxLayout.X_AXIS ));
		sPanel.add( Box.createRigidArea( new Dimension(6,0) ));
		sPanel.add( viewSizeSelect );
		sPanel.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );
		sPanel.setMaximumSize( new Dimension( 70, 30 ) );

		lPanel = new  JPanel();
		lPanel.setLayout(  new BoxLayout( lPanel, BoxLayout.X_AXIS ));
		lPanel.add( layerSelect );
		lPanel.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );
		lPanel.setMaximumSize( new Dimension( 200, 30 ) );

		staticPanel.setLayout( new BoxLayout( staticPanel, BoxLayout.X_AXIS));
		staticPanel.add( timecodeDisplay );
		staticPanel.add( Box.createRigidArea( new Dimension(24,0) ));
		staticPanel.add( viewFlow );
		staticPanel.add( viewSelected );
		staticPanel.add( Box.createRigidArea( new Dimension(6,0) ));
		staticPanel.add( viewColor );
		staticPanel.add( Box.createRigidArea( new Dimension(6,0) ));
		staticPanel.add( editorUpdates );
		staticPanel.add( Box.createRigidArea( new Dimension( 6, 0 ) ) );
		staticPanel.add( layerUp );
		staticPanel.add( allBoxes );
		staticPanel.add( sPanel );
		staticPanel.add( Box.createRigidArea( new Dimension( 6, 0 ) ) );
		staticPanel.add( lPanel );

		setLayout( new BoxLayout( this, BoxLayout.X_AXIS));

		setModeButtons(new Vector<Integer>());

		GUIComponents.viewSizeSelector = viewSizeSelect;
	}

	//--- Called after layer change to determide visible edit mode buttons.
	public void setModeButtons( Vector<Integer> indexes )
	{
		removeAll();
		add( staticPanel );
		add( Box.createRigidArea( new Dimension( 4, 0 ) ) );

		bGroup = new ButtonGroup();
		for( int i = 0; i < indexes.size(); i++)
		{
			Integer ig = indexes.elementAt( i );
			add( modeButtons.elementAt( ig.intValue() ));
 			bGroup.add( modeButtons.elementAt( ig.intValue() ));
		}

		if (indexes.size() > 0)
			add( Box.createRigidArea( new Dimension(4,0) ));
		add( renderClock );

 		setFirstButtonPressed( bGroup );
		parent.repaint();
	}

	public void setButtonIcons( int mode, ImageIcon up, ImageIcon selected )
	{
		ModeButton button = modeButtons.elementAt( mode );
		button.setIcon( up );
		button.setSelectedIcon( selected );
	}

	public void setFlowViewButtonSelected()
	{
		modes.setSelected( viewFlow.getModel(), true );
	}

	private void setFirstButtonPressed( ButtonGroup bGroup )
	{
 		Enumeration<AbstractButton> buttons = bGroup.getElements();
		if( buttons.hasMoreElements() )
		{
			AbstractButton b = buttons.nextElement();
			b.setSelected(true);
		}
	}

	//-- Used if default mode is not in first button.
	public void setSelected( int index )
	{
		int iter = 0;
		Enumeration<AbstractButton> buttons = bGroup.getElements();
		while( buttons.hasMoreElements() )
		{
			AbstractButton b = buttons.nextElement();
			if( iter == index ) b.setSelected(true);
			iter++;
		}
	}

	//-- Use info on selected button after layer change to set edit mode to match.
	//--- NOTE: Does not perform programmed click.
	public void clickSelected()
	{
		Enumeration<AbstractButton> buttons = bGroup.getElements();
		while( buttons.hasMoreElements() )
		{
			AbstractButton b = buttons.nextElement();
			if( b.isSelected() )
			{
				GUIComponents.getViewEditor().setMode( ((ModeButton) b).getMode() );
			}
		}
	}

	//--- called after layer added or removed
	public void updateLayerSelector( Vector<ViewEditorLayer> layers, ViewEditorLayer editLayer )
	{
		layerSelect.update( layers, editLayer );
	}

	public void displayClock( boolean display )
	{
		if( display )
			renderClock.setIcon( clockIcon );
		else
			renderClock.setIcon( null );
	}

	public int getViewSize(){ return viewSizeSelect.getViewSize(); }

	public void zoomIn()
	{
		viewSizeSelect.zoomIn();
	}

	public void zoomOut()
	{
		viewSizeSelect.zoomOut();
	}

	//----------------------------------------- BUTTON EVENTS
	public void actionPerformed( ActionEvent e )
	{
		//--- on / off. Note: this get called  on _press event_ so states are "flipped"
		if( e.getSource() == editorUpdates )
		{
			if( !editorUpdates.isSelected() )
			{
				EditorsController.setViewEditorUpdatesOn( true );
				EditorsController.displayCurrentInViewEditor( false );
			}
			else
			{
				EditorsController.setViewEditorUpdatesOn( false );
			}
		}

		//--- edit mode
		if( e.getSource() == move )
			GUIComponents.getViewEditor().setMode( ViewEditorLayer.MOVE_MODE );
		if( e.getSource() == rotate)
			GUIComponents. getViewEditor().setMode( ViewEditorLayer.ROTATE_MODE );
		if( e.getSource() == kfEdit )
			GUIComponents.getViewEditor().setMode( ViewEditorLayer.KF_EDIT_MODE );
		if( e.getSource() == kfAdd )
			GUIComponents.getViewEditor().setMode( ViewEditorLayer.KF_ADD_MODE );
		if( e.getSource() == kfRemove )
			GUIComponents.getViewEditor().setMode( ViewEditorLayer.KF_REMOVE_MODE );
		if( e.getSource() == pickColor )
			GUIComponents.getViewEditor().setMode( ViewEditorLayer.PICK_COLOR_MODE );
		if( e.getSource() == custom1 )
			GUIComponents.getViewEditor().setMode( ViewEditorLayer.CUSTOM_EDIT_MODE_1 );
		if( e.getSource() == custom2 )
			GUIComponents.getViewEditor().setMode( ViewEditorLayer.CUSTOM_EDIT_MODE_2 );
		if( e.getSource() == custom3 )
			GUIComponents.getViewEditor().setMode( ViewEditorLayer.CUSTOM_EDIT_MODE_3 );
		if( e.getSource() == custom4 )
			GUIComponents.getViewEditor().setMode( ViewEditorLayer.CUSTOM_EDIT_MODE_4 );
		if( e.getSource() == custom5 )
			GUIComponents.getViewEditor().setMode( ViewEditorLayer.CUSTOM_EDIT_MODE_5 );
		if( e.getSource() == custom6 )
			GUIComponents.getViewEditor().setMode( ViewEditorLayer.CUSTOM_EDIT_MODE_6 );
		if( e.getSource() == custom7 )
			GUIComponents.getViewEditor().setMode( ViewEditorLayer.CUSTOM_EDIT_MODE_7 );
		if( e.getSource() == custom8 )
			GUIComponents.getViewEditor().setMode( ViewEditorLayer.CUSTOM_EDIT_MODE_8 );


		//--- layers and all bounding
		if( e.getSource() == layerUp ) EditorsController.rotateNextLayer( false );
		if( e.getSource() == allBoxes )
			GUIComponents. getViewEditor().drawAllLayers( allBoxes.isSelected() );

		//--- view modes
		if( e.getSource() == viewFlow )
			EditorsController.setViewMode( EditorsController.FLOW_VIEW );
		if( e.getSource() == viewSelected )
			EditorsController.setViewMode( EditorsController.SELECT_VIEW );

		//--- color / alpha
		if( e.getSource() == viewColor )
		{
			if( viewColor.getModel().isSelected() )
				EditorsController.setAlphaDisplay( true );
			else
				EditorsController.setAlphaDisplay( false );
		}
	}

}//end class
