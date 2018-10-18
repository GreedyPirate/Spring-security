package com.ttyc.securitydemo.validator;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({Default.class,NewUser.class,RMBUser.class})
public interface UserValidOrder {
}
