package org.see.skf.runtime;

import org.junit.jupiter.api.Test;
import org.see.skf.annotations.Attribute;
import org.see.skf.annotations.ObjectClass;
import org.see.skf.util.models.ExecutionConfiguration;
import org.see.skf.util.encoding.HLAfloat64LECoder;
import org.see.skf.util.encoding.HLAunicodeStringCoder;
import org.see.skf.runtime.objects.ObjectClassModelParser;

import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectClassModelParserTest {
    final ObjectClassModelParser exCOParser = new ObjectClassModelParser(ExecutionConfiguration.class);
    final ObjectClassModelParser physicalEntityParser = new ObjectClassModelParser(PhysicalEntity.class);
    final ObjectClassModelParser dynamicalEntityParser = new ObjectClassModelParser(DynamicalEntity.class);
    final ObjectClassModelParser roverParser = new ObjectClassModelParser(Rover.class);

    @Test
    void testBasicMetadata() {
        assertEquals("HLAobjectRoot.ExecutionConfiguration", exCOParser.getFomClassName());
        assertNotNull(exCOParser.getFieldForFomElement("root_frame_name"));
        assertNotNull(exCOParser.getFieldForFomElement("least_common_time_step"));
        assertNotNull(exCOParser.getFieldForFomElement("scenario_time_epoch"));

        Field rootFrameName = exCOParser.getFieldForFomElement("root_frame_name");
        Field leastCommonTimeStep = exCOParser.getFieldForFomElement("least_common_time_step");
        Field scenarioTimeEpoch = exCOParser.getFieldForFomElement("scenario_time_epoch");
        assertNotNull(exCOParser.getFieldGetter(rootFrameName));
        assertNotNull(exCOParser.getFieldSetter(rootFrameName));
        assertNotNull(exCOParser.getFieldGetter(leastCommonTimeStep));
        assertNotNull(exCOParser.getFieldSetter(leastCommonTimeStep));
        assertNotNull(exCOParser.getFieldGetter(scenarioTimeEpoch));
        assertNotNull(exCOParser.getFieldSetter(scenarioTimeEpoch));

        assertNotNull(exCOParser.getFieldCoder(rootFrameName));
        assertNotNull(CoderCollection.query(exCOParser.getFieldCoder(rootFrameName)));
        assertNotNull(exCOParser.getFieldCoder(leastCommonTimeStep));
        assertNotNull(CoderCollection.query(exCOParser.getFieldCoder(leastCommonTimeStep)));
        assertNotNull(exCOParser.getFieldCoder(scenarioTimeEpoch));
        assertNotNull(CoderCollection.query(exCOParser.getFieldCoder(scenarioTimeEpoch)));

        assertEquals(ScopeLevel.SUBSCRIBE, exCOParser.getAttributeAccessLevel("root_frame_name"));
        assertEquals(ScopeLevel.SUBSCRIBE, exCOParser.getAttributeAccessLevel("least_common_time_step"));
        assertEquals(ScopeLevel.SUBSCRIBE, exCOParser.getAttributeAccessLevel("scenario_time_epoch"));
    }

    @Test
    void testBasicMethodGeneration() {
        assertEquals("getValue", exCOParser.generateMethodName("get", "value"));
        assertEquals("setValue", exCOParser.generateMethodName("set", "value"));
        assertEquals("getPositionVector", exCOParser.generateMethodName("get", "positionVector"));
        assertEquals("setPositionVector", exCOParser.generateMethodName("set", "positionVector"));
        assertEquals("getPhysicalInterface", exCOParser.generateMethodName("get", "physicalInterface"));
        assertEquals("setPhysicalInterface", exCOParser.generateMethodName("set", "physicalInterface"));
    }

    @Test
    void testPhysicalEntityFields() {
        assertEquals("HLAobjectRoot.PhysicalEntity", physicalEntityParser.getFomClassName());

        Field nameField = physicalEntityParser.getFieldForFomElement("name");
        Field statusField = physicalEntityParser.getFieldForFomElement("status");
        Field typeField = physicalEntityParser.getFieldForFomElement("type");

        assertNotNull(nameField);
        assertNotNull(statusField);
        assertNotNull(typeField);

        Set<String> publishableAttributes = physicalEntityParser.getPublishableAttributeNames();
        Set<String> subscribableAttributes = physicalEntityParser.getSubscribableAttributeNames();
        assertTrue(publishableAttributes.contains("name"));
        assertTrue(subscribableAttributes.contains("status"));
        assertTrue(publishableAttributes.contains("type"));

        assertEquals(ScopeLevel.PUBLISH, physicalEntityParser.getAttributeAccessLevel("name"));
        assertEquals(ScopeLevel.SUBSCRIBE, physicalEntityParser.getAttributeAccessLevel("status"));
        assertEquals(ScopeLevel.PUBLISH_SUBSCRIBE, physicalEntityParser.getAttributeAccessLevel("type"));
    }

    @Test
    void testDynamicalEntityFields() {
        Field nameField = dynamicalEntityParser.getFieldForFomElement("name");
        Field statusField = dynamicalEntityParser.getFieldForFomElement("status");
        Field typeField = dynamicalEntityParser.getFieldForFomElement("type");
        Field massField = dynamicalEntityParser.getFieldForFomElement("mass");
        Field massRateField = dynamicalEntityParser.getFieldForFomElement("mass_rate");

        assertNotNull(nameField);
        assertNotNull(statusField);
        assertNotNull(typeField);
        assertNotNull(massField);
        assertNotNull(massRateField);

        Set<String> publishableAttributes = dynamicalEntityParser.getPublishableAttributeNames();
        Set<String> subscribableAttributes = dynamicalEntityParser.getSubscribableAttributeNames();
        assertEquals("HLAobjectRoot.PhysicalEntity.DynamicalEntity", dynamicalEntityParser.getFomClassName());
        assertTrue(publishableAttributes.contains("name"));
        assertTrue(subscribableAttributes.contains("status"));
        assertTrue(publishableAttributes.contains("type"));
        assertTrue(subscribableAttributes.contains("type"));
        assertTrue(publishableAttributes.contains("mass"));
        assertTrue(publishableAttributes.contains("mass"));
        assertFalse(publishableAttributes.contains("mass_rate"));
        assertFalse(subscribableAttributes.contains("mass_rate"));

        assertEquals(ScopeLevel.PUBLISH, dynamicalEntityParser.getAttributeAccessLevel("name"));
        assertEquals(ScopeLevel.SUBSCRIBE, dynamicalEntityParser.getAttributeAccessLevel("status"));
        assertEquals(ScopeLevel.PUBLISH_SUBSCRIBE, dynamicalEntityParser.getAttributeAccessLevel("type"));
        assertEquals(ScopeLevel.PUBLISH, dynamicalEntityParser.getAttributeAccessLevel("mass"));
        assertEquals(ScopeLevel.NONE, dynamicalEntityParser.getAttributeAccessLevel("massRate"));
    }

    // The following classes are meant to be representative of the SpaceFOM object hierarchy which is as follows:
    // HLAobjectRoot -> PhysicalEntity -> DynamicalEntity -> Rover (a custom addition for test purposes)
    @ObjectClass(name = "HLAobjectRoot.PhysicalEntity")
    static class PhysicalEntity {
        @Attribute(name = "name", coder = HLAunicodeStringCoder.class, scope = ScopeLevel.PUBLISH)
        private String name;

        @Attribute(name = "status", coder = HLAunicodeStringCoder.class, scope = ScopeLevel.SUBSCRIBE)
        private String status;

        @Attribute(name = "type", coder = HLAunicodeStringCoder.class)
        private String type;

        public PhysicalEntity() {
            this.name = "physical_entity_1";
            this.type = "Vehicle";
            this.status = "Dormant";
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    @ObjectClass(name = "HLAobjectRoot.PhysicalEntity.DynamicalEntity")
    static class DynamicalEntity extends PhysicalEntity {
        @Attribute(name = "mass", coder = HLAfloat64LECoder.class, scope = ScopeLevel.PUBLISH)
        private double mass;

        @Attribute(name = "mass_rate", coder = HLAfloat64LECoder.class, scope = ScopeLevel.NONE)
        private double massRate;

        public DynamicalEntity() {
            this.mass = 0.0;
            this.massRate = 0.0;
        }

        public Double getMass() {
            return mass;
        }

        public void setMass(Double mass) {
            this.mass = mass;
        }

        public double getMassRate() {
            return massRate;
        }

        public void setMassRate(double massRate) {
            this.massRate = massRate;
        }
    }

    static class Rover extends DynamicalEntity {
        public Rover() { super(); }
    }
}
