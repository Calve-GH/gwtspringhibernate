package com.github.calve.test.part.observer.application.client;

import com.github.calve.test.part.observer.application.shared.PartDto;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface PartProperties extends PropertyAccess<PartDto> {

    ModelKeyProvider<PartDto> id();

    ValueProvider<PartDto, String> name();

    ValueProvider<PartDto, Integer> quantity();

    ValueProvider<PartDto, Boolean> required();
}
