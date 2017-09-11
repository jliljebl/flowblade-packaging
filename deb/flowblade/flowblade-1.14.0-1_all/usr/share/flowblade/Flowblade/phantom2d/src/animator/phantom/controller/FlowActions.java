package animator.phantom.controller;

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

import animator.phantom.renderer.FileSource;
import animator.phantom.renderer.FileSequenceSource;
import animator.phantom.renderer.FileSingleImageSource;
import animator.phantom.renderer.ImageOperation;
import animator.phantom.renderer.VideoClipSource;
import animator.phantom.renderer.imagesource.FileImageSource;
import animator.phantom.renderer.imagesource.ImageSequenceIOP;
import animator.phantom.renderer.imagesource.VideoClipIOP;
import animator.phantom.renderer.RenderNode;
import animator.phantom.undo.PhantomUndoManager;
import animator.phantom.undo.NFNodeAddUndoEdit;

public class FlowActions
{

	public static void addIOPFromFileSource( FileSource fs )
	{
		ImageOperation addIOP = getNewIOPFromSource( fs );
		addIOP.initIOPTimelineValues();

		RenderNode addNode = new RenderNode( addIOP );
		
		NFNodeAddUndoEdit undoEdit = new NFNodeAddUndoEdit( addNode );
		undoEdit.doEdit();

		PhantomUndoManager.addUndoEdit( undoEdit );
		ParamEditController.displayEditFrame( addIOP );// ALSO TO INIT PARAM NAMES IN RAW IOPS, plugins do this by themselves
	}

	private static ImageOperation getNewIOPFromSource( FileSource fs )
	{
		if( fs.getType() == FileSource.IMAGE_FILE )
			return new FileImageSource( (FileSingleImageSource) fs );
		if( fs.getType() == FileSource.IMAGE_SEQUENCE )
			return new ImageSequenceIOP( (FileSequenceSource) fs );
		if( fs.getType() == FileSource.VIDEO_FILE )
			return new VideoClipIOP( (VideoClipSource) fs );
		return null; //this will crash very soon, and it should
	}

	//--- Called from RenderFlowPanel witch creates addNode.
	public static void addRenderNode( RenderNode addNode )
	{

		ImageOperation iop = addNode.getImageOperation();
		TimeLineController.targetIopChanged( iop );

		EditorsController.addLayerForIop( iop );
		//--- If new iop does not have edit layer we need render view editor bg
		//--- because layer add did not trigger render.
		if( iop.getEditorlayer() == null )
			 EditorsController.displayCurrentInViewEditor( false );
		//--- Create initial state for undos
		PhantomUndoManager.newIOPCreated( iop );
		//--- Request editpanel so params will be named
		//--- Params are named in editors and names are used in save/load
		//--- and kfeditor
		//--- Do in thread because might take 500ms+
		final ImageOperation fholder = iop;
		new Thread()
		{
			public void run()
			{
				fholder.getEditFrame( false );
				EditorsController.initKeyFrameEditor( fholder );
			}
		}.start();
	}

}//end class
