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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import animator.phantom.controller.FilterStackController;
import animator.phantom.gui.GUIResources;
import animator.phantom.gui.NodesPanel;
import animator.phantom.gui.PHButtonFactory;
import animator.phantom.renderer.IOPLibrary;
import animator.phantom.renderer.ImageOperation;

//--- Window for editing pre-trasform filter stack of a ImageOperation.
public class FilterStackEdit extends JFrame implements ActionListener 
{
	private ImageOperation iop;
	private JTable stackTable;
	private JTable pluginTable ;

	private JButton addButton;
	private JButton exitButton;
	private JButton deleteFilter = new JButton( GUIResources.getIcon(  GUIResources.deleteClip ) );
	private JButton filterDown = new JButton( GUIResources.getIcon(  GUIResources.clipDown ) );
	private JButton filterUp = new JButton( GUIResources.getIcon( GUIResources.clipUp ) );
	private JButton editTargetButton;

	private static final int ROW_HEIGHT = 20;
	private static final int BUTTON_TABLE_GAP = 4;
	private static final int TABLES_WIDTH = 300;
	private static final int PLUGIN_TABLE_HEIGHT = 200;
	private static final int NAME_PANEL_PAD = 8;
	private static final int SUB_TITLE_GAP = 2;

	private static Vector<ImageOperation> filters;
	
	public FilterStackEdit( ImageOperation iop ) 
	{
		super("Edit Filter Stack");
		
		this.iop = iop;

		//filters = IOPLibrary.getFilters();
		Collections.sort( filters );

		GUIResources.prepareMediumButton( deleteFilter, this, "Delete Selected Filter" );
		GUIResources.prepareMediumButton( filterDown, this, "Move Selected Filter Down" );
		GUIResources.prepareMediumButton( filterUp, this, "Move Selected Filter Up" );

		JPanel namePanel = new JPanel();
		namePanel.setLayout(new BoxLayout( namePanel, BoxLayout.X_AXIS));
		namePanel.add( Box.createRigidArea( new Dimension( NAME_PANEL_PAD, 0 ) ) );
		namePanel.add( iop.getNamePanel() );
		
		addButton =  PHButtonFactory.getButton("Add To Stack"  );
		addButton.addActionListener( this );

		JPanel filterButtons = new JPanel();
		filterButtons.setLayout(new BoxLayout( filterButtons, BoxLayout.X_AXIS));
		filterButtons.add( addButton );
		filterButtons.add( Box.createHorizontalGlue() );

		pluginTable = new JTable( getFilterTableModel() );
		pluginTable.setPreferredScrollableViewportSize(new Dimension( TABLES_WIDTH, PLUGIN_TABLE_HEIGHT));
		pluginTable.setFillsViewportHeight( true );
		pluginTable.setColumnSelectionAllowed( false );
		pluginTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		pluginTable.setShowGrid( false );
		pluginTable.setRowHeight( ROW_HEIGHT );
		pluginTable.setFont( GUIResources.BOLD_FONT_12 );

		JScrollPane pluginScrollPane = new JScrollPane( pluginTable );


		
			/*
		editTargetButton = PHButtonFactory.getButton( "Set Edit Target" );
		editTargetButton.addActionListener( this );

		JPanel stackButtons = new JPanel();
		stackButtons.setLayout(new BoxLayout( stackButtons, BoxLayout.X_AXIS));
		stackButtons.add( deleteFilter );
		stackButtons.add( filterDown  );
		stackButtons.add( filterUp );
		stackButtons.add( Box.createHorizontalGlue() );
		stackButtons.add( editTargetButton );

		stackTable = new JTable(  new CustomTableModel( new Vector<Vector<String>>(), "Filter Stack" ) );
		stackTable.setPreferredScrollableViewportSize(new Dimension( TABLES_WIDTH, STACK_TABLE_HEIGHT));
		stackTable.setFillsViewportHeight( true );
		stackTable.setColumnSelectionAllowed( false );
		stackTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		stackTable.setShowGrid( false );
		stackTable.setRowHeight(ROW_HEIGHT );
		stackTable.setFont( GUIResources.BOLD_FONT_12 );

		initFilterStack( 0 );

		JScrollPane stackScrollPane = new JScrollPane( stackTable );
	*/
		editTargetButton = PHButtonFactory.getButton( "Set Edit Target" );
		editTargetButton.addActionListener( this );
		
		JPanel stackButtons = new JPanel();
		stackButtons.setLayout(new BoxLayout( stackButtons, BoxLayout.X_AXIS));
		stackButtons.add( Box.createHorizontalGlue() );
		stackButtons.add( editTargetButton );
		
		exitButton = PHButtonFactory.getButton( "Exit" );
		exitButton.addActionListener( this );

		JPanel exitButtonPanel = new JPanel();
		exitButtonPanel.setLayout(new BoxLayout( exitButtonPanel, BoxLayout.X_AXIS));
		exitButtonPanel.add( Box.createHorizontalGlue() );
		exitButtonPanel.add( exitButton );

		JPanel top = new JPanel();
		//setBorder( top, "Filters" ); 
		top.setLayout(new BoxLayout( top, BoxLayout.Y_AXIS));
		top.add( Box.createRigidArea( new Dimension( 0, SUB_TITLE_GAP ) ) );
		top.add( filterButtons );
		top.add( Box.createRigidArea( new Dimension( 0, BUTTON_TABLE_GAP ) ) );
		top.add( pluginScrollPane );

		
		NodesPanel np = new NodesPanel();
		
		JScrollPane stackScrollPane = new JScrollPane( np );
		
		JPanel bottom = new JPanel();
		//setBorder( bottom, "Filter Stack" );
		bottom.setLayout(new BoxLayout( bottom, BoxLayout.Y_AXIS));
		bottom.add( Box.createRigidArea( new Dimension( 0, SUB_TITLE_GAP ) ) );
		bottom.add( stackScrollPane );
		

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout( p, BoxLayout.Y_AXIS));
		p.add( namePanel );
		p.add( stackButtons );
		//p.add( Box.createRigidArea( new Dimension( 0, NAME_PANEL_GAP ) ) );
		//p.add( top );
		p.add( Box.createRigidArea( new Dimension( 0, 12 ) ) );
		p.add( bottom );
		//p.add( Box.createRigidArea( new Dimension( 0, MID_GAP ) ) );
		p.add( exitButtonPanel ); 
		p.setBorder( BorderFactory.createEmptyBorder( 12, 2, 12, 8 ) );

