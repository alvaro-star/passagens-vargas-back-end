package com.alvaro.empresas.passagens.configuracoes.jpa;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.EntityNotFoundException;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.core.RepositoryMethodContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ICustomRepositoryImpl<T, U> implements ICustomRepository<T, U> {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public T findByIdOrThr(U id) throws RuntimeException {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Class<T> domainClass = null;
        try {
            domainClass = (Class<T>) RepositoryMethodContext.getContext().getMetadata().getDomainType();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RestRuntimeException(HttpStatus.INTERNAL_SERVER_ERROR, "Houve um erro interno no servidor");
        }
        CriteriaQuery<T> cq = cb.createQuery(domainClass);
        Root<T> root = cq.from(domainClass);
        cq.select(root).where(cb.equal(root.get("id"), id));

        var result = entityManager.createQuery(cq).getResultList();
        switch (result.size()) {
            case 0 -> throw new EntityNotFoundException(id, domainClass);
            case 1 -> {
                return result.get(0);
            }
            default -> {
                log.error("Existem duas entidades com o mesmo nome");
                throw new RestRuntimeException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Houve um erro interno no servidor");
            }
        }
    }

    public T findByIdOrThr(U id, String fieldName) throws RuntimeException {
        try {
            var model = this.findByIdOrThr(id);
            return model;
        } catch (EntityNotFoundException ex) {
            throw new ValidationException(fieldName, getDefaultMessageToValidationError(ex));
        }
    }

    private String getDefaultMessageToValidationError(EntityNotFoundException ex) {
        return "Não existe um(a) " + EntityNotFoundException.clearEntityName(ex.getEntityClass()) + " com este id";
    }
}
