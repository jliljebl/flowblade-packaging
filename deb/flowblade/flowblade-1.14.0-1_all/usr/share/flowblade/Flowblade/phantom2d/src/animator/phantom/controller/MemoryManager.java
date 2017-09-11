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

import java.util.Collections;
import java.util.Vector;

import animator.phantom.renderer.FileSource;

//--- Manger memory, shares it between source cache, view editor cache 
//--- and the amount available for RAM previews
public class MemoryManager
{
	//--- Cache update lock
	protected static Object lock = new Object();
	//--- Mamimum availble free memory.
	private static long maxMem;
	//--- Free memory after application and project loaded.
	private static long appFreeMem;
	//--- Calculated based on preview share after app start.
	private static long cacheMemMax;
	//--- Calculated view editor cache max mem.
	private static long viewMemMax;
	//--- Estimate of size one BufferedImage in screen size.
	private static long frameMemEst;
	//--- Current view cache size
	protected static long viewCacheSize = 0;
	//--- Current view cache size
	private static long svgCacheSize = 0;
	//--- Images that have been worked in view editor are cached here
	//--- is fifo so that file sources of last used layers are in cache.
	protected static Vector<FileSource> viewEditorCache = new Vector<FileSource>();
	//--- Minimum work memory held when rendering previews or caching images.
	private static long MINIM_WORK_MEMORY = 10 * 1024 * 1024;
	//--- Share of preview memory of memory used after app start. User settable.
	protected static float PREVIEW_SHARE = 0.6f;
	//--- Share of cache that is given to view editor cache stach / over policy images. User settable.
	protected static float VIEW_EDITOR_SHARE = 0.5f;
	//--- Max size 0f viewEditorCache -stack. NOT user settable
	private static int MAX_VIEW_EDITOR_CACHE_SIZE = 7;

	//------------------------------------------------------------------ INTERFACE
	//--- This is called as early as possible in startup so app memory consumption
	//--- can be estimated.
	public static void initMemoryManager()
	{	
		Runtime r = Runtime.getRuntime();
		maxMem = r.maxMemory();

		System.out.println("MAX FREE MEM: " + getMBString(maxMem ) );
	}

	//--- This is called after project is loaded so that cache sizes can be calculated.
	//--- Cache sizes ARE NOT UPDATED while user works so MINIMUM_WORK_MEMORY is the amount of 
	//--- memory available to app just after project load, then available memory decreases
	//--- as user adds new stuff into project.
	public static void calculateCacheSizes()
	{
		//--- To get best estimate.
		garbageCollect();

		//--- Get free mem.
		Runtime r = Runtime.getRuntime();
		appFreeMem = r.freeMemory();

		System.out.println("FREE MEMORY AFTER PROJECT LOAD: " + getMBString(appFreeMem ) );
		System.out.println("APP + PROJECT MEMORY CONSUMPTION: " + getMBString((maxMem - appFreeMem )) );

		//--- Calculates maximum cache.
		long sharableWorkMemory = appFreeMem - MINIM_WORK_MEMORY;
		cacheMemMax = (long) Math.round( (float) sharableWorkMemory * ( 1.0f - PREVIEW_SHARE ) );
		viewMemMax = (long) Math.round( (float) cacheMemMax * VIEW_EDITOR_SHARE );
		//--- Estimate memory comsumptio of single preview frame.
		frameMemEst = AppUtils.getImageSizeEstimate( ProjectController.getScreenSample() );

		//--- 
		System.out.println("FILE CACHE MAX: " + getMBString(cacheMemMax ));
		System.out.println("MINIM AVAILABLE FOR PREVIEWS EST: " + getMBString(( appFreeMem - cacheMemMax - MINIM_WORK_MEMORY )) );
		printPreviewEstimate();
	}
	//--- This is callad after project is loaded to set caches into initial state.
	public static void initCache()
	{
		System.out.println("INITIAL CACHE UPDATE " );
		CacheInitializer cinit = new CacheInitializer();
		cinit.start();
	}
	//--- This is called when preview render is started.
	public static void previewRenderStarting()
	{
		System.out.println("PREVIEW RENDER STARTING");
		garbageCollect();
	}
	//--- This is called before every frame when doing preview render 
	//--- to make shure there is enough memory for next frame.
	public static boolean canRenderNextPreview()
	{
		Runtime r = Runtime.getRuntime();
		long freeMem = r.freeMemory();

		if( freeMem - MINIM_WORK_MEMORY > frameMemEst )	return true;

		garbageCollect();
		if( freeMem - MINIM_WORK_MEMORY > frameMemEst ) return true;
		return false;
	}

