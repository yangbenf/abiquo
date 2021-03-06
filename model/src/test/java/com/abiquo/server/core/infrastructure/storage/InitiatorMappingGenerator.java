/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package com.abiquo.server.core.infrastructure.storage;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class InitiatorMappingGenerator extends DefaultEntityGenerator<InitiatorMapping>
{

    VolumeManagementGenerator volumeGenerator;

    public InitiatorMappingGenerator(SeedGenerator seed)
    {
        super(seed);

        volumeGenerator = new VolumeManagementGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(InitiatorMapping obj1, InitiatorMapping obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, InitiatorMapping.TARGET_LUN_PROPERTY,
            InitiatorMapping.TARGET_IQN_PROPERTY, InitiatorMapping.INITIATOR_IQN_PROPERTY);

        volumeGenerator.assertAllPropertiesEqual(obj1.getVolumeManagement(),
            obj2.getVolumeManagement());
    }

    @Override
    public InitiatorMapping createUniqueInstance()
    {
        VolumeManagement vm = volumeGenerator.createUniqueInstance();

        return createInstance(vm);
    }

    public InitiatorMapping createInstance(VolumeManagement vm)
    {
        final String iIQN = newString(nextSeed(), 1, 10);
        final String targetIQN = newString(nextSeed(), 1, 10);
        final Integer targetLUN = nextSeed();

        InitiatorMapping initiatorMapping =
            new InitiatorMapping("IQN" + iIQN, vm, "targetIQN" + targetIQN, targetLUN);

        return initiatorMapping;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(InitiatorMapping entity,
        List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        VolumeManagement volumeManagement = entity.getVolumeManagement();
        volumeGenerator.addAuxiliaryEntitiesToPersist(volumeManagement, entitiesToPersist);
        entitiesToPersist.add(volumeManagement);

    }

}
