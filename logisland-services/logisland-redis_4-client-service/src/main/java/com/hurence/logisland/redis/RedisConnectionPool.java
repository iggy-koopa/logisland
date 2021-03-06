/**
 * Copyright (C) 2016 Hurence (support@hurence.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hurence.logisland.redis;

import org.springframework.data.redis.connection.RedisConnection;

/**
 * A service that provides connections to Redis using spring-data-redis.
 */
public interface RedisConnectionPool {

    /**
     * Obtains a RedisConnection instance from the pool.
     *
     * NOTE: Clients are responsible for ensuring the close() method of the connection is called to return it to the pool.
     *
     * @return a RedisConnection instance
     */
    RedisConnection getConnection();

    /**
     * Some Redis operations are only supported in a specific mode. Clients should use this method to ensure
     * the connection pool they are using supports their required operations.
     *
     * @return the type of Redis instance (i.e. standalone, clustered, sentinel)
     */
    RedisType getRedisType();

}
