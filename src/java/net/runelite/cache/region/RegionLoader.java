/*
 * Copyright (c) 2016-2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.cache.region;

import net.runelite.cache.IndexType;
import net.runelite.cache.definitions.LocationsDefinition;
import net.runelite.cache.definitions.MapDefinition;
import net.runelite.cache.definitions.loaders.LocationsLoader;
import net.runelite.cache.definitions.loaders.MapLoader;
import net.runelite.cache.fs.Archive;
import net.runelite.cache.fs.Index;
import net.runelite.cache.fs.Storage;
import net.runelite.cache.fs.Store;
import net.runelite.cache.util.KeyProvider;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RegionLoader
{
	private static final int MAX_REGION = 32768;

	private final Store store;
	private final Index index;
	private final KeyProvider keyProvider;

	private final Map<Integer, Region> regions = new HashMap<>();
	private Region lowestX = null, lowestY = null;
	private Region highestX = null, highestY = null;

	public RegionLoader(Store store, KeyProvider keyProvider)
	{
		this.store = store;
		index = store.getIndex(IndexType.MAPS);
		this.keyProvider = keyProvider;
	}

	public void loadRegions() throws IOException
	{
		if (!this.regions.isEmpty())
		{
			return;
		}

		for (int i = 0; i < MAX_REGION; ++i)
		{
			try
			{
				this.loadRegionFromArchive(i);
			}
			catch (IOException ex)
			{
				System.out.println("Can't decrypt region " + i);
			}
		}
	}

	public Region loadRegionFromArchive(int i) throws IOException
	{
		int x = i >> 8;
		int y = i & 0xFF;

		Storage storage = store.getStorage();
		Archive map = index.findArchiveByName("m" + x + "_" + y);
		Archive land = index.findArchiveByName("l" + x + "_" + y);

		assert (map == null) == (land == null);

		if (map == null || land == null)
		{
			return null;
		}

		byte[] data = map.decompress(storage.loadArchive(map));

		MapDefinition mapDef = new MapLoader().load(x, y, data);

		Region region = new Region(i);
		region.loadTerrain(mapDef);

		int[] keys = keyProvider.getKey(i);
		if (keys != null)
		{
			data = land.decompress(storage.loadArchive(land), keys);
			LocationsDefinition locDef = new LocationsLoader().load(x, y, data);
			region.loadLocations(locDef);
		}

		regions.put(i, region);

		return region;
	}

	public Region loadRegion(int id, MapDefinition map, LocationsDefinition locs)
	{
		Region r = new Region(id);
		r.loadTerrain(map);
		if (locs != null)
		{
			r.loadLocations(locs);
		}
		regions.put(id, r);
		return r;
	}

	public void calculateBounds()
	{
		for (Region region : regions.values())
		{
			if (lowestX == null || region.getBaseX() < lowestX.getBaseX())
			{
				lowestX = region;
			}

			if (highestX == null || region.getBaseX() > highestX.getBaseX())
			{
				highestX = region;
			}

			if (lowestY == null || region.getBaseY() < lowestY.getBaseY())
			{
				lowestY = region;
			}

			if (highestY == null || region.getBaseY() > highestY.getBaseY())
			{
				highestY = region;
			}
		}
	}

	public Collection<Region> getRegions()
	{
		return regions.values();
	}

	public Region findRegionForWorldCoordinates(int x, int y)
	{
		x >>>= 6;
		y >>>= 6;
		return regions.get((x << 8) | y);
	}

	public Region findRegionForRegionCoordinates(int x, int y)
	{
		return regions.get((x << 8) | y);
	}

	public Region getLowestX()
	{
		return lowestX;
	}

	public Region getLowestY()
	{
		return lowestY;
	}

	public Region getHighestX()
	{
		return highestX;
	}

	public Region getHighestY()
	{
		return highestY;
	}
}
