/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pro.novatech.solutions.bmoffice.jpa.entities.controllers;

import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import pro.novatech.solutions.bmoffice.jpa.entities.Item;
import pro.novatech.solutions.bmoffice.jpa.entities.Sale;
import pro.novatech.solutions.bmoffice.jpa.entities.User;
import pro.novatech.solutions.bmoffice.jpa.entities.controllers.exceptions.IllegalOrphanException;
import pro.novatech.solutions.bmoffice.jpa.entities.controllers.exceptions.NonexistentEntityException;
import pro.novatech.solutions.bmoffice.jpa.repository.EntityRepository;

/**
 *
 * @author Esther Mutombo
 */
public class SaleController extends EntityControllerAbstract implements EntityRepository<Sale> {

  

    @Override
    public void create(Sale sale) {
        if (sale.getItemCollection() == null) {
            sale.setItemCollection(new ArrayList<Item>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User userId = sale.getUserId();
            if (userId != null) {
                userId = em.getReference(userId.getClass(), userId.getId());
                sale.setUserId(userId);
            }
            Collection<Item> attachedItemCollection = new ArrayList<Item>();
            for (Item itemCollectionItemToAttach : sale.getItemCollection()) {
                itemCollectionItemToAttach = em.getReference(itemCollectionItemToAttach.getClass(), itemCollectionItemToAttach.getId());
                attachedItemCollection.add(itemCollectionItemToAttach);
            }
            sale.setItemCollection(attachedItemCollection);
            em.persist(sale);
            if (userId != null) {
                userId.getSaleCollection().add(sale);
                userId = em.merge(userId);
            }
            for (Item itemCollectionItem : sale.getItemCollection()) {
                Sale oldSalesIdOfItemCollectionItem = itemCollectionItem.getSalesId();
                itemCollectionItem.setSalesId(sale);
                itemCollectionItem = em.merge(itemCollectionItem);
                if (oldSalesIdOfItemCollectionItem != null) {
                    oldSalesIdOfItemCollectionItem.getItemCollection().remove(itemCollectionItem);
                    oldSalesIdOfItemCollectionItem = em.merge(oldSalesIdOfItemCollectionItem);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void edit(Sale sale) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Sale persistentSale = em.find(Sale.class, sale.getId());
            User userIdOld = persistentSale.getUserId();
            User userIdNew = sale.getUserId();
            Collection<Item> itemCollectionOld = persistentSale.getItemCollection();
            Collection<Item> itemCollectionNew = sale.getItemCollection();
            List<String> illegalOrphanMessages = null;
            for (Item itemCollectionOldItem : itemCollectionOld) {
                if (!itemCollectionNew.contains(itemCollectionOldItem)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Item " + itemCollectionOldItem + " since its salesId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (userIdNew != null) {
                userIdNew = em.getReference(userIdNew.getClass(), userIdNew.getId());
                sale.setUserId(userIdNew);
            }
            Collection<Item> attachedItemCollectionNew = new ArrayList<Item>();
            for (Item itemCollectionNewItemToAttach : itemCollectionNew) {
                itemCollectionNewItemToAttach = em.getReference(itemCollectionNewItemToAttach.getClass(), itemCollectionNewItemToAttach.getId());
                attachedItemCollectionNew.add(itemCollectionNewItemToAttach);
            }
            itemCollectionNew = attachedItemCollectionNew;
            sale.setItemCollection(itemCollectionNew);
            sale = em.merge(sale);
            if (userIdOld != null && !userIdOld.equals(userIdNew)) {
                userIdOld.getSaleCollection().remove(sale);
                userIdOld = em.merge(userIdOld);
            }
            if (userIdNew != null && !userIdNew.equals(userIdOld)) {
                userIdNew.getSaleCollection().add(sale);
                userIdNew = em.merge(userIdNew);
            }
            for (Item itemCollectionNewItem : itemCollectionNew) {
                if (!itemCollectionOld.contains(itemCollectionNewItem)) {
                    Sale oldSalesIdOfItemCollectionNewItem = itemCollectionNewItem.getSalesId();
                    itemCollectionNewItem.setSalesId(sale);
                    itemCollectionNewItem = em.merge(itemCollectionNewItem);
                    if (oldSalesIdOfItemCollectionNewItem != null && !oldSalesIdOfItemCollectionNewItem.equals(sale)) {
                        oldSalesIdOfItemCollectionNewItem.getItemCollection().remove(itemCollectionNewItem);
                        oldSalesIdOfItemCollectionNewItem = em.merge(oldSalesIdOfItemCollectionNewItem);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = sale.getId();
                if (find(id) == null) {
                    throw new NonexistentEntityException("The sale with id " + id + " no longer exists.");
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
    public void destroy(Integer id) throws  NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Sale sale;
            try {
                sale = em.getReference(Sale.class, id);
                sale.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The sale with id " + id + " no longer exists.", enfe);
            }
            
            em.remove(sale);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public List<Sale> findAll() {
        return findAll(true, -1, -1);
    }

    @Override
    public List<Sale> findAll(int maxResults, int firstResult) {
        return findAll(false, maxResults, firstResult);
    }

    /**
     *
     * @param all
     * @param maxResults
     * @param firstResult
     * @return
     */
    @Override
    public List<Sale> findAll(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Sale.class));
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
    public Sale find(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Sale.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public int getCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Sale> rt = cq.from(Sale.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    
    public Double getTodaySaleAmount()
    {
        EntityManager em = getEntityManager();
          try {
             CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery cq = cb.createQuery();
            Root<Sale> rt = cq.from(Sale.class);
            cq.select(cb.sum(rt.get("totalAmount")));
            Query q = em.createQuery(cq);
            return ((Double) q.getSingleResult());
        } finally {
            em.close();
        }
    }
    
}
