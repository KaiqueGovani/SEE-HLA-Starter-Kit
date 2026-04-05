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
    final ObjectClassModelParser parser = new ObjectClassModelParser(ExecutionConfiguration.class);

    @Test
    void testMetadata() {
        assertEquals("HLAobjectRoot.ExecutionConfiguration", parser.getFomClassName());
        assertNotNull(parser.getFieldForFomElement("root_frame_name"));
        assertNotNull(parser.getFieldForFomElement("least_common_time_step"));
        assertNotNull(parser.getFieldForFomElement("scenario_time_epoch"));

        Field rootFrameName = parser.getFieldForFomElement("root_frame_name");
        Field leastCommonTimeStep = parser.getFieldForFomElement("least_common_time_step");
        Field scenarioTimeEpoch = parser.getFieldForFomElement("scenario_time_epoch");
        assertNotNull(parser.getFieldGetter(rootFrameName));
        assertNotNull(parser.getFieldSetter(rootFrameName));
        assertNotNull(parser.getFieldGetter(leastCommonTimeStep));
        assertNotNull(parser.getFieldSetter(leastCommonTimeStep));
        assertNotNull(parser.getFieldGetter(scenarioTimeEpoch));
        assertNotNull(parser.getFieldSetter(scenarioTimeEpoch));

        assertNotNull(parser.getFieldCoder(rootFrameName));
        assertNotNull(CoderCollection.query(parser.getFieldCoder(rootFrameName)));
        assertNotNull(parser.getFieldCoder(leastCommonTimeStep));
        assertNotNull(CoderCollection.query(parser.getFieldCoder(leastCommonTimeStep)));
        assertNotNull(parser.getFieldCoder(scenarioTimeEpoch));
        assertNotNull(CoderCollection.query(parser.getFieldCoder(scenarioTimeEpoch)));

        assertEquals(ScopeLevel.SUBSCRIBE, parser.getAttributeAccessLevel("root_frame_name"));
        assertEquals(ScopeLevel.SUBSCRIBE, parser.getAttributeAccessLevel("least_common_time_step"));
        assertEquals(ScopeLevel.SUBSCRIBE, parser.getAttributeAccessLevel("scenario_time_epoch"));
    }

    @Test
    void testMethodGeneration() {
        assertEquals("getValue", parser.generateMethodName("get", "value"));
        assertEquals("setValue", parser.generateMethodName("set", "value"));
        assertEquals("getPositionVector", parser.generateMethodName("get", "positionVector"));
        assertEquals("setPositionVector", parser.generateMethodName("set", "positionVector"));
        assertEquals("getPhysicalInterface", parser.generateMethodName("get", "physicalInterface"));
        assertEquals("setPhysicalInterface", parser.generateMethodName("set", "physicalInterface"));
    }

    @Test
    void testSubclassInheritsParentAttributes() {
        ObjectClassModelParser subclassParser = new ObjectClassModelParser(TestChildObject.class);

        Set<String> publishableAttributes = subclassParser.getPublishableAttributeNames();
        assertTrue(publishableAttributes.contains("parent_name"));
        assertTrue(publishableAttributes.contains("shared_status"));
        assertTrue(publishableAttributes.contains("child_mass"));
        assertFalse(publishableAttributes.contains("parent_subscribe_only"));

        Set<String> subscribableAttributes = subclassParser.getSubscribableAttributeNames();
        assertTrue(subscribableAttributes.contains("parent_name"));
        assertTrue(subscribableAttributes.contains("shared_status"));
        assertTrue(subscribableAttributes.contains("child_mass"));
        assertTrue(subscribableAttributes.contains("parent_subscribe_only"));

        Field parentName = subclassParser.getFieldForFomElement("parent_name");
        Field parentState = subclassParser.getFieldForFomElement("shared_status");
        Field childMass = subclassParser.getFieldForFomElement("child_mass");
        Field parentSubscribeOnly = subclassParser.getFieldForFomElement("parent_subscribe_only");

        assertNotNull(parentName);
        assertNotNull(parentState);
        assertNotNull(childMass);
        assertNotNull(parentSubscribeOnly);

        assertEquals(TestParentObject.class, parentName.getDeclaringClass());
        assertEquals(TestChildObject.class, parentState.getDeclaringClass());
        assertEquals(TestChildObject.class, childMass.getDeclaringClass());

        assertNotNull(subclassParser.getFieldGetter(parentName));
        assertNotNull(subclassParser.getFieldSetter(parentName));
        assertNotNull(subclassParser.getFieldGetter(parentState));
        assertNotNull(subclassParser.getFieldSetter(parentState));

        assertEquals(ScopeLevel.PUBLISH_SUBSCRIBE, subclassParser.getAttributeAccessLevel("parent_name"));
        assertEquals(ScopeLevel.PUBLISH_SUBSCRIBE, subclassParser.getAttributeAccessLevel("shared_status"));
        assertEquals(ScopeLevel.PUBLISH_SUBSCRIBE, subclassParser.getAttributeAccessLevel("child_mass"));
        assertEquals(ScopeLevel.SUBSCRIBE, subclassParser.getAttributeAccessLevel("parent_subscribe_only"));
    }

    @ObjectClass(name = "HLAobjectRoot.TestParentObject")
    public static class TestParentObject {
        @Attribute(name = "parent_name", coder = HLAunicodeStringCoder.class)
        private String parentName = "";

        @Attribute(name = "shared_status", coder = HLAunicodeStringCoder.class)
        private String sharedStatus = "";

        @Attribute(name = "parent_subscribe_only", coder = HLAunicodeStringCoder.class, scope = ScopeLevel.SUBSCRIBE)
        private String parentSubscribeOnly = "";

        public String getParentName() {
            return parentName;
        }

        public void setParentName(String parentName) {
            this.parentName = parentName;
        }

        public String getSharedStatus() {
            return sharedStatus;
        }

        public void setSharedStatus(String sharedStatus) {
            this.sharedStatus = sharedStatus;
        }

        public String getParentSubscribeOnly() {
            return parentSubscribeOnly;
        }

        public void setParentSubscribeOnly(String parentSubscribeOnly) {
            this.parentSubscribeOnly = parentSubscribeOnly;
        }
    }

    @ObjectClass(name = "HLAobjectRoot.TestParentObject.TestChildObject")
    public static class TestChildObject extends TestParentObject {
        @Attribute(name = "shared_status", coder = HLAunicodeStringCoder.class)
        private String sharedStatus = "";

        @Attribute(name = "child_mass", coder = HLAfloat64LECoder.class)
        private Double childMass = 0.0;

        @Override
        public String getSharedStatus() {
            return sharedStatus;
        }

        @Override
        public void setSharedStatus(String sharedStatus) {
            this.sharedStatus = sharedStatus;
        }

        public Double getChildMass() {
            return childMass;
        }

        public void setChildMass(Double childMass) {
            this.childMass = childMass;
        }
    }
}
