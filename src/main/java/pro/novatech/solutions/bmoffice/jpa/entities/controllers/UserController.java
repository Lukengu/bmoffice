/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pro.novatech.solutions.bmoffice.jpa.entities.controllers;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.mindrot.jbcrypt.BCrypt;
import pro.novatech.solutions.bmoffice.jpa.entities.Product;
import pro.novatech.solutions.bmoffice.jpa.entities.User;
import pro.novatech.solutions.bmoffice.jpa.entities.controllers.exceptions.LoginException;
import pro.novatech.solutions.bmoffice.jpa.entities.controllers.exceptions.NonexistentEntityException;
import pro.novatech.solutions.bmoffice.jpa.repository.EntityRepository;

/**
 *
 * @author philippefgx
 */
public class UserController extends EntityControllerAbstract implements EntityRepository<User> {

    @Override
    public void create(User user) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void edit(User user) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            user = em.merge(user);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = user.getId();
                if (null == find(id)) {
                    throw new NonexistentEntityException("The product with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public List<User> findAll() {
        return findAll(true, -1, -1);
    }

    @Override
    public List<User> findAll(int maxResults, int firstResult) {
        return findAll(false, maxResults, firstResult);
    }

    @Override
    public List<User> findAll(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public User find(Integer entityId) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, entityId);
        } finally {
            em.close();
        }
    }

    @Override
    public int getCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Product> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public List<User> getPosUsers() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createNamedQuery("User.findByRole");
            q.setParameter("role", "pos_user");
            return q.getResultList();

        } finally {
            em.close();
        }

    }

    public User auth(String username, String password) throws LoginException {
        User user = null;
        EntityManager em = getEntityManager();
        try {
            Query q = em.createNamedQuery("User.findByUsername");
            q.setParameter("username", username);
            user = (User) q.getSingleResult();
            if (!BCrypt.checkpw(password, user.getPassword())) {
                throw new LoginException("Password incorrect for " + user.getName());
            }
        } catch (NoResultException e) {
            throw new LoginException("User not find in the system");
        } finally {
            em.close();
        }

        return user;
    }

}
