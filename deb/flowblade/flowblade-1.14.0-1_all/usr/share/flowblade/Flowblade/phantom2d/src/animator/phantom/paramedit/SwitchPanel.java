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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

import animator.phantom.controller.TimeLineController;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.GUIResources;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.SwitchData;
import animator.phantom.renderer.imagemerge.BasicTwoMergeIOP;

/**
* A GUI component for turning nodes on / off, setting render interpolations, parenting, looping and othet functionality.
* Panel also has a button for  centralizing anchor points.
* This is the special top most component in edit panels for all plugins that have affine transformable animated bitmap image sources.
* This is placed automatically into edit panels for those plugins that need it.
*/
public class SwitchPanel extends JPanel implements ItemListener, ActionListener
{
	private ImageOperation iop;
	private SwitchData switches;
	
	private JLabel mbLabel = new JLabel( GUIResources.getIcon( GUIResources.motionBlurLabel ) );
	private JLabel smoothLabel = new JLabel( GUIResources.getIcon( GUIResources.alfaLineLabel ) );
	private JLabel visLabel = new JLabel( GUIResources.getIcon( GUIResources.iris ) );
	private JLabel bilLabel = new JLabel( GUIResources.getIcon( GUIResources.bilinear ) );
	private JLabel bicLabel = new JLabel( GUIResources.getIcon( GUIResources.bicubic ) );
	private JLabel nearLabel = new JLabel( GUIResources.getIcon( GUIResources.nearest ) );

	private JCheckBox mbBox = new JCheckBox();
	private JCheckBox smoothBox = new JCheckBox();
	private JCheckBox visBox = new JCheckBox();

	private JRadioButton nearest = new JRadioButton();
	private JRadioButton bilinear = new JRadioButton();
	private JRadioButton bicubic = new JRadioButton();

	//--- Source buttons
	private JButton centerAnchor = new JButton(  GUIResources.getIcon( GUIResources.centerAnchor));
	private JToggleButton leafTrans = new JToggleButton(  GUIResources.getIcon( GUIResources.leafTrans));

