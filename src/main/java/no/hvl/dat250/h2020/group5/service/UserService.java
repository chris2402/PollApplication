package no.hvl.dat250.h2020.group5.service;

import no.hvl.dat250.h2020.group5.dao.UserDAO;
import no.hvl.dat250.h2020.group5.entities.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.List;

public class UserService implements UserDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public User createUser(String name, String password) {
        Query q = em.createQuery("SELECT u.userName FROM User u where u.userName = :username");
        q.setParameter("username", name);
        //TODO: Return something else than null
        if(q.getResultList().size() > 0){
            return null;
        } else{
            User user = new User();
            user.setUserName(name);
            user.setPassword(password);
            em.persist(user);
            return user;

        }
    }

    @Override
    public boolean deleteUser(String userId) {
        User user = em.find(User.class, userId);

        //TODO: Return something more useful than null
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

        //TODO: Return something more useful than null
        if(user == null){
            return false;
        }
        else{
            user.setUserName(newName);
            em.merge(user);
            return em.find(User.class, userId).getUserName().equals(newName);
        }
    }

    @Override
    //TODO: Retrieve userID from cookie/JWT or similar
    public boolean updatePassword(String userId, String oldPassword, String newPassword) {
        User user = em.find(User.class, userId);
        if(user == null){
            return false;
        }
        else{
            if(user.getPassword().equals(oldPassword)){
                user.setPassword(newPassword);
                em.merge(user);
                return em.find(User.class, userId).getPassword().equals(newPassword);
            }
            else{
                return false;
            }
        }
    }
}
