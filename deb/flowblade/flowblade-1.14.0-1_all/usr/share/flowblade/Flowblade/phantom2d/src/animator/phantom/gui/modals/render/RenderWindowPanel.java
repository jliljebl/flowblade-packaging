package animator.phantom.gui.modals.render;

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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import animator.phantom.controller.AppUtils;
import animator.phantom.controller.ProjectController;
import animator.phantom.controller.RenderModeController;
import animator.phantom.gui.GUIColors;
import animator.phantom.gui.GUIResources;
import animator.phantom.gui.PHButtonFactory;
import animator.phantom.gui.PHProgressBar;
import animator.phantom.gui.modals.DialogUtils;
import animator.phantom.gui.modals.MCheckBox;
import animator.phantom.gui.modals.MComboBox;
import animator.phantom.gui.modals.MFileSelect;
import animator.phantom.gui.modals.MTextField;
import animator.phantom.gui.timeline.TimeLineDisplayPanel;

//--- GUI panel for render info setting and render progress display.
public class RenderWindowPanel extends JPanel implements ActionListener
{
	private static final int SIDE_GAP = 10;
	private static final int BUTTONS_GAP = 20;
	private static final int SET_BUTTONS_GAP = 10;
	private static final int BOTTOM_GAP = 20;
	private static final int INFO_PROG_GAP = 6;
	private static final int PROG_BUTTONS_GAP = 6;
	private static final int RANGE_GAP = 40;
	private static final int WIDTH = 600;
	private static final int SETTINGS_ROW_GAP = 8;
	private static final int PROG_HEIGHT = 15;

	private JButton setIn;
	private JButton setOut;
	public JButton render;//--- public: state set from WriteRenderThread
	public JButton stop;//--- public: state set from WriteRenderThread
	public JButton exit;//--- public: state set from WriteRenderThread

	private JLabel lastTime = new JLabel("");
	private JLabel renderingInfo = new JLabel("Not started");
	private JLabel elapsed = new JLabel("");
	private JLabel proj = new JLabel();

	private JLabel fromTC = new JLabel();
	private JLabel toTC = new JLabel();
	private JLabel lengthFrames = new JLabel();

	private MFileSelect tfs;
	
	public PHProgressBar pbar = new PHProgressBar();

	private int framesCount;
	private int currentFrame;

	private boolean rendering = false;
	
	private RenderWindow window;