	/**
	* Constructor with <code>ImageOperation</code>.
	*/ 
	public SwitchPanel( ImageOperation iop )
	{
		this.iop = iop;
		this.switches = iop.switches;
		
		//--- Prepare labels
		mbLabel.setToolTipText( "Motion Blur" );
		smoothLabel.setToolTipText( "Smooth Edges. Makes edges smoother, looses 1 pix from edges." );
		visLabel.setToolTipText( "Toggle On / Off" );
		nearLabel.setToolTipText( "Nearest Neighbour Draw Mode" );
		bilLabel.setToolTipText( "Bilinear Draw Mode" );
		bicLabel.setToolTipText( "Bicubic Draw Mode" );

		mbBox.setToolTipText( "Motion Blur" );
		smoothBox.setToolTipText( "Smooth Edges. Makes edges smoother, looses 1 pix from edges." );;
		visBox.setToolTipText( "Toggle On / Off" );

		nearest.setToolTipText( "Nearest Neighbour Draw Mode" );
		bilinear.setToolTipText( "Bilinear Draw Mode" );
		bicubic.setToolTipText( "Bicubic Draw Mode" );
		
		//-- Prepare boxes.
		mbBox.addItemListener(this);
		smoothBox.addItemListener(this);
		visBox.addItemListener(this);

		//--- Prepare radio buttons
		nearest.addActionListener(this);
		bilinear.addActionListener(this);
		bicubic.addActionListener(this);

		GUIResources.prepareMediumButton( centerAnchor, this, "Center Anchor" );
		GUIResources.prepareMediumButton( leafTrans, this, "Make leaf node background transparent. Use Source Alpha will be selected also. " );
		leafTrans.setSelectedIcon( new ImageIcon( GUIResources.leafTransPressed ));

		ButtonGroup group = new ButtonGroup();
		group.add(nearest);
		group.add(bilinear);
		group.add(bicubic);

		JPanel p1 = new JPanel();
		p1.setLayout( new BoxLayout( p1, BoxLayout.Y_AXIS) );
		p1.add( visLabel );
		p1.add( visBox );

		JPanel p2 = new JPanel();
		p2.setLayout( new BoxLayout( p2, BoxLayout.Y_AXIS) );
		p2.add(  mbLabel );
		p2.add(  mbBox );

		JPanel p3 = new JPanel();
		p3.setLayout( new BoxLayout( p3, BoxLayout.Y_AXIS) );
		p3.add( smoothLabel );
		p3.add( smoothBox );

		JPanel p4 = new JPanel();
		p4.setLayout( new BoxLayout( p4, BoxLayout.Y_AXIS) );
		p4.add( nearLabel );
		p4.add(  nearest );

		JPanel p5 = new JPanel();
		p5.setLayout( new BoxLayout( p5, BoxLayout.Y_AXIS) );
		p5.add( bilLabel );
		p5.add( bilinear );

		JPanel p6 = new JPanel();
		p6.setLayout( new BoxLayout( p6, BoxLayout.Y_AXIS) );
		p6.add( bicLabel );
		p6.add( bicubic );

		setLayout( new BoxLayout( this, BoxLayout.X_AXIS) );
		add( p1 );
		add( p2 );
		add( p3 );
		add( p4 );
		add( p5 );
		add( p6 );
		add( Box.createRigidArea( new Dimension(20,0 ) ));
		if (iop.getCenterable() == true )
			add( centerAnchor );

		add( Box.createHorizontalGlue() );

		Dimension size = new Dimension( ParamEditResources.EDIT_ROW_SIZE.width, ParamEditResources.EDIT_ROW_SIZE.height * 2 );
 		setPreferredSize( size );
		setMaximumSize( size );

		//--- Set values
		if( iop.isOn() ) visBox.setSelected( true );
		if( switches.motionBlur ) mbBox.setSelected( true );
		if( switches.fineEdges ) smoothBox.setSelected( true );
		if( switches.interpolation == SwitchData.NEAREST_NEIGHBOR ) nearest.setSelected( true );
		if( switches.interpolation == SwitchData.BILINEAR ) bilinear.setSelected( true );
		if( switches.interpolation == SwitchData.BICUBIC ) bicubic.setSelected( true );
	}
	/**
	* Handles user edit actions.
	*/
	public void itemStateChanged(ItemEvent e)
 	{
		Object source = e.getItemSelectable();
		int stateChange = e.getStateChange();
	
		if( source == mbBox )
		{
			if( stateChange == ItemEvent.SELECTED) switches.motionBlur = true;
			else switches.motionBlur = false;
			UpdateController.valueChangeUpdate();
		}
		if( source == visBox )
		{
			if( stateChange == ItemEvent.SELECTED) iop.setOnOffState( true );
			else iop.setOnOffState( false );
			UpdateController.valueChangeUpdate();
		}
		if( source == smoothBox )
		{
			if( stateChange == ItemEvent.SELECTED) switches.fineEdges = true;
			else
			{
				if( iop.getFileSource() != null )
				{
					//--- Memory copy of file source may have had
					//--- edges cut into it
					iop.getFileSource().clearData();
				}
				switches.fineEdges = false;
			}
			UpdateController.valueChangeUpdate();
		}
	}
	/**
	* Handles user edit actions.
	*/
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if( source == nearest ) switches.interpolation = SwitchData.NEAREST_NEIGHBOR;
		if( source == bilinear )  switches.interpolation = SwitchData.BILINEAR;
		if( source == bicubic )  switches.interpolation = SwitchData.BICUBIC;
		if( source == centerAnchor )
		{
			int width = 100;
			int height = 100;
			if( iop.getFileSource() == null )
			{
				if ( iop instanceof BasicTwoMergeIOP )
				{
					Rectangle r = ((BasicTwoMergeIOP) iop).getImageSize();
					if (r == null ) return;//merge is not defined
					width = r.width;
					height = r.height;
				}
			}
			else
			{

				width = iop.getFileSource().getImageWidth();
				height = iop.getFileSource().getImageHeight();
			}
			iop.getCoords().xAnchor.setValue( TimeLineController.getCurrentFrame(), (float) width / 2.0f );
			iop.getCoords().yAnchor.setValue( TimeLineController.getCurrentFrame(), (float) height / 2.0f );
			UpdateController.updateCurrentFrameDisplayers( false );
		}

		//if( source == filterStackButton )
		//	FilterStackController.displayEditor( iop );

	}

	/**
	* Used to set state after menu selection.
	*/
	public void setOnOff( boolean on )
	{
		visBox.setSelected( on );
	}
	
}//end class
