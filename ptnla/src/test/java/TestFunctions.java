import com.fasterxml.jackson.databind.ObjectMapper;
import com.gokhanozg.ptnla.SerializableMatrix;
import mockit.integration.junit4.JMockit;
import org.apache.commons.io.FileUtils;
import org.ejml.simple.SimpleMatrix;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static junit.framework.TestCase.fail;

/**
 * Created by mephala on 4/24/17.
 */
@RunWith(JMockit.class)
public class TestFunctions {


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
            SerializableMatrix sy = new SerializableMatrix(y);
            SerializableMatrix sA = new SerializableMatrix(A);
            SimpleMatrix ssy = sy.convertToSM();
            SimpleMatrix ssA = sA.convertToSM();
            System.out.println(ssA.transpose().mult(ssA).invert().mult(ssA.transpose().mult(ssy)));

        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void testCoefficientEvaluation() {
        try {
            SimpleMatrix y = readObjectFromFile("y.ptnla", SerializableMatrix.class).convertToSM();
            SimpleMatrix A = readObjectFromFile("A.ptnla", SerializableMatrix.class).convertToSM();
            SimpleMatrix w = A.transpose().mult(A).invert().mult(A.transpose().mult(y));
            SimpleMatrix nny = A.mult(w);
            System.out.println(y);
            System.out.println(nny);
        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    public <T> T readObjectFromFile(String fileName, Class<T> tClass) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        URL url = this.getClass().getClassLoader().getResource(fileName);
        File file = new File(url.getPath());
        String value = FileUtils.readFileToString(file);
        return objectMapper.readValue(value, tClass);
    }


}