		getContentPane().add( p );
		pack();
		setVisible( true );

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}

	public ImageOperation getIop(){ return iop; }

	/*
	private void setBorder( JPanel p, String title )
	{
		EmptyBorder b1 = new EmptyBorder( new Insets( 0,0,0,0 )); 
		Border b2 = BorderFactory.createTitledBorder( b1,  title );
		Border b3 = BorderFactory.createCompoundBorder( b2, BorderFactory.createEmptyBorder( 0, 20, 0, 0) );
		p.setBorder( b3 );
	}
	*/
	
	private CustomTableModel getFilterTableModel()
	{
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		for( ImageOperation filter : filters )
			data.add( getRowVec( filter.getName() ) );

		return new CustomTableModel( data, "Plugin name" );
	}

	public void initFilterStack()
	{
		initFilterStack( stackTable.getSelectedRow() );
	}

	public void initFilterStack( int selIndex )
	{
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		for( int i = 0; i < iop.getFilterStack().size(); i++ )
		{

			ImageOperation filter = iop.getFilterStack().elementAt( i );
			String target = "";
			if( filter == FilterStackController.getEditTarget() )
				target =  " < E >";

			data.add( getRowVec( filter.getName() + target ) );
		}
		Vector<String> colNames = new Vector<String>();
		colNames.add( "Filter Stack" );
	
		CustomTableModel stackModel = (CustomTableModel) stackTable.getModel();
		stackModel.setDataVector( data, colNames );

		if( selIndex < stackTable.getRowCount() && selIndex > -1 )
			stackTable.setRowSelectionInterval( selIndex, selIndex );

		repaint();
	}

	private Vector<String> getRowVec( String str )
	{
		Vector<String> vec = new Vector<String>();
		vec.add( str );
		//vec.add( new ImageIcon(PHButtonFactory.EXIT_IMG ) );
		return vec;
	}

	public void actionPerformed( ActionEvent e )
	{
		if( e.getSource() == addButton )
			FilterStackController.addFilter( iop, filters.elementAt( pluginTable.getSelectedRow() ) );

		if( e.getSource() == deleteFilter )
			FilterStackController.removeFilter( iop, stackTable.getSelectedRow() );

		if( e.getSource() == filterUp )
			FilterStackController.moveFilterUp( iop, stackTable.getSelectedRow()  );

		if( e.getSource() == filterDown )
			FilterStackController.moveFilterDown( iop, stackTable.getSelectedRow()  );

		if( e.getSource() == editTargetButton )
			FilterStackController.setStackIOPEditTarget( iop, stackTable.getSelectedRow() );

		if( e.getSource() == exitButton )
			FilterStackController.closeEditor();
	}

	class CustomTableModel extends DefaultTableModel
	{
		public CustomTableModel( Vector<Vector<String>> data, String columnName )
		{
			super( data.size(), 1);
			Vector<String> columnIdentifiers = new Vector<String>();
			columnIdentifiers.add( columnName );
			setDataVector( data, columnIdentifiers);
		}

		public boolean isCellEditable(int row, int column){ return false; } 
	}

}//end class
