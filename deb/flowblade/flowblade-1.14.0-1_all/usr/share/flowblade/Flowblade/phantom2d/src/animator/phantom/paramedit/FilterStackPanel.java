package animator.phantom.paramedit;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

//import com.jhlabs.image.CellularFilter.Point;

import animator.phantom.controller.FilterStackController;
import animator.phantom.controller.GUIComponents;
import animator.phantom.controller.LayerCompositorMenuActions;
import animator.phantom.controller.ParamEditController;
import animator.phantom.controller.UpdateController;
import animator.phantom.gui.GUIColors;
import animator.phantom.gui.GUIResources;
import animator.phantom.gui.IOPMenuItem;
import animator.phantom.gui.NodesPanel;
import animator.phantom.gui.PHButtonFactory;
import animator.phantom.plugin.PhantomPlugin;
import animator.phantom.renderer.IOPLibrary;
import animator.phantom.renderer.ImageOperation;

public class FilterStackPanel extends JPanel implements ActionListener//, MouseListener
{
	private ImageOperation iop;
	private JTable stackTable;

	private JPopupMenu filtersPopup;
	private Vector<String> groups;
	private Vector<Vector<ImageOperation>> groupIops;
	private Vector<Vector<String>> groupClasses;

	//private JLabel addFilterPopupArea;
	private JButton addFilter;
	private JButton deleteFilter;
	private JButton filterDown;
	private JButton filterUp;
	private JButton editTargetButton;

	private static final int ROW_HEIGHT = 20;
	private static final int BUTTON_TABLE_GAP = 4;
	private static final int TABLES_WIDTH = 266;
	private static final int STACK_TABLE_HEIGHT = 140;
	private static final int NAME_PANEL_PAD = 8;
	private static final int SUB_TITLE_GAP = 2;

