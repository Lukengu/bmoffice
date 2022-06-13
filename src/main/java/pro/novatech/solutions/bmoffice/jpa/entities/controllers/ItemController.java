/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pro.novatech.solutions.bmoffice.jpa.entities.controllers;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import pro.novatech.solutions.bmoffice.jpa.entities.Item;
import pro.novatech.solutions.bmoffice.jpa.entities.Sale;
import pro.novatech.solutions.bmoffice.jpa.entities.controllers.exceptions.NonexistentEntityException;
import pro.novatech.solutions.bmoffice.jpa.repository.EntityRepository;


/**
 *
 * @author Esther Mutombo
 */
public class ItemController extends EntityControllerAbstract implements EntityRepository<Item> {

  
    @Override
    public void create(Item item) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Sale salesId = item.getSalesId();
            if (salesId != null) {
                salesId = em.getReference(salesId.getClass(), salesId.getId());
                item.setSalesId(salesId);
            }
            em.persist(item);
            if (salesId != null) {
                salesId.getItemCollection().add(item);
                salesId = em.merge(salesId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void edit(Item item) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Item persistentItem = em.find(Item.class, item.getId());
            Sale salesIdOld = persistentItem.getSalesId();
            Sale salesIdNew = item.getSalesId();
            if (salesIdNew != null) {
                salesIdNew = em.getReference(salesIdNew.getClass(), salesIdNew.getId());
                item.setSalesId(salesIdNew);
            }
            item = em.merge(item);
            if (salesIdOld != null && !salesIdOld.equals(salesIdNew)) {
                salesIdOld.getItemCollection().remove(item);
                salesIdOld = em.merge(salesIdOld);
            }
            if (salesIdNew != null && !salesIdNew.equals(salesIdOld)) {
                salesIdNew.getItemCollection().add(item);
                salesIdNew = em.merge(salesIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = item.getId();
                if (find(id) == null) {
                    throw new NonexistentEntityException("The item with id " + id + " no longer exists.");
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
            Item item;
            try {
                item = em.getReference(Item.class, id);
                item.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The item with id " + id + " no longer exists.", enfe);
            }
            Sale salesId = item.getSalesId();
            if (salesId != null) {
                salesId.getItemCollection().remove(item);
                salesId = em.merge(salesId);
            }
            em.remove(item);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public List<Item> findAll() {
        return findAll(true, -1, -1);
    }

    @Override
    public List<Item> findAll(int maxResults, int firstResult) {
        return findAll(false, maxResults, firstResult);
    }

    @Override
    public List<Item> findAll(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Item.class));
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
    public Item find(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Item.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public int getCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Item> rt = cq.from(Item.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