	//--- FileSources have been added to project. Updates file cache.
	public static void fileSourcesAdded()
	{

		CacheUpdater update = new CacheUpdater();
		update.start();
	}
	//--- View editor layer has been set. We need to cache the FileSource being edited, if exists.
	public static void cacheViewEditFileSource( FileSource fs )
	{
		if( fs == null ) return;
		if( Application.isLoading() ) return;
		
		//--- Remove earlier version in cache.
		viewEditorCache.remove( fs );
		//--- Set as first in cache vec
		viewEditorCache.add( 0, fs );
		//--- If bigger that limit remove last
		if( viewEditorCache.size() == MAX_VIEW_EDITOR_CACHE_SIZE ) viewEditorCache.remove( viewEditorCache.size() - 1 );

		CacheUpdater update = new CacheUpdater();
		update.start();
	}
	//--- FileSources removed
	public static void deleteFromViewCache( Vector<FileSource> vec )
	{
		viewEditorCache.removeAll( vec );
	}
	
	//-------------------------------------------------------- CACHE OPS
	//--- Clears cache for full rebuilding for current state.
	//--- Called as first step of cache update.
	protected static void clearCache()
	{
		viewCacheSize = 0;
		Vector<FileSource> fileSources = ProjectController.getFileSources();
		for( FileSource fs : fileSources )
			fs.setInCache( false );
	}
	//--- Sets file sources to null or loads them into memory, depending on keepImageInMemory -flag.
	//--- Called as last step of cache update.
	protected static void executeCacheChange()
	{
		Vector<FileSource> fileSources = ProjectController.getFileSources();
		for( FileSource fs : fileSources )
			fs.cacheOrClearData();
	}
	//--- Flags as many of the biggest images to be cached as possible
	//--- NOTE: expects file sources are arranged by  getSizeSortedFileSources( ... )
	protected static void cacheBiggestImages()
	{
		//--- Get fileSources in size sorted vector
		Vector<FileSource> fileSources = ProjectController.getFileSources();
		Collections.sort( fileSources );
		//--- Start from last (biggest) fs and cache as many FileSources as there is room for.
		//--- view cache and svgs are already cached and space used by them is 
		long usedCache = viewCacheSize + svgCacheSize;
		for( int i = fileSources.size() -1; i > -1; i-- )
		{
			FileSource fs = fileSources.elementAt( i );
	
			//--- View cache is already set to be cached
 			//--- and used memory calculated.
			if( inViewCache( fs ) ) continue;

			if( usedCache + fs.getSizeEstimate() < MemoryManager.cacheMemMax )
			{
				fs.setInCache( true );
				usedCache += fs.getSizeEstimate();
			}
			else break;
		}
	}
	//--- Returns true if fs in mmfs is in view cache.
	protected static boolean inViewCache( FileSource fs )
	{
		for( int i = 0; i < MemoryManager.viewEditorCache.size(); i++ )
			if( fs == viewEditorCache.elementAt( i ) ) return true;

		return false;
	}

	protected static void updateViewEditorCache()
	{
		long usedCache = 0;

		//--- Keep all in view cache that that fit.
		Vector<FileSource> killVec = new Vector<FileSource>();
		for( int i = 0; i < viewEditorCache.size(); i++ )
		{
			FileSource fs = viewEditorCache.elementAt( i );

			if( usedCache + fs.getSizeEstimate() < MemoryManager.viewMemMax )
			{
				fs.setInCache( true );
				usedCache += fs.getSizeEstimate();
			}
			else
			{
				fs.setInCache( false );// probably reduntant
				killVec.add( fs );
			}
		}
		//--- Remove from view cache the ones that didn't fit
		for( FileSource fs : killVec )
			viewEditorCache.remove( fs );

		viewCacheSize = usedCache;
	}