	public RenderWindowPanel( RenderWindow window )
	{
		this.window = window;

		//--- build components
		render = PHButtonFactory.getButton( "Render" );//--- public: state set from WriteRenderThread
		stop = PHButtonFactory.getButton( "Stop" );//--- public: state set from WriteRenderThread
		exit = PHButtonFactory.getButton( "Close" );//--- public: state set from WriteRenderThread
		setIn =  PHButtonFactory.getButton( "Set" );
		setOut = PHButtonFactory.getButton( "Set" );

		int fps =  ProjectController.getFramesPerSecond();
		fromTC.setText( TimeLineDisplayPanel.parseTimeCodeString( RenderModeController.writeRangeStart, 6, fps ));
		toTC.setText( TimeLineDisplayPanel.parseTimeCodeString( RenderModeController.writeRangeEnd, 6, fps ));
		lengthFrames.setText( Integer.toString(RenderModeController.writeRangeEnd - RenderModeController.writeRangeStart ) + " frames");
		
		stop.setEnabled( false );

		lastTime.setHorizontalAlignment( SwingConstants.LEFT );
		renderingInfo.setHorizontalAlignment( SwingConstants.LEFT );
		elapsed.setHorizontalAlignment( SwingConstants.LEFT );

		setIn.setFont( GUIResources.BASIC_FONT_12 );
		setOut.setFont( GUIResources.BASIC_FONT_12 );
		render.setFont( GUIResources.BASIC_FONT_12 );
		stop.setFont( GUIResources.BASIC_FONT_12 );
		exit.setFont( GUIResources.BASIC_FONT_12 );
		lastTime.setFont( GUIResources.BASIC_FONT_12 );
		renderingInfo.setFont( GUIResources.BASIC_FONT_12 );
		elapsed.setFont( GUIResources.BASIC_FONT_12 );
		proj.setFont( GUIResources.BASIC_FONT_12 );

		render.addActionListener( this );
		stop.addActionListener( this );
		exit.addActionListener( this );
		setIn.addActionListener( this );
		setOut.addActionListener( this );
		
		File targetFolder = RenderModeController.getWriteFolder();
		tfs = new MFileSelect( "Render Output Folder", "Select folder for frames", 25, targetFolder, null );
		tfs.setType( JFileChooser.DIRECTORIES_ONLY );
		
		String fname = RenderModeController.getFrameName();
		if( fname == null ) fname = "frame";
		MTextField framename = new MTextField( "Frame name", 75, fname );
		framename.setTextFieldSize( 160 );

		String[] padOtps = { "3 digits","4 digits","5 digits", "no padding" };
		MComboBox pad = new MComboBox( "Zero padding", padOtps );		
		MCheckBox overWrite = new MCheckBox( "Overwrite without warning", true );

		//--- build panels and layout
		JPanel setpanel = new JPanel();
		setpanel.setLayout( new BoxLayout( setpanel, BoxLayout.Y_AXIS) );
		setpanel.add( Box.createRigidArea( new Dimension(0,10) ) );
		setpanel.add( tfs );
		setpanel.add( Box.createRigidArea( new Dimension(0,SETTINGS_ROW_GAP) ) );
		setpanel.add( framename );
		setpanel.add( Box.createRigidArea( new Dimension(0,SETTINGS_ROW_GAP) ) );
		setpanel.add( pad );
		setpanel.add( Box.createRigidArea( new Dimension(0,SETTINGS_ROW_GAP) ) );
		setpanel.add( overWrite );
		setBorder( "Frame Output", setpanel );

		JPanel top = new JPanel();
		top.setLayout( new BoxLayout( top, BoxLayout.X_AXIS) );
		top.add( Box.createRigidArea( new Dimension(SIDE_GAP,0) ) );
		top.add( setpanel );
		top.add( Box.createRigidArea( new Dimension(SIDE_GAP,0) ) );

		JPanel rangePanel = new JPanel();
		rangePanel.setLayout( new BoxLayout( rangePanel, BoxLayout.X_AXIS) );
		JLabel rlfrom = new JLabel( "From:" );
		rlfrom.setFont( GUIResources.BASIC_FONT_12 );
		rangePanel.add( rlfrom );
		rangePanel.add( Box.createRigidArea( new Dimension(SET_BUTTONS_GAP, 0) ) );
		rangePanel.add( fromTC );
		rangePanel.add( Box.createRigidArea( new Dimension(SET_BUTTONS_GAP, 0) ) );
		rangePanel.add( setIn );
		rangePanel.add( Box.createRigidArea( new Dimension(RANGE_GAP,0)));
		JLabel rlto = new JLabel( "To:" );
		rlto.setFont( GUIResources.BASIC_FONT_12 );
		rangePanel.add( rlto );
		rangePanel.add( Box.createRigidArea( new Dimension(SET_BUTTONS_GAP, 0) ) );
		rangePanel.add( toTC );
		rangePanel.add( Box.createRigidArea( new Dimension(SET_BUTTONS_GAP, 0) ) );
		rangePanel.add( setOut );
		rangePanel.add( Box.createRigidArea( new Dimension(RANGE_GAP, 0)));
		JLabel lenLabel = new JLabel( "Length:" );
		lenLabel.setFont( GUIResources.BASIC_FONT_12 );
		rangePanel.add( lenLabel );
		rangePanel.add( Box.createRigidArea( new Dimension(SET_BUTTONS_GAP, 0) ) );
		rangePanel.add( lengthFrames );
		rangePanel.add( Box.createHorizontalGlue() );
		setBorder( "Range", rangePanel );

		JPanel middle = new JPanel();
		middle.setLayout( new BoxLayout( middle, BoxLayout.X_AXIS) );
		middle.add( Box.createRigidArea( new Dimension(SIDE_GAP, 0) ) );
		middle.add( rangePanel );
		middle.add( Box.createRigidArea( new Dimension(SIDE_GAP, 0) ) );

		JPanel renderedP = new JPanel();
		JPanel lastTimeP = new JPanel();
		JPanel elapsedTimeP = new JPanel();
		renderedP.setLayout( new BoxLayout( renderedP, BoxLayout.X_AXIS) );
		renderedP.add( renderingInfo );
		renderedP.add( Box.createHorizontalGlue() );
		lastTimeP.setLayout( new BoxLayout( lastTimeP, BoxLayout.X_AXIS) );
		lastTimeP.add( lastTime );
		lastTimeP.add( Box.createHorizontalGlue() );
		elapsedTimeP.setLayout( new BoxLayout( elapsedTimeP, BoxLayout.X_AXIS) );
		elapsedTimeP.add( elapsed );
		elapsedTimeP.add( Box.createHorizontalGlue() );

		JPanel gridP = new JPanel();
		gridP.setLayout( new GridLayout( 1, 3 ));
		gridP.add( renderedP );
		gridP.add( lastTimeP );
		gridP.add( elapsedTimeP );

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout( new BoxLayout( infoPanel, BoxLayout.X_AXIS) );
		infoPanel.add( Box.createRigidArea( new Dimension(SIDE_GAP,0) ) );
		infoPanel.add( gridP );
		infoPanel.add( Box.createRigidArea( new Dimension(SIDE_GAP,0) ) );

		JPanel progPanel = new JPanel();
		progPanel.setLayout( new BoxLayout( progPanel, BoxLayout.X_AXIS) );
		progPanel.add( Box.createRigidArea( new Dimension(SIDE_GAP,0) ) );
		progPanel.add( pbar );
		progPanel.add( Box.createRigidArea( new Dimension(SIDE_GAP,0) ) );

		pbar.setPreferredSize( new Dimension( WIDTH, PROG_HEIGHT ));

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout( new BoxLayout( buttonsPanel, BoxLayout.X_AXIS) );
		buttonsPanel.add( Box.createRigidArea( new Dimension(SIDE_GAP,0) ) );
		buttonsPanel.add( proj );
		buttonsPanel.add( Box.createHorizontalGlue() );
		buttonsPanel.add( stop );
		buttonsPanel.add( Box.createRigidArea( new Dimension(BUTTONS_GAP,0) ) );
		buttonsPanel.add( render );
		buttonsPanel.add( Box.createRigidArea( new Dimension(SIDE_GAP,0) ) );
		buttonsPanel.add( exit );
		buttonsPanel.add( Box.createRigidArea( new Dimension(BUTTONS_GAP,0) ) );

		JPanel rendPanel = new JPanel();
		rendPanel.setLayout( new BoxLayout( rendPanel, BoxLayout.Y_AXIS) );
		rendPanel.add( infoPanel );
		rendPanel.add( Box.createRigidArea( new Dimension( 0, INFO_PROG_GAP )));
		rendPanel.add( progPanel );
		rendPanel.add( Box.createRigidArea( new Dimension( 0, PROG_BUTTONS_GAP )));
		rendPanel.add( buttonsPanel );

		JPanel bottom = new JPanel();
		bottom.setLayout( new BoxLayout( bottom, BoxLayout.X_AXIS) );
		bottom.add( Box.createRigidArea( new Dimension(SIDE_GAP, 0) ) );
		bottom.add( rendPanel );
		bottom.add( Box.createRigidArea( new Dimension(SIDE_GAP, 0) ) );

		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS) );
		add( Box.createRigidArea( new Dimension( 0, 12 )));
		add( top );
		add( Box.createRigidArea( new Dimension( 0, 24 )));
		add( middle );
		add( Box.createRigidArea( new Dimension( 0, 48 )));
		add( bottom );
		add( Box.createRigidArea( new Dimension( 0,BOTTOM_GAP )));
	}

	private void setBorder( String title, JPanel pane )
	{
		EmptyBorder b1 = new EmptyBorder( new Insets( 0,0,0,0 )); 
		TitledBorder b2 = (TitledBorder) BorderFactory.createTitledBorder( 	b1,
								title,
								TitledBorder.CENTER,
								TitledBorder.TOP );
		b2.setTitleColor( GUIColors.grayTitle );
	
			//void 	setTitleFont(Font titleFont)
		Border b3 = BorderFactory.createCompoundBorder( b2, BorderFactory.createEmptyBorder( 8, 0, 10, 0));
		pane.setBorder( b3 );
	}

	protected JPanel getRow( JComponent leftComponent, JComponent rightComponent)
	{
		JPanel rowPanel = new JPanel();
			
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout( new BoxLayout( leftPanel, BoxLayout.X_AXIS) );
		leftPanel.add( leftComponent );
		leftPanel.add(  Box.createHorizontalGlue() );

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout( new BoxLayout( rightPanel, BoxLayout.X_AXIS) );
		rightPanel.add( rightComponent );
		rightPanel.add( Box.createHorizontalGlue() );

		leftComponent.setFont(  GUIResources.BASIC_FONT_12 );
 		rightComponent.setFont( GUIResources.BASIC_FONT_12 );
 		
		rowPanel.setLayout( new GridLayout(0,2) );
		rowPanel.add( leftPanel );
		rowPanel.add( rightPanel );

		return rowPanel;
	}

	public void renderStart( int framesCount )
	{
		this.framesCount = framesCount;
		this.currentFrame = 1;
		displayRenderingOf();

		repaint();
	}

	public void nextFrame( int totalMillis, int lastMillis )
	{
		currentFrame++;
		displayRenderingOf();
		lastTime.setText( "Frame time:" + createTimeString( lastMillis, true ) );
		elapsed.setText( "Elapsed time:" + createTimeString( totalMillis, false ) );
		pbar.advanceOne();

		repaint();
	}

	private void displayRenderingOf()
	{
		renderingInfo.setText( "Rendering " + Integer.toString( currentFrame ) + " of " + Integer.toString( framesCount ) );
	}

	public void renderingDone()
	{
		renderingInfo.setText( "Rendering done");
		render.setEnabled( true );
		stop.setEnabled( false );
		exit.setEnabled( true );
		rendering = false;
	}
	
	public static String createTimeString( int millis, boolean fractions )
	{
		return AppUtils.createTimeString( millis, fractions );
	}

	public int parseTCString( String tc )
	{
		String numberString = getNumberString( tc );
		System.out.println(numberString);
		Vector<String> tokens = getTimeTokens( numberString );
		
		int frames = 0;
		try
		{
			int c = 0;
			for( int i = tokens.size() -1; i >= 0; i-- )
			{
				int val = Integer.parseInt( tokens.elementAt( i ) );
				if( c == 0 )
				{
					if (val >= ProjectController.getFramesPerSecond())
						return -1;
					frames += val;//f
				}
				if( c == 1 ) 
				{
					if( val >= 60 )
						return -1;
					frames += ProjectController.getFramesPerSecond() * val;//s
				}
				if( c == 2 )
				{
					if( val >= 60 )
						return -1;
					frames += ProjectController.getFramesPerSecond() * val * 60;//m
				}
				if( c == 3 ) frames += ProjectController.getFramesPerSecond() * val * 60 * 60;//h
				c++;
			}
		}
		catch( Exception e )
		{
			return -1;
		}
		
		return frames;
	}

	private String getNumberString( String tc )
	{
		StringTokenizer st = new StringTokenizer( tc, ":" );
		String numberString = new String();

		while( st.hasMoreTokens() )
		{
			String token = st.nextToken();
			numberString = numberString + token;
		}

		int missingZeros = 8 - numberString.length();
		String addZeros = new String();
		for( int i = 0; i <  missingZeros; i++ )
			addZeros = addZeros + "0";

		return addZeros + numberString;
	}

	//--- assumes 8 char long String
	private Vector<String> getTimeTokens( String numberString )
	{
		Vector<String> tokens = new Vector<String>();
		tokens.add( numberString.substring(0, 2) );
		tokens.add( numberString.substring(2, 4) );
		tokens.add( numberString.substring(4, 6) );
		tokens.add( numberString.substring(6, 8) );
		return tokens;
	}

	public void setRendering( boolean b ){ rendering = b; }
	public boolean getRendering(){ return rendering; }

	public void actionPerformed( ActionEvent e )
	{
		if( e.getSource() == render )
		{
			File targetFolder = tfs.getSelectedFile();
			if( targetFolder == null )
			{
				String[] options = { "Ok" };
				String[] bLines = { "No folder selected for frames" };
				String[] tLines = { "Select folder for frames:", "Select Frames Output Settins -> Folder for frames" };
				DialogUtils.showTwoTextStyleDialog( JOptionPane.WARNING_MESSAGE, "Render info", options, bLines, tLines, window );
				return;
			}
			RenderModeController.setWriteFolder( targetFolder );

			int start = RenderModeController.writeRangeStart;
			int end = RenderModeController.writeRangeEnd;		

			RenderModeController.setWriteRange( start, end );
			RenderModeController.startWriteRender();
			render.setEnabled( false );
			stop.setEnabled( true );
			exit.setEnabled( false );
			rendering = true;
		}

		if( e.getSource() == setIn )
		{
			DialogUtils.setDialogParent( window );
			int startFrame = DialogUtils.showFrameSelectDialog("Set Render Start Frame", "Render start frame:", RenderModeController.writeRangeStart, 0,  RenderModeController.writeRangeEnd);
			
			DialogUtils.setDialogParent( null );
			if ( startFrame != -1)
			{
				RenderModeController.writeRangeStart = startFrame;
				updateRangeValuesGUI();
				repaint();
			}
		}

		if( e.getSource() == setOut )
		{
			DialogUtils.setDialogParent( window );
			
			int endFrame = DialogUtils.showFrameSelectDialog("Set Render End Frame", "Render end frame:", RenderModeController.writeRangeEnd, RenderModeController.writeRangeStart,  ProjectController.getLength());

			if ( endFrame != -1)
			{
				RenderModeController.writeRangeEnd = endFrame;
				updateRangeValuesGUI();
				repaint();
			}
			
			DialogUtils.setDialogParent( null );

		}
		
		if( e.getSource() == stop )
		{
			RenderModeController.stopWriteRender();
			render.setEnabled( true );
			stop.setEnabled( false );
			exit.setEnabled( true );
			rendering = false;
		}

		if( e.getSource() == exit )
		{
			RenderModeController.disposeRenderWindow();
		}
	}

	private void updateRangeValuesGUI()
	{
		int fps =  ProjectController.getFramesPerSecond();
		fromTC.setText( TimeLineDisplayPanel.parseTimeCodeString( RenderModeController.writeRangeStart, 6, fps ));
		toTC.setText( TimeLineDisplayPanel.parseTimeCodeString( RenderModeController.writeRangeEnd, 6, fps ));
		lengthFrames.setText( Integer.toString(RenderModeController.writeRangeEnd - RenderModeController.writeRangeStart ) + " frames");
		repaint();
	}
	
}//end class