	public FilterStackPanel( ImageOperation iop ) 
	{
		this.iop = iop;
		GUIComponents.filterStackPanel = this;

		//addFilterPopupArea = new JLabel(GUIResources.getIcon(  GUIResources.addClip ) );
		addFilter = new JButton( GUIResources.getIcon(  GUIResources.addClip ) );
		deleteFilter = new JButton( GUIResources.getIcon(  GUIResources.deleteClip ) );
		filterDown = new JButton( GUIResources.getIcon(  GUIResources.clipDown ) );
		filterUp = new JButton( GUIResources.getIcon( GUIResources.clipUp ) );
		
		GUIResources.prepareMediumButton( addFilter, this, "Add Filter" );
		GUIResources.prepareMediumButton( deleteFilter, this, "Delete Selected Filter" );
		GUIResources.prepareMediumButton( filterDown, this, "Move Selected Filter Down" );
		GUIResources.prepareMediumButton( filterUp, this, "Move Selected Filter Up" );

		JPanel namePanel = new JPanel();
		namePanel.setLayout(new BoxLayout( namePanel, BoxLayout.X_AXIS));
		namePanel.add( Box.createRigidArea( new Dimension( NAME_PANEL_PAD, 0 ) ) );
		namePanel.add( iop.getNamePanel() );

		editTargetButton = PHButtonFactory.getButton( "Edit" );
		editTargetButton.addActionListener( this );

		JPanel stackButtons = new JPanel();
		stackButtons.setLayout(new BoxLayout( stackButtons, BoxLayout.X_AXIS));
		//stackButtons.add( addFilterPopupArea );
		//stackButtons.add( Box.createRigidArea(new Dimension( 12, 0 ) ) );
		stackButtons.add( deleteFilter );
		stackButtons.add( filterDown  );
		stackButtons.add( filterUp );
		stackButtons.add( Box.createHorizontalGlue() );
		stackButtons.add( editTargetButton );

		stackTable = new JTable( new CustomTableModel( new Vector<Vector<String>>(), "" ) );
		stackTable.setPreferredScrollableViewportSize(new Dimension( TABLES_WIDTH, STACK_TABLE_HEIGHT));
		stackTable.setFillsViewportHeight( true );
		stackTable.setColumnSelectionAllowed( false );
		stackTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		stackTable.setShowGrid( false );
		stackTable.setRowHeight(ROW_HEIGHT );
		stackTable.setFont( GUIResources.BOLD_FONT_12 );
		stackTable.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent me) {
		        if (me.getClickCount() == 2) {
					setIOPEdited();
		        }
		    }
		});
		
		initFilterStack( 0 );
		 //initPopupMenu();

		JScrollPane stackScrollPane = new JScrollPane( stackTable );
		GUIComponents.filterStackTablePane = stackScrollPane;
		
		setLayout(new BoxLayout( this, BoxLayout.Y_AXIS));
		add( Box.createRigidArea( new Dimension( 0, SUB_TITLE_GAP ) ) );
		add( stackButtons );
		add( Box.createRigidArea( new Dimension( 0, BUTTON_TABLE_GAP ) ) );
		add( stackScrollPane );

		this.setComponentPopupMenu( filtersPopup );

		EmptyBorder b1 = new EmptyBorder( new Insets( 0,0,0,0 )); 
		TitledBorder b2 = (TitledBorder) BorderFactory.createTitledBorder( 	b1,
								"Layer Filters",
								TitledBorder.CENTER,
								TitledBorder.TOP );
		b2.setTitleColor( GUIColors.grayTitle );
		Border b3 = BorderFactory.createCompoundBorder( b2, BorderFactory.createEmptyBorder( 0, 0, 0, 4));
		setBorder( b3 );
	
	}

	public ImageOperation getIop(){ return iop; }

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
		colNames.add( "" );
	
		CustomTableModel stackModel = (CustomTableModel) stackTable.getModel();
		stackModel.setDataVector( data, colNames );

		if( selIndex < stackTable.getRowCount() && selIndex > -1 )
			stackTable.setRowSelectionInterval( selIndex, selIndex );

		repaint();
	}

		/*
	private void initPopupMenu()
	{
		groups = IOPLibrary.getGroupKeys();
		groupIops = new Vector<Vector<ImageOperation>>();
		groupClasses = new Vector<Vector<String>>();
		for( String group : groups )
		{
			Vector<ImageOperation> iops = new Vector<ImageOperation>();
			Vector<String> filterClasses = new Vector<String>();
			@SuppressWarnings("rawtypes")
			Vector iopsObjs = IOPLibrary.getGroupContents( group );

			for( int j = 0; j < iopsObjs.size(); j++ )
			{
				Object o = iopsObjs.elementAt( j );
				if( o instanceof ImageOperation )
				{
					ImageOperation iop = (ImageOperation) o;
					if( iop.makeAvailableInFilterStack == true )
					{
							iops.add( iop );
							filterClasses.add( iop.getClass().getName() );
					}
				}
				else
				{
					PhantomPlugin p = (PhantomPlugin) o;
					System.out.println("kkkkkkk" + p.getName());
					if( p.getIOP().makeAvailableInFilterStack == true )
					{
						//--- No merge type filters can be stack filters.
						if( !p.getIOP().hasMaskInput() && p.getIOP().getInputsCount() == 2 )
							continue;
	
						iops.add( p.getIOP() );
						filterClasses.add( p.getClass().getName() );
						System.out.println(p.getName());
					}
				}
	
			}
			groupIops.add( iops );
			groupClasses.add( filterClasses );
		}
		
		// Remove empty groups
		Vector<Vector<ImageOperation>> removeIopVectors = new Vector<Vector<ImageOperation>>();
		Vector<String> removeGroups = new Vector<String>();
		Vector<Vector<String>> removeClassGroups = new Vector<Vector<String>>();
		for( int i = 0; i < groups.size(); i++ )
		{
			Vector<ImageOperation> iopGroup = groupIops.elementAt( i );
			Vector<String> filterClasses = groupClasses.elementAt( i );
			if( iopGroup.size() == 0)
			{
				removeIopVectors.add( iopGroup );
				removeGroups.add( groups.elementAt( i ) );
				removeClassGroups.add( filterClasses );
			}
		}
		groups.removeAll( removeGroups );
		groupIops.removeAll( removeIopVectors );
		groupClasses.removeAll( removeClassGroups );
		
		
		//--- Node context popup menu
		filtersPopup = new JPopupMenu();
		/*
		Vector<JMenu> nodeGroupMenus = getNodesMenus( this );
		for( JMenu subMenu : nodeGroupMenus )
		{
			filtersPopup.add( subMenu );
		}

	}
		*/
	/*
	@SuppressWarnings("unchecked")
	public Vector<JMenu> getNodesMenus(  ActionListener listener )
	{
		//Vector<String> groups = IOPLibrary.getGroupKeys();
		Vector<JMenu> groupMenus = new Vector<JMenu>();

		for( int i = 0; i < groups.size(); i++ )
		{
			String group = groups.elementAt( i );
			Vector<ImageOperation> iops = groupIops.elementAt( i );
			Vector<String> classNames = groupClasses.elementAt( i );
			Collections.sort( iops );
			JMenu subMenu = new JMenu( group );
			
			for( int j = 0; j < iops.size(); j++ )
			{
				ImageOperation iop = iops.elementAt( j );
				String claasName  = classNames.elementAt( j );
				IOPMenuItem item =  new IOPMenuItem( iop.getName(), claasName );
				System.out.println("mmmm" + iop.getName());
				item.addActionListener(listener);
				subMenu.add( item );
			}
			groupMenus.add( subMenu );
		}
		return groupMenus;
	}*/
	
	private Vector<String> getRowVec( String str )
	{
		Vector<String> vec = new Vector<String>();
		vec.add( str );
		return vec;
	}
	
	public void actionPerformed( ActionEvent e )
	{

		if( e.getSource() == deleteFilter )
		{
			int index = stackTable.getSelectedRow();
			if(  index == -1 )
				return;

			iop.getFilterStack().removeElementAt( index );
			initFilterStack( 0 );
	 		UpdateController.valueChangeUpdate();
		}

		if( e.getSource() == filterDown )
		{
			int index = stackTable.getSelectedRow();
			if( index == iop.getFilterStack().size() - 1 || index == -1 )
				return;
	
			ImageOperation filter = (ImageOperation) iop.getFilterStack().remove( index );
			index++;
			iop.getFilterStack().insertElementAt( filter, index );

			initFilterStack( index );
			UpdateController.valueChangeUpdate();
		}

		if( e.getSource() == filterUp )
		{
			int index = stackTable.getSelectedRow();
			if( index < 1 )
				return;
			ImageOperation filter = (ImageOperation) iop.getFilterStack().remove( index );
			index--;
			iop.getFilterStack().insertElementAt( filter, index );
			initFilterStack( index );
	
			UpdateController.valueChangeUpdate();
		}

		if( e.getSource() == editTargetButton )
		{
			setIOPEdited();
		}
		
		/*
		if( e.getSource() instanceof IOPMenuItem )
		{
			IOPMenuItem source = ( IOPMenuItem )e.getSource();
			ImageOperation filterIop = IOPLibrary.getNewInstance( source.getIopClassName() );
			ParamEditController.addSelectedIOPToFilterStack( filterIop );
		}
		*/
	}

		/*
	public void mousePressed(MouseEvent e)
	{
		showFiltersPopUp( e );
	}

	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	
	private void showFiltersPopUp( MouseEvent e )
	{
		filtersPopup.show( e.getComponent(), e.getX(), e.getY() );
	}
	*/
	private void setIOPEdited()
	{
		int index = stackTable.getSelectedRow();
		if(  index == -1 )
			return;
	
		ImageOperation stackFilter = iop.getFilterStack().elementAt( index );
		UpdateController.editTargetIOPChangedFromStackEditor( stackFilter );
		ParamEditController.displayEditFrame( stackFilter );
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
}
