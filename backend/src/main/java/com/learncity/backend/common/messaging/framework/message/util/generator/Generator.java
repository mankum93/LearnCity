package com.learncity.backend.common.messaging.framework.message.util.generator;

/**
 * Created by DJ on 5/2/2017.
 */
public interface Generator<Outcome, Input> {

    Outcome next(Input input);
}
