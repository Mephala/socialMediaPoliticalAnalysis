import com.gokhanozg.ptnla.FacebookTrendInterval;
import com.gokhanozg.ptnla.HibernateUtil;
import com.gokhanozg.ptnla.Politician;
import mockit.integration.junit4.JMockit;
import org.hibernate.Session;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Created by mephala on 4/24/17.
 */
@RunWith(JMockit.class)
public class TestPoliticianCreation {


    @Test
    public void testCreatingAPoliticianWithTrend() {

        try {
            Politician p = new Politician();
            p.setPoliticianId(UUID.randomUUID().toString());
            p.setPoliticianTurkishName("Gökhan Özgözen");
            p.setPoliticianTwitterAccountName("gokhanOzgozen");
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.saveOrUpdate(p);
            FacebookTrendInterval trendInterval = new FacebookTrendInterval();
            trendInterval.setStart(new Date());
            Date d = new Date();
            d.setTime(d.getTime() + (1000 * 60 * 60 * 24 * 7));
            trendInterval.setEnd(d);
//            trendInterval.setPolitician(p);
            trendInterval.setPopulationChange(new BigDecimal(-40.36));
            trendInterval.setTrendId(UUID.randomUUID().toString());
            session.saveOrUpdate(trendInterval);
            session.getTransaction().commit();
            session.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }


}
