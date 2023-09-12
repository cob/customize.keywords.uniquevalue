package com.cultofbits.customizations.validators;

import com.cultofbits.recordm.core.model.Definition;
import com.cultofbits.recordm.core.model.FieldDefinition;
import com.cultofbits.recordm.core.model.Instance;
import com.cultofbits.recordm.core.model.InstanceField;
import com.cultofbits.recordm.customvalidators.api.OnCreateValidator;
import com.cultofbits.recordm.customvalidators.api.OnUpdateValidator;
import com.cultofbits.recordm.customvalidators.api.ValidationError;
import com.cultofbits.recordm.customvalidators.api.WithRepositorySupport;
import com.cultofbits.recordm.persistence.InstanceRepository;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.cultofbits.recordm.customvalidators.api.ValidationError.custom;

public class UniqueValueValidator implements OnCreateValidator, OnUpdateValidator, WithRepositorySupport {

    public static final String KEYWORD = "$uniqueValue";

    private InstanceRepository instanceRepository;

    @Override
    public void setInstanceRepository(InstanceRepository instanceRepository) {
        this.instanceRepository = instanceRepository;

    }

    @Override
    public Collection<ValidationError> onCreate(Instance instance) {
        return validateInstanceFields(instance.getRootFields());
    }

    @Override
    public Collection<ValidationError> onUpdate(Instance persistedInstance, Instance updatedInstance) {
        return validateInstanceFields(updatedInstance.getRootFields());
    }

    public Collection<ValidationError> validateInstanceFields(List<InstanceField> instanceFields) {
        List<ValidationError> errors = new ArrayList<>();

        for (InstanceField instanceField : instanceFields) {
            if ((!instanceField.isVisible() || instanceField.getValue() == null)
                    && instanceField.children.isEmpty()) {
                continue;
            }

            if (instanceField.fieldDefinition.containsExtension(KEYWORD)) {
                List<Integer> matchingInstances = instanceRepository.getInstanceIdsWithFieldsMatching(
                        Collections.singletonList(instanceField.fieldDefinition.id),
                        instanceField.getValue().trim(),
                        0, 2);

                if (!matchingInstances.isEmpty()) {
                    if ((instanceField.instance.id == null || instanceField.instance.id < 0)
                            || (matchingInstances.size() >= 2 || !matchingInstances.get(0).equals(instanceField.instance.id))) {
                        errors.add(custom(instanceField, getErrorMsg(instanceField)));
                    }
                }
            }

            if (!instanceField.children.isEmpty()) {
                errors.addAll(validateInstanceFields(instanceField.children));
            }
        }

        return errors;
    }

    protected String getErrorMsg(InstanceField instanceField) {
        String query = instanceField.fieldDefinition.name.toLowerCase().replaceAll(" ", "_") + ".raw:\"" + instanceField.getValue() + "\"";
        String uri = "/recordm/index.html#/definitions/" + instanceField.instance.definition.id + "/q=" + query;
        return "This field must be unique. <a href='" + uri + "' target=\"_blank\">[show]</a>";
    }
}
