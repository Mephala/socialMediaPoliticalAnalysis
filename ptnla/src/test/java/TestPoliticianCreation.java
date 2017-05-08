import mockit.integration.junit4.JMockit;
import org.ejml.simple.SimpleMatrix;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.fail;

/**
 * Created by mephala on 4/24/17.
 */
@RunWith(JMockit.class)
public class TestPoliticianCreation {


    @Test
    public void testCreatingAPoliticianWithTrend() {

        try {
//            Politician p = new Politician();
//            p.setPoliticianId(UUID.randomUUID().toString());
//            p.setPoliticianTurkishName("Gökhan Özgözen");
//            p.setPoliticianTwitterAccountName("gokhanOzgozen");
//            Session session = HibernateUtil.getSessionFactory().openSession();
//            session.beginTransaction();
//            session.saveOrUpdate(p);
//            FacebookTrendInterval trendInterval = new FacebookTrendInterval();
//            trendInterval.setStart(new Date());
//            Date d = new Date();
//            d.setTime(d.getTime() + (1000 * 60 * 60 * 24 * 7));
//            trendInterval.setEnd(d);
////            trendInterval.setPolitician(p);
//            trendInterval.setPopulationChange(new BigDecimal(-40.36));
//            trendInterval.setTrendId(UUID.randomUUID().toString());
//            session.saveOrUpdate(trendInterval);
//            session.getTransaction().commit();
//            session.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    @Test
    public void matrixStuff() {
        try {
            SimpleMatrix y = new SimpleMatrix(4, 1);
            y.setRow(0, 0, 4d);
            y.setRow(1, 0, 8d);
            y.setRow(2, 0, 16d);
            y.setRow(3, 0, 32d);
            SimpleMatrix A = new SimpleMatrix(4, 1);
            A.setRow(0, 0, 2d);
            A.setRow(1, 0, 4d);
            A.setRow(2, 0, 8d);
            A.setRow(3, 0, 16d);
            System.out.println(y);
            System.out.println(A);
            System.out.println(A.transpose());
            System.out.println(A.transpose().mult(A));
            System.out.println(A.transpose().mult(A).invert());
            System.out.println(A.transpose().mult(A).invert().mult(A.transpose().mult(y)));


        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }



}
