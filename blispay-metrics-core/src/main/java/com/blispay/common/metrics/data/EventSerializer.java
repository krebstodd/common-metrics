package com.blispay.common.metrics.data;

import com.blispay.common.metrics.model.EventModel;

public interface EventSerializer {

    String serialize(EventModel metric);

}
