package com.cultofbits.customizations.validators;

import com.cultofbits.customizations.utils.DefinitionBuilder;
import com.cultofbits.customizations.utils.FieldDefinitionBuilder;
import com.cultofbits.customizations.utils.InstanceBuilder;
import com.cultofbits.recordm.core.model.Definition;
import com.cultofbits.recordm.core.model.FieldDefinition;
import com.cultofbits.recordm.core.model.Instance;
import com.cultofbits.recordm.core.model.InstanceField;
import com.cultofbits.recordm.persistence.InstanceRepository;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

public class UniqueValueValidatorTest {

    @Mock
    private InstanceRepository instanceRepository;

    private UniqueValueValidator validator;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        validator = new UniqueValueValidator();
        validator.setInstanceRepository(instanceRepository);
    }

    @Test
    public void no_errors_when_unique() {

        Definition definition = DefinitionBuilder.aDefinition()
                .fieldDefinitions(
                        FieldDefinitionBuilder.aFieldDefinition().name("Type").description("$[Robot,User] $uniqueValue"))
                .build();

        Instance instance = InstanceBuilder.anInstance(definition)
                .fieldValue("Type", "Robot")
                .build();

        when(instanceRepository.getInstanceIdsWithFieldsMatching(anyList(), eq("Robot"), eq(0), eq(2)))
                .thenReturn(Collections.emptyList());

        assertTrue(validator.validateInstanceFields(instance.getFields()).isEmpty());
    }

    @Test
    public void new_instance_with_non_unique_value_will_return_errors() {

        Definition definition = DefinitionBuilder.aDefinition()
                .fieldDefinitions(
                        FieldDefinitionBuilder.aFieldDefinition().name("Type").description("$[Robot,User] $uniqueValue"))
                .build();

        Instance instance = InstanceBuilder.anInstance(definition)
                .fieldValue("Type", "Robot")
                .build();

        when(instanceRepository.getInstanceIdsWithFieldsMatching(anyList(), eq("Robot"), eq(0), eq(2)))
                .thenReturn(Collections.singletonList(1));

        assertFalse(validator.validateInstanceFields(instance.getFields()).isEmpty());
    }

    @Test
    public void updating_instance_with_value_continues_to_be_unique() {

        Definition definition = DefinitionBuilder.aDefinition()
                .fieldDefinitions(
                        FieldDefinitionBuilder.aFieldDefinition().name("Type").description("$[Robot,User] $uniqueValue"))
                .build();

        Instance instance = InstanceBuilder.anInstance(definition)
                .id(2)
                .fieldValue("Type", "Robot")
                .build();

        when(instanceRepository.getInstanceIdsWithFieldsMatching(anyList(), eq("Robot"), eq(0), eq(2)))
                .thenReturn(Collections.singletonList(2));

        assertTrue(validator.validateInstanceFields(instance.getFields()).isEmpty());
    }

    @Test
    public void updating_instance_with_non_unique_value_will_return_errors() {

        Definition definition = DefinitionBuilder.aDefinition()
                .fieldDefinitions(
                        FieldDefinitionBuilder.aFieldDefinition().name("Type").description("$[Robot,User] $uniqueValue"))
                .build();

        Instance instance = InstanceBuilder.anInstance(definition)
                .id(3)
                .fieldValue("Type", "Robot")
                .build();

        when(instanceRepository.getInstanceIdsWithFieldsMatching(anyList(), eq("Robot"), eq(0), eq(2)))
                .thenReturn(Collections.singletonList(2));

        assertFalse(validator.validateInstanceFields(instance.getFields()).isEmpty());
    }
}