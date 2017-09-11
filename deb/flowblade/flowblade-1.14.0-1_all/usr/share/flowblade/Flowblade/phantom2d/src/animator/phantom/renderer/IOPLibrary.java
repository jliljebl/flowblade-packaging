package animator.phantom.renderer;

/*
    Copyright Janne Liljeblad.

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

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import animator.phantom.plugin.PhantomPlugin;

//--- Class for storing all available ImageOperations and their group structure.
public class IOPLibrary
{
	public static final int BOX_SOURCE = 0; 
	public static final int BOX_FILTER = 1;
	public static final int BOX_MERGE = 2;
	public static final int BOX_ALPHA = 3;
	public static final int BOX_MEDIA = 4;
	
	//--- ImageOperations available to application.
	private static Hashtable<String,ImageOperation> 
		imageOperations = new Hashtable<String,ImageOperation>();
	private static Hashtable<String,PhantomPlugin> 
		plugins = new Hashtable<String,PhantomPlugin>();
	//--- ImageOperation groups
	private static Hashtable<String,Vector<Object>> 
		iopGroups = new Hashtable<String,Vector<Object>>();
	//--- Group keys in add order
	private static Vector<String> groupsInAddOrder = new Vector<String>();
	//--- Filters Vector for filter stack edit
	//private static Vector<ImageOperation> filterIops = new Vector<ImageOperation>();
	//--- ImageOperation groups
	private static HashMap<String, String> 
		groupForClassName = new HashMap<String, String>();

	public static void registerGroup( String groupName )
	{
		iopGroups.put( groupName, new Vector<Object>() );
		groupsInAddOrder.add( groupName );
	}

	public static void registerIOP( ImageOperation iop, String group )
	{
		String hashKey = iop.getClass().getName();
		imageOperations.put( hashKey, iop );
		putToGroup( iop, group );
		//if( iop.makeAvailableInFilterStack == true )
		//	filterIops.add( iop );
		groupForClassName.put(iop.getClassIDName(), group );
	}

	public static void registerPlugin( PhantomPlugin plugin, String group )
	{
		savePlugin( plugin );
		putToGroup( plugin, group );
		groupForClassName.put(plugin.getIOP().getClassIDName(), group );
	}

	/*
	public static void registerPluginAlphaMask( PhantomPlugin plugin, String group )
	{
		plugin.getIOP().makeAvailableInFilterStack = false;
		plugin.getIOP().makeAvailableForLayerMasks = true;
		registerPlugin( plugin, group );
	}
	*/
	public static void registerNonUserPlugin( PhantomPlugin plugin )
	{
		savePlugin( plugin );
	}

	private static void savePlugin(  PhantomPlugin plugin )
	{
		String hashKey = plugin.getClass().getName();
		plugins.put( hashKey, plugin );

			/*
		//--- Add to stack filters if filter
		if( plugin.getType() == PhantomPlugin.FILTER )
		{
			//--- No merge type filters can be stack filters.
			if( !plugin.getIOP().hasMaskInput() && plugin.getIOP().getInputsCount() == 2 )
				return;

			filterIops.add( plugin.getIOP() );
		}

		if(  plugin.getIOP().makeAvailableInFilterStack == true )
			filterIops.add( plugin.getIOP() );
			*/
	}

	private static void putToGroup( Object groupItem, String group )
	{
		if( group != null )
		{
			Vector<Object> iopGroup = iopGroups.get( group );
			iopGroup.add( groupItem );
		}
	}

	public static Vector<Object> getGroupContents( String group ){ return iopGroups.get( group ); }

	public static ImageOperation getNewInstance( String className )	
	{
		ImageOperation iop = imageOperations.get( className );
		ImageOperation retIOP = null;
		try
		{
			//--- This is saved as iop
			if( iop != null )
			{
				retIOP = iop.getClass().newInstance();
			}
			else if( iop == null ) //--- This is saved as plugin
			{
				PhantomPlugin plugin = plugins.get( className );
				PhantomPlugin newInstance = plugin.getClass().newInstance();
				retIOP = newInstance.getIOP();
			}

		}
		catch( Exception e )
		{
			System.out.println("Ioplibrary.getNewInstance() catch");
		}

		return retIOP;
	}

	public static Vector<String> getGroupKeys()
	{
		Vector<String> sortedKeys = (Vector<String> )groupsInAddOrder.clone();
		Collections.sort(sortedKeys);// remove this to get order defined in IOPLibraryInitializer
		return sortedKeys;
	}
	
	public static int libSize(){ return imageOperations.size(); }
	
	public static void clear()
	{
		imageOperations = new Hashtable<String,ImageOperation>();
		iopGroups = new Hashtable<String,Vector<Object>>();
		groupsInAddOrder = new Vector<String>();
		//filterIops.clear();
	}
	
	//public static Vector<ImageOperation> getFilters(){ return filterIops; }

	public static int getBoxType( ImageOperation iop )
	{
		String IDName =  iop.getClassIDName();
		if ( iop.getFileSource() != null )
			return BOX_MEDIA;
		else if (iop.isOutput() == true  )
			return BOX_SOURCE;
		else if ( groupForClassName.get(IDName).equals( "Source") == true || groupForClassName.get(IDName).equals( "Render") == true )
			return BOX_SOURCE;
		else if ( groupForClassName.get(IDName).equals( "Merge") == true )
			return BOX_MERGE;
		else if ( groupForClassName.get(IDName).equals( "Alpha") == true || groupForClassName.get(IDName).equals( "Mask") == true ||  groupForClassName.get(IDName).equals( "Key") )
			return BOX_ALPHA;
		else	
			return BOX_FILTER;
	}

}//end class
