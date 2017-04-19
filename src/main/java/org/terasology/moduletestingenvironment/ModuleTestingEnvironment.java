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
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.nio.file.ShrinkWrapFileSystems;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.TerasologyTestingEnvironment;
import org.terasology.context.Context;
import org.terasology.engine.GameEngine;
import org.terasology.engine.StateChangeSubscriber;
import org.terasology.engine.TerasologyEngine;
import org.terasology.engine.TerasologyEngineBuilder;
import org.terasology.engine.modes.StateIngame;
import org.terasology.engine.paths.PathManager;
import org.terasology.engine.subsystem.headless.HeadlessAudio;
import org.terasology.engine.subsystem.headless.HeadlessGraphics;
import org.terasology.engine.subsystem.headless.HeadlessInput;
import org.terasology.engine.subsystem.headless.HeadlessTimer;
import org.terasology.engine.subsystem.headless.mode.HeadlessStateChangeListener;
import org.terasology.registry.CoreRegistry;

import java.nio.file.FileSystem;
import java.util.Set;

public class ModuleTestingEnvironment {
    private static final Logger logger = LoggerFactory.getLogger(TerasologyTestingEnvironment.class);
    private boolean doneLoading;
    protected TerasologyEngine host;
    protected Context hostContext;

    @Before
    public void setup() throws Exception {
        final JavaArchive homeArchive = ShrinkWrap.create(JavaArchive.class);
        final FileSystem vfs = ShrinkWrapFileSystems.newFileSystem(homeArchive);
        PathManager.getInstance().useOverrideHomePath(vfs.getPath(""));

        TerasologyEngineBuilder terasologyEngineBuilder = new TerasologyEngineBuilder();
        terasologyEngineBuilder
                .add(new HeadlessGraphics())
                .add(new HeadlessTimer())
                .add(new HeadlessAudio())
                .add(new HeadlessInput());

        TerasologyEngine terasologyEngine = terasologyEngineBuilder.build();
        host = terasologyEngine;
        CoreRegistry.put(GameEngine.class, terasologyEngine);
        terasologyEngine.initialize();
        terasologyEngine.subscribeToStateChange(new HeadlessStateChangeListener(terasologyEngine));
        terasologyEngine.changeState(new TestingStateHeadlessSetup(getDependencies()));

        doneLoading = false;
        terasologyEngine.subscribeToStateChange(new StateChangeSubscriber() {
            @Override
            public void onStateChange() {
                if(terasologyEngine.getState() instanceof StateIngame) {
                    hostContext = ((StateIngame) terasologyEngine.getState()).getContext();
                    doneLoading = true;
                }
            }
        });

        while(!doneLoading && terasologyEngine.tick()) { /* do nothing */ }
    }

    @After
    public void tearDown() throws Exception {
        host.shutdown();
    }

    /**
     * Override this to change which modules must be loaded for the environment
     * @return
     */
    public Set<String> getDependencies() {
        return Sets.newHashSet("engine");
    }
}