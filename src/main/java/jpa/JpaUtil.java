package jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

public final class JpaUtil {

    private static final JpaUtil INSTANCE = new JpaUtil();

    private final EntityManagerFactory emf;

    private JpaUtil() {
        try (InputStream input = JpaUtil.class.getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                throw new IllegalStateException("Не знайдено application.properties");
            }

            Properties props = new Properties();
            props.load(input);

            Map<String, String> settings = Map.of(
                    "jakarta.persistence.jdbc.url", props.getProperty("db.url"),
                    "jakarta.persistence.jdbc.user", props.getProperty("db.user"),
                    "jakarta.persistence.jdbc.password", props.getProperty("db.password")
            );

            emf = Persistence.createEntityManagerFactory("internet-shop", settings);

        } catch (Exception e) {
            throw new IllegalStateException("Помилка налаштування Hibernate", e);
        }
    }

    public static JpaUtil getInstance() {
        return INSTANCE;
    }

    public void executeInTransaction(Consumer<EntityManager> action) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            action.accept(em);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public <T> T executeReadOnly(Function<EntityManager, T> action) {
        EntityManager em = emf.createEntityManager();

        try {
            return action.apply(em);
        } finally {
            em.close();
        }
    }

    public Statistics getStatistics() {
        return emf.unwrap(SessionFactory.class).getStatistics();
    }

    public void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
