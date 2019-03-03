package com.suarezlin.controller;

import com.suarezlin.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {

    @Autowired
    public RedisOperator redisOperator;

    public static final String USER_REDIS_SESSION = "user-redis-session";


}
