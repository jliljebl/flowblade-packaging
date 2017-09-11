package animator.phantom.undo;

/*
    Copyright Janne Liljeblad

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

import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.param.Param;
import animator.phantom.xml.AbstractXML;

public class PhantomUndoManager
{
	private static Vector<PhantomUndoableEdit> undoStack;
	//--- index == index of next redo
	//--- index = index of next undo + 1
	//--- so add 1 action and  index will be 1, there will be no possible redos 
	//--- and index of next undo will be 0
	private static int index = 0;

	private static Document doc;//--- empty xml document required for Element creation

	//--- changed by last undo / redo
	//--- Needed so that key frame Vector can be updated when undo / redo done
	private static ImageOperation lastActionIOP = null;

	public static void init()
	{
		undoStack = new Vector<PhantomUndoableEdit>();	
		index = 0;

		//--- Create doc available for element creators.
		try 
		{
			//--- Create Document
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			doc = docBuilder.newDocument();
		}
		catch (Exception e) 
		{
			System.out.println("PhantomUndoManager init failed.");
			System.exit(1);
        }

		//--- Set UndoEngine.doc available for element creators.
		AbstractXML.setDoc( doc );
	}

	public static void addUndoEdit( PhantomUndoableEdit undoEdit )
	{
		if( index != undoStack.size() )
			clearRedos();

		undoStack.add( undoEdit );
		index++;
	}

	//--- Remove all actions with indexes >= index
	public static void clearRedos()
	{
		while( undoStack.size() != index )
			undoStack.remove( undoStack.size() - 1 );
	}

	public static void doUndo()
	{
		if( index == 0 ) return;

		boolean more = true;
		while( more )
		{
			index--;
			PhantomUndoableEdit undoEdit = undoStack.elementAt( index );
			undoEdit.undo();
			lastActionIOP = undoEdit.getIOP();
			if( undoEdit.isSignificant() ) more = false;
			if( index == 0 && !undoEdit.isSignificant()) 
			{
				System.out.println( "BING,BING,BING!!!! undo in index 0 NOT SIGNIFICANT" );
				System.exit( 1 );
			}
		}
	}

	public static void doRedo()
	{
		if( index == undoStack.size() ) return;

		boolean more = true;
		boolean first = true;

		while( more )
		{
			PhantomUndoableEdit redoEdit = undoStack.elementAt( index );

			if( !first && redoEdit.isSignificant() )
				more = false;
			else
			{
				redoEdit.redo();
				lastActionIOP = redoEdit.getIOP();
				index++;
			}

			if( index == undoStack.size() )
				more = false;

			first = false;
		}
	}

	//--- for undos the default avlue needs to saved as Param.currentValue
	public static void newIOPCreated( ImageOperation iop )
	{
		Vector<Param>  params = iop.getParameters();
		for( int i = 0; i < params.size(); i++ )
		{
			Param p = (Param) params.elementAt( i );
			p.initCurrentValue();
		}
	}

	//--- Returns iop that was changed with last undo /redo
	public static ImageOperation getLastActionIOP(){ return lastActionIOP; }

}//end class
