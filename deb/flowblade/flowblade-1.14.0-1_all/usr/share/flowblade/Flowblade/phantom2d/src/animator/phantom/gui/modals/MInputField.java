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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import animator.phantom.gui.GUIResources;

public abstract class MInputField extends JPanel
{
	protected Object value;

	protected Component leftComponent;
	protected Component rightComponent;

	protected int topBuf = 0; //applied when building panel
	protected int bottomBuf = 4; //applied when building panel
	protected boolean rightJustifyRightComponent = false;

	public static final int HEIGHT = 25;

	public static final int ROW_LEFT_SIZE = 160;
	public static final int ROW_RIGHT_SIZE = 220;
	public static final int ROW_INSET = 14;
	public static final int WIDTH = ROW_LEFT_SIZE + ROW_INSET + ROW_RIGHT_SIZE;
	
	protected void initGUI( int leftComponentCut, int rComponentCut )
	{
		initGUI( leftComponentCut, rComponentCut, true );
	}

	protected void initGUI( int leftComponentCut, int rComponentCut, boolean addRightGlue )
	{

		//--- Create dimensions
		Dimension LEFT_SIZE = new Dimension( ROW_LEFT_SIZE + ROW_INSET, HEIGHT );
		Dimension RIGHT_SIZE = new Dimension( ROW_RIGHT_SIZE, HEIGHT );
		Dimension SIZE = new Dimension( WIDTH, HEIGHT );
	
		//--- Create layout
		//--- Left side
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout( new BoxLayout( leftPanel, BoxLayout.X_AXIS) );
		leftPanel.setPreferredSize( LEFT_SIZE );
		leftPanel.add( Box.createRigidArea( new Dimension( ROW_INSET ,0) ) );
		leftPanel.add( leftComponent );
		leftPanel.add( Box.createHorizontalGlue() );

		//--- Right side
		JPanel rightPanel = new JPanel();	
		rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.X_AXIS) );
		rightPanel.setPreferredSize( RIGHT_SIZE );
		if( rightJustifyRightComponent )
			rightPanel.add( Box.createHorizontalGlue() );
		rightPanel.add( rightComponent );

		setLayout( new BoxLayout( this, BoxLayout.X_AXIS) );
		add( leftPanel );
		add( rightPanel );
 		setPreferredSize( SIZE );
		setMaximumSize( SIZE );
		setBuffering( 0, 2 );
		leftComponent.setFont( GUIResources.BASIC_FONT_12 );
 		rightComponent.setFont( GUIResources.BASIC_FONT_12 );
	}

	protected void initGUI2( int leftSize, int rightSize )
	{
		initGUIWithSizes( leftSize, rightSize );
	}

	protected void initGUI3()
	{
		int leftSize = 130;
		int rightSize = 170;

		 initGUIWithSizes( leftSize, rightSize );
	}

	protected void initGUIWithSizes( int leftSize, int rightSize )
	{

		Dimension LEFT_SIZE = new Dimension( leftSize, HEIGHT );
		Dimension SIZE = new Dimension( leftSize + rightSize, HEIGHT );

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout( new BoxLayout( leftPanel, BoxLayout.X_AXIS) );
		leftPanel.add( leftComponent );
		leftPanel.add(  Box.createHorizontalGlue() );
		leftPanel.add( Box.createRigidArea( new Dimension( 4, 0 ) ) );
		leftPanel.setMaximumSize( LEFT_SIZE );
		leftPanel.setPreferredSize(  LEFT_SIZE );

		JPanel rightPanel = new JPanel();	
		rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.X_AXIS) );
		rightPanel.add( rightComponent );
		rightPanel.add( Box.createHorizontalGlue() );

		setLayout( new BoxLayout( this, BoxLayout.X_AXIS) );
		add( leftPanel );
		add( rightPanel );
		add( Box.createHorizontalGlue() );

		setPreferredSize( SIZE );
		setMaximumSize( SIZE );

		leftComponent.setFont(  GUIResources.BASIC_FONT_12 );
 		rightComponent.setFont( GUIResources.BASIC_FONT_12 );
	}

	protected void initPanels()
	{

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout( new BoxLayout( leftPanel, BoxLayout.X_AXIS) );
		leftPanel.add( leftComponent );
		leftPanel.add(  Box.createHorizontalGlue() );

		JPanel rightPanel = new JPanel();	
		rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.X_AXIS) );
		rightPanel.add( rightComponent );
		rightPanel.add( Box.createHorizontalGlue() );

		setLayout( new GridLayout(0,2) );
		add( leftPanel );
		add( rightPanel );

		leftComponent.setFont(  GUIResources.BASIC_FONT_12 );
 		rightComponent.setFont( GUIResources.BASIC_FONT_12 );
	}
	
	//--- Convenience method
	protected void setLeftAsLabel( String msg )
	{
		leftComponent = new JLabel( msg );
	}

	public void setBuffering( int topBuf_, int bottomBuf_ )
	{
		topBuf = topBuf_;
		bottomBuf = bottomBuf_;
	}

	public void setEnabled( boolean val )
	{
		leftComponent.setEnabled( val );
		rightComponent.setEnabled( val );
	}

}//end class