	//------------------------------------------------------- GARBAGE COLLECTION
	//--- Requests carbage collection and provides information.
	protected static void garbageCollect()
	{
		System.out.println("GARBAGE COLLECTION");
		
		Runtime r = Runtime.getRuntime();
		long freeMem = r.freeMemory();
		long startTime = System.currentTimeMillis();

		System.gc();

		long endTime = System.currentTimeMillis();
		long endFreeMem = r.freeMemory();

		System.out.println( "Freed " + getMBString( ( endFreeMem - freeMem ) )+ " in " + (endTime-startTime) + " ms." );
		System.out.println( "Free memory:" + getMBString( endFreeMem ) );
	}

	//------------------------------------------------------ OUT OF MEMORY HANDLING
	public static void handlePreviewOutOfMemory( Vector<NumberedFrame> frames )
	{
		//--- Remove some frames to free some memory
		int framesToRemove = (int) ( MINIM_WORK_MEMORY / frameMemEst ) + 1;


		if( frames.size() > framesToRemove )
		{
			int lastRemovedIndex = frames.size() - 1 - framesToRemove;
			for( int i = frames.size() - 1; i > lastRemovedIndex; i-- )
				frames.removeElementAt( i );
			System.out.println( "MemoryManager: Freed " + framesToRemove + "frames." );
		}
		else
		{
			System.out.println( "MemoryManager; Freed all frames." );
			frames.clear();
		}

		garbageCollect();
	}


	//------------------------------------------------------- SIZE, PRINT AND OTHER HELP METHODS
	//--- Returns estimation of how many fullsize frames can be rendered in preview.
	public static int getPreviewSizeEstimate()
	{
		return (int) (( appFreeMem - cacheMemMax - MINIM_WORK_MEMORY ) / ( frameMemEst ) );
	}
	public static long getPreviewMemSizeEstimate()
	{
		return (( appFreeMem - cacheMemMax - MINIM_WORK_MEMORY ));
	}
	
	//--- String with amount of of memory in megabytes.
	public static String getMBString( long bytes )
	{
		float mb = (float) bytes / (1024.0f * 1024.0f);
		String str1 = (new Float( mb )).toString();
		int pointIndex = str1.lastIndexOf('.');
		String str2;
		if( pointIndex <= str1.length() + 1 ) str2 = str1.substring( 0, pointIndex + 2 );
		else str2 = str1;

		return str2 + " MB";
	
	}

	public static long getViewCacheSize(){ return viewCacheSize; }
	public static long getAppFreeMem() { return appFreeMem; }
	public static long getCacheMemMax(){ return cacheMemMax; }
	public static long getViewMemMax(){ return viewMemMax; }

	//--- print info on available preview size
	public static void printPreviewEstimate()
	{
		int preEst = getPreviewSizeEstimate();
		System.out.println("PREVIEW SIZE ESTIMATE: " + preEst + " frames." );
	}

	
	//--- print info
	public static void printCacheContents()
	{
		System.out.println("CACHE CONTENTS:");
		Vector<FileSource> fileSources = ProjectController.getFileSources();
		for( int i = 0; i < fileSources.size(); i++ )
		{
			FileSource fs = fileSources.elementAt( i );
			if( fs.dataInMemory() ) fs.printInfo();
		}
	}
	//--- print info
	public static void printViewCacheContents()
	{
		System.out.println("VIEW CACHE CONTENTS:");
		for( int i = 0; i < viewEditorCache.size(); i++ )
		{
			FileSource fs = viewEditorCache.elementAt( i );
			if( fs.dataInMemory() ) fs.printInfo();
		}
	}

	public static void printCache()
	{
		System.out.println("------------------------ CACHE STATE ---------------------------");
		System.out.println( "viewCacheSize:" + getMBString( viewCacheSize ) );
		System.out.println( "svgCacheSize:" + getMBString(  svgCacheSize ) );
		printViewCacheContents();
		printCacheContents();
	}
	
}//end class
