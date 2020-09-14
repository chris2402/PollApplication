package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.dao.UserDAO;
import no.hvl.dat250.h2020.group5.entities.User;

import javax.persistence.*;

import java.util.List;

public class UserService implements UserDAO {

    private static final String PERSISTENCE_UNIT_NAME = "polls";
    private static EntityManagerFactory factory;

    @PersistenceContext
    private EntityManager em;

    @Override
    public User createUser(String name, String password) {
        Query q = em.createQuery("SELECT u.userName FROM User u where u.userName = :username");
        q.setParameter("username", name);
        if(q.getResultList().size() > 0){
            return null;
        } else{
            User user = new User();
            user.setUserName(name);
            //TODO: Automatic set id
            user.setId((Integer.toString(em.createQuery("select u from User u").getResultList().size() + 1)));
            user.setPassword(password);
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return user;

        }
    }

    @Override
    public boolean deleteUser(String userId) {
        User user = em.find(User.class, userId);

        if(user == null){
            return false;
        }
        else{
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE from User u WHERE u.id = :id");
            q.setParameter("id", userId);
            int deleted = q.executeUpdate();
            em.getTransaction().commit();
            return deleted == 1;
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> userList = em.createQuery("select u from User u").getResultList();
        return userList;
    }

    @Override
    public User getUser(String userId) {
        return em.find(User.class, userId);
    }

    @Override
    public boolean updateUsername(String userId, String newName) {
        User user = em.find(User.class, userId);

        if(user == null){
            return false;
        }
        else{
            user.setUserName(newName);

            em.getTransaction().begin();
            em.merge(user);
            em.flush();
            em.getTransaction().commit();
            return em.find(User.class, userId).getUserName().equals(newName);
        }
    }

    @Override
    public boolean updatePassword(String userId, String oldPassword, String newPassword) {
        User user = em.find(User.class, userId);

        if(user == null){
            return false;
        }
        else{
            if(user.getPassword().equals(oldPassword)){
                user.setPassword(newPassword);
                em.getTransaction().begin();
                em.merge(user);
                em.flush();
                em.getTransaction().commit();
                return em.find(User.class, userId).getPassword().equals(newPassword);
            }
            else{
                return false;
            }
        }
    }

    public void setup(){
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = factory.createEntityManager();
    }
}
