package service;

import java.util.List;
import model.Medicament;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import util.HibernateUtil;

public class MedicamentService {

    private SessionFactory factory = HibernateUtil.getSessionFactory();

    public void ajouter(Medicament m) {
        Session session = factory.openSession();
        session.beginTransaction();
        session.save(m);
        session.getTransaction().commit();
        session.close();
    }

    public void modifier(Medicament m) {
        Session session = factory.openSession();
        session.beginTransaction();
        session.update(m);
        session.getTransaction().commit();
        session.close();
    }

    public void supprimer(int id) {
        Session session = factory.openSession();
        session.beginTransaction();
        Medicament m = (Medicament) session.get(Medicament.class, id);
        if (m != null) session.delete(m);
        session.getTransaction().commit();
        session.close();
    }

    public List<Medicament> listerTous() {
        Session session = factory.openSession();
        List<Medicament> liste = session.createQuery("from Medicament").list();
        session.close();
        return liste;
    }

    public List<Medicament> rechercher(String motCle) {
        Session session = factory.openSession();
        List<Medicament> liste = session.createQuery(
            "from Medicament where nom like :mc or type like :mc")
            .setParameter("mc", "%" + motCle + "%")
            .list();
        session.close();
        return liste;
    }
}