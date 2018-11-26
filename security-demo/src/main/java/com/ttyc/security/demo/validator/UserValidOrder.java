package com.ttyc.security.demo.validator;

import javax.validation.GroupSequence;

@GroupSequence({RMBUser.class, NewUser.class})
public interface UserValidOrder {
}
