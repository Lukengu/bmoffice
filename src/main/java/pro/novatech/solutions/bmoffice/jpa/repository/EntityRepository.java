/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pro.novatech.solutions.bmoffice.jpa.repository;

import java.util.List;
import pro.novatech.solutions.bmoffice.jpa.entities.controllers.exceptions.NonexistentEntityException;

/**
 *
 * @author philippefgx
 * @param <T>
 */
public interface EntityRepository<T>
{
    void create(T obj);
    void edit(T obj) throws NonexistentEntityException, Exception;
    void destroy(Integer id) throws NonexistentEntityException;
    List<T> findAll();
    List<T> findAll(int maxResults, int firstResult);
    List<T> findAll(boolean all, int maxResults, int firstResult);
    T find(Integer entityId);
    int getCount();
    
}
