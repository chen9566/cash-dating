package me.jiangcai.dating.jpa;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.UnitOfWork;

/**
 * @author CJ
 */
public class MysqlSessionCustomizer implements SessionCustomizer {
    @Override
    public void customize(Session session) throws Exception {
        session.getEventManager().addListener(new SessionEventAdapter() {
            @Override
            public void postConnect(SessionEvent event) {
                UnitOfWork work = event.getSession().acquireUnitOfWork();
                try {
                    work.executeNonSelectingSQL("set names utf8mb4");
                } finally {
                    work.release();
                }
            }
        });
    }
}
