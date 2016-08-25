/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package ren.boot.cas.ticket.registry;

import org.jasig.cas.ticket.ServiceTicket;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.encrypt.AbstractCrypticTicketRegistry;
import org.springframework.beans.factory.DisposableBean;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Key-value ticket registry implementation that stores tickets in redis keyed on the ticket ID.
 *
 * @author serv
 */
public final class RedisTicketRegistry extends AbstractCrypticTicketRegistry implements DisposableBean {

    private final static String TICKET_PREFIX = "CAS:TICKET:";

    /**
     * redis client.
     */
    @NotNull
    private TicketRedisTemplate client;

    /**
     * TGT cache entry timeout in seconds.
     */
    @Min(0)
    private int tgtTimeout;

    /**
     * ST cache entry timeout in seconds.
     */
    @Min(0)
    private int stTimeout;

    public void setClient(TicketRedisTemplate client) {
        this.client = client;
    }

    public void setTgtTimeout(int tgtTimeout) {
        this.tgtTimeout = tgtTimeout;
    }

    public void setStTimeout(int stTimeout) {
        this.stTimeout = stTimeout;
    }

    public RedisTicketRegistry() {
    }

    /**
     * Creates a new instance using the given redis client instance, which is presumably configured via
     * <code>net.spy.redis.spring.redisClientFactoryBean</code>.
     *
     * @param client                      redis client.
     * @param ticketGrantingTicketTimeOut TGT timeout in seconds.
     * @param serviceTicketTimeOut        ST timeout in seconds.
     */
    public RedisTicketRegistry(final TicketRedisTemplate client, final int ticketGrantingTicketTimeOut,
                               final int serviceTicketTimeOut) {
        this.tgtTimeout = ticketGrantingTicketTimeOut;
        this.stTimeout = serviceTicketTimeOut;
        this.client = client;
    }

    protected void updateTicket(final Ticket ticket) {
        logger.debug("Updating ticket {}", ticket);
        try {
            String redisKey = this.getTicketRedisKey(ticket.getId());
            this.client.boundValueOps(redisKey).set(ticket, getTimeout(ticket), TimeUnit.SECONDS);
        } catch (final Exception e) {
            logger.error("Failed updating {}", ticket, e);
        }
    }

    public void addTicket(final Ticket ticket) {
        logger.debug("Adding ticket {}", ticket);
        try {
            String redisKey = this.getTicketRedisKey(ticket.getId());
            this.client.boundValueOps(redisKey).set(ticket, getTimeout(ticket), TimeUnit.SECONDS);
        } catch (final Exception e) {
            logger.error("Failed Adding {}", ticket, e);
        }
    }

    public boolean deleteTicket(final String ticketId) {
        logger.debug("Deleting ticket {}", ticketId);
        try {
            this.client.delete(this.getTicketRedisKey(ticketId));
            return true;
        } catch (final Exception e) {
            logger.error("Failed deleting {}", ticketId, e);
        }
        return false;
    }

    public Ticket getTicket(final String ticketId) {
        try {
            final Ticket t = (Ticket) this.client.boundValueOps(this.getTicketRedisKey(ticketId)).get();
            if (t != null) {
                return getProxiedTicketInstance(t);
            }
        } catch (final Exception e) {
            logger.error("Failed fetching {} ", ticketId, e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * This operation is not supported.
     *
     * @throws UnsupportedOperationException if you try and call this operation.
     */
    public Collection<Ticket> getTickets() {
        Set<Ticket> tickets = new HashSet<Ticket>();
        Set<String> keys = this.client.keys(this.getPatternTicketRedisKey());
        for (String key : keys) {
            Ticket ticket = this.client.boundValueOps(key).get();
            if (ticket == null) {
                this.client.delete(key);
            } else {
                tickets.add(ticket);
            }
        }
        return tickets;
    }

    public void destroy() throws Exception {
        client.getConnectionFactory().getConnection().close();
    }

    @Override
    protected boolean needsCallback() {
        return true;
    }

    private int getTimeout(final Ticket t) {
        if (t instanceof TicketGrantingTicket) {
            return this.tgtTimeout;
        } else if (t instanceof ServiceTicket) {
            return this.stTimeout;
        }
        throw new IllegalArgumentException("Invalid ticket type");
    }

    //Add a prefix as the key of redis
    private String getTicketRedisKey(String ticketId) {
        return TICKET_PREFIX + ticketId;
    }

    // pattern all ticket redisKey
    private String getPatternTicketRedisKey() {
        return TICKET_PREFIX + "*";
    }
}