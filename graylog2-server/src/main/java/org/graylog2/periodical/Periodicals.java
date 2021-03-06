/**
 * Copyright 2014 Lennart Koopmann <lennart@torch.sh>
 *
 * This file is part of Graylog2.
 *
 * Graylog2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog2.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.graylog2.periodical;

import com.google.common.collect.Lists;
import org.graylog2.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class Periodicals {

    private static final Logger LOG = LoggerFactory.getLogger(Periodicals.class);

    private final Core core;

    private final List<Periodical> periodicals;

    public Periodicals(Core core) {
        this.periodicals = Lists.newArrayList();
        this.core = core;
    }

    public synchronized void registerAndStart(Periodical periodical) {
        if (periodical.runsForever()) {
            LOG.info("Starting [{}] periodical, running forever.", periodical.getClass().getCanonicalName());

            Thread t = new Thread(periodical);
            t.setDaemon(periodical.isDaemon());
            t.start();
        } else {
            LOG.info(
                    "Starting [{}] periodical in [{}s], polling every [{}s].",
                    new Object[]{ periodical.getClass().getCanonicalName(),
                            periodical.getInitialDelaySeconds(),
                            periodical.getPeriodSeconds()
                    }
            );

            ScheduledExecutorService scheduler = periodical.isDaemon() ? core.getDaemonScheduler() : core.getScheduler();
            scheduler.scheduleAtFixedRate(
                    periodical,
                    periodical.getInitialDelaySeconds(),
                    periodical.getPeriodSeconds(),
                    TimeUnit.SECONDS
            );
        }

        periodicals.add(periodical);
    }

    /**
     *
     * @return a copy of all registered periodicals.
     */
    public List<Periodical> getAll() {
        return Lists.newArrayList(periodicals);
    }

}
