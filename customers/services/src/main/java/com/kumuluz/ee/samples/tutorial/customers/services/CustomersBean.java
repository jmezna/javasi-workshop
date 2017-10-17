package com.kumuluz.ee.samples.tutorial.customers.services;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.fault.tolerance.annotations.CircuitBreaker;
import com.kumuluz.ee.fault.tolerance.annotations.CommandKey;
import com.kumuluz.ee.fault.tolerance.annotations.Fallback;
import com.kumuluz.ee.fault.tolerance.annotations.GroupKey;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.kumuluz.ee.samples.tutorial.customers.Customer;
import com.kumuluz.ee.samples.tutorial.customers.services.config.RestProperties;
import com.kumuluz.ee.samples.tutorial.orders.Order;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RequestScoped
@GroupKey("customers-bean")
public class CustomersBean {

    private Logger log = LogManager.getLogger(CustomersBean.class.getName());

    @Inject
    private RestProperties restProperties;

    @Inject
    private EntityManager em;

    @Inject
    private CustomersBean customersBean;

    private Client httpClient;

    @Inject
    @DiscoverService("order-service")
    private Optional<String> baseUrl;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
    }


    public List<Customer> getCustomers() {

        TypedQuery<Customer> query = em.createNamedQuery("Customer.getAll", Customer.class);

        return query.getResultList();

    }

    public List<Customer> getCustomersFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, Customer.class, queryParameters);
    }

    public Customer getCustomer(String customerId) {

        Customer customer = em.find(Customer.class, customerId);

        if (customer == null) {
            throw new NotFoundException();
        }

        if (restProperties.isOrderServiceEnabled()) {
            List<Order> orders = customersBean.getOrders(customerId);
            customer.setOrders(orders);
        }

        return customer;
    }

    public Customer createCustomer(Customer customer) {

        try {
            beginTx();
            em.persist(customer);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return customer;
    }

    public Customer putCustomer(String customerId, Customer customer) {

        Customer c = em.find(Customer.class, customerId);

        if (c == null) {
            return null;
        }

        try {
            beginTx();
            customer.setId(c.getId());
            customer = em.merge(customer);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return customer;
    }

    public boolean deleteCustomer(String customerId) {

        Customer customer = em.find(Customer.class, customerId);

        if (customer != null) {
            try {
                beginTx();
                em.remove(customer);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else
            return false;

        return true;
    }


    @CircuitBreaker
    @CommandKey("get-orders-http")
    @Fallback(fallbackMethod = "getOrdersFallback")
    public List<Order> getOrders(String customerId) {

        if (baseUrl.isPresent()) {

            try {
                return httpClient
                        .target(baseUrl.get() + "/v1/orders?where=customerId:EQ:" + customerId)
                        .request().get(new GenericType<List<Order>>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {
                log.error(e);
                throw new InternalServerErrorException(e);
            }
        } else {
            return new ArrayList<>();
        }
    }

    public List<Order> getOrdersFallback(String customerId) {
        return new ArrayList<>();
    }


    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }

    public void loadOrder(Integer n) {


    }
}
