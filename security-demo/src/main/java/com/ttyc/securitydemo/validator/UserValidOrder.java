package com.ttyc.securitydemo.validator;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({RMBUser.class,NewUser.class})
public interface UserValidOrder {
}
