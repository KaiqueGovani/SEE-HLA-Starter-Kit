package org.see.skf.runtime;

import org.junit.jupiter.api.Test;
import org.see.skf.annotations.InteractionClass;
import org.see.skf.annotations.Parameter;
import org.see.skf.util.encoding.HLAfloat64LECoder;
import org.see.skf.util.encoding.HLAinteger16BECoder;
import org.see.skf.util.encoding.HLAunicodeStringCoder;
import org.see.skf.util.models.ModeTransitionRequest;
import org.see.skf.runtime.interactions.InteractionClassModelParser;

import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InteractionClassModelParserTest {
    final InteractionClassModelParser parser = new InteractionClassModelParser(ModeTransitionRequest.class);
    final InteractionClassModelParser baseInteractionParser = new InteractionClassModelParser(BaseInteraction.class);
    final InteractionClassModelParser dockingRequestInteractionParser = new InteractionClassModelParser(DockingRequestInteraction.class);
    final InteractionClassModelParser repairRequestInteractionParser = new InteractionClassModelParser(RepairRequestInteraction.class);
    final InteractionClassModelParser repairRequestCompleteInteractionParser = new InteractionClassModelParser(RepairRequestCompleteInteraction.class);

    @Test
    void testMetadata() {
        assertEquals("HLAinteractionRoot.ModeTransitionRequest", parser.getFomClassName());

        Field name = parser.getFieldForFomElement("execution_mode");
        assertNotNull(name);
        assertNotNull(parser.getFieldGetter(name));
        assertNotNull(parser.getFieldSetter(name));
        assertNotNull(parser.getFieldCoder(name));
        assertNotNull(CoderCollection.query(parser.getFieldCoder(name)));
    }

    @Test
    void testBaseInteractionFields() {
        assertEquals("HLAinteractionRoot.BaseInteraction", baseInteractionParser.getFomClassName());

        Field senderField = baseInteractionParser.getFieldForFomElement("sender");
        Field targetObjectField = baseInteractionParser.getFieldForFomElement("target_object");

        assertNotNull(senderField);
        assertNotNull(targetObjectField);

        Set<String> parameterNames = baseInteractionParser.getParameterNames();
        assertTrue(parameterNames.contains("sender"));
        assertTrue(parameterNames.contains("target_object"));
    }

    @Test
    void testDockingRequestFields() {
        Field senderField = dockingRequestInteractionParser.getFieldForFomElement("sender");
        Field targetObjectField = dockingRequestInteractionParser.getFieldForFomElement("target_object");
        Field zDistanceField = dockingRequestInteractionParser.getFieldForFomElement("z_distance");

        assertNotNull(senderField);
        assertNotNull(targetObjectField);
        assertNotNull(zDistanceField);

        Set<String> parameterNames = dockingRequestInteractionParser.getParameterNames();
        assertTrue(parameterNames.contains("sender"));
        assertTrue(parameterNames.contains("target_object"));
        assertTrue(parameterNames.contains("z_distance"));
    }

    @Test
    void testRepairRequestFields() {
        Field senderField = repairRequestInteractionParser.getFieldForFomElement("sender");
        Field targetObjectField = repairRequestInteractionParser.getFieldForFomElement("target_object");
        Field messageIdField = repairRequestInteractionParser.getFieldForFomElement("messageId");

        assertNotNull(senderField);
        assertNotNull(targetObjectField);
        assertNull(messageIdField);

        Set<String> parameterNames = repairRequestInteractionParser.getParameterNames();
        assertTrue(parameterNames.contains("sender"));
        assertTrue(parameterNames.contains("target_object"));
    }

    @Test
    void testRepairRequestCompleteFields() {
        Field senderField = repairRequestCompleteInteractionParser.getFieldForFomElement("sender");
        Field targetObjectField = repairRequestCompleteInteractionParser.getFieldForFomElement("target_object");
        Field messageIdField = repairRequestCompleteInteractionParser.getFieldForFomElement("messageId");
        Field repairCodeField = repairRequestCompleteInteractionParser.getFieldForFomElement("repair_code");

        assertNotNull(senderField);
        assertNotNull(targetObjectField);
        assertNull(messageIdField);
        assertNotNull(repairCodeField);

        Set<String> parameterNames = repairRequestCompleteInteractionParser.getParameterNames();
        assertTrue(parameterNames.contains("sender"));
        assertTrue(parameterNames.contains("target_object"));
        assertTrue(parameterNames.contains("repair_code"));
    }

    @InteractionClass(name = "HLAinteractionRoot.BaseInteraction")
    static class BaseInteraction {
        @Parameter(name = "sender", coder = HLAunicodeStringCoder.class)
        private String sender;

        @Parameter(name = "target_object", coder = HLAunicodeStringCoder.class)
        private String targetObject;

        public BaseInteraction() {
            this.sender = "orion_spacecraft";
            this.targetObject = "gateway";
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getTargetObject() {
            return targetObject;
        }

        public void setTargetObject(String targetObject) {
            this.targetObject = targetObject;
        }
    }

    @InteractionClass(name = "HLAinteractionRoot.BaseInteraction.DockingRequestInteraction")
    static class DockingRequestInteraction extends BaseInteraction {
        @Parameter(name = "z_distance", coder = HLAfloat64LECoder.class)
        private double zDistance;

        public DockingRequestInteraction() {
            this.zDistance = 12.0;
        }

        public Double getZDistance() {
            return zDistance;
        }

        public void setZDistance(Double zDistance) {
            this.zDistance = zDistance;
        }
    }

    static class RepairRequestInteraction extends BaseInteraction {
        private String messageId;

        public RepairRequestInteraction() {
            super();
        }

        public String getMessageId() {
            return messageId;
        }

        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }
    }

    static class RepairRequestCompleteInteraction extends RepairRequestInteraction {
        @Parameter(name = "repair_code", coder = HLAinteger16BECoder.class)
        private short repairCode;

        public RepairRequestCompleteInteraction() { super(); }

        public short getRepairCode() {
            return repairCode;
        }

        public void setRepairCode(short repairCode) {
            this.repairCode = repairCode;
        }
    }
}
