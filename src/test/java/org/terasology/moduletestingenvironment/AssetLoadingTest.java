/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.moduletestingenvironment;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;

import java.util.Set;

public class AssetLoadingTest extends ModuleTestingEnvironment {

    @Override
    public Set<String> getDependencies() {
        return Sets.newHashSet("engine", "Core", "ModuleTestingEnvironment");
    }

    @Test
    public void blockPrefabLoadingTest() {
        Block block = hostContext.get(BlockManager.class).getBlock("core:dirt");
        Assert.assertNotNull(block);
        Assert.assertEquals(9, block.getHardness());
        Assert.assertEquals("Dirt", block.getDisplayName());
    }

    @Test
    public void simpleLoadingTest() {
        AssetManager assetManager = hostContext.get(AssetManager.class);
        Assert.assertNotNull(assetManager.getAsset("core:axe", Prefab.class).get());
    }
}