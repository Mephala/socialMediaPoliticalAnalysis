import com.fasterxml.jackson.databind.ObjectMapper;
import com.gokhanozg.ptnla.HibernateUtil;
import com.gokhanozg.ptnla.SerializableMatrix;
import com.gokhanozg.ptnla.Word;
import mockit.integration.junit4.JMockit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.ejml.simple.SimpleMatrix;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

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

    @Test
    public void testCoefficientPrecision() {
        try {
            SimpleMatrix y = new SimpleMatrix(2, 1);
            SimpleMatrix A = new SimpleMatrix(2, 3);
            y.setRow(0, 0, 106);
            y.setRow(1, 0, 120);
            A.setRow(0, 0, 3, 5, 10);
            A.setRow(1, 0, 4, 6, 11);
            System.out.println(y);
            System.out.println(A);
            System.out.println(A.transpose().mult(A).invert().mult(A.transpose().mult(y)));

        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void testApacheOLSRegression() {
        try {
            OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
            double[] y = new double[]{11.0, 12.0, 13.0, 14.0, 15.0, 16.0};
            double[][] x = new double[6][];
            x[0] = new double[]{0, 3, 8, 2, 0};
            x[1] = new double[]{2.0, 0, 0, 0, 0};
            x[2] = new double[]{0, 3.0, 0, 0, 0};
            x[3] = new double[]{0, 0, 4.0, 0, 0};
            x[4] = new double[]{0, 3, 0, 5.0, 0};
            x[5] = new double[]{0, 0, 0, 0, 6.0};
            regression.newSampleData(y, x);
            double[] beta = regression.estimateRegressionParameters();

            double[] residuals = regression.estimateResiduals();

            double[][] parametersVariance = regression.estimateRegressionParametersVariance();

            double regressandVariance = regression.estimateRegressandVariance();

            double rSquared = regression.calculateRSquared();

            double sigma = regression.estimateRegressionStandardError();
            System.out.println(sigma);


        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void testOLSSimpleProblem() {
        try {
            OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
            double[] y = new double[]{106, 120, 190, 167, 220, 249};
            double[][] x = new double[6][];
            x[0] = new double[]{3, 5, 10, 5, 7};
            x[1] = new double[]{4, 6, 11, 5, 6};
            x[2] = new double[]{2, 7, 13, 8, 11};
            x[3] = new double[]{4, 8, 11, 4, 5};
            x[4] = new double[]{4, 8, 11, 4, 5};
            x[5] = new double[]{4, 8, 11, 4, 5};
            regression.newSampleData(y, x);
            double[] beta = regression.estimateRegressionParameters();

            double[] residuals = regression.estimateResiduals();

            double[][] parametersVariance = regression.estimateRegressionParametersVariance();

            double regressandVariance = regression.estimateRegressandVariance();

            double rSquared = regression.calculateRSquared();

            double sigma = regression.estimateRegressionStandardError();
            System.out.println(sigma);

        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    @Test
    public void testWordExtraction() {
        try {
            Set<String> ignoredWords = new HashSet<>();
            File f = new File(this.getClass().getClassLoader().getResource("ignoredTurkishWords.txt").getPath());
            List<String> lines = FileUtils.readLines(f);
            for (String line : lines) {
                ignoredWords.add(line.trim());
            }
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Criteria criteria = session.createCriteria(Word.class);
            List<Word> wordList = criteria.list();
            session.getTransaction().commit();
            session.close();
            System.out.println(wordList.size());
            Locale locale = Locale.forLanguageTag("tr");
            Iterator<Word> wordIterator = wordList.iterator();
            while (wordIterator.hasNext()) {
                Word next = wordIterator.next();
                String val = next.getWordText();
                if (val.equals("Oluyor?”")) {
                    System.out.println("debuggo");
                }
                val = val.toLowerCase(locale);
                val = val.trim();
                val = val.replaceAll("\\!", "");
                val = val.replaceAll("\\.", "");
                val = val.replaceAll(",", "");
                val = val.replaceAll("”", "");
                if (val.endsWith("dir") || val.endsWith("dır")) {
                    wordIterator.remove();
                } else if (val.endsWith("miştir") || val.endsWith("muştur") || val.endsWith("müştür") || val.endsWith("mıştır")) {
                    wordIterator.remove();
                } else if (isNumeric(val)) {
                    wordIterator.remove();
                } else if (val.endsWith("'da") || val.endsWith("'de") || val.endsWith("’de") || val.endsWith("’da") || (val.endsWith("'dan") || val.endsWith("'den") || val.endsWith("’den") || val.endsWith("’dan"))) {
                    wordIterator.remove();
                } else if (val.endsWith("'na") || val.endsWith("'ü") || val.endsWith("’ü") || val.endsWith("'u") || val.endsWith("’u") || val.endsWith("'yü") || val.endsWith("’yü") || val.endsWith("'yu") || val.endsWith("’yu") || val.endsWith("'la") || val.endsWith("’la") || val.endsWith("'le") || val.endsWith("’le") || val.endsWith("'teki") || val.endsWith("’teki") || val.endsWith("'taki") || val.endsWith("’taki") || val.endsWith("'ni") || val.endsWith("’ni") || val.endsWith("'nı") || val.endsWith("’nı") || val.endsWith("'i") || val.endsWith("’i") || val.endsWith("'ı") || val.endsWith("’ı") || val.endsWith("'ne") || val.endsWith("’ne") || val.endsWith("’na") || (val.endsWith("'ndan") || val.endsWith("'nden") || val.endsWith("’nden") || val.endsWith("’ndan"))) {
                    wordIterator.remove();
                } else if (val.endsWith("'ta") || val.endsWith("'te") || val.endsWith("’te") || val.endsWith("’ta") || (val.endsWith("'tan") || val.endsWith("'ten") || val.endsWith("’ten") || val.endsWith("’tan"))) {
                    wordIterator.remove();
                } else if (val.endsWith("'a") || val.endsWith("'e") || val.endsWith("’i") || val.endsWith("’ı") || val.endsWith("’ya") || val.endsWith("’ye") || val.endsWith("’a") || val.endsWith("’e") || (val.endsWith("'yi") || val.endsWith("'yı")) || (val.endsWith("'ye") || val.endsWith("'ya") || val.endsWith("’yi"))) {
                    wordIterator.remove();
                } else if (val.endsWith("'nın") || val.endsWith("'nin") || val.endsWith("'nun") || val.endsWith("'nün") || val.endsWith("’nin") || val.endsWith("’nun") || val.endsWith("’nün") || val.endsWith("’nın") || (val.endsWith("'yi") || val.endsWith("'yı"))) {
                    wordIterator.remove();
                } else if (val.endsWith("'") || val.endsWith("’") || val.endsWith("?")) {
                    wordIterator.remove();
                } else if (val.endsWith("'yla") || val.endsWith("'yle") || val.endsWith("’yla") || val.endsWith("’yle")) {
                    wordIterator.remove();
                } else if (val.length() <= 2) {
                    wordIterator.remove();
                } else if (val.endsWith("'in") || val.endsWith("'ın") || val.endsWith("'nin") || val.endsWith("'nın") || val.endsWith("’in") || val.endsWith("’ın") || val.endsWith("’nin") || val.endsWith("’nın")) {
                    wordIterator.remove();
                } else if (ignoredWords.contains(val)) {
                    wordIterator.remove();
                } else if (val.startsWith("@") || val.startsWith("#")) {
                    wordIterator.remove();
                } else if (val.startsWith("http://") || val.startsWith("https://")) {
                    wordIterator.remove();
                } else {
                    for (String ignoredWord : ignoredWords) {
                        if (val.startsWith(ignoredWord) || val.endsWith(ignoredWord)) {
                            wordIterator.remove();
                            break;
                        }
                    }
                }
            }
            Collections.sort(wordList);
            System.out.println("Filtered size:" + wordList.size());
            System.out.println("End filtering");

        } catch (Throwable t) {
            t.printStackTrace();
            fail();
        }
    }

    private boolean isNumeric(String val) {
        try {
            new BigDecimal(val);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }


}
