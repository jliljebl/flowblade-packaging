package animator.phantom.controller;

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

import animator.phantom.renderer.FileSource;
import animator.phantom.renderer.ImageOperation;

//--- Initializes cache on background thread after project opened.
class CacheInitializer extends Thread
{
	public void run()
	{
		synchronized( MemoryManager.lock )
		{
			//--- Clear cache and view cache vector.
			MemoryManager.clearCache();
			MemoryManager.viewEditorCache.clear();

			//--- Cache current layer file source if available.
			ImageOperation iop = GUIComponents.viewEditor.getCurrentLayerIOP();
			FileSource currenLayerFS = null;
			if( iop != null ) currenLayerFS = iop.getFileSource();
			if( currenLayerFS != null )
			{
				MemoryManager.viewEditorCache.add( currenLayerFS );
				currenLayerFS.setInCache( true );
				currenLayerFS.cacheOrClearData();
				MemoryManager.viewCacheSize = currenLayerFS.getSizeEstimate();
			}

			//--- Cache FileSources
			MemoryManager.cacheBiggestImages();

			//--- Load and release memory
			MemoryManager.executeCacheChange();

			MemoryManager.printCache();
		}
	}

}//end  class